package com.example.talkturkey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.talkturkey.R;
import com.example.talkturkey.database.Constants;
import com.example.talkturkey.database.PreferenceManager;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getSupportActionBar().hide();

        PreferenceManager manager = new PreferenceManager(this);
        Boolean b = manager.getBoolean(Constants.KEY_IS_SIGNED_IN);

        new Handler().postDelayed(() -> {
            if(b) startActivity(new Intent(this, MainActivity.class));
            else startActivity(new Intent(IntroActivity.this, WelcomeActivity1.class));
            finish();
        },1000);
    }
}