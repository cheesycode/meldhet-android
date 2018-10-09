package com.cheesycode.meldhet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static android.view.Gravity.RIGHT;

public class ChatAdapter extends RecyclerView.Adapter {
    private List<ChatMessage> messages;

    ChatAdapter(List<ChatMessage> p0) {
        messages = p0;
    }

    void insert(ChatMessage m){
        messages.add(m);
    }
    private  Context context;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LinearLayout v = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_view, viewGroup, false);
        return new MessageHolder(v);
}

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MessageHolder messageHolder= (MessageHolder)viewHolder;
        messageHolder.sender.setText(messages.get(i).sender);
        messageHolder.message.setText(messages.get(i).body);
        if(messages.get(i).sender.equals(MessagingService.getToken(context))|| messages.get(i).sender.equals("U")){
            messageHolder.linearLayout.setGravity(RIGHT);
            messageHolder.sender.setText("U");
            messageHolder.message.setBackground(ContextCompat.getDrawable(context, R.drawable.chatsend));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MessageHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        LinearLayout linearLayout;
        public TextView message;
        TextView sender;
        MessageHolder(LinearLayout v) {
            super(v);
            linearLayout = v;
            message = v.findViewById(R.id.message);
            sender = v.findViewById(R.id.from);
        }
    }
}
