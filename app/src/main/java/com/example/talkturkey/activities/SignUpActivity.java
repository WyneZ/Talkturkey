package com.example.talkturkey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.talkturkey.R;

public class SignUpActivity extends AppCompatActivity {

    private Button btn_continue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        btn_continue = findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(v -> startActivity(new Intent(this, AuthCodeActivity.class)));

    }
}