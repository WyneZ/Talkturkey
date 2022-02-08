package com.example.talkturkey.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.talkturkey.R;
import com.example.talkturkey.database.Constants;
import com.example.talkturkey.database.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    private Button btn_Login, btn_SignUp;
    private ProgressBar pbOfSignIn;
    private EditText et_Email, et_pw;

    private PreferenceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();

        btn_Login = findViewById(R.id.btn_Login);
        btn_SignUp = findViewById(R.id.btn_Signup);
        et_Email = findViewById(R.id.et_email);
        et_pw = findViewById(R.id.et_pw);
        pbOfSignIn = findViewById(R.id.pbOfSignIn);

        manager = new PreferenceManager(this);

        btn_Login.setOnClickListener(view -> {
            if(validSignIn()) signIn();
        });

        btn_SignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    void signIn() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, et_Email.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, et_pw.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                        manager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        manager.putString(Constants.KEY_USER_ID, snapshot.getId());
                        manager.putString(Constants.KEY_NAME, snapshot.getString(Constants.KEY_NAME));
                        manager.putString(Constants.KEY_IMAGE, snapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        loading(false);
                        showToast("Enable to SignIn");
                    }
                });
    }

    private Boolean validSignIn() {
        if(et_Email.getText().toString().trim().isEmpty()) {
            showToast("Enter Email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(et_Email.getText().toString()).matches()) {
            showToast("Enter Valid Email");
            return false;
        }
        else if(et_pw.getText().toString().trim().isEmpty()) {
            showToast("Enter Password");
            return false;
        }
        else return true;
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    void loading(boolean b) {
        if(b) {
            btn_Login.setClickable(false);
            btn_SignUp.setClickable(false);
            et_pw.setClickable(false);
            et_Email.setClickable(false);
            pbOfSignIn.setVisibility(View.VISIBLE);
        }
        else {
            pbOfSignIn.setVisibility(View.GONE);
            btn_Login.setClickable(true);
            btn_SignUp.setClickable(true);
            et_pw.setClickable(true);
            et_Email.setClickable(true);
        }
    }
}