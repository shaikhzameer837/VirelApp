package com.intelj.y_ral_gaming;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.Activity.NotficationAdapter;
import com.intelj.y_ral_gaming.Activity.NotificationModel;
import com.intelj.y_ral_gaming.Activity.ProFileActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;

import java.util.ArrayList;

public class FollowActivity extends AppCompatActivity {
    RecyclerView rv_noti;
    ArrayList<NotificationModel> followList;
    String title;
    TextView tv_title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_activity);
        rv_noti = findViewById(R.id.rv_noti);
        title = getIntent().getStringExtra("title");
        tv_title = findViewById(R.id.title);
        tv_title.setText(title);
        followList = new ArrayList<>();
        NotficationAdapter notficationAdapter = new NotficationAdapter(this, followList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv_noti.setLayoutManager(mLayoutManager);
        rv_noti.setAdapter(notficationAdapter);
        if(AppController.getInstance().follow != null) {
            for (DataSnapshot dataSnapshot : AppController.getInstance().follow.getChildren()) {
                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(dataSnapshot.getKey())
                        .child(AppConstant.pinfo)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.e("dataSnapshot", dataSnapshot.getKey());
                                followList.add(new NotificationModel(dataSnapshot.getKey(), snapshot.child(AppConstant.name).getValue() == null ? "Player" : snapshot.child(AppConstant.name).getValue(String.class), snapshot.child(AppConstant.bio).getValue(String.class), snapshot.child(AppConstant.bio).getValue(String.class)));
                                notficationAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        }
//        if (AppController.getInstance().notification.getChildrenCount() == 0) {
//            findViewById(R.id.pBar3).setVisibility(View.VISIBLE);
//            findViewById(R.id.info).setVisibility(View.VISIBLE);
//        }
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        rv_noti.addOnItemTouchListener(new RecyclerTouchListener(this, rv_noti, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(FollowActivity.this, ProFileActivity.class);
                String transitionName = "fade";
                View transitionView = view.findViewById(R.id.profile);
                ViewCompat.setTransitionName(transitionView, transitionName);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(FollowActivity.this, transitionView, transitionName);
                intent.putExtra("userid", followList.get(position).getUserId());
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(new AppConstant(this).getId()).child(AppConstant.realTime).child(AppConstant.noti).removeValue();
    }
}