package com.intelj.y_ral_gaming.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.InputFilter;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.ChatActivity;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.FollowActivity;
import com.intelj.y_ral_gaming.Fragment.PostFragment;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SigninActivity;
import com.intelj.y_ral_gaming.TournamentAdapter;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;
import com.intelj.y_ral_gaming.model.TournamentModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProFileActivity extends AppCompatActivity {
    TextView txt;
    String userid;
    SharedPreferences sharedPreferences;
    ImageView imgProfile;
    TabLayout tabLayout;
    ViewPager viewPager;
    AppConstant appConstant;
    TextView name, bio, title, userName, follower_count, following_count, edit_profile;
    long followers = 0;
    long following = 0;
    ImageView iconImage,chatIcon;
    TextView  rank,rank_button;
    ProgressBar progress;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        txt = findViewById(R.id.info);
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
            rank.setCompoundDrawablesWithIntrinsicBounds(0,id,0,0);
            // findViewById(R.id.chat).setVisibility(View.GONE);
            iconImage.setImageResource(R.drawable.ic_edit);
            findViewById(R.id.rel_button).setBackgroundResource(R.drawable.curved_red);
            edit_profile.setTextColor(Color.parseColor("#ffffff"));
            edit_profile.setText("Edit Profile");
            findViewById(R.id.rel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProFileActivity.this, EditProfile.class));
                }
            });
        }else{
            findViewById(R.id.rel_mess).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProFileActivity.this,"Coming Soon",Toast.LENGTH_LONG).show();
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
                                        startActivity(new Intent(ProFileActivity.this,CreateTeam.class));
                                    }
                                });
                            }
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
    RecyclerView rv_contact;
    SharedPreferences shd;
    LinearLayout lin;
    ArrayList<EditText> editTextList = new ArrayList<>();
    ArrayList<TextInputLayout> textInputLayouts = new ArrayList<>();
    ArrayList<ContactListModel> contactModel;
    HashMap<String, String> contactArrayList = new HashMap<>();
    HashSet<String> originalContact = new HashSet<>();
    ContactListAdapter contactListAdapter;
    int returnPhone = 0;
    EditText teamName;
    private static final int REQUEST = 112;
    private void createTeam() {
            editTextList.clear();
            contactModel = new ArrayList<>();
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ProFileActivity.this);
            bottomSheetDialog.setContentView(R.layout.add_team_info);
            lin = bottomSheetDialog.findViewById(R.id.lin);
            bottomSheetDialog.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
                    if (!hasPermissions(ProFileActivity.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(ProFileActivity.this, PERMISSIONS, REQUEST);
                    } else {
                        loadChats();
                    }
                }
            });
            teamName = bottomSheetDialog.findViewById(R.id.teamName);
            shd = getSharedPreferences(AppConstant.id, MODE_PRIVATE);
            rv_contact = bottomSheetDialog.findViewById(R.id.rv_contact);
            appConstant = new AppConstant(this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ProFileActivity.this);
            rv_contact.setLayoutManager(mLayoutManager);
            contactModel = new ArrayList<>();

            contactListAdapter = new ContactListAdapter(ProFileActivity.this, contactModel);
            rv_contact.setAdapter(contactListAdapter);
            contactListAdapter.checkVisible();
            rv_contact.addOnItemTouchListener(new RecyclerTouchListener(ProFileActivity.this, rv_contact, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    int slectedContact = contactListAdapter.setChecBox(editTextList.size() == 6 ? -1 : position);
                    if (slectedContact != -1) {
                        addEditText(contactModel.get(position).getName(), contactModel.get(position).getUserid());
                    } else
                        Toast.makeText(ProFileActivity.this, "Max 6 players can register", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
            bottomSheetDialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (teamName.getText().toString().trim().equals("")) {
                        teamName.setError("Enter your team name");
                        teamName.requestFocus();
                        return;
                    }
                    if (editTextList.size() < 4) {
                        Toast.makeText(ProFileActivity.this, "Minimum 4 players are required", Toast.LENGTH_LONG).show();
                        return;
                    }
                    for (int x = 0; x < editTextList.size(); x++) {
                        if (editTextList.get(x).getText().toString().trim().equals("")) {
                            editTextList.get(x).setError("Player Name cannot be empty");
                            editTextList.get(x).requestFocus();
                            return;
                        }
                    }
                    bottomSheetDialog.cancel();
                }
            });
            addEditText("", new AppConstant(ProFileActivity.this).getId());
            bottomSheetDialog.show();
    }
    private void loadChats() {
        Set<String> set = shd.getStringSet(AppConstant.contact, null);
        if (set != null) {
            for (String s : set) {
                SharedPreferences userInfo = getSharedPreferences(s, Context.MODE_PRIVATE);
                contactModel.add(new ContactListModel(s,userInfo.getString(AppConstant.myPicUrl, ""), appConstant.getContactName(userInfo.getString(AppConstant.phoneNumber, "")), userInfo.getString(AppConstant.id, ""), userInfo.getString(AppConstant.bio, "")));
            }
            contactListAdapter.notifyDataSetChanged();
        } else
            new readContactTask().execute();
    }

    public void createTeam(View view) {
    }

    class readContactTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute() {
            super.onPreExecute();
            contactModel.clear();
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(Void... arg0) {
            readContacts();
            returnPhone = 0;
            originalContact.clear();
            for (String phoneNo : contactArrayList.keySet()) {
                serverContact(phoneNo, contactArrayList.get(phoneNo));
            }
            return "You are at PostExecute";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            contactListAdapter.notifyDataSetChanged();
        }
    }
    public void serverContact(String number, String original) {
        Log.e("userNum", number);
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child("users").orderByChild("phoneNumber").equalTo(number);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                returnPhone++;
                if (dataSnapshot != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.e("number//", original);
                        Log.e("number//---", postSnapshot.getKey());
                        originalContact.add(postSnapshot.getKey());
                        appConstant.saveUserInfo(original, postSnapshot.getKey(), AppConstant.AppUrl + "images/" + postSnapshot.getKey() + ".png?u=" + AppConstant.imageExt(), null, "", postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue() != null ? postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue().toString() : null, postSnapshot.child(AppConstant.userName).getValue() != null ? postSnapshot.child(AppConstant.userName).getValue().toString() : System.currentTimeMillis() + "");
                        contactModel.add(new ContactListModel(original,AppConstant.AppUrl + "images/" + postSnapshot.getKey() + ".png?u=" + AppConstant.imageExt(), appConstant.getContactName(postSnapshot.child(AppConstant.phoneNumber).getValue(String.class)), postSnapshot.getKey(), postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue() != null ? postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue().toString() : ""));
                    }
                }
                if (contactArrayList.size() == returnPhone) {
                    SharedPreferences.Editor setEditor = shd.edit();
                    setEditor.putStringSet(AppConstant.contact, originalContact);
                    setEditor.apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void showTeamList() {
        View inflated = getLayoutInflater().inflate(R.layout.tournament, null);
        final BottomSheetDialog dialogBottom = new BottomSheetDialog(ProFileActivity.this);
        List<TournamentModel> tournamentModelList = new ArrayList<>();
        TextView title = inflated.findViewById(R.id.title);
        title.setText("Tournament");
        RecyclerView recyclerView = inflated.findViewById(R.id.recycler_view);
        ShimmerFrameLayout shimmerFrameLayout = inflated.findViewById(R.id.shimmer_layout);
        shimmerFrameLayout.startShimmer();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "get_team.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        shimmerFrameLayout.hideShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        try {

                        } catch (Exception e) {
                            Log.e("error Rec", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
/*                shimmer_container.hideShimmer();
                shimmer_container.setVisibility(View.GONE);*/
                error.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
    public void readContacts() {
        contactArrayList.clear();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
                        if (phoneNo.length() > 8) {
                            String original = phoneNo;
                            if (phoneNo.startsWith("0"))
                                phoneNo = phoneNo.substring(1);
                            if (!phoneNo.startsWith("+"))
                                phoneNo = appConstant.getCountryCode() + phoneNo;
                            Log.i("Phone Number: ", appConstant.getCountryCode());
                            contactArrayList.put(phoneNo, original);
                        }
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    public void addEditText(String userName, String userId) {
        for (int i = 0; i < editTextList.size(); i++) {
            if (editTextList.get(i).getTag().equals(userId)) {
                lin.removeView(textInputLayouts.get(i));
                editTextList.remove(editTextList.get(i));
                return;
            }
        }
        EditText editText = new EditText(ProFileActivity.this);
        editText.setTextSize(12);
        editText.setSingleLine(true);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        editText.setTag(userId);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        TextInputLayout textInputLayout = new TextInputLayout(ProFileActivity.this, null, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        textInputLayout.setBoxCornerRadii(5, 5, 5, 5);
        LinearLayout.LayoutParams textInputLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textInputLayout.setLayoutParams(textInputLayoutParams);
        textInputLayout.addView(editText, editTextParams);
        textInputLayout.setHint(editTextList.size() == 0 ? "Enter your ingame name" : "Enter " + userName + "'s ingame name");
        editTextList.add(editText);
        lin.addView(textInputLayout);
        textInputLayouts.add(textInputLayout);
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
