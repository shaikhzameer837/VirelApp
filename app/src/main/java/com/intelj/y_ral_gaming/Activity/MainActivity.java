package com.intelj.y_ral_gaming.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intelj.y_ral_gaming.Adapter.MemberListAdapter;
import com.intelj.y_ral_gaming.Adapter.PayMentAdapter;
import com.intelj.y_ral_gaming.Adapter.RankAdapter;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.BuildConfig;
import com.intelj.y_ral_gaming.ComingSoon;
import com.intelj.y_ral_gaming.CustomPagerAdapter;
import com.intelj.y_ral_gaming.DatabaseHelper;
import com.intelj.y_ral_gaming.FirebaseFCMServices;
import com.intelj.y_ral_gaming.Fragment.BottomSheetDilogFragment;
import com.intelj.y_ral_gaming.Fragment.OneFragment;
import com.intelj.y_ral_gaming.GameItem;
import com.intelj.y_ral_gaming.MatchAdapter;
import com.intelj.y_ral_gaming.NotificationAdapter;
import com.intelj.y_ral_gaming.PaidScrims;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SigninActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;
import com.intelj.y_ral_gaming.VolleyMultipartRequest;
import com.intelj.y_ral_gaming.model.NotificationModel;
import com.intelj.y_ral_gaming.model.PaymentHistoryModel;
import com.intelj.y_ral_gaming.model.UserListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    AppConstant appConstant;
    private ViewPager gameViewpager;
    BottomNavigationView bottomNavigation;
    private TabLayout tabLayout;
    TextView timeLeft, textView, coins;
    EditText ign, igid;
    private RecyclerView recyclerView;
    View inflated;
    int RESULT_LOAD_IMAGE = 9;
    int PROFILE_IMAGE = 11;
    ImageView imgProfile, saveProf, edit;
    String picturePath = null;
    TextInputEditText playerName, discordId;
    private Uri filePath = null;
    private DatabaseHelper db;
    AlertDialog dialog;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 15;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    int oldId;
    String amount = "0";
    private TextView package_name;
    ImageView imageView;
    private EditText et_datetime, upi;
    private AdView mAdView;
    LinearLayout moneyList;
    String wAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        AppController.getInstance().getGameName();
        AppController.getInstance().getTournamentTime();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view_drawer);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
        setUpNavigationView();
        if (savedInstanceState == null) {
            invalidateOptionsMenu();
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        FirebaseDatabase.getInstance().getReference("poster").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                ArrayList<String> dataSnapshots = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    dataSnapshots.add(0, child.getValue(String.class));
                }
                viewPager.setAdapter(new CustomPagerAdapter(MainActivity.this,dataSnapshots));
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (position == 2) {
                    //  addviewDisplay();
                }
            }
        });
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int Dheight = displayMetrics.heightPixels;

        Log.e("allheight", Dheight + "");
        findViewById(R.id.lin).post(new Runnable() {
            @Override
            public void run() {
//                findViewById(R.id.bottom).getLayoutParams().height = Dheight - findViewById(R.id.lin).getHeight() - findViewById(R.id.bottom_navigation).getHeight();
            }
        });
        findViewById(R.id.showSupport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSupport();
            }
        });
        coins = findViewById(R.id.coins);
        coins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCoins();
            }
        });
        bottomNavigation = findViewById(R.id.bottom_navigation);
        final ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.game_slot);
        appConstant = new AppConstant(this);
        Log.e("appCon", appConstant.getUserId());
        db = new DatabaseHelper(this, "notifications");
        inflated = stub.inflate();
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//        AdLoader adLoader = new AdLoader.Builder(this, AppConstant.google_ad_mobs)
//                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                    @Override
//                    public void onNativeAdLoaded(NativeAd NativeAd) {
//                        // Show the ad.
//                    }
//                })
//                .withAdListener(new AdListener() {
//                    @Override
//                    public void onAdFailedToLoad(LoadAdError adError) {
//                        // Handle the failure by logging, altering the UI, and so on.
//                    }
//                })
//                .withNativeAdOptions(new NativeAdOptions.Builder()
//                        // Methods in the NativeAdOptions.Builder class can be
//                        // used here to specify individual options settings.
//                        .build())
//                .build();
//        adLoader.loadAd(new AdRequest.Builder().build());
//        SwipeSelector swipeSelector = findViewById(R.id.swipeSelector);
//        swipeSelector.setItems(
//                // The first argument is the value for that item, and should in most cases be unique for the
//                // current SwipeSelector, just as you would assign values to radio buttons.
//                // You can use the value later on to check what the selected item was.
//                // The value can be any Object, here we're using ints.
//                new SwipeItem(0, "Silver", "Earn more on Every Chicken Dinner"),
//                new SwipeItem(1, "Gold", "it is very easy to apply for subscription"),
//                new SwipeItem(2, "Platinum", "Free coins")
//        );
//        findViewById(R.id.views).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openSubscribe();claim_now
//            }
//        });
//        findViewById(R.id.discord).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String url = "https://discord.gg/KPXDCGsmem";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//        });
//        findViewById(R.id.insta).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String url = "https://www.instagram.com/y_ral_gaming/";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//        });
//        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String url = "https://www.youtube.com/c/YRALGaming";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//        });
//        findViewById(R.id.claim_now).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (new AppConstant(MainActivity.this).checkLogin())
//                    showPopDialog();
//                else
//                    showBottomSheetDialog();
//            }
//        });
//        final Handler handler = new Handler();
//        final int delay = 5000; // 1000 milliseconds == 1 second

