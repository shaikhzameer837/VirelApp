package com.intelj.y_ral_gaming.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;
import com.intelj.y_ral_gaming.model.SuggesstionModel;
import com.intelj.y_ral_gaming.model.SuggestionViewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    DatabaseReference mDatabase;
    EditText search;
    TextView result;
    private RecyclerView recyclerView, rv_suggest;
    TeamAdapter mAdapter;
    ArrayList<DataSnapshot> userInfoList = new ArrayList<>();
    ProgressBar progress;
    ArrayList<SuggesstionModel> recyclerDataArrayList;
    SuggestionViewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);
        mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users);
        search = findViewById(R.id.rank);
        result = findViewById(R.id.result);
        progress = findViewById(R.id.progress);
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
        rv_suggest = findViewById(R.id.rv_suggest);
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

        recyclerDataArrayList = new ArrayList<>();
        adapter = new SuggestionViewAdapter(recyclerDataArrayList, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, 3, GridLayoutManager.VERTICAL, false);
        rv_suggest.setLayoutManager(gridLayoutManager);
        rv_suggest.setAdapter(adapter);
//        for (int i = 0; i < userIDs.size(); i++) {
//            if (!userIDs.get(i).equals(new AppConstant(this).getId())) {
//                if (AppController.getInstance().follow == null || !AppController.getInstance().follow.child(userIDs.get(i)).exists()) {
//                    final int m = i;
//                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users)
//                            .child(userIDs.get(i)).child(AppConstant.pinfo);
//                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            recyclerDataArrayList.add(new SuggesstionModel(snapshot.child(AppConstant.name).getValue(String.class),
//                                    AppConstant.AppUrl + "images/" + userIDs.get(m) + ".png?u=" + AppConstant.imageExt(), userIDs.get(m), snapshot.child(AppConstant.verified).exists()));
//                            adapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//            }
//        }
//        final  DatabaseReference conversationsRef = FirebaseDatabase.getInstance().getReference(AppConstant.users);
//        Query getLastMessagesQuery = conversationsRef.orderByKey().limitToLast(5);
//        getLastMessagesQuery.addListenerForSingleValueEvent(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snap)
//            {
//                for (DataSnapshot dataSnapshot : snap.getChildren())
//                {
//                    if(!dataSnapshot.getKey().equals(new AppConstant(SearchActivity.this).getId())) {
//                        if (AppController.getInstance().follow == null || !AppController.getInstance().follow.child(dataSnapshot.getKey()).exists()) {
//                            DataSnapshot snapshot = dataSnapshot.child(AppConstant.pinfo);
//                            recyclerDataArrayList.add(new SuggesstionModel(snapshot.child(AppConstant.name).getValue(String.class),
//                                    AppConstant.AppUrl + "images/" + dataSnapshot.getKey() + ".png?u=" + AppConstant.imageExt(), dataSnapshot.getKey(),snapshot.child(AppConstant.verified).exists()));
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError)
//            {
//
//            }
//        });
        getSearchResult();
    }

    public void getSearchResult() {
        progress.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "search.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        progress.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("success")) {
                                Toast.makeText(SearchActivity.this, "Failed", Toast.LENGTH_LONG).show();
                                return;
                            }
                            JSONArray jsonArray = jsonObject.getJSONArray("users");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObjectRes = (JSONObject) jsonArray.get(i);
                                Log.e("imgs", AppConstant.AppUrl + "images/" + jsonObjectRes.getString("user_id") + ".png?u=" + AppConstant.imageExt());
                                String name = jsonObjectRes.getString("name");
                                if (name.equalsIgnoreCase("Player"))
                                    name = jsonObjectRes.getString("userName").startsWith("Player.") ? name : jsonObjectRes.getString("userName");
                                recyclerDataArrayList.add(new SuggesstionModel(name,
                                        AppConstant.AppUrl + "images/" + jsonObjectRes.getString("user_id") + ".png?u=" + AppConstant.imageExt(), jsonObjectRes.getString("user_id"), jsonObjectRes.getBoolean("isVerified")));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", new AppConstant(SearchActivity.this).getId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void performSearch() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        mDatabase.orderByChild(AppConstant.userName).equalTo(search.getText().toString().toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                result.setVisibility(View.VISIBLE);
                if (dataSnapshot.exists()) {
                    result.setText("result found");
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        userInfoList.add(postSnapshot);
                        new AppConstant(SearchActivity.this).saveUserInfo("", postSnapshot.getKey(), AppConstant.AppUrl + "images/" + postSnapshot.getKey() + ".png?u=" + AppConstant.imageExt(), null, "", postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue() != null ? postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue().toString() : null, postSnapshot.child(AppConstant.userName).getValue() != null ? postSnapshot.child(AppConstant.userName).getValue().toString() : System.currentTimeMillis() + "");
                        mAdapter = new TeamAdapter(SearchActivity.this, userInfoList, AppConstant.search);
                        recyclerView.setAdapter(mAdapter);
                    }
                } else
                    result.setText("No result found");
                findViewById(R.id.progress).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("postSnapshot", error.getMessage());
            }
        });
    }
}
