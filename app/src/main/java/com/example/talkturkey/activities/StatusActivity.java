package com.example.talkturkey.activities;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.talkturkey.database.Constants;
import com.example.talkturkey.database.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class StatusActivity extends AppCompatActivity {

    private DocumentReference reference;
    private PreferenceManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new PreferenceManager(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reference = db.collection(Constants.KEY_COLLECTION_USERS)
                .document(manager.getString(Constants.KEY_USER_ID));
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.update(Constants.KEY_STATUS, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reference.update(Constants.KEY_STATUS, 1);
    }
}
