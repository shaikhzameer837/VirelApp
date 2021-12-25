package com.intelj.yral_gaming.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intelj.yral_gaming.AppController;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;

import java.util.HashMap;

public class NewProfileActivity extends AppCompatActivity {
    private ImageView image1, image2;
    private EditText username;
    private Button btn_invite;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        username = findViewById(R.id.nameEdt);
        btn_invite = findViewById(R.id.invite);
        firestore = FirebaseFirestore.getInstance();

        Glide.with(NewProfileActivity.this).load(AppController.getInstance().dataSnapshot.child(AppConstant.pinfo).child(AppConstant.myPicUrl).getValue() + "").placeholder(R.drawable.profile_icon).circleCrop().into(image1);
        Glide.with(NewProfileActivity.this).load(AppController.getInstance().dataSnapshot.child(AppConstant.pinfo).child(AppConstant.myPicUrl).getValue() + "").placeholder(R.drawable.profile_icon).circleCrop().into(image2);
        username.setText(AppController.getInstance().dataSnapshot.child(AppConstant.username_search).getValue() + "");
        setfollow();

        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put(AppController.getInstance().dataSnapshot.getKey(), (System.currentTimeMillis() / 1000) + "");
                firestore.collection(AppConstant.user).document(new AppConstant(NewProfileActivity.this).getUserId()).collection(AppConstant.follow).document(AppConstant.following).set(hashMap);
                hashMap = new HashMap<>();
                hashMap.put(new AppConstant(NewProfileActivity.this).getUserId(), (System.currentTimeMillis() / 1000) + "");
                firestore.collection(AppConstant.user).document(AppController.getInstance().dataSnapshot.getKey()).collection(AppConstant.follow).document(AppConstant.follower).set(hashMap);
                if (btn_invite.getText().toString().equals("Follow"))
                    btn_invite.setText("Unfollow");
                else
                    btn_invite.setText("Follow");
            }
        });
    }

    private void setfollow() {
//        if (AppController.getInstance().followingList.contains(AppController.getInstance().dataSnapshot.getKey())) {
//            btn_invite.setText("Unfollow");
//        } else
//            btn_invite.setText("Follow");
    }
}