package com.intelj.y_ral_gaming.Activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;

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
        for (DataSnapshot snapshot : AppController.getInstance().notification.getChildren()) {
            String substitle = "";
            if(snapshot.child("subject").getValue().equals("follow"))
                substitle = "Followed you";
            notificationModelArrayList.add(new NotificationModel(snapshot.child("userid").getValue()+"", snapshot.child("name").getValue()+"", snapshot.child("subject").getValue()+"", snapshot.getKey()+"",substitle));
        }
        NotficationAdapter notficationAdapter = new NotficationAdapter(this, notificationModelArrayList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv_noti.setLayoutManager(mLayoutManager);
        rv_noti.setAdapter(notficationAdapter);

    }
}
