package com.example.talkturkey.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.talkturkey.R;
import com.example.talkturkey.database.Constants;
import com.example.talkturkey.database.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class InputPpActivity extends AppCompatActivity {

    private ImageView iv_setPp;
    private MaterialButton btn_openGallery, btn_PpNext;
    private ProgressBar pbOfPp;

    private PreferenceManager manager;

    private String name, email, password;
    private String encodedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_pp);

        iv_setPp = findViewById(R.id.iv_setPp);
        btn_openGallery = findViewById(R.id.btn_OpenGallery);
        btn_PpNext = findViewById(R.id.btn_PpNext);
        pbOfPp = findViewById(R.id.pbOfPp);

        manager = new PreferenceManager(getApplicationContext());

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        btn_openGallery.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        btn_PpNext.setOnClickListener(view -> {
            if(encodedImage == null) Toast.makeText(this, "Select Profile Image", Toast.LENGTH_SHORT).show();
            else signUp();
        });
    }

    private void signUp() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, name);
        user.put(Constants.KEY_EMAIL, email);
        user.put(Constants.KEY_PASSWORD, password);
        user.put(Constants.KEY_IMAGE, encodedImage);

        db.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    manager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    manager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    manager.putString(Constants.KEY_NAME, name);
                    manager.putString(Constants.KEY_IMAGE, encodedImage);
                    Intent intent = new Intent(InputPpActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            iv_setPp.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    void loading(boolean b) {
        if(b) {
            btn_openGallery.setClickable(false);
            btn_PpNext.setClickable(false);
            pbOfPp.setVisibility(View.VISIBLE);
        }
        else {
            pbOfPp.setVisibility(View.GONE);
            btn_PpNext.setClickable(true);
            btn_openGallery.setClickable(true);
        }
    }

}