//        handler.postDelayed(new Runnable() {
//            public void run() {
//                SwipeItem selectedItem = swipeSelector.getSelectedItem();
//                int current = (Integer) selectedItem.value;
//                if (current == 2)
//                    current = 0;
//                else
//                    ++current;
//                swipeSelector.selectItemAt(current); // Do your work here
//                handler.postDelayed(this, delay);
//            }
//        }, delay);
//        getGameName();
//        openTopSheetDialog(roomPassword);
        setFirstView();
        timeLeft = findViewById(R.id.timeLeft);
        oldId = bottomNavigation.getSelectedItemId();
        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if ((item.getItemId() != R.id.tournament && item.getItemId() != R.id.tournament) && !new AppConstant(MainActivity.this).checkLogin()) {
                            showBottomSheetDialog();
                            return true;
                        }
                        if (item.getItemId() == oldId)
                            return true;

                        oldId = item.getItemId();
                        switch (item.getItemId()) {
                            case R.id.game_slot:
                                inflateView(R.layout.game_slot);
                                setFirstView();
                                return true;
//                           case R.id.game_slot:
//                                inflateView(R.layout.fragment_one);
//                                //setFirstView();
//                                return true;
                            case R.id.status:
                                inflateView(R.layout.rank);
                                showNotification();
                                return true;
                            case R.id.rank:
                                inflateView(R.layout.rank);
                                //showRank();
                                return true;
//                            case R.id.team:
//                                inflateView(R.layout.bottom_sheet_dialog);
//                                showTeam();
//                                return true;
                            case R.id.tournament:
                                showEvents();
                                return true;
                            case R.id.history:
                                inflateView(R.layout.history);
                                showHistory(inflated);
                                return true;
                            case R.id.profile:
                                inflateView(R.layout.edit_profile);
                                imgProfile = inflated.findViewById(R.id.imgs);
                                package_name = inflated.findViewById(R.id.amount);
//                                inflated.findViewById(R.id.withdraw).setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        showPopDialog();
//                                    }
//                                });
//                                getUserAmount();
//                                //setPackagename();
//                                imgProfile.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        ActivityCompat.requestPermissions(MainActivity.this,
//                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                                                1);
//                                    }
//                                });

                                Glide.with(MainActivity.this).load(AppController.getInstance().mySnapShort.child(AppConstant.myPicUrl).getValue() + "").placeholder(R.drawable.profile_icon).apply(new RequestOptions().circleCrop()).into(imgProfile);
                                playerName = inflated.findViewById(R.id.name);
                                discordId = inflated.findViewById(R.id.discordId);
                                TextInputEditText phoneNumber = inflated.findViewById(R.id.phoneNumber);
                                saveProf = inflated.findViewById(R.id.save);
                                playerName.setText(AppController.getInstance().mySnapShort.child(AppConstant.userName).getValue() == null ? "" : AppController.getInstance().mySnapShort.child(AppConstant.userName).getValue() + "");
                                phoneNumber.setText(new AppConstant(MainActivity.this).getPhoneNumber());
                                discordId.setText(AppController.getInstance().mySnapShort.child(AppConstant.discordId).exists() ? AppController.getInstance().mySnapShort.child(AppConstant.discordId).getValue() + "" : "");
                                saveProf.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!discordId.isEnabled()) {
                                            discordId.setEnabled(true);
                                            playerName.setEnabled(true);
                                            saveProf.setImageResource(R.drawable.check);
                                            return;
                                        }
                                        if (picturePath == null)
                                            saveToProfile(null);
                                        else
                                            uploadFiles();
                                    }
                                });
                                return true;
                        }
                        return false;
                    }
                };
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
//        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        if (mWifi.isConnected()) {
//
//        }
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Do something after 5s = 5000ms
//                playYTVideo();
//            }
//
//
//        }, 5000);

    }

    public void WidthDrawAmount(View view) {
        TextView selected = ((TextView) view);
        selected.setBackgroundColor(Color.parseColor("#000000"));
        selected.setTextColor(Color.parseColor("#ffffff"));
        for (int i = 0; i < moneyList.getChildCount(); i++) {
            TextView unselected = (TextView) moneyList.getChildAt(i);
            if (selected != moneyList.getChildAt(i)) {
                unselected.setBackgroundResource(R.drawable.outline);
                unselected.setTextColor(Color.parseColor("#000000"));
            }
        }
        wAmount = selected.getText().toString();
    }

    private void showCoins() {
        wAmount = "";
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.money_sheet);
        moneyList = bottomSheetDialog.findViewById(R.id.moneyList);
        bottomSheetDialog.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new AppConstant(MainActivity.this).checkLogin()) {
                    showBottomSheetDialog();
                    return;
                }
                if (wAmount.equals("")) {
                    Toast.makeText(MainActivity.this, "Please any amount to withdraw", Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(wAmount) <= AppController.getInstance().amount) {
                    EditText upi = bottomSheetDialog.findViewById(R.id.upi);
                    if (upi.getText().toString().trim().equals("")) {
                        upi.setError("Upi id cannot be empty");
                        upi.requestFocus();
                        return;
                    }
                    requestMoney(upi.getText().toString());
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Insufficient Balance", Toast.LENGTH_LONG).show();
                }
            }
        });
        bottomSheetDialog.findViewById(R.id.add_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppConstant(MainActivity.this).addMoney(MainActivity.this);
            }
        });
        bottomSheetDialog.show();
    }

    private void requestMoney(String upi) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("loading...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/request_payment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick3", response);
                        progressDialog.cancel();
                        try {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("success")
                                    .setMessage("Payment requested you will recieve payment in 24hrs")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            bottomNavigation.findViewById(R.id.status).performClick();
                         } catch (Exception e) {
                            Log.e("logMess", e.getMessage());

                        }
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
                params.put("upi", upi);
                params.put("userid", new AppConstant(MainActivity.this).getUserId());
                params.put("amount", wAmount);
                params.put("time", (System.currentTimeMillis()) + "");
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

    private void showSupport() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.support);
        bottomSheetDialog.findViewById(R.id.discord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/5XDFC53w5q"));
                startActivity(browserIntent);
            }
        });
        bottomSheetDialog.findViewById(R.id.instagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/y_ral_gaming/"));
                startActivity(browserIntent);
            }
        });
        bottomSheetDialog.findViewById(R.id.youtube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/c/YRALGaming"));
                startActivity(browserIntent);
            }
        });
        bottomSheetDialog.show();
    }

    private void playYTVideo() {
        SharedPreferences prefs = getSharedPreferences(AppConstant.AppName, MODE_PRIVATE);
        String url = prefs.getString("url", "");
        YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtubeFragment);
        youtubeFragment.initialize(AppConstant.youtubeApiKey,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        System.out.println("susccess " + url);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!b) {
                                    if (url.equals("")) {
                                        youTubePlayer.loadPlaylist("PLFSCz7rRk_hVFGD9d25S_789spdVYLLj_");
                                    } else {
                                        youTubePlayer.loadVideo(url);
                                    }
                                }
                            }
                        });
                        thread.start();

                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {
                        Log.d("onInitianFailure: ", youTubeInitializationResult.toString());
                    }
                });
    }

    Dialog Pdialog;

    private void showPopDialog() {
        final int REQUEST_PERMISSIONS = 100;
        Pdialog = new Dialog(this);
        Pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Pdialog.setCancelable(true);
        Pdialog.setContentView(R.layout.fragment_claim_now);
        Pdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogButton = Pdialog.findViewById(R.id.upload_file);
        imageView = Pdialog.findViewById(R.id.imageview);
        et_datetime = Pdialog.findViewById(R.id.et_datetime);
        upi = Pdialog.findViewById(R.id.upi);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {
                        showFileChooser();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    showFileChooser();
                }
            }
        });
        et_datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog fromDatePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                newDate.set(year, monthOfYear, dayOfMonth, selectedHour, selectedMinute);
                                et_datetime.setText(new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.US).format(newDate.getTime()));
                            }
                        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);//Yes 24 hour time
                        //mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                fromDatePickerDialog.show();
            }
        });
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImage != null && !et_datetime.getText().toString().equals("") && !upi.getText().toString().equals(""))
                    uploadBitmap();
                else
                    Toast.makeText(MainActivity.this, "Please Select image and enter Time & UPI Id", Toast.LENGTH_LONG).show();
            }
        });

        Pdialog.show();
    }

    private void showFileChooser() {
//        Intent inz = new Intent(this, GallerySelector.class);
//        startActivityForResult(inz, 80);
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"),RESULT_LOAD_IMAGE);


        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, RESULT_LOAD_IMAGE);

