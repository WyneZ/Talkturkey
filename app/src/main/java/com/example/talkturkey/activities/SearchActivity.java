package com.example.talkturkey.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;

import com.example.talkturkey.R;
import com.example.talkturkey.adapters.UserAdapter;
import com.example.talkturkey.database.Constants;
import com.example.talkturkey.database.PreferenceManager;
import com.example.talkturkey.listeners.UserListener;
import com.example.talkturkey.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements UserListener {

    private RecyclerView rv_Search;

    private PreferenceManager manager;
    private UserAdapter adapter;

    private View searchAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        setContentView(R.layout.activity_search);

        rv_Search = findViewById(R.id.rv_Search);
        manager = new PreferenceManager(this);

        searchAnim = findViewById(R.id.motion);

        if (savedInstanceState == null) {
            searchAnim.setVisibility(View.INVISIBLE);

            final ViewTreeObserver viewTreeObserver = searchAnim.getViewTreeObserver();

            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                        searchAnim.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }

                });
            }

        }

        getUsers();
    }

    private void circularRevealActivity() {
        int cx = searchAnim.getRight() - getDips(55);
        int cy = searchAnim.getBottom() - getDips(110);

        float finalRadius = Math.max(searchAnim.getWidth(), searchAnim.getHeight());

        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                searchAnim,
                cx,
                cy,
                0,
                finalRadius);

        circularReveal.setDuration(1000);
        searchAnim.setVisibility(View.VISIBLE);
        circularReveal.start();

    }

    private int getDips(int dps) {
        Resources resources = getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                resources.getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cx = searchAnim.getWidth() - getDips(55);
            int cy = searchAnim.getBottom() - getDips(110);

            float finalRadius = Math.max(searchAnim.getWidth(), searchAnim.getHeight());
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(searchAnim, cx, cy, finalRadius, 0);

            circularReveal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    searchAnim.setVisibility(View.INVISIBLE);
                    finish();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            circularReveal.setDuration(1000);
            circularReveal.start();
        }
        else {
            super.onBackPressed();
        }
    }

    private void getUsers() {
        //loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    //loading(false);
                    String currentUserId = manager.getString(Constants.KEY_USER_ID);
                    if(task.isSuccessful() && task.getResult() != null) {
                        List<User> userList = new ArrayList<>();
                        for(QueryDocumentSnapshot snapshot : task.getResult()) {
                            if(currentUserId.equals(snapshot.getId())) continue;
                            User user = new User();
                            user.setName(snapshot.getString(Constants.KEY_NAME));
                            user.setEmail(snapshot.getString(Constants.KEY_EMAIL));
                            user.setImage(snapshot.getString(Constants.KEY_IMAGE));
                            user.setToken(snapshot.getString(Constants.KEY_FCM_TOKEN));
                            user.setId(snapshot.getId());
                            userList.add(user);
                        }
                        if(userList.size() > 0) {
                            adapter = new UserAdapter(userList, this);
                            rv_Search.setLayoutManager(new LinearLayoutManager(this));
                            rv_Search.setAdapter(adapter);
                            rv_Search.setVisibility(View.VISIBLE);
                        }
                        else {
                            //tv_errorMsg.setText(String.format("%s" + "No User Available"));
                            //tv_errorMsg.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}