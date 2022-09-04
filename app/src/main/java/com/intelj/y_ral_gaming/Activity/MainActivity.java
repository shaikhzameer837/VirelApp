package com.intelj.y_ral_gaming.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.intelj.y_ral_gaming.Adapter.MemberListAdapter;
import com.intelj.y_ral_gaming.Adapter.PopularAdapter;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.BaseActivity;
import com.intelj.y_ral_gaming.ChatActivity;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.Fragment.BottomSheetDilogFragment;
import com.intelj.y_ral_gaming.Fragment.OneFragment;
import com.intelj.y_ral_gaming.PopularModel;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SigninActivity;
import com.intelj.y_ral_gaming.TournamentAdapter;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;
import com.intelj.y_ral_gaming.db.AppDataBase;
import com.intelj.y_ral_gaming.main.PaymentWithdraw;
import com.intelj.y_ral_gaming.model.TournamentModel;
import com.intelj.y_ral_gaming.model.UserListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends BaseActivity {
    AppConstant appConstant;
    private ViewPager gameViewpager;
    BottomNavigationView bottomNavigation;
    private TabLayout tabLayout;
    TextView coins;
    private RecyclerView recyclerView, rv_popular;
    View inflated;
    int RESULT_LOAD_IMAGE = 9;
    int PROFILE_IMAGE = 11;
    ImageView imgProfile, saveProf;
    String picturePath = null;
    TextView playerName, ncount;
    AlertDialog dialog;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 15;
    TextView kill;
    int oldId;
    ImageView imageView;
    List<PopularModel> popularModels = new ArrayList<>();
    PopularAdapter popularAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    SharedPreferences sharedPreferences;
    ArrayList<UserListModel> teamModel;
    MemberListAdapter userAdapter;
    RecyclerView recyclerviewTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgProfile = findViewById(R.id.imgs);
        ncount = findViewById(R.id.ncount);
        appConstant = new AppConstant(this);
        kill = findViewById(R.id.kill);
        kill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRankChat();
            }
        });
        sharedPreferences = getSharedPreferences(appConstant.getId(), 0);
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();

        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);

        getWindow().setExitTransition(fade);
        AppController.getInstance().getGameName();
        AppController.getInstance().getTournamentTime();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
        if (savedInstanceState == null) {
            invalidateOptionsMenu();
        }
        rv_popular = findViewById(R.id.rv_popular);
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        getPopularFace();
        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new AppConstant(MainActivity.this).checkLogin())
                    startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                else
                    showBottomSheetDialog();
            }
        });
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViralWeb.class));
            }
        });
        findViewById(R.id.search).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContextMenuDialogFragment();
            }
        });
        coins = findViewById(R.id.coins);
        coins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new AppConstant(MainActivity.this).checkLogin())
                    showCoins();
                else
                    showBottomSheetDialog();
            }
        });

        bottomNavigation = findViewById(R.id.bottom_navigation);
        final ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.game_slot);
        inflated = stub.inflate();
        setFirstView();
        oldId = bottomNavigation.getSelectedItemId();
        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        if (item.getItemId() == oldId)
                            return true;

                        oldId = item.getItemId();
                        switch (item.getItemId()) {
                            case R.id.game_slot:
                                inflateView(R.layout.game_slot);
                                setFirstView();
                                return true;

                            case R.id.tournament:
                                inflateView(R.layout.tournament);
                                showTournament();
                                return true;
                            case R.id.challenge:
                                inflateView(R.layout.challenge);
                                showChallenge(inflated);
                                return true;
                            case R.id.chat:
                                inflateView(R.layout.contacts);
                                inflated.findViewById(R.id.fMessage).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        startActivity(new Intent(MainActivity.this, new AppConstant(MainActivity.this).checkLogin() ? ChatList.class : SigninActivity.class));
                                    }
                                });
                                ImageView newChat = inflated.findViewById(R.id.newChat);
                                newChat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(MainActivity.this, new AppConstant(MainActivity.this).checkLogin() ? ChatList.class : SigninActivity.class));
                                    }
                                });

                                SharedPreferences shd = getSharedPreferences(AppConstant.recent, MODE_PRIVATE);
                                Set<String> set = shd.getStringSet(AppConstant.contact, null);
                                ArrayList<ContactListModel> contactModel = new ArrayList<>();
                                try {
                                    if (set != null) {
                                        for (String s : set) {
                                            SharedPreferences userInfo = getSharedPreferences(s, Context.MODE_PRIVATE);
                                            AppDataBase appDataBase = AppDataBase.getDBInstance(MainActivity.this, s + "_chats");
                                            Log.e("messages", s);
                                            contactModel.add(0, new ContactListModel(s, userInfo.getString(AppConstant.myPicUrl, ""), appConstant.getContactName(userInfo.getString(AppConstant.phoneNumber, "")), userInfo.getString(AppConstant.id, ""), appDataBase.chatDao().getlastMess().size() > 0 ? appDataBase.chatDao().getlastMess().get(0).messages : ""));
                                        }
                                        Collections.sort(contactModel, new Comparator<ContactListModel>() {
                                            @Override
                                            public int compare(final ContactListModel object1, final ContactListModel object2) {
                                                Log.e("Collections", object1.getName() + " " + object2.getName());
                                                return object1.getName().compareTo(object2.getName());
                                            }
                                        });
                                        inflated.findViewById(R.id.lin).setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    SharedPreferences.Editor setEditor = shd.edit();
                                    setEditor.putStringSet(AppConstant.contact, null);
                                    setEditor.apply();
                                }
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
                                RecyclerView rv_contact = inflated.findViewById(R.id.rv_contact);
                                rv_contact.setLayoutManager(mLayoutManager);
                                ContactListAdapter contactListAdapter = new ContactListAdapter(MainActivity.this, contactModel);
                                rv_contact.setAdapter(contactListAdapter);
                                rv_contact.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this, rv_contact, new RecyclerTouchListener.ClickListener() {
                                    @Override
                                    public void onClick(View view, int position) {
                                        if (!appConstant.checkLogin()) {
                                            startActivity(new Intent(MainActivity.this, SigninActivity.class));
                                            return;
                                        }
                                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                        String transitionName = "fade";
                                        View transitionView = view.findViewById(R.id.profile);
                                        ViewCompat.setTransitionName(transitionView, transitionName);
                                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                                makeSceneTransitionAnimation(MainActivity.this, transitionView, transitionName);
                                        intent.putExtra(AppConstant.id, contactModel.get(position).getUserid());
                                        startActivity(intent, options.toBundle());
                                    }

                                    @Override
                                    public void onLongClick(View view, int position) {

                                    }
                                }));


