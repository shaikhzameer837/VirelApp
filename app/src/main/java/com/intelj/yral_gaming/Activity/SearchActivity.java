package com.intelj.yral_gaming.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    DatabaseReference mDatabase;
    EditText search;
    private RecyclerView recyclerView;
    TeamAdapter mAdapter;
    ArrayList<DataSnapshot> userInfoList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);
        mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users);
        search =findViewById(R.id.rank);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

    }

    private void performSearch() {
        mDatabase.orderByChild(AppConstant.userName).equalTo(search.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            FirebaseDatabase.getInstance().getReference(AppConstant.users).child(postSnapshot.getKey() + "")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot childDataSnapshot) {
                                            userInfoList.add(childDataSnapshot);
                                            mAdapter = new TeamAdapter(SearchActivity.this, userInfoList,AppConstant.search);
                                            recyclerView.setAdapter(mAdapter);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {

                                        }
                                    });
                        }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("postSnapshot",error.getMessage());
            }
        });
    }
}
