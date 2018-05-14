package mazad.mazad.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindBitmap;
import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.R;
import mazad.mazad.models.SearchModel;
import mazad.mazad.models.SubCategoryModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;

public class SpecializedSearchAdapter extends RecyclerView.Adapter<SpecializedSearchAdapter.SpecializedSearchHolder> {

    private Context mContext;

    private ArrayList<SearchModel> items;

    private OnItemClick onItemClick;

    private OnSearchClick onSearchClick;

    ArrayList<SubCategoryModel> children;

    ArrayList<SubCategoryModel> subCategoryModels;

    boolean isCheckedGlobal = false;


    private static final String TAG = "SpecializedSearchAdapter";

    private int selectedSubCategory = -1;
    private int selectedSubCategoryChildren = -1;

    public SpecializedSearchAdapter(Context mContext, ArrayList<SearchModel> items, OnItemClick onItemClick,OnSearchClick onSearchClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
        this.onSearchClick = onSearchClick;
    }

    public interface OnItemClick {
        void setOnItemClick(int position);
    }

    public interface OnSearchClick {
        void setOnSearchClick(int position,int selectedCategory,int selectedChildren,boolean isChecked);
    }

    @NonNull
    @Override
    public SpecializedSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);
        return new SpecializedSearchHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final SpecializedSearchHolder holder, int position) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        Connector connector = new Connector(mContext, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                subCategoryModels = new ArrayList<>(Connector.getSubCategoryJson(response));
                items.get(holder.getAdapterPosition()).setSubCategoryModels(subCategoryModels);
                for (int i = 0; i < subCategoryModels.size(); i++) {
                    RadioButton radioButton = new RadioButton(mContext);
                    radioButton.setTextSize(12);
                    if (!subCategoryModels.get(i).getChildren().isEmpty()){
                        radioButton.setTag(subCategoryModels.get(i).getChildren());
                    }
                    radioButton.setText(subCategoryModels.get(i).getName());
                    radioButton.setTypeface(Typeface.DEFAULT_BOLD);
                    radioButton.setLayoutParams(params);
                    radioButton.setId(i);
                    radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                isCheckedGlobal = false;
                                holder.radioGroup.removeAllViews();
                                selectedSubCategory = buttonView.getId();
                                children = (ArrayList<SubCategoryModel>) buttonView.getTag();
                                if (children != null) {
                                    if (children.isEmpty()) {

                                    } else {
                                        for (int j = 0; j < children.size(); j++) {
                                            RadioButton radioButton2 = new RadioButton(mContext);
                                            radioButton2.setTextSize(12);
                                            radioButton2.setId(j);
                                            radioButton2.setText(children.get(j).getName());
                                            radioButton2.setTypeface(Typeface.DEFAULT_BOLD);
                                            radioButton2.setLayoutParams(params);
                                            radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        selectedSubCategoryChildren = buttonView.getId();
                                                        isCheckedGlobal = isChecked;
                                                    }
                                                }
                                            });
                                            holder.radioGroup.addView(radioButton2, j, params);
                                        }
                                    }
                                }
                            }
                        }
                    });
                    holder.subCategories.addView(radioButton, i, params);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {

            }
        });
        Map<String, String> map = new HashMap<>();
        map.put("category_id", items.get(position).getDepartmentModel().getId());
        connector.setMap(map);
        connector.getRequest(TAG, Connector.createGetSubCategoriesUrl());
        params.gravity = Gravity.CENTER;
        holder.category.setText(items.get(position).getDepartmentModel().getName());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class SpecializedSearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.category)
        TextView category;
        @BindView(R.id.radio_parent)
        RadioGroup subCategories;
        @BindView(R.id.search)
        Button search;
        @BindView(R.id.radio_parent_2)
        RadioGroup radioGroup;
        @BindView(R.id.radio_parent_2_p)
        View view;

        public SpecializedSearchHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            search.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.search) {
                selectedSubCategoryChildren = radioGroup.getCheckedRadioButtonId();
                selectedSubCategory = subCategories.getCheckedRadioButtonId();
                onSearchClick.setOnSearchClick(getAdapterPosition(),selectedSubCategory,selectedSubCategoryChildren,isCheckedGlobal);
            } else {
                int position = getAdapterPosition();
                onItemClick.setOnItemClick(position);
            }
        }



    }

    public ArrayList<SearchModel> getItems() {
        return items;
    }
}
