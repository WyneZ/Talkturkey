package com.example.talkturkey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.talkturkey.R;

public class AuthCodeActivity extends AppCompatActivity {

    private Button btn_Con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_code);
        getSupportActionBar().hide();

        btn_Con = findViewById(R.id.btn_Con);

        btn_Con.setOnClickListener(v -> startActivity(new Intent(AuthCodeActivity.this, MainActivity.class)));
    }
}