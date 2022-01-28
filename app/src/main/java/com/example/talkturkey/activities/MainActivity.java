package com.example.talkturkey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.talkturkey.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


    }
}