package mazad.mazad.adapters;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.R;
import mazad.mazad.models.DepartmentModel;
import mazad.mazad.models.NewModel;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentHolder> {

    private Context mContext;

    private ArrayList<DepartmentModel> items;

    private OnItemClick onItemClick;

    public DepartmentAdapter(Context mContext, ArrayList<DepartmentModel> items, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
    }


    public interface OnItemClick {
        void setOnItemClick(int position);
    }

    @NonNull
    @Override
    public DepartmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.departement_item, parent,false);
        return new DepartmentHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final DepartmentHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        Picasso.get().load(items.get(position).getImage()).into(holder.image);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public class DepartmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.image)
        ImageView image;

        public DepartmentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            onItemClick.setOnItemClick(position);
        }
    }

}
