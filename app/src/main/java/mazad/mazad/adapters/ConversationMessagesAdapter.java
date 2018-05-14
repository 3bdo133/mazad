package mazad.mazad.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mazad.mazad.R;
import mazad.mazad.models.MessageModel;

public class ConversationMessagesAdapter extends RecyclerView.Adapter<ConversationMessagesAdapter.ConversationMessageHolder> {


    private Context mContext;

    private ArrayList<MessageModel> items;

    private OnItemClick onItemClick;

    private static final int SEND_MESSAGE = 0;
    private static final int RECEIVED_MESSAGE = 1;

    public ConversationMessagesAdapter(Context mContext, ArrayList<MessageModel> items, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.items = items;
        this.onItemClick = onItemClick;
    }


    public interface OnItemClick {
        void setOnItemClick(int position);
    }


    @Override
    public int getItemViewType(int position) {
        if (items.get(position).isMine()){
            return SEND_MESSAGE;
        } else {
            return RECEIVED_MESSAGE;
        }
    }

    @NonNull
    @Override
    public ConversationMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SEND_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.send_message_item, parent, false);
            return new ConversationMessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.receive_message_item, parent, false);
            return new ConversationMessageHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final ConversationMessageHolder holder, int position) {
        holder.message.setText(items.get(position).getMessage());
        holder.date.setText(items.get(position).getDate());
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ConversationMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.message)
        TextView message;
        @BindView(R.id.date)
        TextView date;

        public ConversationMessageHolder(View itemView) {
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
