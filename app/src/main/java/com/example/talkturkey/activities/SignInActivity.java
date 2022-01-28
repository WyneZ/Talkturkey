package com.example.talkturkey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.talkturkey.R;

public class SignInActivity extends AppCompatActivity {

    private Button btn_Login, btn_SignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();

        btn_Login = findViewById(R.id.btn_Login);
        btn_SignUp = findViewById(R.id.btn_Signup);

        btn_SignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
    }
}