package com.example.talkturkey.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telecom.TelecomManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talkturkey.R;
import com.example.talkturkey.activities.ChatActivity;
import com.example.talkturkey.activities.MainActivity;
import com.example.talkturkey.adapters.ConversationsAdapter;
import com.example.talkturkey.database.Constants;
import com.example.talkturkey.database.PreferenceManager;
import com.example.talkturkey.listeners.ConversationListener;
import com.example.talkturkey.listeners.UserListener;
import com.example.talkturkey.models.ChatMessage;
import com.example.talkturkey.models.User;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;


public class ConversationFragment extends Fragment implements ConversationListener {

    private RecyclerView rv_Conversations;
    private ProgressBar pbOfConversation;
    private TextView tv_errorCon;

    private List<ChatMessage> conList;
    private ConversationsAdapter adapter;
    private FirebaseFirestore db;
    private PreferenceManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("WhatUp", "OK");

        rv_Conversations = view.findViewById(R.id.rv_Con);
        pbOfConversation = view.findViewById(R.id.pbOfConversation);
        tv_errorCon = view.findViewById(R.id.tv_errorCon);

        conList = new ArrayList<>();
        adapter = new ConversationsAdapter(conList, this);
        rv_Conversations.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_Conversations.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        manager = new PreferenceManager(getContext());

        getToken();
        listenConversations();
    }

    private void listenConversations() {
        Log.d("WhatUp", "Listen");
        loading(true);
        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, manager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

        db.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, manager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            return;
        }
        if (value != null) {
            for(DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(senderId);
                    chatMessage.setReceiverId(receiverId);
                    if(manager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.setConImage(documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE));
                        chatMessage.setConName(documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME));
                        chatMessage.setConId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    }
                    else {
                        chatMessage.setConImage(documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE));
                        chatMessage.setConName(documentChange.getDocument().getString(Constants.KEY_SENDER_NAME));
                        chatMessage.setConId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    }
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    conList.add(chatMessage);
                }
                else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for(int i=0; i < conList.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if(conList.get(i).getSenderId().equals(senderId) && conList.get(i).getReceiverId().equals(receiverId)) {
                            conList.get(i).setMessage(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                            conList.get(i).setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                            break;
                        }
                    }
                }
            }
            Collections.sort(conList, (obj1, obj2) -> obj2.getDateObject().compareTo(obj1.getDateObject()));

            Log.d("WhatUp", conList.size() + " ");
            if(conList.size() <= 0) tv_errorCon.setVisibility(View.VISIBLE);
            else {
                loading(false);
                tv_errorCon.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
            rv_Conversations.smoothScrollToPosition(0);
            rv_Conversations.setVisibility(View.VISIBLE);
            pbOfConversation.setVisibility(View.GONE);
        }
    };

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection(Constants.KEY_COLLECTION_USERS)
                .document(manager.getString(Constants.KEY_USER_ID));
        reference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Unable to update token", Toast.LENGTH_SHORT).show());
    }

    void loading(boolean b) {
        if(b) {
            rv_Conversations.setVisibility(View.GONE);
            pbOfConversation.setVisibility(View.VISIBLE);
        }
        else {
            pbOfConversation.setVisibility(View.GONE);
            rv_Conversations.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConversationClicked(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}