package mazad.mazad.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.R;
import mazad.mazad.models.RecentSearchModel;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.RecentSearchHolder> {


    private Context mContext;

    private ArrayList<RecentSearchModel> items;

    private OnItemClick onItemClick;

    public RecentSearchAdapter(Context mContext, ArrayList<RecentSearchModel> items, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
    }


    public interface OnItemClick {
        void setOnItemClick(int position);
    }

    @NonNull
    @Override
    public RecentSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_search_item, parent,false);
        return new RecentSearchHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final RecentSearchHolder holder, int position) {
        holder.searchText.setText(items.get(position).getSearchText());
    }


    @Override
    public int getItemCount() {
        return items.size();
    }



    public class RecentSearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.search_text)
        TextView searchText;

        public RecentSearchHolder(View itemView) {
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
