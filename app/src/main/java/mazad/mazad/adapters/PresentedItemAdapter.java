package mazad.mazad.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.R;
import mazad.mazad.models.PresentedItemModel;

public class PresentedItemAdapter extends RecyclerView.Adapter<PresentedItemAdapter.PresentedItemHolder>  {

    private Context mContext;

    private ArrayList<PresentedItemModel> items;

    private OnItemClick onItemClick;

    public PresentedItemAdapter(Context mContext, ArrayList<PresentedItemModel> items, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public PresentedItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.presented_item, parent,false);
        return new PresentedItemHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final PresentedItemHolder holder, int position) {
        holder.location.setText(items.get(position).getmLocation());
        holder.personName.setText(items.get(position).getmUser());
        holder.productName.setText(items.get(position).getmName());
        holder.itemView.setTag(items.get(position).getmId());
        if (TextUtils.isEmpty(items.get(position).getmImage())){
             holder.image.setImageResource(R.drawable.im_logo);
        } else {
            Picasso.get().load(items.get(position).getmImage()).placeholder(R.drawable.im_logo).error(R.drawable.im_logo).into(holder.image);
        }

        if (items.get(position).getFavorite() == 1){
            holder.favorite.setVisibility(View.VISIBLE);
        } else {
            holder.favorite.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PresentedItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.product_image)
        ImageView image;
        @BindView(R.id.product_name)
        TextView productName;
        @BindView(R.id.person_name)
        TextView personName;
        @BindView(R.id.location)
        TextView location;
        @BindView(R.id.favorite)
        ImageView favorite;

        public PresentedItemHolder(View itemView) {
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


    public interface OnItemClick {
        void setOnItemClick(int position);
    }

}
