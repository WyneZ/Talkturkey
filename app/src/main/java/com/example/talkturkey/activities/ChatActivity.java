package com.example.talkturkey.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.talkturkey.R;
import com.example.talkturkey.adapters.ChatAdapter;
import com.example.talkturkey.database.Constants;
import com.example.talkturkey.database.PreferenceManager;
import com.example.talkturkey.models.ChatMessage;
import com.example.talkturkey.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatActivity extends StatusActivity {

    private EditText et_InputMsg;
    private ImageView iv_sendMsg, iv_backChatarrow, iv_ConImg;
    private TextView tv_ConName, tv_Status;
    private RecyclerView rv_Chat;
    private ProgressBar pbOfchat;

    private ChatAdapter adapter;
    private User rUser;
    private List<ChatMessage> msgList;
    private PreferenceManager manager;
    private FirebaseFirestore db;
    private String conId = null;
    private Boolean isAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();

        et_InputMsg = findViewById(R.id.et_InputMsg);
        iv_sendMsg = findViewById(R.id.iv_sendMsg);
        iv_backChatarrow = findViewById(R.id.iv_backChatarrow);
        iv_ConImg = findViewById(R.id.iv_ConImg);
        tv_ConName = findViewById(R.id.tv_ConName);
        tv_Status = findViewById(R.id.tv_status);
        rv_Chat = findViewById(R.id.rv_Chat);
        pbOfchat = findViewById(R.id.pbOfchat);

        loadReceiverDetails();

        manager = new PreferenceManager(this);
        msgList = new ArrayList<>();
        adapter = new ChatAdapter(msgList, getBitmap(rUser.getImage()), manager.getString(Constants.KEY_USER_ID));

        rv_Chat.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        iv_ConImg.setImageBitmap(getBitmap(rUser.getImage()));

        setClicked();
        listenMessages();
    }

    public void setClicked() {
        iv_backChatarrow.setOnClickListener(view -> onBackPressed());
        iv_sendMsg.setOnClickListener(view -> {
            if(et_InputMsg.getText().toString().trim().equals("")) {
                iv_sendMsg.setClickable(false);
                et_InputMsg.setText("");
            }
            else sendMsg();
            iv_sendMsg.setClickable(true);
        });
    }

    private void sendMsg() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, manager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, rUser.getId());
        message.put(Constants.KEY_MESSAGE, et_InputMsg.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());

        db.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conId != null) {
            updateConversation(et_InputMsg.getText().toString());
        }
        else {
            HashMap<String, Object> conversations = new HashMap<>();
            conversations.put(Constants.KEY_SENDER_ID, manager.getString(Constants.KEY_USER_ID));
            conversations.put(Constants.KEY_SENDER_NAME, manager.getString(Constants.KEY_NAME));
            conversations.put(Constants.KEY_SENDER_IMAGE, manager.getString(Constants.KEY_IMAGE));
            conversations.put(Constants.KEY_RECEIVER_ID, rUser.getId());
            conversations.put(Constants.KEY_RECEIVER_NAME, rUser.getName());
            conversations.put(Constants.KEY_RECEIVER_IMAGE, rUser.getImage());
            conversations.put(Constants.KEY_LAST_MESSAGE, et_InputMsg.getText().toString());
            conversations.put(Constants.KEY_TIMESTAMP, new Date());

            db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                    .add(conversations)
                    .addOnSuccessListener(documentReference -> conId = documentReference.getId());
        }
        et_InputMsg.setText(null);
    }

    private void updateConversation(String msg) {
        DocumentReference reference = db.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conId);
        reference.update(Constants.KEY_LAST_MESSAGE, msg, Constants.KEY_TIMESTAMP, new Date());
    }

    private void loadReceiverDetails() {
        rUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        tv_ConName.setText(rUser.getName());
    }

    private Bitmap getBitmap(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void listenMessages() {
        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, manager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, rUser.getId())
                .addSnapshotListener(eventListener);

        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, rUser.getId())
                .whereEqualTo(Constants.KEY_RECEIVER_ID, manager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null){
            return;
        }
        if(value != null) {
            int count = msgList.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage message = new ChatMessage();
                    message.setSenderId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    message.setReceiverId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    message.setMessage(documentChange.getDocument().getString(Constants.KEY_MESSAGE));
                    message.setDateTime(getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)));
                    message.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    msgList.add(message);
                }
            }
            Collections.sort(msgList, (obj1, obj2) -> obj1.getDateObject().compareTo(obj2.getDateObject()));
            if(count == 0) adapter.notifyDataSetChanged();
            else {
                adapter.notifyItemRangeInserted(msgList.size(), msgList.size());
                rv_Chat.smoothScrollToPosition(msgList.size() - 1);
            }
            rv_Chat.setVisibility(View.VISIBLE);
        }
        pbOfchat.setVisibility(View.GONE);
        if(conId == null) checkForConversation();
    });

    private void checkForConversation() {
        checkForConversationRemotely(manager.getString(Constants.KEY_USER_ID), rUser.getId());
        checkForConversationRemotely(rUser.getId(), manager.getString(Constants.KEY_USER_ID));
    }

    private void checkForConversationRemotely(String senderId, String receiverId) {
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversationOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conId = documentSnapshot.getId();
        }
    };

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void statusOfReceiver() {
        db.collection(Constants.KEY_COLLECTION_USERS)
                .document(rUser.getId())
                .addSnapshotListener(ChatActivity.this, (value, error) -> {
                    if(error != null) return;
                    if(value != null) {
                        if(value.getLong(Constants.KEY_STATUS) != null) {
                            int status = Objects.requireNonNull(value.getLong(Constants.KEY_STATUS)).intValue();
                            isAvailable = status == 1;
                        }
                    }
                    if(isAvailable) tv_Status.setVisibility(View.VISIBLE);
                    else tv_Status.setVisibility(View.GONE);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusOfReceiver();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChatActivity.this, MainActivity.class));
        finish();
    }
}