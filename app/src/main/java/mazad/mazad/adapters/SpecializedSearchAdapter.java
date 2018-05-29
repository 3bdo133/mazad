package mazad.mazad.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindBitmap;
import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.AddAdvertisingActivity;
import mazad.mazad.R;
import mazad.mazad.models.CityModel;
import mazad.mazad.models.SearchModel;
import mazad.mazad.models.SubCategoryModel;
import mazad.mazad.utils.Connector;
import mazad.mazad.utils.Helper;

public class SpecializedSearchAdapter extends RecyclerView.Adapter<SpecializedSearchAdapter.SpecializedSearchHolder> {

    private Context mContext;

    private ArrayList<SearchModel> items;

    private OnItemClick onItemClick;

    private OnSearchClick onSearchClick;

    private ArrayList<CityModel> cityModels;

    ArrayList<SubCategoryModel> children;

    ArrayList<SubCategoryModel> subCategoryModels;

    CityModel mCityModelSelected;

    boolean isCheckedGlobal = false;

    String mModelSelected = "الموديل";


    private static final String TAG = "SpecializedSearchAdapter";

    private int selectedSubCategory = -1;
    private int selectedSubCategoryChildren = -1;

    public SpecializedSearchAdapter(Context mContext, ArrayList<SearchModel> items, OnItemClick onItemClick, OnSearchClick onSearchClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
        this.onSearchClick = onSearchClick;
    }

    public interface OnItemClick {
        void setOnItemClick(int position);
    }

    public interface OnSearchClick {
        void setOnSearchClick(int position, int selectedCategory, int selectedChildren, boolean isChecked, String modelSelected, CityModel cityModel);
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

        if (items.get(position).getDepartmentModel().getName().equals("سيارات")) {
            holder.mModelSpinner.setVisibility(View.VISIBLE);
            holder.mModelParent.setVisibility(View.VISIBLE);
            final ArrayAdapter<CharSequence> adapterModel =
                    new ArrayAdapter<CharSequence>(mContext, android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());

            adapterModel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapterModel.add("الموديل");
            for (int i = 1970; i <= 2018; i++) {
                adapterModel.add(String.valueOf(i));
            }

            holder.mModelSpinner.setAdapter(adapterModel);


            holder.mModelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mModelSelected = (String) adapterModel.getItem(position);
                    Helper.writeToLog(mModelSelected);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        } else {
            holder.mModelSpinner.setVisibility(View.GONE);
            holder.mModelParent.setVisibility(View.GONE);
        }

        Connector connector = new Connector(mContext, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                holder.subCategories.removeAllViews();
                holder.radioGroup.removeAllViews();
                subCategoryModels = new ArrayList<>(Connector.getSubCategoryJson(response));
                items.get(holder.getAdapterPosition()).setSubCategoryModels(subCategoryModels);
                for (int i = 0; i < subCategoryModels.size(); i++) {
                    RadioButton radioButton = new RadioButton(mContext);
                    radioButton.setTextSize(12);
                    if (!subCategoryModels.get(i).getChildren().isEmpty()) {
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


        cityModels = new ArrayList<>();

        final ArrayAdapter<CharSequence> adapter =
                new ArrayAdapter<CharSequence>(mContext, android.R.layout.simple_spinner_item, new ArrayList<CharSequence>());


        cityModels.add(new CityModel("-1", "المدينة"));

        adapter.add(cityModels.get(0).getName());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        holder.mCitySpinner.setAdapter(adapter);

        holder.mCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCityModelSelected = cityModels.get(position);
                Helper.writeToLog(mCityModelSelected.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Connector mCityConnector = new Connector(mContext, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    cityModels.clear();
                    cityModels.addAll(Connector.getCitiesJson(response));
                    for (int i = 1; i < cityModels.size(); i++) {
                        adapter.add(cityModels.get(i).getName());
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {

            }
        });


        mCityConnector.getRequest(TAG, Connector.createGetCitiesUrl());

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
        @BindView(R.id.model_spinner)
        Spinner mModelSpinner;
        @BindView(R.id.model_parent)
        View mModelParent;
        @BindView(R.id.city_spinner)
        Spinner mCitySpinner;

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
                if (mModelSpinner.getVisibility() == View.GONE) {
                    onSearchClick.setOnSearchClick(getAdapterPosition(), selectedSubCategory, selectedSubCategoryChildren, isCheckedGlobal, "-1", mCityModelSelected);
                    mModelSelected = "الموديل";
                    mCityModelSelected = new CityModel("-1", "المدينة");

                } else {
                    onSearchClick.setOnSearchClick(getAdapterPosition(), selectedSubCategory, selectedSubCategoryChildren, isCheckedGlobal, mModelSelected, mCityModelSelected);
                }
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