//        new ImagePicker.Builder(this)
//                .mode(ImagePicker.Mode.GALLERY)
//                .compressLevel(ImagePicker.ComperesLevel.NONE)
//                .directory(ImagePicker.Directory.DEFAULT)
//                .allowMultipleImages(false)
//                .build();
    }

    private void saveToProfile(String imageUrl) {
        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.userName).setValue(playerName.getText().toString());
        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.discordId).setValue(discordId.getText().toString());
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstant.userName, playerName.getText().toString());
        editor.putString(AppConstant.discordId, discordId.getText().toString());
        if (imageUrl != null) {
            FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.myPicUrl).setValue(imageUrl);
            editor.putString(AppConstant.myPicUrl, imageUrl);
            progressDialog.dismiss();
            picturePath = null;
        }
        editor.apply();
        discordId.setEnabled(false);
        playerName.setEnabled(false);
        saveProf.setImageResource(R.drawable.ic_edit);
        Toast.makeText(MainActivity.this, "Profile Updated Susccessfully", Toast.LENGTH_LONG).show();
    }

    ArrayList<UserListModel> teamModel;
    MemberListAdapter userAdapter;
    RecyclerView recyclerviewTeam;

    @Override
    protected void onResume() {
        super.onResume();
        if (AppController.getInstance().mySnapShort != null && recyclerviewTeam != null) {
            teamModel.clear();
            for (DataSnapshot snapshot : AppController.getInstance().mySnapShort.child(AppConstant.team).getChildren()) {
                SharedPreferences prefs = getSharedPreferences(snapshot.getKey(), Context.MODE_PRIVATE);
                teamModel.add(new UserListModel(prefs.getString(AppConstant.teamName, null),
                        prefs.getString(AppConstant.myPicUrl, null),
                        snapshot.getKey(),
                        prefs.getStringSet(AppConstant.teamMember, null)));
            }
            userAdapter.notifyDataSetChanged();
        }
        //  setPackagename();
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
        recyclerviewTeam.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerviewTeam, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, GroupProfile.class);
                intent.putExtra(AppConstant.team, teamModel.get(position).getTeamId());
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this, view.findViewById(R.id.imgs), ViewCompat.getTransitionName(view.findViewById(R.id.imgs)));
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        inflated.findViewById(R.id.create_team).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestContactPermission();
            }
        });
    }

    private void showEvents() {
        inflateView(R.layout.rank);
        List<NotificationModel> notificationModelList = new ArrayList<>();
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
                                notificationModelList.add(new NotificationModel(obj.getString("name"),
                                        obj.getString("game_name"),
                                        obj.getString("image_url"),
                                        obj.getString("date"),
                                        obj.getString("status"),
                                        obj.getString("discord_url")));
                            }
                            NotificationAdapter pAdapter = new NotificationAdapter(MainActivity.this, notificationModelList, true);
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
                String url = notificationModelList.get(position).getDiscord_url();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    private void showNotification() {
        List<NotificationModel> notificationModelList = new ArrayList<>();
        TextView title = inflated.findViewById(R.id.title);
        title.setText("Prize Claim History");
        recyclerView = inflated.findViewById(R.id.recycler_view);
        ShimmerFrameLayout shimmerFrameLayout = inflated.findViewById(R.id.shimmer_layout);
        shimmerFrameLayout.startShimmer();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/get_status.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tokenResponse", response);
                        shimmerFrameLayout.hideShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        try {
                            JSONArray array = new JSONArray(response);
                            if (array.length() == 0) {
                                inflated.findViewById(R.id.pBar3).setVisibility(View.VISIBLE);
                                inflated.findViewById(R.id.not).setVisibility(View.VISIBLE);
                                return;
                            }
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                notificationModelList.add(
                                        new NotificationModel(obj.getString("name"),
                                                "",
                                                obj.getString("image_url"),
                                                obj.getString("date"),
                                                obj.getString("status"),
                                                obj.getString("comment")));
                            }
                            NotificationAdapter pAdapter = new NotificationAdapter(MainActivity.this, notificationModelList, false);
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
                Map<String, String> params = new HashMap<>();
                params.put("user_id", appConstant.getUserId());
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


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void showRank() {
        final RecyclerView recyclerView = inflated.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        FirebaseDatabase.getInstance().getReference(AppConstant.users).orderByChild("winner").limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                ArrayList<DataSnapshot> dataSnapshots = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    dataSnapshots.add(0, child);
                }
                RankAdapter pAdapter = new RankAdapter(MainActivity.this, dataSnapshots);
                recyclerView.setAdapter(pAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void inflateView(int layout) {
        ViewStub newViewStub = deflate(inflated);
        newViewStub.setLayoutResource(layout);
        inflated = newViewStub.inflate();
    }

    private void showHistory(View inflated) {
        ArrayList<PaymentHistoryModel> paymentHistoryModels = new ArrayList<>();
        final RecyclerView recyclerView = inflated.findViewById(R.id.recycler_view);
        ShimmerFrameLayout shimmer_container = inflated.findViewById(R.id.shimmer_container);
        shimmer_container.startShimmer();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/get_payment_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tokenResponse", response);
                        shimmer_container.hideShimmer();
                        shimmer_container.setVisibility(View.GONE);
                        try {
                            JSONArray array = new JSONArray(response);
                            if (array.length() == 0) {
                                inflated.findViewById(R.id.pBar3).setVisibility(View.VISIBLE);
                                inflated.findViewById(R.id.msg).setVisibility(View.VISIBLE);
                                return;
                            }
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                paymentHistoryModels.add(new PaymentHistoryModel(AppConstant.getTimeAgo(Integer.parseInt(obj.getString("date"))),
                                        obj.getString("transaction_id"), obj.getString("amount"),
                                        obj.getString("screenshort"), obj.getString("ticket_id")));
                            }
                            PayMentAdapter pAdapter = new PayMentAdapter(MainActivity.this, paymentHistoryModels);
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
                Map<String, String> params = new HashMap<>();
                params.put("user_id", appConstant.getUserId());
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

        /*if (AppController.getInstance().userId != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users)
                    .child(AppController.getInstance().userId).child(AppConstant.paymentHistory);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    ArrayList<DataSnapshot> dataSnapshots = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        dataSnapshots.add(child);
                    }
                    *//*PayMentAdapter pAdapter = new PayMentAdapter(MainActivity.this, dataSnapshots);
                    recyclerView.setAdapter(pAdapter);*//*
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

        }*/
    }


    private void uploadFiles() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (filePath != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            saveToProfile(imageUrl);
                                        }
                                    });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }

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

                // If request is cancelled, the result arrays are empty.
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
            Uri selectedImage = data.getData();
            filePath = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
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
            Glide.with(this).load(picturePath).apply(new RequestOptions().circleCrop()).into(imgProfile);
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

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void uploadBitmap() {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Uploading file, please wait.");
        dialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                "http://y-ral-gaming.com/admin/api/upload.php?" +
                        "userid=" + appConstant.getUserId() + "&&time=" + et_datetime.getText().toString() +
                        "&&profile=" + AppController.getInstance().mySnapShort.child(AppConstant.myPicUrl).getValue() +
                        "&&upi=" + upi.getText().toString() +
                        "&&discord=" + AppController.getInstance().mySnapShort.child(AppConstant.discordId).getValue()
                        + "&&name=" + AppController.getInstance().mySnapShort.child(AppConstant.userName).getValue()

                ,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        if (Pdialog.isShowing())
                            Pdialog.dismiss();
                        try {
                            Log.e("GotError", "success");
                            JSONObject obj = new JSONObject(new String(response.data));
                            // selectedImage = null;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (R.id.status == oldId)
                                        showNotification();
                                    else
                                        bottomNavigation.findViewById(R.id.status).performClick();
                                    //bottomNavigation.getMenu().findItem(R.id.status).setChecked(true);
                                    Toast.makeText(MainActivity.this, "Upload Successfully", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("GotError", "" + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                        // Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError", "" + error.getMessage());
                    }
                }) {

            //            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("tags", tags);
//                return params;
//            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("uploaded_file", new DataPart(imagename + ".png", getFileDataFromDrawable()));
                // params.put("userId", new DataPart("1605435786512"));
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable() {
        selectedImage = getResizedBitmap(selectedImage, 700);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void setFirstView() {
        gameViewpager = inflated.findViewById(R.id.gameViewpager);
        tabLayout = inflated.findViewById(R.id.tabs);
        ign = inflated.findViewById(R.id.ign);
        igid = inflated.findViewById(R.id.igid);
        edit = inflated.findViewById(R.id.edit);
        tabLayout.setupWithViewPager(gameViewpager);
        textView = inflated.findViewById(R.id.subscription);
        textView.setSelected(true);
        ArrayList<String> titleList = new ArrayList<>();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        FirebaseDatabase.getInstance().getReference("masters").child("gameList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance().getReference("masters").keepSynced(true);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("index", (long) snapshot.getValue() + " " + snapshot.getKey());
                    titleList.add(snapshot.getKey());
                    adapter.addFragment((long) snapshot.getValue(), new OneFragment(snapshot.getKey(), (long) snapshot.getValue()), snapshot.getKey());
                    // adapter.addFragment(new PaidScrims(snapshot.getKey(), (boolean) snapshot.getValue()), snapshot.getKey());
                }
                gameViewpager.setAdapter(adapter);
                Intent intent = new Intent("custom-event-name");
                if (titleList.size() > 1)
                    intent.putExtra(AppConstant.title, titleList.get(0));
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
//        if (AppController.getInstance().gameNameHashmap.size() > 0) {
//            for (Map.Entry<String, Boolean> entry : AppController.getInstance().gameNameHashmap.entrySet()) {
//                adapter.addFragment(new PaidScrims(entry.getKey(), entry.getValue()), entry.getKey());
//                titleList.add(entry.getKey());
//                // do something with key and/or tab
//            }
//
//        }
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

    public void getUserAmount() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/get_amount.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tokenResponse", response);
                        amount = response;
                        package_name.setText("Rs " + response);
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
                Map<String, String> params = new HashMap<>();
                params.put("user_id", appConstant.getUserId());
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


    public void openSubscribe() {
        Intent intent = new Intent(this, ComingSoon.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    DatabaseReference mDatabase;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("onClick3: ", intent.getBooleanExtra(AppConstant.userName, false) + "");
            if (intent.getBooleanExtra(AppConstant.userName, false)) {
                Log.e("onClick3: ", "set error");
                ign.setEnabled(true);
                ign.setError("Enter your BGMI in game name");
                ign.requestFocus();
                edit.setImageResource(R.drawable.ic_check);
                return;
            }
            if (intent.getBooleanExtra(AppConstant.AppName, false)) {
                showBottomSheetDialog();
                return;
            }
            if (intent.getBooleanExtra(AppConstant.amount, false)) {
                coins.setText(AppController.getInstance().amount + "");
                return;
            }
            if (intent.getBooleanExtra(AppConstant.teamMember, false)) {
                Set<String> myList = (Set<String>) getIntent().getSerializableExtra("teammeber");
                BottomSheetDilogFragment bottomSheetFragment = new BottomSheetDilogFragment(myList);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                return;
            }
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, 0);
            // ign.setHint(intent.getStringExtra(AppConstant.title) + " in game name");
            // igid.setHint(intent.getStringExtra(AppConstant.title) + " id");
            // ign.setText(sharedPreferences.getString(intent.getStringExtra(AppConstant.title), ""));
            // igid.setText(sharedPreferences.getString(intent.getStringExtra(AppConstant.title) + "_" + AppConstant.userName, ""));
            inflated.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!new AppConstant(context).checkLogin()) {
                        showBottomSheetDialog();
                        return;
                    }

                    if (ign.isEnabled()) {
                        // if (igid.getText().toString().trim().equals("") || ign.getText().toString().trim().equals("")) {
                        if (ign.getText().toString().trim().equals("")) {
                            Toast.makeText(MainActivity.this, intent.getStringExtra(AppConstant.title) + " id cannot be empty", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ign.setEnabled(false);
                        igid.setEnabled(false);
                        ign.clearFocus();
                        edit.setImageResource(R.drawable.ic_edit);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(intent.getStringExtra(AppConstant.title), ign.getText().toString());
                        editor.putString(intent.getStringExtra(AppConstant.title) + "_" + AppConstant.userName, igid.getText().toString());
                        editor.apply();
                        Toast.makeText(MainActivity.this, "Id updated", Toast.LENGTH_LONG).show();
                        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(intent.getStringExtra(AppConstant.title)).setValue(ign.getText().toString());
                        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(intent.getStringExtra(AppConstant.title) + "_" + AppConstant.userName).setValue(igid.getText().toString());
                    } else {
                        edit.setImageResource(R.drawable.ic_check);
                        ign.setEnabled(true);
                        igid.setEnabled(true);
                        igid.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(igid, InputMethodManager.SHOW_IMPLICIT);
                        ign.setSelection(ign.getText().length());
                    }
                }
            });


            // game_id.setText(intent.getStringExtra(AppConstant.title));
//            // Get extra data included in the Intent
//            String message = intent.getStringExtra("message");
//            if (message.equals("bottom_sheet_broadcast")) {
//                showBottomSheetDialog();
//                return;
//            }
//            final String roomPlan = intent.getStringExtra("roomPlan");
//            new CountDownTimer(Long.parseLong(message), 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    int seconds = (int) (millisUntilFinished / 1000) % 60;
//                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
//                    int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
//                    timeLeft.setText("Your next Game starts in " + hours + ":" + minutes + ":" + seconds);
//                    timeLeft.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                           // startService(new Intent(MainActivity.this, MyService.class));
//                        }
//                    });
//                }
//
//                @Override
//                public void onFinish() {
//                    timeLeft.setText("Finished");
//                }
//            }.start();
            //   setRoomVideo(roomPlan);
        }
    };

    private void setRoomVideo(final String roomPlan) {
        AppController.getInstance().uploadUrl = AppConstant.live_stream + "/" + AppController.getInstance().channelId + "/" + roomPlan + "/";
        mDatabase = FirebaseDatabase.getInstance().getReference(AppController.getInstance().uploadUrl + "room/");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                Log.e("dataSnapshotss", dataSnapshot.child(AppConstant.stopTime).exists() + "");
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(AppConstant.stopTime).exists()) {
//                        Intent intent = new Intent(MainActivity.this, HelloService.class);
//                        intent.putExtra("stopTiming", dataSnapshot.child(AppConstant.stopTime).getValue() + "");
//                        intent.setAction(HelloService.ACTION_STOP_FOREGROUND_SERVICE);
//                        startService(intent);
                        try {
                            appConstant.saveSlot("XYZ");
                        } catch (Exception e) {
                            e.getLocalizedMessage();
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    } else {
                        appConstant.saveSlot(dataSnapshot.child(AppConstant.youtubeId).getValue() + "");
                        // if (db.getNotesCount(dataSnapshot.child(AppConstant.youtubeId).getValue() + "") == 0)
                        //createNote(roomPlan, dataSnapshot.child(AppConstant.youtubeId).getValue() + "");
//                        Intent intent = new Intent(MainActivity.this, HelloService.class);
//                        intent.putExtra("roomPlan", roomPlan);
//                        intent.setAction(HelloService.ACTION_START_FOREGROUND_SERVICE);
//                        startService(intent);
                        if (dataSnapshot.child(AppConstant.backgroundData).child(AppController.getInstance().userId).exists()) {
                            HashMap<String, Object> allBackground = new HashMap<>();
//                            for (Note allNote : backgroundDB.getAllNotes()) {
//                                if (allNote.getNote() != null)
//                                    allBackground.put(System.currentTimeMillis() + "", allNote.getNote());
//                                Log.e("backgroundDB", allNote.getTimestamp());
//                            }
                            mDatabase.child("backgroundData").child(AppController.getInstance().userId).setValue(allBackground);
                        }
                    }
//                    roomId.setText("Room Id:- " + dataSnapshot.child("roomId").getValue() + "");
//                    roomPassword.setText("Password:- " + dataSnapshot.child("password").getValue() + "");
//                    roomId.setVisibility(View.VISIBLE);
//                    roomPassword.setVisibility(View.VISIBLE);
//                    roomId.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            final int DRAWABLE_LEFT = 0;
//                            if (event.getAction() == MotionEvent.ACTION_UP) {
//                                Log.e("copyClip", roomId.getText().toString());
//                                if (event.getRawX() >= (roomId.getLeft() - roomId.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
//                                    setClipboard(roomId.getText().toString());
//                                    return true;
//                                }
//                            }
//                            return false;
//                        }
//                    });
//                    roomPassword.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            final int DRAWABLE_LEFT = 0;
//                            if (event.getAction() == MotionEvent.ACTION_UP) {
//                                Log.e("copyClip", roomPassword.getText().toString());
//                                if (event.getRawX() >= (roomId.getLeft() - roomId.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
//                                    setClipboard(roomPassword.getText().toString());
//                                    return true;
//                                }
//                            }
//                            return false;
//                        }
//                    });
                    YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
                            getFragmentManager().findFragmentById(R.id.youtubeFragment);
                    youtubeFragment.initialize(AppConstant.youtubeApiKey,
                            new YouTubePlayer.OnInitializedListener() {
                                @Override
                                public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                                    YouTubePlayer youTubePlayer, boolean b) {
                                    // do any work here to cue video, play video, etc.
                                    if (!b) {
                                        youTubePlayer.loadVideo(dataSnapshot.child(AppConstant.youtubeId).getValue() + "");
                                    }
                                    // youTubePlayer.cueVideo(dataSnapshot.child(AppConstant.youtubeId).getValue() + "",1);
                                }

                                @Override
                                public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                                    YouTubeInitializationResult youTubeInitializationResult) {
                                    Log.d("onInitianFailure: ", youTubeInitializationResult.toString());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.moveTaskToBack(true);
    }

    private void setClipboard(String text) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_login_bottom_sheet_fragment, null);
        final BottomSheetDialog dialogBottom = new BottomSheetDialog(MainActivity.this);
        dialogBottom.setContentView(view);
        dialogBottom.show();
        Button btn_ok = view.findViewById(R.id.ok);
//        Button btn_cancel = view.findViewById(R.id.cancel);
        ImageView imageView = view.findViewById(R.id.imageview);
        Glide.with(MainActivity.this).load(R.drawable.login).into(imageView);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) && dialog == null) {
                        //If User was asked permission before and denied
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

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_aboutus:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tcs.com/")));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_privacypolicy:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tcs.com/privacy-policy")));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_tt:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tcs.com/legal-disclaimer")));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_facebook:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Kakelious/")));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_discord:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/uygzekRnhE")));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_youtube:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/UCt3oqOC4xYAQBzWIhiupLBg"));
                        startActivity(intent);
                        drawer.closeDrawers();
                        return true;
                    /*case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_settings:


                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;*/
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

//                loadHomeFragment();

                return true;
            }
        });


        /*ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();*/
    }

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                Intent intent = new Intent(MainActivity.this, SearchFriendActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, SearchFriendActivity.class);
            startActivity(intent);
        }
    }

    private void setPackagename() {
        if (package_name != null) {
            try {
                JSONObject jsnobject = new JSONObject(AppController.getInstance().getSubscription_package());
                JSONArray jsonArray = jsnobject.getJSONArray("package");
                JSONObject explrObject = jsonArray.getJSONObject(Integer.parseInt(new AppConstant(MainActivity.this)
                        .getDataFromShared(AppConstant.package_id, "0")));
                // package_name.setText(explrObject.getString(AppConstant.package_name));
            } catch (Exception e) {
                Log.e("My App", "Could not parse malformed JSON:" + e.getMessage());
            }
        }
    }

    private void addviewDisplay() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                super.onAdLoaded();
                Toast.makeText(MainActivity.this, "Add loaded", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError);
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                super.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                super.onAdClicked();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
//            super.onAdClosed();
            }
        });

    }
}