package com.example.talkturkey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.talkturkey.R;
import com.example.talkturkey.database.Constants;
import com.example.talkturkey.database.PreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private Button btn_continue;
    private EditText et_fName, et_lName, et_email, et_Pw, et_confirmPw;
    private ImageView iv_backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        btn_continue = findViewById(R.id.btn_continue);
        et_fName = findViewById(R.id.et_fName);
        et_lName = findViewById(R.id.et_lName);
        et_email = findViewById(R.id.et_email);
        et_Pw = findViewById(R.id.et_Pw);
        et_confirmPw = findViewById(R.id.et_confirmPw);
        iv_backArrow = findViewById(R.id.iv_backArrow);

        iv_backArrow.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, SignInActivity.class)));
        btn_continue.setOnClickListener(v -> {
            if(isVisibleSingUp()){
                Intent intent = new Intent(SignUpActivity.this, InputPpActivity.class);
                intent.putExtra("name", et_fName.getText().toString() + " " + et_lName.getText().toString());
                intent.putExtra("email", et_email.getText().toString());
                intent.putExtra("password", et_Pw.getText().toString());
                startActivity(intent);
            }
        });
    }

    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public boolean isVisibleSingUp() {
        if(et_fName.getText().toString().trim().isEmpty()) {
            showToast("Enter First Name");
            return false;
        }
        else if(et_lName.getText().toString().trim().isEmpty()) {
            showToast("Enter Last Name");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(et_email.getText().toString()).matches()){
            showToast("Enter Valid Email");
            return false;
        }
        else if(et_Pw.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        }
        else if(!et_confirmPw.getText().toString().equals(et_Pw.getText().toString())) {
            showToast("Password & Confirm Password must be same");
            return false;
        }
        else return true;
    }
}