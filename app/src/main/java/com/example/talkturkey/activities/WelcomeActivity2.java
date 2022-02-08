package com.example.talkturkey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.talkturkey.R;
import com.google.android.material.button.MaterialButton;

public class WelcomeActivity2 extends AppCompatActivity {

    private MaterialButton btn_back, btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome2);
        getSupportActionBar().hide();

        btn_back = findViewById(R.id.btn_back);
        btn_next = findViewById(R.id.btn_next);

        btn_next.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity2.this, SignInActivity.class));
            finish();
        });

        btn_back.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity2.this, WelcomeActivity1.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

    }
}