//                                shd = getSharedPreferences(AppConstant.id, MODE_PRIVATE);
//                                rv_contact = inflated.findViewById(R.id.rv_contact);
//                                inflated.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        if (Build.VERSION.SDK_INT >= 23) {
//                                            String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS};
//                                            if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
//                                                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST);
//                                            } else {
//                                                new readContactTask().execute();
//                                            }
//                                        } else {
//                                            new readContactTask().execute();
//                                        }
//                                    }
//                                });
//                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
//                                rv_contact.setLayoutManager(mLayoutManager);
//                                contactModel = new ArrayList<>();
//                                Set<String> set = shd.getStringSet(AppConstant.contact, null);
//                                if (set != null) {
//                                    for (String s : set) {
//                                        SharedPreferences userInfo = getSharedPreferences(s, Context.MODE_PRIVATE);
//                                        contactModel.add(new ContactListModel(userInfo.getString(AppConstant.myPicUrl, ""), appConstant.getContactName(userInfo.getString(AppConstant.phoneNumber, "")), userInfo.getString(AppConstant.id, ""), userInfo.getString(AppConstant.bio, "")));
//                                    }
//                                    inflated.findViewById(R.id.la_contact).setVisibility(View.GONE);
//                                } else {
//                                    if (Build.VERSION.SDK_INT >= 23) {
//                                        String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS};
//                                        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
//                                            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST);
//                                        } else {
//                                            new readContactTask().execute();
//                                        }
//                                    } else {
//                                        new readContactTask().execute();
//                                    }
//                                }
//                                contactListAdapter = new ContactListAdapter(MainActivity.this, contactModel);
//                                rv_contact.setAdapter(contactListAdapter);
//                                rv_contact.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this, recyclerviewTeam, new RecyclerTouchListener.ClickListener() {
//                                    @Override
//                                    public void onClick(View view, int position) {
//                                        Intent intent = new Intent(MainActivity.this, ProFileActivity.class);
//                                        String transitionName = "fade";
//                                        View transitionView = view.findViewById(R.id.profile);
//                                        ViewCompat.setTransitionName(transitionView, transitionName);
//
//                                        ActivityOptionsCompat options = ActivityOptionsCompat.
//                                                makeSceneTransitionAnimation(MainActivity.this, transitionView, transitionName);
//                                        intent.putExtra("userid", contactModel.get(position).getUserid());
//                                        startActivity(intent, options.toBundle());
//                                    }
//
//                                    @Override
//                                    public void onLongClick(View view, int position) {
//
//                                    }
//                                }));
                                return true;
                        }
                        return false;
                    }
                };
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

    }

    private void showChallenge(View inflated) {

    }

    public void showRankChat() {
        View view = getLayoutInflater().inflate(R.layout.rank_row, null);
        final BottomSheetDialog dialogBottom = new BottomSheetDialog(MainActivity.this);
        dialogBottom.setContentView(view);
        dialogBottom.show();
    }

    private void getPopularFace() {
        AppController.getInstance().popularList.clear();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/popular.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClicks3", response);
                        shimmerFrameLayout.hideShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        try {
                            JSONObject json = new JSONObject(response);
                            JSONArray ja_data = json.getJSONArray("info");
                            for (int i = 0; i < ja_data.length(); i++) {
                                JSONObject jObj = ja_data.getJSONObject(i);
                                appConstant.saveUserInfo("", jObj.getString("userid"), "http://y-ral-gaming.com/admin/api/images/" + jObj.getString("userid") + ".png?u=" + AppConstant.imageExt(), jObj.getString("name"), "", null, jObj.getString("userid"));
                                popularModels.add(new PopularModel(jObj.getString("url"), jObj.getString("name"), jObj.getString("amount"), jObj.getString("userid")));
                            }
                            Collections.sort(popularModels, new Comparator<PopularModel>() {
                                @Override
                                public int compare(PopularModel o1, PopularModel o2) {
                                    return Integer.compare(Integer.parseInt(o2.getTotal_coins()), Integer.parseInt(o1.getTotal_coins()));
                                }
                            });
                            for (PopularModel popularModel : popularModels) {
                                AppController.getInstance().popularList.put(popularModel.getUser_id(), AppController.getInstance().popularList.size() + 1);
                            }
                            popularAdapter = new PopularAdapter(MainActivity.this, popularModels);
                            GridLayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this, 1, GridLayoutManager.HORIZONTAL, false);
                            rv_popular.setLayoutManager(mLayoutManager);
                            rv_popular.setAdapter(popularAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.cancel();
                shimmerFrameLayout.hideShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
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
    }

    private void showCoins() {
        startActivity(new Intent(MainActivity.this, PaymentWithdraw.class));
//        View paymentBottomSheet = getLayoutInflater().inflate(R.layout.delete_demo, null);
//        final BottomSheetDialog dialogBottom = new BottomSheetDialog(MainActivity.this,R.style. BottomSheetDialog);
//
//
//      //  tabs.setupWithViewPager(viewPager);
//
//        FragmentManager fm = getSupportFragmentManager();
//// create a FragmentTransaction to begin the transaction and replace the Fragment
//        FragmentTransaction fragmentTransaction = fm.beginTransaction();
//// replace the FrameLayout with new Fragment
//        fragmentTransaction.replace(R.id.frameLayout,new PlaceholderFragment());
//        fragmentTransaction.commit();
//
//
//        dialogBottom.setContentView(paymentBottomSheet);
//        dialogBottom.show();
//
//        mainTab=paymentBottomSheet.findViewById(R.id.tablayout);
//        fragment = new PrizePoolFragment("");
//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frameLayout, fragment);
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        fragmentTransaction.commit();
/////        viewPager=paymentBottomSheet.findViewById(R.id.viewPager);
//        tabLayout.addTab(tabLayout.newTab().setText("Tab1"));
//        tabLayout.addTab(tabLayout.newTab().setText("Tab2"));
//        tabLayout.addTab(tabLayout.newTab().setText("Tab3"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        TabLayoutAdapter tabLayoutAdapter =new TabLayoutAdapter(this,getSupportFragmentManager(),tabLayout.getTabCount());
//        viewPager.setAdapter(tabLayoutAdapter);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
//
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

    }


//    private void playYTVideo() {
//        SharedPreferences prefs = getSharedPreferences(AppConstant.AppName, MODE_PRIVATE);
//        String url = prefs.getString("url", "");
//        YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
//                getFragmentManager().findFragmentById(R.id.youtubeFragment);
//        youtubeFragment.initialize(AppConstant.youtubeApiKey,
//                new YouTubePlayer.OnInitializedListener() {
//                    @Override
//                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
//                                                        YouTubePlayer youTubePlayer, boolean b) {
//                        System.out.println("susccess " + url);
//                        Thread thread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (!b) {
//                                    if (url.equals("")) {
//                                        youTubePlayer.loadPlaylist("PLFSCz7rRk_hVFGD9d25S_789spdVYLLj_");
//                                    } else {
//                                        youTubePlayer.loadVideo(url);
//                                    }
//                                }
//                            }
//                        });
//                        thread.start();
//
//                    }
//
//                    @Override
//                    public void onInitializationFailure(YouTubePlayer.Provider provider,
//                                                        YouTubeInitializationResult youTubeInitializationResult) {
//                        Log.d("onInitianFailure: ", youTubeInitializationResult.toString());
//                    }
//                });
//    }

//    Dialog Pdialog;
//
//    private void showPopDialog() {
//        final int REQUEST_PERMISSIONS = 100;
//        Pdialog = new Dialog(this);
//        Pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Pdialog.setCancelable(true);
//        Pdialog.setContentView(R.layout.fragment_claim_now);
//        Pdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        Button dialogButton = Pdialog.findViewById(R.id.upload_file);
//        imageView = Pdialog.findViewById(R.id.imageview);
//        et_datetime = Pdialog.findViewById(R.id.et_datetime);
//        upi = Pdialog.findViewById(R.id.upi);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if ((ContextCompat.checkSelfPermission(MainActivity.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(MainActivity.this,
//                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//                    if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                            Manifest.permission.READ_EXTERNAL_STORAGE))) {
//                        showFileChooser();
//                    } else {
//                        ActivityCompat.requestPermissions(MainActivity.this,
//                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
//                                REQUEST_PERMISSIONS);
//                    }
//                } else {
//                    showFileChooser();
//                }
//            }
//        });
//        et_datetime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar newCalendar = Calendar.getInstance();
//                DatePickerDialog fromDatePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
//
//                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        Calendar newDate = Calendar.getInstance();
//                        TimePickerDialog mTimePicker;
//                        mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                            @Override
//                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                                newDate.set(year, monthOfYear, dayOfMonth, selectedHour, selectedMinute);
//                                et_datetime.setText(new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.US).format(newDate.getTime()));
//                            }
//                        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);//Yes 24 hour time
//                        //mTimePicker.setTitle("Select Time");
//                        mTimePicker.show();
//                    }
//
//                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
//                fromDatePickerDialog.show();
//            }
//        });
//        dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (selectedImage != null && !et_datetime.getText().toString().equals("") && !upi.getText().toString().equals(""))
//                    uploadBitmap();
//                else
//                    Toast.makeText(MainActivity.this, "Please Select image and enter Time & UPI Id", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        Pdialog.show();
//    }

//    private void showFileChooser() {
//        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//        startActivityForResult(gallery, RESULT_LOAD_IMAGE);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appConstant.checkLogin() && imgProfile != null) {
            if (!sharedPreferences.getString(AppConstant.name, "").equals("")) {
                ((TextView) findViewById(R.id.complete)).setText(" Refer a Friend & 25 rs per invite");
                Drawable img = getResources().getDrawable(R.drawable.refer);
                Drawable img1 = getResources().getDrawable(R.drawable.arrow_right);
                ((TextView) findViewById(R.id.complete)).setCompoundDrawablesWithIntrinsicBounds(img, null, img1, null);
            }
            Glide.with(MainActivity.this).load(sharedPreferences.getString(AppConstant.myPicUrl, "") == null ? "" : sharedPreferences.getString(AppConstant.myPicUrl, "")).placeholder(R.drawable.game_avatar).apply(new RequestOptions().circleCrop()).into(imgProfile);
            playerName = findViewById(R.id.playerName);
            playerName.setText(sharedPreferences.getString(AppConstant.name, "Player"));
            imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!appConstant.checkLogin()) {
                        showBottomSheetDialog();
                        return;
                    }
                    wayToProfile();
                }
            });
            if (AppController.getInstance().notification == null || AppController.getInstance().notification.getChildrenCount() == 0)
                ncount.setVisibility(View.GONE);
            else {
                ncount.setText(AppController.getInstance().notification.getChildrenCount() + "");
                ncount.setVisibility(View.VISIBLE);
            }

        }
        //        if (AppController.getInstance().mySnapShort != null && recyclerviewTeam != null) {
