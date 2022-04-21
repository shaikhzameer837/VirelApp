package com.intelj.y_ral_gaming.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.intelj.y_ral_gaming.Fragment.OneFragment;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProFileActivity extends AppCompatActivity {
    private List<ytModel> ytModelList = new ArrayList<>();
    TextView txt;
    String userid;
    SharedPreferences sharedPreferences;
    ImageView imgProfile,title_pic;
    TabLayout tabLayout;
    ViewPager viewPager;
    AppConstant appConstant;
    TextView name, bio,title,userName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        //  FillCustomGradient(findViewById(R.id.imgs));
        txt = findViewById(R.id.info);
        imgProfile = findViewById(R.id.profPic);
        userName = findViewById(R.id.userName);
        bio = findViewById(R.id.bio);
        title_pic = findViewById(R.id.title_pic);
        title = findViewById(R.id.title);
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
            findViewById(R.id.edit_profile).setVisibility(View.VISIBLE);
            findViewById(R.id.edit_profile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProFileActivity.this, EditProfile.class));
                }
            });
        }else if(AppController.getInstance().popularList.get(userid) != null){
            TextView popular = findViewById(R.id.popular);
            popular.setVisibility(View.VISIBLE);
            popular.setText("Popularity #" + (AppController.getInstance().popularList.get(userid)));
        }
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userid).child(AppConstant.pinfo);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(AppConstant.verified).getValue() != null)
                    name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verified, 0);
                bio.setText(dataSnapshot.child(AppConstant.bio).getValue(String.class));
                title.setText(dataSnapshot.child(AppConstant.title).getValue(String.class));
                SharedPreferences sharedPreferences = getSharedPreferences(userid, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AppConstant.bio, bio.getText().toString()).apply();
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
        Glide.with(ProFileActivity.this).load(sharedPreferences.getString(AppConstant.myPicUrl, "")).apply(new RequestOptions().circleCrop()).placeholder(R.drawable.game_avatar).into(imgProfile);
       // Glide.with(ProFileActivity.this).load("https://yt3.ggpht.com/OlpmWQZZxLMk97J_2sXOKMFTEmbwiGH80EqRY45EMa5y5yyCf2QHJ2OfYGYfPcZWNN-Z0ohHrw=s900-c-k-c0x00ffffff-no-rj").placeholder(R.drawable.game_avatar).into(title_pic);
        if (userid.equals(appConstant.getId()))
            name.setText(sharedPreferences.getString(AppConstant.name, ""));
        else
            name.setText(sharedPreferences.getString(AppConstant.phoneNumber, "").equals("") ? sharedPreferences.getString(AppConstant.name, "") : appConstant.getContactName(sharedPreferences.getString(AppConstant.phoneNumber, "")));
        bio.setText(sharedPreferences.getString(AppConstant.bio, ""));
        userName.setText("@"+sharedPreferences.getString(AppConstant.userName,"Player"+System.currentTimeMillis()+""));
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
                    OneFragment homeFragment = new OneFragment();
                    return homeFragment;
                case 1:
                    OneFragment sportFragment = new OneFragment();
                    return sportFragment;
                case 2:
                    OneFragment movieFragment = new OneFragment();
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
