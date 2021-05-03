


package com.intelj.yral_gaming;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.intelj.yral_gaming.Activity.MainActivity;
import com.intelj.yral_gaming.Activity.PreRegistartionActivity;
import com.intelj.yral_gaming.Activity.SplashScreen;
import com.intelj.yral_gaming.Fragment.OneFragment;
import com.intelj.yral_gaming.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class AppController extends Application {
    public static AppController instance;
    public String userId = "";
    public String channelId = "O-1601351657084";
    public FragmentManager supportFragmentManager;
    public ViewPager gameViewpager;
    AppConstant appConstant;
    DatabaseReference mDatabase;
    public ArrayList<String> timeArray = new ArrayList<>();
    public HashMap<String, String> pakageInfo = new HashMap<>();
    public DataSnapshot ytdataSnapshot;
    public DataSnapshot mySnapShort;
    public ArrayList<DataSnapshot> userInfoList;
    public HashMap<String, Boolean> gameNameHashmap = new HashMap<>();
    public ProgressDialog progressDialog = null;
    public Intent mIntent;
    public String uploadUrl;
    public FirebaseRemoteConfig remoteConfig;
  //  public FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.fetchAndActivate();
        getReadyForCheckin();
        getGameName();
        getTournamentTime();
    }

    public void getReadyForCheckin() {
        appConstant = new AppConstant(this);
        if (new AppConstant(this).checkLogin()) {
            userId = appConstant.getUserId();
            getUserInfo();
        }
    }

    private void getUserInfo() {
        mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userId).child(AppConstant.pinfo);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mySnapShort = dataSnapshot;
                userInfoList = new ArrayList<>();
                if (progressDialog != null) {
                    progressDialog.cancel();
                    progressDialog = null;
                   startToRunActivity();
                }
//                for (DataSnapshot postSnapshot : dataSnapshot.child(AppConstant.myTeam).getChildren()) {
//                    FirebaseDatabase.getInstance().getReference(AppConstant.users).child(postSnapshot.getKey() + "")
//                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot childDataSnapshot) {
//                                    userInfoList.add(childDataSnapshot);
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError error) {
//
//                                }
//                            });
//                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void startToRunActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        if (remoteConfig.getString(AppConstant.pre_registration).equalsIgnoreCase("yes")) {
            intent = new Intent(this, PreRegistartionActivity.class);
        }
        startActivity(intent);
    }

    public void getGameName() {
        gameNameHashmap.clear();

        String game_name = remoteConfig.getString("gameStreaming");
        Log.e("game_name_cont", game_name);
        try {
            JSONObject jsonObject = new JSONObject(game_name);
            JSONArray keys = jsonObject.names();
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.getString(i); // Here's your key
                boolean value = jsonObject.getBoolean(key);
                gameNameHashmap.put(key, value);
                /*if (value)
                    gameNameArray.add(key);*/

            }
            if (gameViewpager != null)
                setupViewPager(gameViewpager);

        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }





        /*mDatabase = FirebaseDatabase.getInstance().getReference("gameStreaming");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gameNameArray.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if ((boolean) postSnapshot.getValue())
                            gameNameArray.add(postSnapshot.getKey());
                    }
                }
                if (gameViewpager != null)
                    setupViewPager(gameViewpager);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });*/
    }

    public void setupViewPager(ViewPager viewPager) {

        if (gameNameHashmap.size() > 0) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(supportFragmentManager);

            for (Map.Entry<String, Boolean> entry : gameNameHashmap.entrySet()) {
                adapter.addFragment(new OneFragment(entry.getKey(), entry.getValue()), entry.getKey());
                // do something with key and/or tab
            }
            viewPager.setAdapter(adapter);
        }
        /*if (gameNameArray.size() > 0) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(supportFragmentManager);
            for (String s : gameNameArray) {
                adapter.addFragment(new OneFragment(s), s);
            }
            viewPager.setAdapter(adapter);
        }*/
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void getTournamentTime() {
        timeArray.clear();
        String game_name = remoteConfig.getString("game_slot");
        Log.e("game_name_cont", game_name);
        try {
            JSONObject jsonObject = new JSONObject(game_name);
            JSONArray keys = jsonObject.names();
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.getString(i); // Here's your key
                boolean value = jsonObject.getBoolean(key);
                if (value)
                    timeArray.add(key);
            }

        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
/*      mDatabase = FirebaseDatabase.getInstance().getReference("gameSlot");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                timeArray.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if ((boolean) postSnapshot.getValue())
                            timeArray.add(postSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });*/
    }

    public static AppController getInstance() {
        return instance;
    }
}
