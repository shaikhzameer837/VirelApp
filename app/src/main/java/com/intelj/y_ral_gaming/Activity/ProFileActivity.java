package com.intelj.y_ral_gaming.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.transition.Fade;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intelj.y_ral_gaming.Adapter.MyListAdapter;
import com.intelj.y_ral_gaming.Adapter.TeamDisplayList;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.ChatActivity;
import com.intelj.y_ral_gaming.FollowActivity;
import com.intelj.y_ral_gaming.Fragment.PostFragment;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.RoundedBottomSheetDialog;
import com.intelj.y_ral_gaming.SigninActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;
import com.intelj.y_ral_gaming.model.MyListData;
import com.intelj.y_ral_gaming.model.TournamentModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProFileActivity extends AppCompatActivity {
    String userid;
    SharedPreferences sharedPreferences;
    ImageView imgProfile;
    TabLayout tabLayout;
    ViewPager viewPager;
    AppConstant appConstant;
    TextView name, bio, title, userName, follower_count, following_count, edit_profile;
    long followers = 0;
    long following = 0;
    ImageView iconImage, chatIcon;
    TextView rank, rank_button;
    ProgressBar progress;
    String teamIdList = "";
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        imgProfile = findViewById(R.id.profPic);
        userName = findViewById(R.id.userName);
        rank_button = findViewById(R.id.rank_button);
        chatIcon = findViewById(R.id.chatIcon);
        progress = findViewById(R.id.progress);
        bio = findViewById(R.id.bio);
        rank = findViewById(R.id.rank);
        follower_count = findViewById(R.id.follower_count);
        following_count = findViewById(R.id.following_count);
        title = findViewById(R.id.title);
        iconImage = findViewById(R.id.iconImage);
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
            int id = getResources().getIdentifier(AppConstant.getRank(AppController.getInstance().rank), "drawable", getPackageName());
            rank.setCompoundDrawablesWithIntrinsicBounds(0, id, 0, 0);
            // findViewById(R.id.chat).setVisibility(View.GONE);
            iconImage.setImageResource(R.drawable.ic_edit);
            findViewById(R.id.rel_button).setBackgroundResource(R.drawable.curved_red);
            edit_profile.setTextColor(Color.parseColor("#ffffff"));
            edit_profile.setText("Edit Profile");
            findViewById(R.id.rel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // editProfile();
                    startActivity(new Intent(ProFileActivity.this,EditProfile.class));
                }
            });
            if (userid.equals(appConstant.getId())) {
                chatIcon.setImageResource(R.drawable.group);
                rank_button.setText("My Team");
                findViewById(R.id.rel_mess).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                rank_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // showTeamList();
                        showTeamList();
                    }
                });
            }
        } else {
            findViewById(R.id.rel_mess).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProFileActivity.this, "Coming Soon", Toast.LENGTH_LONG).show();
                }
            });
            chatIcon.setImageResource(R.drawable.chat);
            rank_button.setText("Message");
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
                        edit_profile.setTextColor(Color.parseColor("#ffffff"));
                        iconImage.setImageResource(R.drawable.lock_outline);
                        findViewById(R.id.rel_button).setBackgroundResource(R.drawable.curved_red);
                    } else if (!snapshot.child(AppConstant.profile).child(AppConstant.follower).child(appConstant.getId()).exists()) {
                        edit_profile.setText("follow");
                        edit_profile.setTextColor(Color.WHITE);
                        iconImage.setImageResource(R.drawable.account_plus);
                        findViewById(R.id.rel_button).setBackgroundResource(R.drawable.curved_blue);
                    } else {
                        edit_profile.setText("unfollow");
                        edit_profile.setTextColor(Color.parseColor("#333333"));
                        iconImage.setImageResource(R.drawable.account_minus);
                        findViewById(R.id.rel_button).setBackgroundResource(R.drawable.curved_gray);
                    }
                    edit_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!appConstant.checkLogin()) {
                                startActivity(new Intent(ProFileActivity.this, SigninActivity.class));
                                return;
                            }
                            if (edit_profile.getText().toString().equals("follow")) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put(AppConstant.subject, "follow");
                                hashMap.put(AppConstant.id, appConstant.getId());
                                hashMap.put(AppConstant.name, appConstant.getName());
                                FirebaseFirestore.getInstance().collection(AppConstant.realTime)
                                        .document(userid).collection(AppConstant.noti)
                                                .document((System.currentTimeMillis() / 1000)+"").set(hashMap);
                                                  FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userid).child(AppConstant.profile).child(AppConstant.follower).child(appConstant.getId()).setValue((System.currentTimeMillis() / 1000));
                                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(appConstant.getId()).child(AppConstant.profile).child(AppConstant.following).child(userid).setValue((System.currentTimeMillis() / 1000));

                                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userid).child(AppConstant.realTime).child(AppConstant.noti).child((System.currentTimeMillis() / 1000) + "").setValue(hashMap);
                                followers = followers + 1;
                                follower_count.setText(Html.fromHtml("<b><font size='14' color='#000000'>" + followers + "</font></b> <br/>Follower"));
                                edit_profile.setText("unfollow");
                                edit_profile.setTextColor(Color.parseColor("#333333"));
                                iconImage.setImageResource(R.drawable.account_minus);
                                findViewById(R.id.rel_button).setBackgroundResource(R.drawable.curved_gray);
                                appConstant.callingPingApi(userid);
                            } else {
                                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userid).child(AppConstant.profile).child(AppConstant.follower).child(appConstant.getId()).removeValue();
                                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(appConstant.getId()).child(AppConstant.profile).child(AppConstant.following).child(userid).removeValue();
                                followers = followers - 1;
                                follower_count.setText(Html.fromHtml("<b><font size='14' color='#000000'>" + followers + "</font></b> <br/>Follower"));
                                edit_profile.setText("follow");
                                edit_profile.setTextColor(Color.WHITE);
                                iconImage.setImageResource(R.drawable.account_plus);
                                findViewById(R.id.rel_button).setBackgroundResource(R.drawable.curved_blue);
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
        getProfileInfo();
        //  prepareytModelData();
    }
    public void editProfile() {
        ImageView imgProfile;
        int PROFILE_IMAGE = 11;
        String picturePath = null;
        Bitmap selectedImage;
        TextInputEditText playerName, TI_userName, bio;
        String userName = "";
        DatabaseReference mDatabase;
        AutoCompleteTextView tv_title;
        LinearLayout gameList;
        SharedPreferences prefs;
        TextView avail;
        SharedPreferences sharedPreferences = getSharedPreferences(appConstant.getId(), 0);
        prefs = getSharedPreferences(AppConstant.AppName, MODE_PRIVATE);
        View view = getLayoutInflater().inflate(R.layout.edit_profile, null);
        final BottomSheetDialog dialogBottom = new RoundedBottomSheetDialog(ProFileActivity.this);
        imgProfile = view.findViewById(R.id.imgs);
        avail = view.findViewById(R.id.avail);
        playerName = view.findViewById(R.id.name);
        TI_userName = view.findViewById(R.id.userName);
        bio = view.findViewById(R.id.bio);
        gameList = view.findViewById(R.id.gameList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, AppConstant.player_title);
        tv_title = view.findViewById(R.id.autoCompleteTextView1);
        tv_title.setThreshold(0);
        tv_title.setKeyListener(null);
        tv_title.setAdapter(adapter);
        dialogBottom.setContentView(view);
        dialogBottom.show();
        tv_title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                tv_title.showDropDown();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (tv_title.getRight() - tv_title.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        tv_title.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        view.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerName.getText().toString().trim().equals("")) {
                    playerName.requestFocus();
                    playerName.setError("Player name cannot be empty");
                    return;
                }
                if (userName.equals("")) {
                    Toast.makeText(ProFileActivity.this, "Please check user name first", Toast.LENGTH_LONG).show();
                    return;
                }
                boolean isSlected = false;
                for (int i = 0; i < gameList.getChildCount(); i++) {
                    TextView textView = (TextView) gameList.getChildAt(i);
                    if (textView.getTag().toString().equals("1")) {
                        isSlected = true;
                    }
                }
                if (!isSlected) {
                    Toast.makeText(ProFileActivity.this, "Please select the game you play", Toast.LENGTH_LONG).show();
                    return;
                }
                if (picturePath == null)
                    updateName();
                else
                    uploadProfile();
            }
        });
    }

    private void uploadProfile() {
    }

    private void updateName() {
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Updating...");
//        progressDialog.show();
//        RequestQueue queue = Volley.newRequestQueue(this);
//        String url = AppConstant.AppUrl + "profile_update.php";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("response", response);
//                        progressDialog.cancel();
//                        saveToProfile();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.cancel();
//                Toast.makeText(ProFileActivity.this, "Something went wrong try again later ", Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", appConstant.getId());
//                params.put("name", playerName.getText().toString() + "");
//                params.put("userName", TI_userName.getText().toString().toLowerCase() + "");
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//
//        queue.add(stringRequest);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences(userid, 0);
        Glide.with(ProFileActivity.this).load(AppConstant.AppUrl + "images/" + userid + ".png?u=" + AppConstant.imageExt()).apply(new RequestOptions().circleCrop()).placeholder(R.drawable.game_avatar).into(imgProfile);
        if (userid.equals(appConstant.getId()))
            name.setText(sharedPreferences.getString(AppConstant.name, ""));
        else
            name.setText(sharedPreferences.getString(AppConstant.phoneNumber, "").equals("") ? sharedPreferences.getString(AppConstant.name, "") : appConstant.getContactName(sharedPreferences.getString(AppConstant.phoneNumber, "")));
        bio.setText(sharedPreferences.getString(AppConstant.bio, ""));
        userName.setText("@" + sharedPreferences.getString(AppConstant.userName, "Player" + System.currentTimeMillis() + ""));
        title.setText(sharedPreferences.getString(AppConstant.title, ""));
    }

    public void getProfileInfo() {
        progress.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "profile_info.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        progress.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            teamIdList = jsonObject.getString("teamIdList");
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

    public void showTeamList() {
        //  startActivity(new Intent(ProFileActivity.this,CreateTeam.class));
        View inflated = getLayoutInflater().inflate(R.layout.team_list, null);
        final BottomSheetDialog dialogBottom = new RoundedBottomSheetDialog(ProFileActivity.this);
        RecyclerView recyclerView = inflated.findViewById(R.id.rv_teamlist);
        ShimmerFrameLayout shimmerFrameLayout = inflated.findViewById(R.id.shimmer_layout);
        shimmerFrameLayout.startShimmer();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "get_team_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("responseT", response);
                        shimmerFrameLayout.hideShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        try {
                            ArrayList<MyListData> myListData = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("teamList"));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
                                    myListData.add(new MyListData(jsonObject2.getString("teamName"), jsonObject2.getString("teamId"), jsonObject2.getString("teamMember").split(",").length +" Member"));
                                }
                                TeamDisplayList adapter = new TeamDisplayList(myListData);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(adapter);
                            } else {
                                Toast.makeText(ProFileActivity.this, "No Team Found", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            Log.e("error Rec", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                shimmerFrameLayout.hideShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                error.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                Log.e("teamIdList", teamIdList);
                params.put("teamIdList", teamIdList);
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


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        dialogBottom.setContentView(inflated);
        dialogBottom.show();
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