//            teamModel.clear();
//            for (DataSnapshot snapshot : AppController.getInstance().mySnapShort.child(AppConstant.team).getChildren()) {
//                SharedPreferences prefs = getSharedPreferences(snapshot.getKey(), Context.MODE_PRIVATE);
//                teamModel.add(new UserListModel(prefs.getString(AppConstant.teamName, null),
//                        prefs.getString(AppConstant.myPicUrl, null),
//                        snapshot.getKey(),
//                        prefs.getStringSet(AppConstant.teamMember, null)));
//            }
//            userAdapter.notifyDataSetChanged();
//        }

        //  setPackagename();
    }

    private void wayToProfile() {
        Intent intent = new Intent(MainActivity.this, ProFileActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                MainActivity.this, imgProfile, ViewCompat.getTransitionName(imgProfile));
        intent.putExtra("userid", appConstant.getId());
        startActivity(intent, options.toBundle());
    }

    private void showTeam() {
        inflated.findViewById(R.id.newTeam).setVisibility(View.GONE);
        inflated.findViewById(R.id.bott_button).setVisibility(View.GONE);
        inflated.findViewById(R.id.create_team).setVisibility(View.VISIBLE);
        recyclerviewTeam = inflated.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerviewTeam.setLayoutManager(mLayoutManager);
        teamModel = new ArrayList<>();
        userAdapter = new MemberListAdapter(this, teamModel, AppConstant.applyMatches);
        onResume();
        recyclerviewTeam.setAdapter(userAdapter);
//        recyclerviewTeam.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerviewTeam, new RecyclerTouchListener.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                wayToProfile();
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));
        inflated.findViewById(R.id.create_team).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestContactPermission();
            }
        });
    }

    private void showTournament() {
        List<TournamentModel> tournamentModelList = new ArrayList<>();
        TextView title = inflated.findViewById(R.id.title);
        title.setText("Tournament");
        recyclerView = inflated.findViewById(R.id.recycler_view);
        ShimmerFrameLayout shimmerFrameLayout = inflated.findViewById(R.id.shimmer_layout);
        shimmerFrameLayout.startShimmer();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/get_tournament.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tokenResponse", response);
                        shimmerFrameLayout.hideShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        try {

                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Log.e("team_count", obj.getInt("team_count") + "");
                                tournamentModelList.add(
                                        new TournamentModel(obj.getString("name"),
                                                obj.getString("game_name"),
                                                obj.getString("image_url"),
                                                obj.getString("date"),
                                                obj.getString("status"),
                                                obj.getString("discord_url"),
                                                obj.getString("prize_pool"),
                                                obj.getString("info"),
                                                obj.getString("id"),
                                                obj.getInt("max"),
                                                obj.getInt("team_count")
                                        ));
                            }
                            TournamentAdapter pAdapter = new TournamentAdapter(MainActivity.this, tournamentModelList, true);
                            recyclerView.setAdapter(pAdapter);
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
                Intent intent = new Intent(MainActivity.this, EventInfo.class);
                String transitionName = "fade";
                View transitionView = view.findViewById(R.id.images);
                ViewCompat.setTransitionName(transitionView, transitionName);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this, transitionView, transitionName);
                //intent.putExtra("userid", moviesList.get(position).getUser_id());
                AppController.getInstance().tournamentModel = tournamentModelList.get(position);
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void inflateView(int layout) {
        ViewStub newViewStub = deflate(inflated);
        newViewStub.setLayoutResource(layout);
        inflated = newViewStub.inflate();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (dialog != null)
                    dialog.cancel();
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startActivity(new Intent(MainActivity.this, SigninActivity.class));
                break;
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, PROFILE_IMAGE);
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, SearchFriendActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    Bitmap selectedImage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImg = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImg,
                    filePathColumn, null, null, null);

            if (cursor == null || cursor.getCount() < 1) {
                return; // no cursor or no record. DO YOUR ERROR HANDLING
            }

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            if (columnIndex < 0) // no column index
                return; // DO YOUR ERROR HANDLING

            picturePath = cursor.getString(columnIndex);
            cursor.close();
            try {
                selectedImage = getBitmapFromUri(data.getData());
                saveProf.setImageResource(R.drawable.ic_check);
                Glide.with(this).load(picturePath).apply(new RequestOptions().circleCrop()).into(imgProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMAGE) {
            try {
                selectedImage = getBitmapFromUri(data.getData());
                Glide.with(this).load(selectedImage).apply(new RequestOptions()).into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void setFirstView() {
        gameViewpager = inflated.findViewById(R.id.gameViewpager);
        tabLayout = inflated.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(gameViewpager);
        ArrayList<String> titleList = new ArrayList<>();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        HashMap<String, String> gameList = new HashMap<>();
        gameList.put("BGMI", "1");
        gameList.put("Valorant", "0");
        gameList.put("Apex Legend", "0");
        gameList.put("COD", "0");
        for (Map.Entry<String, String> entry : gameList.entrySet()) {
            titleList.add(entry.getKey());
            adapter.addFragment(Long.parseLong(entry.getValue()), new OneFragment(entry.getKey(), Long.parseLong(entry.getValue())), entry.getKey());
        }
        gameList.put("FREE FIRE", "1");
        titleList.add("FREE FIRE");
        adapter.addFragment(1L, new OneFragment("FREE FIRE", 1), "FREE FIRE");

        gameViewpager.setAdapter(adapter);
        Intent intent = new Intent("custom-event-name");
        if (titleList.size() > 1)
            intent.putExtra(AppConstant.title, titleList.get(0));
        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);

        gameViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                Intent intent = new Intent("custom-event-name");
                intent.putExtra(AppConstant.title, titleList.get(position));
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
            }
        });
    }

    public void editProfile(View view) {
        if (sharedPreferences.getString(AppConstant.name, "").equals("")) {
            if (appConstant.checkLogin())
                startActivity(new Intent(MainActivity.this, EditProfile.class));
            else
                showBottomSheetDialog();
        } else {
            startActivity(new Intent(MainActivity.this, ReferralActivity.class));
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Long index, Fragment fragment, String title) {
            mFragmentList.add(index.intValue() != 0 ? 0 : mFragmentList.size(), fragment);
            mFragmentTitleList.add(index.intValue() != 0 ? 0 : mFragmentTitleList.size(), title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    public ViewStub deflate(View view) {
        ViewParent viewParent = view.getParent();
        if (viewParent != null && viewParent instanceof ViewGroup) {
            int index = ((ViewGroup) viewParent).indexOfChild(view);
            int inflatedId = view.getId();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            ((ViewGroup) viewParent).removeView(view);
            Context context = ((ViewGroup) viewParent).getContext();
            ViewStub viewStub = new ViewStub(context);
            viewStub.setInflatedId(inflatedId);
            viewStub.setLayoutParams(layoutParams);
            ((ViewGroup) viewParent).addView(viewStub, index);
            return viewStub;
        } else {
            throw new IllegalStateException("Inflated View has not a parent");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("onClick3: ", intent.getBooleanExtra(AppConstant.name, false) + "");
            if (intent.getBooleanExtra(AppConstant.name, false)) {
                Log.e("onClick3: ", "set error");

                return;
            }
            if (intent.getBooleanExtra(AppConstant.AppName, false)) {
                showBottomSheetDialog();
                return;
            }
            if (new AppConstant(MainActivity.this).checkLogin() && intent.getBooleanExtra(AppConstant.amount, false)) {
                coins.setText(withSuffix(AppController.getInstance().amount));
                try {
                    kill.setText(Html.fromHtml("<img src='" + AppConstant.getRank(AppController.getInstance().rank) + "'/> " + AppController.getInstance().rank + " points", new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            int resourceId = getResources().getIdentifier(source, "drawable", getPackageName());
                            Drawable drawable = getResources().getDrawable(resourceId);
                            drawable.setBounds(0, 0, 40, 30);
                            return drawable;
                        }
                    }, null));
                } catch (Exception e) {
                    Log.e("onReceive: ", AppConstant.getRank(AppController.getInstance().rank));
                }
                //   kill.setText(appConstant.getCountryCode());
                //  AppController.getInstance().rank + " points"

                return;
            }
            if (intent.getBooleanExtra(AppConstant.teamMember, false)) {
                Set<String> myList = (Set<String>) getIntent().getSerializableExtra("teammember");
                BottomSheetDilogFragment bottomSheetFragment = new BottomSheetDilogFragment(myList);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                return;
            }
        }
    };


    //    private void setRoomVideo(final String roomPlan) {
//        AppController.getInstance().uploadUrl = AppConstant.live_stream + "/" + AppController.getInstance().channelId + "/" + roomPlan + "/";
//        mDatabase = FirebaseDatabase.getInstance().getReference(AppController.getInstance().uploadUrl + "room/");
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(final DataSnapshot dataSnapshot) {
//
//                Log.e("dataSnapshotss", dataSnapshot.child(AppConstant.stopTime).exists() + "");
//                if (dataSnapshot.exists()) {
//                    if (dataSnapshot.child(AppConstant.stopTime).exists()) {
////                        Intent intent = new Intent(MainActivity.this, HelloService.class);
////                        intent.putExtra("stopTiming", dataSnapshot.child(AppConstant.stopTime).getValue() + "");
////                        intent.setAction(HelloService.ACTION_STOP_FOREGROUND_SERVICE);
////                        startService(intent);
//                        try {
//                            appConstant.saveSlot("XYZ");
//                        } catch (Exception e) {
//                            e.getLocalizedMessage();
//                            FirebaseCrashlytics.getInstance().recordException(e);
//                        }
//                    } else {
//                        appConstant.saveSlot(dataSnapshot.child(AppConstant.youtubeId).getValue() + "");
//                        // if (db.getNotesCount(dataSnapshot.child(AppConstant.youtubeId).getValue() + "") == 0)
//                        //createNote(roomPlan, dataSnapshot.child(AppConstant.youtubeId).getValue() + "");
////                        Intent intent = new Intent(MainActivity.this, HelloService.class);
////                        intent.putExtra("roomPlan", roomPlan);
////                        intent.setAction(HelloService.ACTION_START_FOREGROUND_SERVICE);
////                        startService(intent);
//                        if (dataSnapshot.child(AppConstant.backgroundData).child(AppController.getInstance().userId).exists()) {
//                            HashMap<String, Object> allBackground = new HashMap<>();
////                            for (Note allNote : backgroundDB.getAllNotes()) {
////                                if (allNote.getNote() != null)
////                                    allBackground.put(System.currentTimeMillis() + "", allNote.getNote());
////                                Log.e("backgroundDB", allNote.getTimestamp());
////                            }
//                            mDatabase.child("backgroundData").child(AppController.getInstance().userId).setValue(allBackground);
//                        }
//                    }
////                    roomId.setText("Room Id:- " + dataSnapshot.child("roomId").getValue() + "");
////                    roomPassword.setText("Password:- " + dataSnapshot.child("password").getValue() + "");
////                    roomId.setVisibility(View.VISIBLE);
////                    roomPassword.setVisibility(View.VISIBLE);
////                    roomId.setOnTouchListener(new View.OnTouchListener() {
////                        @Override
////                        public boolean onTouch(View v, MotionEvent event) {
////                            final int DRAWABLE_LEFT = 0;
////                            if (event.getAction() == MotionEvent.ACTION_UP) {
////                                Log.e("copyClip", roomId.getText().toString());
////                                if (event.getRawX() >= (roomId.getLeft() - roomId.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
////                                    setClipboard(roomId.getText().toString());
////                                    return true;
////                                }
////                            }
////                            return false;
////                        }
////                    });
////                    roomPassword.setOnTouchListener(new View.OnTouchListener() {
////                        @Override
////                        public boolean onTouch(View v, MotionEvent event) {
////                            final int DRAWABLE_LEFT = 0;
////                            if (event.getAction() == MotionEvent.ACTION_UP) {
////                                Log.e("copyClip", roomPassword.getText().toString());
////                                if (event.getRawX() >= (roomId.getLeft() - roomId.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
////                                    setClipboard(roomPassword.getText().toString());
////                                    return true;
////                                }
////                            }
////                            return false;
////                        }
////                    });
//                    YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
//                            getFragmentManager().findFragmentById(R.id.youtubeFragment);
//                    youtubeFragment.initialize(AppConstant.youtubeApiKey,
//                            new YouTubePlayer.OnInitializedListener() {
//                                @Override
//                                public void onInitializationSuccess(YouTubePlayer.Provider provider,
//                                                                    YouTubePlayer youTubePlayer, boolean b) {
//                                    // do any work here to cue video, play video, etc.
//                                    if (!b) {
//                                        youTubePlayer.loadVideo(dataSnapshot.child(AppConstant.youtubeId).getValue() + "");
//                                    }
//                                    // youTubePlayer.cueVideo(dataSnapshot.child(AppConstant.youtubeId).getValue() + "",1);
//                                }
//
//                                @Override
//                                public void onInitializationFailure(YouTubePlayer.Provider provider,
//                                                                    YouTubeInitializationResult youTubeInitializationResult) {
//                                    Log.d("onInitianFailure: ", youTubeInitializationResult.toString());
//                                }
//                            });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//
//            }
//        });
//    }
    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp - 1));
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_login_bottom_sheet_fragment, null);
        final BottomSheetDialog dialogBottom = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetDialog);
        dialogBottom.setContentView(view);
        dialogBottom.show();
        TextView btn_ok = view.findViewById(R.id.ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) && dialog == null) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                        alertDialogBuilder.setTitle("Permission needed");
                        alertDialogBuilder.setMessage("Storage permission needed for accessing Storage");
                        alertDialogBuilder.setPositiveButton("Open Setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(),
                                        null);
                                intent.setData(uri);
                                MainActivity.this.startActivity(intent);
                            }
                        });
                        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

                        dialog = alertDialogBuilder.create();
                        dialog.show();
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                100);
                    }
                } else
                    startActivity(new Intent(MainActivity.this, SigninActivity.class));
            }
        });

    }
}