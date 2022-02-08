package com.example.talkturkey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.talkturkey.R;
import com.google.android.material.button.MaterialButton;

public class WelcomeActivity1 extends AppCompatActivity {

    private MaterialButton btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome1);
        getSupportActionBar().hide();

        btn_next = findViewById(R.id.btn_next);

        btn_next.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity1.this, WelcomeActivity2.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}