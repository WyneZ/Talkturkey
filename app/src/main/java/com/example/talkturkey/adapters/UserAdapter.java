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
import com.example.talkturkey.listeners.UserListener;
import com.example.talkturkey.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private UserListener listener;

    public UserAdapter(List<User> userList, UserListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_container, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.setUserData(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_userName, tv_userEmail;
        private ImageView iv_userImg;

        public UserViewHolder(View itemView) {
            super(itemView);
            tv_userName = itemView.findViewById(R.id.tv_userName);
            tv_userEmail = itemView.findViewById(R.id.tv_userEmail);
            iv_userImg = itemView.findViewById(R.id.iv_userImg);
        }

        void setUserData(User user) {
            tv_userName.setText(user.getName());
            tv_userEmail.setText(user.getEmail());
            iv_userImg.setImageBitmap(getUserImage(user.getImage()));
            itemView.setOnClickListener(view -> listener.onUserClicked(user));
        }

        private Bitmap getUserImage(String encodeImage) {
            byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

    }
}















