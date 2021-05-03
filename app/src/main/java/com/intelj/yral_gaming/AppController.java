package com.intelj.yral_gaming;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

public class AppController extends Application implements Application.ActivityLifecycleCallbacks{
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
    public FirebaseRemoteConfig mFirebaseRemoteConfig;
    AlertDialog.Builder builder;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config);
//        mFirebaseRemoteConfig.fetchAndActivate()
//                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Boolean> task) {
//                        if (task.isSuccessful()) {
//                            boolean updated = task.getResult();
//                            Log.d( "Config params updated: " , updated + "");
//                            /*Toast.makeText(SplashScreen.this, "Fetch and activate succeeded",
//                                    Toast.LENGTH_SHORT).show();*/
//
//                        } else {
//                            Log.d( "Config params updated: " , "FAiled");
//                            /*Toast.makeText(SplashScreen.this, "Fetch failed",
//                                    Toast.LENGTH_SHORT).show();*/
//                        }
//                       // Pre_registartion_activity = mFirebaseRemoteConfig.getString("Pre_registration");
//                    }
//                });

        getReadyForCheckin();
//        getGameName();
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
                    Intent intent = null;
                    if (SplashScreen.Pre_registartion_activity.equalsIgnoreCase("yes")) {
                        intent = new Intent(AppController.this, PreRegistartionActivity.class);
                    } else
                        intent = new Intent(AppController.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
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

    public void getGameName() {
        gameNameHashmap.clear();
        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.fetchAndActivate();
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

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

        if (!AppConstant.isTimeAutomatic(getApplicationContext())) {
            dialogforautomatictime();
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

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
        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.fetchAndActivate();
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

    public void dialogforautomatictime() {
        builder = new AlertDialog.Builder(this);
        builder.setMessage("Please turn on the automatic time from setting") .setTitle("Automatic time");

        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("AlertDialogExample");
        alert.show();
    }
}
