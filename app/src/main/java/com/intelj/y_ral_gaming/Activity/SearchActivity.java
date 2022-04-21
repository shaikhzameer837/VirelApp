package com.intelj.y_ral_gaming.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;

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
                    userInfoList.clear();
                    performSearch();
                    return true;
                }
                return false;
            }
        });
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(SearchActivity.this, ProFileActivity.class);
                String transitionName = "fade";
                View transitionView = view.findViewById(R.id.profilePic);
                ViewCompat.setTransitionName(transitionView, transitionName);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SearchActivity.this, transitionView, transitionName);
                intent.putExtra("userid", userInfoList.get(position).getKey());
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void performSearch() {
        mDatabase.orderByChild(AppConstant.userName).equalTo(search.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            userInfoList.add(postSnapshot);
                            new AppConstant(SearchActivity.this).saveUserInfo("", postSnapshot.getKey(), "http://y-ral-gaming.com/admin/api/images/" + postSnapshot.getKey() + ".png?u=" + (System.currentTimeMillis() / 1000), null, "", postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue() != null ? postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue().toString() : null, postSnapshot.child(AppConstant.userName).getValue() != null ? postSnapshot.child(AppConstant.userName).getValue().toString() : System.currentTimeMillis() + "");
                            mAdapter = new TeamAdapter(SearchActivity.this, userInfoList,AppConstant.search);
                            recyclerView.setAdapter(mAdapter);
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
