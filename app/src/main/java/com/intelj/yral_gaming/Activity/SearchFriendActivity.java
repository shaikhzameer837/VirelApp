package com.intelj.yral_gaming.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intelj.yral_gaming.Adapter.RankAdapter;
import com.intelj.yral_gaming.Adapter.SearchFriendAdapter;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;

import java.util.ArrayList;

public class SearchFriendActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerview;
    private ArrayList<DataSnapshot> dataSnapshots = new ArrayList<>();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        searchView = findViewById(R.id.searchview);
        recyclerview = findViewById(R.id.recyclerview);
        SearchFriendAdapter pAdapter = new SearchFriendAdapter(SearchFriendActivity.this, dataSnapshots);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchFriendActivity.this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(pAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dataSnapshots.clear();
                if (newText.length() > 0) {
                    DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.users);
//                Query query = mFirebaseDatabaseReference.child(AppConstant.pinfo).child(AppConstant.userName).startAt(newText).endAt(newText+"\uf8ff");
                    Query query = mFirebaseDatabaseReference.orderByChild(AppConstant.username_search).startAt(newText).endAt(newText + "\uf8ff");

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                dataSnapshots.add(child);
                            }
                            pAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                pAdapter.notifyDataSetChanged();
                /*FirebaseDatabase.getInstance().getReference(AppConstant.users).orderByChild("winner").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        ArrayList<DataSnapshot> dataSnapshots = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                           String username =  child.child(AppConstant.pinfo).child(AppConstant.userName).getValue().toString();
                           String bio = child.child(AppConstant.pinfo).child(AppConstant.bio).getValue().toString();
                           if (username.contains(newText) || bio.contains(newText)) {
                               dataSnapshots.add(0, child);
                           }
                        }
                        SearchFriendAdapter pAdapter = new SearchFriendAdapter(SearchFriendActivity.this, dataSnapshots);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchFriendActivity.this);
                        recyclerview.setLayoutManager(mLayoutManager);
                        recyclerview.setAdapter(pAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });*/


                return true;
            }
        });
    }
}