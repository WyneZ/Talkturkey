package com.example.talkturkey.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talkturkey.R;
import com.example.talkturkey.listeners.ConversationListener;
import com.example.talkturkey.models.ChatMessage;
import com.example.talkturkey.models.User;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ConViewHolder> {

    private List<ChatMessage> msgList;
    private ConversationListener listener;

    public ConversationsAdapter(List<ChatMessage> msgList, ConversationListener listener) {
        this.msgList = msgList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("WhatUp", "Created");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_container, parent, false);
        return new ConViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ConViewHolder holder, int position) {
        Log.d("WhatUp", "Binding");
        holder.setData(msgList.get(position));
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    class ConViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_conName, tv_RecentMessage;
        private ImageView iv_conImg;

        public ConViewHolder(@NonNull View v) {
            super(v);
            tv_conName = v.findViewById(R.id.tv_conName);
            tv_RecentMessage = v.findViewById(R.id.tv_RecentMessage);
            iv_conImg = v.findViewById(R.id.iv_conImg);
        }

        void setData(ChatMessage chatMessage) {
            iv_conImg.setImageBitmap(getConversationImage(chatMessage.getConImage()));
            tv_conName.setText(chatMessage.getConName());
            tv_RecentMessage.setText(chatMessage.getMessage());
            itemView.setOnClickListener(view -> {
                User user = new User();
                user.setId(chatMessage.getConId());
                user.setName(chatMessage.getConName());
                user.setImage(chatMessage.getConImage());
                listener.onConversationClicked(user);
            });
        }
    }

    private Bitmap getConversationImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
