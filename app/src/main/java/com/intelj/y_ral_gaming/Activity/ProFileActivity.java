package com.intelj.y_ral_gaming.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.ChatActivity;
import com.intelj.y_ral_gaming.FollowActivity;
import com.intelj.y_ral_gaming.Fragment.OneFragment;
import com.intelj.y_ral_gaming.Fragment.PostFragment;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SigninActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.HashMap;
import java.util.Map;

public class ProFileActivity extends AppCompatActivity {
    TextView txt;
    String userid;
    SharedPreferences sharedPreferences;
    ImageView imgProfile, title_pic;
    TabLayout tabLayout;
    ViewPager viewPager;
    AppConstant appConstant;
    TextView name, bio, title, userName, follower_count, following_count, edit_profile;
    long followers = 0;
    long following = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        txt = findViewById(R.id.info);
        imgProfile = findViewById(R.id.profPic);
        userName = findViewById(R.id.userName);
        bio = findViewById(R.id.bio);
        title_pic = findViewById(R.id.title_pic);
        follower_count = findViewById(R.id.follower_count);
        following_count = findViewById(R.id.following_count);
        title = findViewById(R.id.title);
        edit_profile = findViewById(R.id.edit_profile);
        Fade fade = new Fade();
        appConstant = new AppConstant(this);
        userid = getIntent().getStringExtra("userid");
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        name = findViewById(R.id.name);
        tabLayout.addTab(tabLayout.newTab().setText("Post"));
        tabLayout.addTab(tabLayout.newTab().setText("Achievement"));
        tabLayout.addTab(tabLayout.newTab().setText("Challenges"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final MyAdapter adapter = new MyAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        if (userid.equals(appConstant.getId())) {
           // findViewById(R.id.chat).setVisibility(View.GONE);
            edit_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProFileActivity.this, EditProfile.class));
                }
            });
        }
        findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProFileActivity.this, ChatActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        ProFileActivity.this, imgProfile, ViewCompat.getTransitionName(imgProfile));
                intent.putExtra(AppConstant.id, userid);
                startActivity(intent, options.toBundle());
            }
        });
        if (AppController.getInstance().popularList.get(userid) != null) {
            TextView popular = findViewById(R.id.popular);
            popular.setVisibility(View.VISIBLE);
            popular.setText("Popularity #" + (AppController.getInstance().popularList.get(userid)));
        }
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userid);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                DataSnapshot dataSnapshot = snapshot.child(AppConstant.pinfo);
                if (dataSnapshot.child(AppConstant.verified).getValue() != null)
                    name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verified, 0);
                bio.setText(dataSnapshot.child(AppConstant.bio).getValue(String.class));
                title.setText(dataSnapshot.child(AppConstant.title).getValue(String.class));
                name.setText(dataSnapshot.child(AppConstant.name).getValue(String.class));
                userName.setText("@" + snapshot.child(AppConstant.userName).getValue(String.class));
                SharedPreferences sharedPreferences = getSharedPreferences(userid, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AppConstant.bio, bio.getText().toString()).apply();
                editor.putString(AppConstant.name, name.getText().toString()).apply();
                editor.putString(AppConstant.userName, snapshot.child(AppConstant.userName).getValue(String.class)).apply();
                if (!userid.equals(appConstant.getId())) {
                    if (!appConstant.checkLogin()) {
                        edit_profile.setText("Login to follow");
                        edit_profile.setTextColor(Color.parseColor("#333333"));
                        edit_profile.setBackgroundResource(R.drawable.curved_white);
                    } else if (!snapshot.child(AppConstant.profile).child(AppConstant.follower).child(appConstant.getId()).exists()) {
                        edit_profile.setText("follow");
                        edit_profile.setTextColor(Color.WHITE);
                        edit_profile.setBackgroundResource(R.drawable.curved_blue);
                    } else {
                        edit_profile.setText("unfollow");
                        edit_profile.setTextColor(Color.parseColor("#333333"));
                        edit_profile.setBackgroundResource(R.drawable.curved_white);
                    }
                    edit_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!appConstant.checkLogin()) {
                                startActivity(new Intent(ProFileActivity.this, SigninActivity.class));
                                return;
                            }
                            if (edit_profile.getText().toString().equals("follow")) {
                                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userid).child(AppConstant.profile).child(AppConstant.follower).child(appConstant.getId()).setValue((System.currentTimeMillis() / 1000));
                                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(appConstant.getId()).child(AppConstant.profile).child(AppConstant.following).child(userid).setValue((System.currentTimeMillis() / 1000));
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put(AppConstant.subject, "follow");
                                hashMap.put(AppConstant.id, appConstant.getId());
                                hashMap.put(AppConstant.name, appConstant.getName());
                                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userid).child(AppConstant.realTime).child(AppConstant.noti).child((System.currentTimeMillis() / 1000) + "").setValue(hashMap);
                                followers = followers + 1;
                                follower_count.setText(Html.fromHtml("<b><font size='14' color='#000000'>" + followers + "</font></b> <br/>Follower"));
                                edit_profile.setText("unfollow");
                                edit_profile.setTextColor(Color.parseColor("#333333"));
                                edit_profile.setBackgroundResource(R.drawable.curved_white);
                            } else {
                                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userid).child(AppConstant.profile).child(AppConstant.follower).child(appConstant.getId()).removeValue();
                                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(appConstant.getId()).child(AppConstant.profile).child(AppConstant.following).child(userid).removeValue();
                                followers = followers - 1;
                                follower_count.setText(Html.fromHtml("<b><font size='14' color='#000000'>" + followers + "</font></b> <br/>Follower"));
                                edit_profile.setText("follow");
                                edit_profile.setTextColor(Color.WHITE);
                                edit_profile.setBackgroundResource(R.drawable.curved_blue);
                            }
                        }
                    });
                }
                followers = snapshot.child(AppConstant.profile).child(AppConstant.follower).getChildrenCount();
                following = snapshot.child(AppConstant.profile).child(AppConstant.following).getChildrenCount();
                follower_count.setText(Html.fromHtml("<b><font size='14' color='#000000'>" + followers + "</font></b> <br/>Follower"));
                following_count.setText(Html.fromHtml("<b><font size='14' color='#000000'>" + following + "</font></b> <br/>Following"));
                follower_count.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppController.getInstance().follow = snapshot.child(AppConstant.profile).child(AppConstant.follower);
                        Intent intent = new Intent(ProFileActivity.this, FollowActivity.class);
                        intent.putExtra("title", "Follower");
                        startActivity(intent);
                    }
                });
                following_count.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppController.getInstance().follow = snapshot.child(AppConstant.profile).child(AppConstant.following);
                        Intent intent = new Intent(ProFileActivity.this, FollowActivity.class);
                        intent.putExtra("title", "Following");
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // getProfileInfo();
        //  prepareytModelData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences(userid, 0);
        Glide.with(ProFileActivity.this).load("http://y-ral-gaming.com/admin/api/images/" + userid + ".png?u=" + AppConstant.imageExt()).apply(new RequestOptions().circleCrop()).placeholder(R.drawable.game_avatar).into(imgProfile);
        if (userid.equals(appConstant.getId()))
            name.setText(sharedPreferences.getString(AppConstant.name, ""));
        else
            name.setText(sharedPreferences.getString(AppConstant.phoneNumber, "").equals("") ? sharedPreferences.getString(AppConstant.name, "") : appConstant.getContactName(sharedPreferences.getString(AppConstant.phoneNumber, "")));
        bio.setText(sharedPreferences.getString(AppConstant.bio, ""));
        userName.setText("@" + sharedPreferences.getString(AppConstant.userName, "Player" + System.currentTimeMillis() + ""));
        title.setText(sharedPreferences.getString(AppConstant.title, ""));
    }

    public void getProfileInfo() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/profile_info.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userid);
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


    public class MyAdapter extends FragmentPagerAdapter {

        private Context myContext;
        int totalTabs;

        public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
            super(fm);
            myContext = context;
            this.totalTabs = totalTabs;
        }

        // this is for fragment tabs
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    PostFragment homeFragment = new PostFragment();
                    return homeFragment;
                case 1:
                    PostFragment sportFragment = new PostFragment();
                    return sportFragment;
                case 2:
                    PostFragment movieFragment = new PostFragment();
                    return movieFragment;
                default:
                    return null;
            }
        }

        // this counts total number of tabs
        @Override
        public int getCount() {
            return totalTabs;
        }
    }
}
