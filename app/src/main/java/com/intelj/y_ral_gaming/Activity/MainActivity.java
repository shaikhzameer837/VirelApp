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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Html;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
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
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intelj.y_ral_gaming.Adapter.MyListAdapter;
import com.intelj.y_ral_gaming.Adapter.PopularAdapter;
import com.intelj.y_ral_gaming.Adapter.TeamDisplayList;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.BaseActivity;
import com.intelj.y_ral_gaming.BuildConfig;
import com.intelj.y_ral_gaming.Fragment.BottomSheetDilogFragment;
import com.intelj.y_ral_gaming.Fragment.HomeFragment;
import com.intelj.y_ral_gaming.Fragment.OneFragment;
import com.intelj.y_ral_gaming.PopularModel;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.RoundedBottomSheetDialog;
import com.intelj.y_ral_gaming.SigninActivity;
import com.intelj.y_ral_gaming.Utils.AmazonUrlOpener;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.bottomSheet.RulesBottomSheet;
import com.intelj.y_ral_gaming.main.PaymentWithdraw;
import com.intelj.y_ral_gaming.model.TeamListPOJO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends BaseActivity implements RulesBottomSheet.ChallengeAcceptedListener {
    AppConstant appConstant;
    private ViewPager gameViewpager;
    BottomNavigationView bottomNavigation;
    private TabLayout tabLayout;
    TextView coins;
    View inflated;
    int RESULT_LOAD_IMAGE = 9;
    int PROFILE_IMAGE = 11;
    ImageView imgProfile, saveProf;
    String picturePath = null;
    TextView playerName, ncount;
    AlertDialog dialog;
    TextView kill;
    int oldId;
    ImageView imageView;
    List<PopularModel> affiliateModel = new ArrayList<>();
    //    PopularAdapter popularAdapter;
//    private ShimmerFrameLayout shimmerFrameLayout;
    SharedPreferences sharedPreferences;
    private ViewPager viewPager;

    private int currentPage = 0;
    private final long delayTime = 2000; // 2 seconds
    private Handler handler;
    RecyclerView rv_popular;
    ShimmerFrameLayout shimmerFrameLayout;

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
        Log.e("onCreateId: ", new AppConstant(this).getPhoneNumber());
        sharedPreferences = getSharedPreferences(appConstant.getId(), 0);
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();

        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);

        getWindow().setExitTransition(fade);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-event-name"));
        if (savedInstanceState == null) {
            invalidateOptionsMenu();
        }
        getUserInfo();
//        rv_popular = findViewById(R.id.rv_popular);
        //shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        getAffiliate();
        rv_popular = findViewById(R.id.rv_popular);
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        getPopularFace();
        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new AppConstant(MainActivity.this).checkLogin())
                    startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                else LoginSheet();
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ViralWeb.class));
            }
        });
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {

            }
        }
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
                    LoginSheet();
            }
        });

        bottomNavigation = findViewById(R.id.bottom_navigation);
        final ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.game_slot);
        inflated = stub.inflate();
        oldId = bottomNavigation.getSelectedItemId();
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == oldId) return true;

                oldId = item.getItemId();
                switch (item.getItemId()) {
                    case R.id.game_slot:
                        Log.e("getString", tab.length() + "");
                        inflateView(R.layout.game_slot);
                        setFirstView();
                        titleList.clear();
                        gamingAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                        Iterator<String> keys = tab.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            Log.e("getString", key);
                            String value = "";
                            try {
                                value = tab.getString(key);

                                titleList.add(key);
                                Log.e("valueHolder", value);
                                gamingAdapter.addFragment(new HomeFragment(key, value), key);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        inflated.findViewById(R.id.lin).setVisibility(View.GONE);
                        gameViewpager.setAdapter(gamingAdapter);
                        return true;
                    case R.id.giveAway:
                        inflateView(R.layout.game_slot);
                        setSecondView();
                        return true;
                    case R.id.challenge:
                        inflateView(R.layout.store);
//                        showWebView(AppConstant.AppUrl + "web/delete_me.php?u=" + new AppConstant(MainActivity.this).getId());
                        showWebView(AppConstant.AppUrl + "web/give_away.php?u=" + new AppConstant(MainActivity.this).getId());
                        return true;
                    case R.id.store:
                        inflateView(R.layout.store);
                        showWebView(AppConstant.AppUrl + "reward/rewards.php?u="+ new AppConstant(MainActivity.this).getId());
                        return true;
//                    case R.id.challenge:
//                        inflateView(R.layout.store);
//                        try {
//                            showWebView(AppController.getInstance().homeUrl.get(2) + "?u=" + new AppConstant(MainActivity.this).getId());
//                        } catch (Exception e) {
//
//                        }
//                        inflated.findViewById(R.id.lin).setVisibility(View.GONE);
//                        return true;
//                    case R.id.chat:
//                        inflateView(R.layout.store);
//                        try {
//                            showWebView(AppController.getInstance().homeUrl.get(3) + "?u=" + new AppConstant(MainActivity.this).getId());
//                        } catch (Exception e) {
//
//                        }
//                        inflated.findViewById(R.id.lin).setVisibility(View.GONE);
                    //inflateView(R.layout.contacts);
//                        inflated.findViewById(R.id.fMessage).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                startActivity(new Intent(MainActivity.this,ChatList.class));
//                            }
//                        });
//                        inflated.findViewById(R.id.newChat).setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                startActivity(new Intent(MainActivity.this,ChatList.class));
//                            }
//                        });
//                        return true;
                }
                return false;

            }

        });


    }

    public class AutoSliderAdapter extends PagerAdapter {

        private Context context;

        public AutoSliderAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return affiliateModel.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_slider, container, false);
            ImageView imageView = view.findViewById(R.id.imageView);
            CardView cardView = view.findViewById(R.id.cardView);
