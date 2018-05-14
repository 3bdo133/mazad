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
import mazad.mazad.models.NewModel;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewHolder> {


    private Context mContext;

    private ArrayList<NewModel> items;

    private OnItemClick onItemClick;

    public NewsAdapter(Context mContext, ArrayList<NewModel> items, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
    }



    public interface OnItemClick {
        void setOnItemClick(int position);
    }

    @NonNull
    @Override
    public NewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_item, parent,false);
        return new NewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final NewHolder holder, int position) {
        holder.description.setText(items.get(position).getName());
        //holder.time.setText(items.get(position).getTime());
        holder.type.setText(items.get(position).getCategory());
        Picasso.get().load(items.get(position).getImage()).placeholder(R.drawable.im_logo).error(R.drawable.im_logo).into(holder.image);
        if (!TextUtils.isEmpty(items.get(position).getNotification()) && items.get(position).getNotification() != null) {
            holder.notification.setVisibility(View.VISIBLE);
            holder.notification.setText(items.get(position).getNotification());
        } else {
            holder.notification.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public class NewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.notification)
        TextView notification;
        @BindView(R.id.type)
        TextView type;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.new_image)
        ImageView image;

        public NewHolder(View itemView) {
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
