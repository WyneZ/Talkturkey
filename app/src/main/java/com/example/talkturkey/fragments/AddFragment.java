package com.example.talkturkey.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talkturkey.R;
import com.example.talkturkey.activities.ChatActivity;
import com.example.talkturkey.adapters.UserAdapter;
import com.example.talkturkey.database.Constants;
import com.example.talkturkey.database.PreferenceManager;
import com.example.talkturkey.listeners.UserListener;
import com.example.talkturkey.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class AddFragment extends Fragment implements UserListener {

    private RecyclerView rv_Add;
    private ProgressBar pbOfAdd;
    private TextView tv_errorMsg;

    private PreferenceManager manager;
    private UserAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_Add = view.findViewById(R.id.rv_Add);
        pbOfAdd = view.findViewById(R.id.pbOfAdd);
        tv_errorMsg = view.findViewById(R.id.tv_errorMsg);

        manager = new PreferenceManager(getContext());

        getUsers();
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = manager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null) {
                        List<User> userList = new ArrayList<>();
                        for(QueryDocumentSnapshot snapshot : task.getResult()) {
                            if(currentUserId.equals(snapshot.getId())) continue;
                            User user = new User();
                            user.setName(snapshot.getString(Constants.KEY_NAME));
                            user.setEmail(snapshot.getString(Constants.KEY_EMAIL));
                            user.setImage(snapshot.getString(Constants.KEY_IMAGE));
                            user.setToken(snapshot.getString(Constants.KEY_FCM_TOKEN));
                            user.setId(snapshot.getId());
                            userList.add(user);
                        }
                        if(userList.size() > 0) {
                            adapter = new UserAdapter(userList, this);
                            rv_Add.setLayoutManager(new LinearLayoutManager(getContext()));
                            rv_Add.setAdapter(adapter);
                            rv_Add.setVisibility(View.VISIBLE);
                        }
                        else {
                            tv_errorMsg.setText("No User Available");
                            tv_errorMsg.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    void loading(boolean b) {
        if(b) {
            rv_Add.setVisibility(View.GONE);
            pbOfAdd.setVisibility(View.VISIBLE);
        }
        else {
            pbOfAdd.setVisibility(View.GONE);
            rv_Add.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        getActivity().finish();
    }
}