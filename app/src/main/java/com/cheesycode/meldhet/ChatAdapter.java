package com.cheesycode.meldhet;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatAdapter extends RecyclerView.Adapter {
    private String[] messages;

    public ChatAdapter(String[] p0) {
        messages = p0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_view, viewGroup, false);
        MessageHolder vh = new MessageHolder(v);
        return vh;
}

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MessageHolder messageHolder= (MessageHolder)viewHolder;
        messageHolder.message.setText(messages[i]);
        if(i%2==0){
            messageHolder.linearLayout.setGravity(Gravity.RIGHT);
        }
    }

    @Override
    public int getItemCount() {
        return messages.length;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MessageHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout linearLayout;
        public TextView message;
        public MessageHolder(LinearLayout v) {
            super(v);
            linearLayout = v;
            message = v.findViewById(R.id.message);
        }
    }
}
