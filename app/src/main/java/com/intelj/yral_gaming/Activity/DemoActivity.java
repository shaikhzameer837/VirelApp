package com.intelj.yral_gaming.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intelj.yral_gaming.Adapter.DemoRecyclerviewAdapter;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.model.DemoRecyclerviewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DemoActivity extends AppCompatActivity {
    DatabaseReference mDatabase;
    RecyclerView recyclerview;
    ShimmerFrameLayout shimmer_view_container;
    List<DemoRecyclerviewModel> list;
    int count =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        recyclerview = findViewById(R.id.recyclerview);
        shimmer_view_container = findViewById(R.id.shimmer_view_container);
        shimmer_view_container.startShimmer();
        list = new ArrayList<>();
        Set<String> myList = (Set<String>) getIntent().getSerializableExtra("teammeber");
        for (String s : myList) {
            Log.i("teammember", s);

            /*DemoRecyclerviewAdapter adapter = new DemoRecyclerviewAdapter(myListData);
            recyclerview.setHasFixedSize(true);
            recyclerview.setLayoutManager(new LinearLayoutManager(this));
            recyclerview.setAdapter(adapter);  */
            mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users).child(s).child(AppConstant.pinfo);
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    count++;
                    DemoRecyclerviewModel demoRecyclerviewModel = new DemoRecyclerviewModel();
                    demoRecyclerviewModel.setTitle(s);
                    demoRecyclerviewModel.setDiscordId(snapshot.child("discordId").getValue() != null ? snapshot.child("discordId").getValue().toString() : null);
                    demoRecyclerviewModel.setGameId(snapshot.child("pubg").getValue() != null ? snapshot.child("pubg").getValue().toString() : null);
                    demoRecyclerviewModel.setGameName(snapshot.child("pubg_userName").getValue() != null ? snapshot.child("pubg_userName").getValue().toString() : null);
                    demoRecyclerviewModel.setImage(snapshot.child("myPicUrl").getValue() != null ? snapshot.child("myPicUrl").getValue().toString() : null);
                    list.add(demoRecyclerviewModel);
                    if (count == myList.size()) {
                        DemoRecyclerviewAdapter adapter = new DemoRecyclerviewAdapter(list,DemoActivity.this);
                        recyclerview.setLayoutManager(new LinearLayoutManager(DemoActivity.this));
                        recyclerview.setAdapter(adapter);
                        shimmer_view_container.hideShimmer();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}