package com.intelj.y_ral_gaming.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {
    RecyclerView rv_noti;
    ArrayList<NotificationModel> notificationModelArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notfication);
        rv_noti = findViewById(R.id.rv_noti);
        notificationModelArrayList = new ArrayList<>();
        NotficationAdapter notficationAdapter = new NotficationAdapter(this, notificationModelArrayList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv_noti.setLayoutManager(mLayoutManager);
        rv_noti.setAdapter(notficationAdapter);
        if(AppController.getInstance().notification != null) {
            for (DataSnapshot dataSnapshot : AppController.getInstance().notification.getChildren()) {
                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(dataSnapshot.child("id").getValue(String.class))
                        .child(AppConstant.pinfo).child(AppConstant.name)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String subtitle = "";
                                if (dataSnapshot.child("subject").getValue().equals("follow"))
                                    subtitle = "Followed you";
                                notificationModelArrayList.add(new NotificationModel(dataSnapshot.child("id").getValue(String.class), snapshot.getValue(String.class), dataSnapshot.child("subject").getValue() + "", dataSnapshot.getKey(), subtitle));
                                notficationAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        }
        if (AppController.getInstance().notification == null || AppController.getInstance().notification.getChildrenCount() == 0) {
            findViewById(R.id.pBar3).setVisibility(View.VISIBLE);
            findViewById(R.id.info).setVisibility(View.VISIBLE);
        }
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rv_noti.addOnItemTouchListener(new RecyclerTouchListener(this, rv_noti, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(NotificationActivity.this, ProFileActivity.class);
                String transitionName = "fade";
                View transitionView = view.findViewById(R.id.profile);
                ViewCompat.setTransitionName(transitionView, transitionName);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(NotificationActivity.this, transitionView, transitionName);
                intent.putExtra("userid", notificationModelArrayList.get(position).getUserId());
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
       // FirebaseDatabase.getInstance().getReference(AppConstant.users).child(new AppConstant(this).getId()).child(AppConstant.realTime).child(AppConstant.noti).removeValue();
    }
}
