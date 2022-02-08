package com.example.talkturkey.adapters;

import android.graphics.Bitmap;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talkturkey.R;
import com.example.talkturkey.models.ChatMessage;

import org.w3c.dom.Text;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> msgList;
    private final Bitmap receiverImage;
    private String senderId;

    public static final int VIEW_TYPE_SEND = 1;
    public static final int VIEW_TYPE_RECEIVE = 2;

    public ChatAdapter(List<ChatMessage> msgList, Bitmap receiverImage, String senderId) {
        this.msgList = msgList;
        this.receiverImage = receiverImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SEND) {
            return new SendMsgViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_send_message, parent, false));
        }
        else {
            return new ReceiveMsgViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.layout_receive_message, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SEND) {
            ((SendMsgViewHolder) holder).setData(msgList.get(position));
        }
        else {
            ((ReceiveMsgViewHolder) holder).setData(msgList.get(position), receiverImage);
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(msgList.get(position).getSenderId().equals(senderId)) return VIEW_TYPE_SEND;
        else return VIEW_TYPE_RECEIVE;
    }

    class SendMsgViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_sMsg, tv_sDateTime;

        public SendMsgViewHolder(@NonNull View v) {
            super(v);
            tv_sMsg = v.findViewById(R.id.tv_sMsg);
            tv_sDateTime = v.findViewById(R.id.tv_sDateTime);
        }

        void setData(ChatMessage msg) {
            tv_sMsg.setText(msg.getMessage());
            tv_sDateTime.setText(msg.getDateTime());
        }
    }

    class ReceiveMsgViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_rImg;
        private TextView tv_rMsg, tv_rDateTime;

        public ReceiveMsgViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_rImg = itemView.findViewById(R.id.iv_rImg);
            tv_rMsg = itemView.findViewById(R.id.tv_rMsg);
            tv_rDateTime = itemView.findViewById(R.id.tv_rDateTime);
        }

        void setData(ChatMessage msg, Bitmap receiverImg) {
            tv_rMsg.setText(msg.getMessage());
            tv_rDateTime.setText(msg.getDateTime());
            iv_rImg.setImageBitmap(receiverImg);
        }
    }
}
