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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.R;
import mazad.mazad.models.CommentModel;
import mazad.mazad.models.NewModel;

public class RepliesAdapter extends RecyclerView.Adapter<RepliesAdapter.ReplyHolder> {


    private Context mContext;

    private ArrayList<CommentModel> items;

    private OnItemClick onItemClick;

    private OnSpamClick onSpamClick;

    public RepliesAdapter(Context mContext, ArrayList<CommentModel> items, OnItemClick onItemClick, OnSpamClick onSpamClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
        this.onSpamClick = onSpamClick;
    }

    public interface OnItemClick {
        void setOnItemClick(int position);
    }

    public interface OnSpamClick {
        void setOnItemClick(int position);
    }

    @NonNull
    @Override
    public ReplyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reply_item, parent,false);
        return new ReplyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ReplyHolder holder, int position) {
        holder.number.setText(items.get(position).getUser());
        holder.reply.setText(items.get(position).getName());
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ReplyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.number)
        TextView number;
        @BindView(R.id.reply)
        TextView reply;
        @BindView(R.id.spam)
        ImageView spam;

        public ReplyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            spam.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() != R.id.spam){
                int position = getAdapterPosition();
                onItemClick.setOnItemClick(position);
            } else {
                onSpamClick.setOnItemClick(getAdapterPosition());
            }
        }
    }

}