//            TextView title = view.findViewById(R.id.title);
//            TextView desc = view.findViewById(R.id.desc);
//            title.setText(affiliateModel.get(position).getImg_name());
//            desc.setText(affiliateModel.get(position).getDescription());
            Glide.with(MainActivity.this).load(affiliateModel.get(position).getUser_id()).placeholder(R.drawable.game_avatar).into(imageView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = affiliateModel.get(position).getTotal_coins();
                    AmazonUrlOpener.openAmazonUrl(MainActivity.this, url);
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    private void startAutoSlider() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int nextPage = (currentPage + 1) % affiliateModel.size();
                viewPager.setCurrentItem(nextPage);
                currentPage = nextPage;
                startAutoSlider();
            }
        }, delayTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        handler.removeCallbacksAndMessages(null);
    }

    String key;
    JSONObject tab;
    Integer MyCoins = 0;

    private void getUserInfo() {
        Log.e("AppConstant-AppUrl4", "start");
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "user_info.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("AppConstant-AppUrl3", response);
                        // progressDialog.cancel();

                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success") && !json.has("msg")) {
                                AppController.getInstance().amount = Integer.parseInt(json.getString("amount"));
                                AppController.getInstance().rank = Integer.parseInt(json.getString("rank"));
                                AppController.getInstance().referral = json.getString("referral");
                                appConstant.setDataFromShared(AppConstant.teamList,json.getString("teamList"));
                                Log.e("teamIdList-data", appConstant.getDataFromShared(AppConstant.teamList,""));
                                tab = json.getJSONObject("tab");
                                AppController.getInstance().homeUrl = json.getJSONArray("homeUrl");
                                MyCoins = Integer.parseInt(json.getString("amount"));
                                Log.e("AppController-amount-9", AppController.getInstance().amount + "");
                                coins.setText(withSuffix(Integer.parseInt(json.getString("amount"))));
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
                                if (appConstant.checkLogin() && !sharedPreferences.getString(AppConstant.name, "").equals("")) {
                                    //   ((TextView) findViewById(R.id.complete)).setText(" Refer a Friend & earn " + AppController.getInstance().referral + " rs per invite");
                                }

                            } else {
                                key = "OK";
                                if (json.getInt("key") == 1)
                                    key = "Click Download new version";
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Alert")
                                        .setCancelable(false)
                                        .setMessage(json.getString("msg"))
                                        .setPositiveButton(key, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if (key.equals("Click Download new version")) {
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.intelj.y_ral_gaming"));
                                                    startActivity(browserIntent);
                                                } else {
                                                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                                                    startMain.addCategory(Intent.CATEGORY_HOME);
                                                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(startMain);
                                                }
                                            }
                                        })

                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                                startMain.addCategory(Intent.CATEGORY_HOME);
                                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(startMain);
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                                // AppController.getInstance().amount = 0;
                                Intent intent = new Intent("custom-event-name");
                                intent.putExtra(AppConstant.amount, true);
                                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            Log.e("AppConstant.AppUrl2", e.getMessage());

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AppConstant.AppUrl1", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", new AppConstant(MainActivity.this).getId());
                int versionCode = BuildConfig.VERSION_CODE;
                params.put("version", versionCode + "");
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

    WebView browser;

    private void showWebView(String Url) {
        browser = inflated.findViewById(R.id.webview);
        browser.loadUrl(Url);
        Log.e("AppConstant", Url);
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setWebViewClient(new MyBrowser());
        browser.setInitialScale(0);
        browser.setVerticalScrollBarEnabled(false);
        browser.setHorizontalScrollBarEnabled(false);
        browser.setScrollbarFadingEnabled(false);// Enable cache
        browser.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        inflated.findViewById(R.id.rewards).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    view.setBackgroundResource(R.drawable.outline_black);
//                    inflated.findViewById(R.id.store).setBackgroundColor(Color.parseColor("#ffffff"));
//                    showWebView(AppController.getInstance().homeUrl.get(0) + "?u=" + new AppConstant(MainActivity.this).getId());
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
//        inflated.findViewById(R.id.store).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    view.setBackgroundResource(R.drawable.outline_black);
//                    inflated.findViewById(R.id.rewards).setBackgroundColor(Color.parseColor("#ffffff"));
//                    showWebView(AppController.getInstance().homeUrl.get(1) + "?u=" + new AppConstant(MainActivity.this).getId());
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });
        browser.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                inflated.findViewById(R.id.pBar3).setVisibility(View.VISIBLE);
                if (progress == 100)
                    inflated.findViewById(R.id.pBar3).setVisibility(View.GONE);
            }
        });
        // browser.loadUrl("http://y-ral-gaming.com/admin/api/reward/rewards.php");
    }

    public class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("shouldLoading: ", url);
            if (url.startsWith("!http://redirect_to_app")) {
                view.loadUrl(url);
            } else {
                if (appConstant.checkLogin()) {
                    String[] urlSplit = url.split("/");
                    if (urlSplit[3].equals("redeem")) {
                        AppController.getInstance().amount = MyCoins;
                        Intent intent = new Intent(MainActivity.this, PaymentWithdraw.class);
                        intent.putExtra("coins", urlSplit[5]);
                        intent.putExtra("reward_id", urlSplit[4]);
                        startActivity(intent);
                    } else if (urlSplit[3].equals("challenge")) {
                        RulesBottomSheet bottomSheet = RulesBottomSheet.newInstance(urlSplit[4], urlSplit[5], urlSplit[6]);
                        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
                    } else if (urlSplit[3].equals("other")) {
                        getCommonApi(urlSplit[4]);
                    }
                }else{
                    LoginSheet();
                }
            }

            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {

        }
    }

    @Override
    public void onChallengeAccepted() {
        if (browser != null) {
            browser.reload();
        }
    }

    private void getCommonApi(String url) {
        Log.e("AppConstant.AppUrl4", "start");
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "?u=" + new AppConstant(MainActivity.this),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("AppConstant.AppUrl3", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                Toast.makeText(MainActivity.this, json.getString("msg"), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AppConstant.AppUrl1", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", new AppConstant(MainActivity.this).getId());
                int versionCode = BuildConfig.VERSION_CODE;
                params.put("version", versionCode + "");
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

    public void showRankChat() {
        View view = getLayoutInflater().inflate(R.layout.rank_row, null);
        final BottomSheetDialog dialogBottom = new RoundedBottomSheetDialog(MainActivity.this);
        dialogBottom.setContentView(view);
        dialogBottom.show();
    }

    HashMap<String, String> jsonAnimationList = new HashMap<>();
    TextView refer, referral;
    RecyclerView invite_recyclerView;
    ArrayList<TeamListPOJO> myListData = new ArrayList<>();

    public void showInvite() {
        View view = getLayoutInflater().inflate(R.layout.referral_activity, null);
        final BottomSheetDialog dialogBottom = new RoundedBottomSheetDialog(MainActivity.this);
        dialogBottom.setContentView(view);
        dialogBottom.show();
        invite_recyclerView = view.findViewById(R.id.recyclerView);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        jsonAnimationList.put("referral.json", "Refer a friend");
        jsonAnimationList.put("login.json", "getReferralList & play Game");
        jsonAnimationList.put("cash.json", "You earn 10rs after game played");
        refer = view.findViewById(R.id.refer);
        referral = view.findViewById(R.id.referal);
        refer.setText(" YRAL" + new AppConstant(this).getId());
        referral.setText(" My Referral id [YRAL" + new AppConstant(this).getId() + "]  ");
        viewPager.setAdapter(new CustomPagerAdapter(MainActivity.this));
        getReferalList();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                viewPager.setCurrentItem(viewPager.getCurrentItem() == (jsonAnimationList.size() - 1) ? 0 : viewPager.getCurrentItem() + 1);
                handler.postDelayed(this, 2000); //now is every 2 minutes
            }
        }, 2000);
    }

    private void getReferalList() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "get_referral_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("responses", response);
                        findViewById(R.id.progress).setVisibility(View.GONE);
                        if (response.equals("[]")) {
                            findViewById(R.id.anim).setVisibility(View.VISIBLE);
                            findViewById(R.id.recyclerView).setVisibility(View.GONE);
                            return;
                        }
                        try {
                            JSONArray json = new JSONArray(response);
                            TextView totalAmount = findViewById(R.id.totalAmount);
                            int totalSuccessInvite = 0;
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = (JSONObject) json.get(i);
                                if (!jsonObject.getString("playing_status").equals("0"))
                                    totalSuccessInvite = totalSuccessInvite + Integer.parseInt(jsonObject.getString("playing_status"));
                                myListData.add(new TeamListPOJO(jsonObject.getString("name"), jsonObject.getString("userId"), jsonObject.getString("playing_status")));
                                Log.e("responses", jsonObject.getString("name"));
                            }
                            totalAmount.setText("+" + totalSuccessInvite);
                            MyListAdapter adapter = new MyListAdapter(myListData);
                            invite_recyclerView.setHasFixedSize(true);
                            invite_recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            invite_recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                findViewById(R.id.progress).setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Something went wrong try again later ", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("referral_id", "YRAL" + appConstant.getId());
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

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.image_view, collection, false);
            TextView textView = layout.findViewById(R.id.title);
            LottieAnimationView lottieAnimationView = layout.findViewById(R.id.animationView);
            String firstKey = jsonAnimationList.keySet().toArray()[position].toString();
            lottieAnimationView.setAnimation(firstKey);
            textView.setText(jsonAnimationList.get(firstKey));
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return jsonAnimationList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }

    private void getAffiliate() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "web/getaffilated.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("popular-onClicks3", response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        // appConstant.saveUserInfo("", jObj.getString("id"), jObj.getString("imageURL"), jObj.getString("name"), "", null, jObj.getString("id"));
                        affiliateModel.add(new PopularModel(jObj.getString("name"), jObj.getString("url"), jObj.getString("banner_url"), jObj.getString("description")));
                    }
                    viewPager = findViewById(R.id.viewpager);
                    AutoSliderAdapter adapter = new AutoSliderAdapter(MainActivity.this);
                    viewPager.setAdapter(adapter);
                    handler = new Handler();
                    startAutoSlider();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

    ArrayList<PopularModel> popularModels = new ArrayList<>();

    private void getPopularFace() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "popular.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("onClicks-popular", response);
                popularModels.clear();
                shimmerFrameLayout.hideShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray ja_data = json.getJSONArray("info");
                    for (int i = 0; i < ja_data.length(); i++) {
                        JSONObject jObj = ja_data.getJSONObject(i);
                        appConstant.saveUserInfo("", jObj.getString("userid"), "http://y-ral-gaming.com/admin/api/images/" + jObj.getString("userid") + ".png?u=" + AppConstant.imageExt(), jObj.getString("name"), "", null, jObj.getString("userid"));
                        popularModels.add(new PopularModel(AppConstant.AppUrl + "images/" + jObj.getString("userid"), jObj.getString("name"), jObj.getString("amount"), jObj.getString("userid"), ""));
                        // recyclerDataArrayList.add(new RecyclerData(jObj.getString("userid"),"http://y-ral-gaming.com/admin/api/images/" + jObj.getString("userid") + ".png?u=" + (System.currentTimeMillis() / 1000),jObj.getString("name")));
//                                recyclerDataArrayList.add(new RecyclerData(jObj.getString("url"), jObj.getString("name"), jObj.getString("amount"), jObj.getString("userid")));
                    }
                    Collections.sort(popularModels, new Comparator<PopularModel>() {
                        @Override
                        public int compare(PopularModel o1, PopularModel o2) {
                            return Integer.compare(Integer.parseInt(o2.getAmount()), Integer.parseInt(o1.getAmount()));
                        }
                    });
                    Log.e("onClicks-popular", popularModels.size() + "");
                    PopularAdapter popularAdapter = new PopularAdapter(MainActivity.this, popularModels);
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
        AppController.getInstance().amount = MyCoins;
        Intent intent = new Intent(MainActivity.this, PaymentWithdraw.class);
        intent.putExtra("coins", "25");
        intent.putExtra("reward_id", "0");
        startActivity(intent);
    }

    TeamDisplayList teamAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        //
        inflateView(R.layout.game_slot);
        if (bottomNavigation.getSelectedItemId() == R.id.game_slot) {
            setFirstView();
        } else if (bottomNavigation.getSelectedItemId() == R.id.giveAway) {
            setSecondView();
        }
        Log.e("setFirstView", "yes");
        if (appConstant.checkLogin() && imgProfile != null) {
            Glide.with(MainActivity.this).load(AppConstant.AppUrl + "images/" + appConstant.getId() + ".png?u=" + sharedPreferences.getString(AppConstant.profile, "Player")).placeholder(R.drawable.game_avatar).apply(new RequestOptions().circleCrop()).into(imgProfile);
            playerName = findViewById(R.id.playerName);
            playerName.setText(sharedPreferences.getString(AppConstant.userName, "Player"));
            imgProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!appConstant.checkLogin()) {
                        LoginSheet();
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
            if (sharedPreferences.getString(AppConstant.gameList, "").equals(""))
                showGameSelection();
        }
        if (!appConstant.checkLogin()) {
            //  ((TextView) findViewById(R.id.complete)).setText(" Register now and get 10Rs Instantly ");
        }
    }

    String gameListStr = "";
    BottomSheetDialog gameSheet;

    public void showGameSelection() {
        gameListStr = sharedPreferences.getString(AppConstant.gameList, "");
        ArrayList<String> gameList = new ArrayList<>();
        gameList.add("https://media.discordapp.net/attachments/1024724326957715567/1024724437498609664/54f31449f5f91cf0cc223cc635cd5952jpg_1655955051259_1655955067513.jpeg");
        gameList.add("https://media.discordapp.net/attachments/1024724326957715567/1024728236741099620/BGMI-Ban1659073440553.jpg");
        gameList.add("https://media.discordapp.net/attachments/1024724326957715567/1024729236805791744/Valorant_2022_E5A2_PlayVALORANT_ContentStackThumbnail_1200x625_MB01.png");
        gameList.add("https://media.discordapp.net/attachments/1024724326957715567/1024875824031219732/COD-LAUNCH-TOUT.jpg");
        View view = getLayoutInflater().inflate(R.layout.select_game, null);
        if (gameListStr.contains("Free Fire,")) {
            view.findViewById(R.id.rel1).setBackgroundResource(R.drawable.curved_red);
            ((TextView) view.findViewById(R.id.text1)).setTextColor(Color.parseColor("#ffffff"));
        } else if (gameListStr.contains("BGMI,")) {
            view.findViewById(R.id.rel2).setBackgroundResource(R.drawable.curved_red);
            ((TextView) view.findViewById(R.id.text3)).setTextColor(Color.parseColor("#ffffff"));
        }
        if (gameListStr.contains("Valorant,")) {
            view.findViewById(R.id.rel3).setBackgroundResource(R.drawable.curved_red);
            ((TextView) view.findViewById(R.id.text3)).setTextColor(Color.parseColor("#ffffff"));
        }
        if (gameListStr.contains("COD Mobile,")) {
            view.findViewById(R.id.rel4).setBackgroundResource(R.drawable.curved_red);
            ((TextView) view.findViewById(R.id.text4)).setTextColor(Color.parseColor("#ffffff"));
        }
        view.findViewById(R.id.rel1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("gameListStr", gameListStr);
                if (gameListStr.contains("Free Fire,")) {
                    gameListStr = gameListStr.replace("Free Fire,", "");
                    view.findViewById(R.id.rel1).setBackgroundResource(R.drawable.border_white_curved);
                    ((TextView) view.findViewById(R.id.text1)).setTextColor(Color.parseColor("#000000"));
                } else {
                    gameListStr = gameListStr + "Free Fire,";
                    view.findViewById(R.id.rel1).setBackgroundResource(R.drawable.curved_red);
                    ((TextView) view.findViewById(R.id.text1)).setTextColor(Color.parseColor("#ffffff"));
                }
            }
        });
        view.findViewById(R.id.rel2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameListStr.contains("BGMI,")) {
                    gameListStr = gameListStr.replace("BGMI,", "");
                    view.findViewById(R.id.rel2).setBackgroundResource(R.drawable.border_white_curved);
                    ((TextView) view.findViewById(R.id.text2)).setTextColor(Color.parseColor("#000000"));
                } else {
                    gameListStr = gameListStr + "BGMI,";
                    view.findViewById(R.id.rel2).setBackgroundResource(R.drawable.curved_red);
                    ((TextView) view.findViewById(R.id.text2)).setTextColor(Color.parseColor("#ffffff"));
                }
            }
        });
        view.findViewById(R.id.rel3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameListStr.contains("Valorant,")) {
                    gameListStr = gameListStr.replace("Valorant,", "");
                    view.findViewById(R.id.rel3).setBackgroundResource(R.drawable.border_white_curved);
                    ((TextView) view.findViewById(R.id.text3)).setTextColor(Color.parseColor("#000000"));
                } else {
                    gameListStr = gameListStr + "Valorant,";
                    view.findViewById(R.id.rel3).setBackgroundResource(R.drawable.curved_red);
                    ((TextView) view.findViewById(R.id.text3)).setTextColor(Color.parseColor("#ffffff"));
                }
            }
        });
        view.findViewById(R.id.rel4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameListStr.contains("COD Mobile,")) {
                    gameListStr = gameListStr.replace("COD Mobile,", "");
                    view.findViewById(R.id.rel4).setBackgroundResource(R.drawable.border_white_curved);
                    ((TextView) view.findViewById(R.id.text4)).setTextColor(Color.parseColor("#000000"));
                } else {
                    gameListStr = gameListStr + "COD Mobile,";
                    view.findViewById(R.id.rel4).setBackgroundResource(R.drawable.curved_red);
                    ((TextView) view.findViewById(R.id.text4)).setTextColor(Color.parseColor("#ffffff"));
                }
            }
        });
        view.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gameListStr.equals("")) {
                    Toast.makeText(MainActivity.this, "Please select atleast one Games", Toast.LENGTH_LONG).show();
                    return;
                }
                saveGameList();
            }
        });
        gameSheet = new RoundedBottomSheetDialog(MainActivity.this);
        Glide.with(this).load(gameList.get(0)).placeholder(R.drawable.game_avatar).into((ImageView) view.findViewById(R.id.img1));
        Glide.with(this).load(gameList.get(1)).placeholder(R.drawable.game_avatar).into((ImageView) view.findViewById(R.id.img2));
        Glide.with(this).load(gameList.get(2)).placeholder(R.drawable.game_avatar).into((ImageView) view.findViewById(R.id.img3));
        Glide.with(this).load(gameList.get(3)).placeholder(R.drawable.game_avatar).into((ImageView) view.findViewById(R.id.img4));
        gameSheet.setContentView(view);
        gameSheet.show();
    }

    private void saveGameList() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "save_game.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                gameSheet.cancel();
                Log.e("tokenResponse", response);
                if (response.equals("1")) {
                    SharedPreferences.Editor shd = sharedPreferences.edit();
                    shd.putString(AppConstant.gameList, gameListStr);
                    shd.apply();
                    Toast.makeText(MainActivity.this, "Games Updated", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
/*                shimmer_container.hideShimmer();
                shimmer_container.setVisibility(View.GONE);*/
                error.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("game", gameListStr);
                hashMap.put("userid", new AppConstant(MainActivity.this).getId() + "");
                return hashMap;
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

    private void wayToProfile() {
        Intent intent = new Intent(MainActivity.this, ProFileActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imgProfile, ViewCompat.getTransitionName(imgProfile));
        intent.putExtra("userid", appConstant.getId());
        startActivity(intent, options.toBundle());
    }

    private void inflateView(int layout) {
        ViewStub newViewStub = deflate(inflated);
        inflated = null;
        newViewStub.setLayoutResource(layout);
        inflated = newViewStub.inflate();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (dialog != null) dialog.cancel();
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startActivity(new Intent(MainActivity.this, SigninActivity.class));
                break;
            case 5:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to display notitification", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, PROFILE_IMAGE);
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    Bitmap selectedImage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImg = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImg, filePathColumn, null, null, null);

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
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    ViewPagerAdapter gamingAdapter;
    ArrayList<String> titleList = new ArrayList<>();

    public void setFirstView() {
        gameViewpager = inflated.findViewById(R.id.gameViewpager);
        tabLayout = inflated.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(gameViewpager);


        Intent intent = new Intent("custom-event-name");
        if (titleList.size() > 1) intent.putExtra(AppConstant.title, titleList.get(0));
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

    public void setSecondView() {
        gameViewpager = inflated.findViewById(R.id.gameViewpager);
        tabLayout = inflated.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(gameViewpager);

        ArrayList<String> titleList = new ArrayList<>();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        FirebaseDatabase.getInstance().getReference("masters").child("gameList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseDatabase.getInstance().getReference("masters").keepSynced(true);
                if (inflated != null)
                    inflated.findViewById(R.id.lin).setVisibility(View.GONE);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("index", (long) snapshot.getValue() + " " + snapshot.getKey());
                    titleList.add(snapshot.getKey());
                    adapter.addFragment(new OneFragment(snapshot.getKey(), (long) snapshot.getValue()), snapshot.getKey());
                    // adapter.addFragment(new PaidScrims(snapshot.getKey(), (boolean) snapshot.getValue()), snapshot.getKey());
                }
                gameViewpager.setAdapter(adapter);
//               Intent intent = new Intent("custom-event-name");
//               if (titleList.size() > 1)
//                   intent.putExtra(AppConstant.title, titleList.get(0));
//               LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void editProfile(View view) {
        if (sharedPreferences.getString(AppConstant.name, "").equals("")) {
            if (appConstant.checkLogin())
                startActivity(new Intent(MainActivity.this, EditProfile.class));
            else LoginSheet();
        } else {
            //  showInvite();
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(mFragmentTitleList.size(), title);
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
            if (intent.getBooleanExtra(AppConstant.AppName, false)) {
                LoginSheet();
                return;
            }
            if (intent.getBooleanExtra(AppConstant.teamMember, false)) {
                Set<String> myList = (Set<String>) getIntent().getSerializableExtra("teammember");
                BottomSheetDilogFragment bottomSheetFragment = new BottomSheetDilogFragment(myList);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());

            }

        }
    };

    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c", count / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }


    public void LoginSheet() {
        wayToLogin();
//        View view = getLayoutInflater().inflate(R.layout.login_sheet, null);
//        final BottomSheetDialog dialogBottom = new RoundedBottomSheetDialog(MainActivity.this);
//        dialogBottom.setContentView(view);
//        dialogBottom.show();
//        TextView btn_ok = view.findViewById(R.id.loginBtn);
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              wayToLogin();
//            }
//        });

    }

    private void wayToLogin() {
        startActivity(new Intent(MainActivity.this, SigninActivity.class));
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) && dialog == null) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//                alertDialogBuilder.setTitle("Permission needed");
//                alertDialogBuilder.setMessage("Storage permission needed for accessing Storage");
//                alertDialogBuilder.setPositiveButton("Open Setting", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
//                        intent.setData(uri);
//                        MainActivity.this.startActivity(intent);
//                    }
//                });
//                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                });
//
//                dialog = alertDialogBuilder.create();
//                dialog.show();
//            } else {
//                // No explanation needed; request the permission
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
//            }
//        } else startActivity(new Intent(MainActivity.this, SigninActivity.class));
    }
}