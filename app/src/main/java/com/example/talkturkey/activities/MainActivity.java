package com.example.talkturkey.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.talkturkey.R;
import com.example.talkturkey.database.Constants;
import com.example.talkturkey.database.PreferenceManager;
import com.example.talkturkey.fragments.AddFragment;
import com.example.talkturkey.fragments.ConversationFragment;
import com.example.talkturkey.listeners.UserListener;
import com.example.talkturkey.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends StatusActivity {

    private ImageView iv_Con, iv_add, iv_menu;
    private FloatingActionButton fab;

    private PreferenceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        iv_add = findViewById(R.id.iv_add);
        iv_Con = findViewById(R.id.iv_Con);
        iv_menu = findViewById(R.id.iv_menu);
        fab = findViewById(R.id.fab);

        manager = new PreferenceManager(this);

        addFragment(new ConversationFragment(), true);
        setClick();

    }

    private void setClick() {
        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
        iv_Con.setOnClickListener(view -> addFragment(new ConversationFragment(), true));
        iv_add.setOnClickListener(view -> addFragment(new AddFragment(), true));
        iv_menu.setOnClickListener(view -> showPopupMenu(view));

    }

    public void addFragment(Fragment f, boolean stack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //if(stack) ft.addToBackStack(f.getTag());
        ft.replace(R.id.frags, f);
        ft.commit();
    }

    private void showPopupMenu(View v) {
        PopupMenu menu = new PopupMenu(v.getContext(), v);
        menu.inflate(R.menu.option_menu);
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.mProfile:

                    return true;

                case R.id.mHelp:

                    return true;

                case R.id.mSignOut:
                    signOut();
                    return true;
            }
            return false;
        });
        menu.show();
    }

    private void signOut(){
        Toast.makeText(this, "Signing Out", Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection(Constants.KEY_COLLECTION_USERS)
                .document(manager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> deleteToken = new HashMap<>();
        deleteToken.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        reference.update(deleteToken)
                .addOnSuccessListener(unused -> {
                    manager.clear();
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Unable to SignOut", Toast.LENGTH_SHORT).show());
    }

}