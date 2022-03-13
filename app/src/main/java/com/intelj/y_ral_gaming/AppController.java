package com.intelj.y_ral_gaming;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppController extends Application implements Application.ActivityLifecycleCallbacks {
    public static AppController instance;
    public String userId = "";
    public String channelId = "O-1601351657084";
    AppConstant appConstant;
    DatabaseReference mDatabase;
    public ArrayList<String> timeArray = new ArrayList<>();
    public DataSnapshot ytdataSnapshot;
    public DataSnapshot mySnapShort;
    public ArrayList<DataSnapshot> userInfoList;
    public HashMap<String, Boolean> gameNameHashmap = new HashMap<>();
    public ProgressDialog progressDialog = null;
    public Intent mIntent;
    public String uploadUrl;
    public FirebaseRemoteConfig remoteConfig;
    public DataSnapshot dataSnapshot;
    public String is_production;
    AlertDialog.Builder builder;
    public boolean isFirstTime = false;
    public String subscription_package = "";
    public int amount = 0;
    public List<GameItem> movieList = new ArrayList<>();
    public GameItem gameItem;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.fetchAndActivate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        getReadyForCheckin();
        getGameName();
        getTournamentTime();
        is_production = remoteConfig.getString("is_production");
    }

    public String getSubscription_package() {
        return subscription_package.equals("") ? new AppConstant(this).getDataFromShared(AppConstant.package_info, "") : subscription_package;
    }

    public void getReadyForCheckin() {
        appConstant = new AppConstant(this);
        if (new AppConstant(this).checkLogin()) {
            userId = appConstant.getUserId();
            Log.e("userId",userId);
            getUserInfo();
        }
    }

    int x = 0;

    private void getUserInfo() {
        x = 0;
        Log.e("userId1",userId);
        mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mySnapShort = dataSnapshot.child(AppConstant.pinfo);
                userInfoList = new ArrayList<>();
                if(!dataSnapshot.exists()) {
                    new AppConstant(AppController.this).logout();
                    return;
                }
                if (!dataSnapshot.child(AppConstant.realTime).child(AppConstant.deviceId).getValue(String.class).equals(Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID))) {
                    Log.e("statusLog","Logout");
                    new AppConstant(AppController.this).logout();
                    return;
                }
//                for (DataSnapshot child : mySnapShort.child(AppConstant.team).getChildren()) {
//                    DocumentReference docRef = FirebaseFirestore.getInstance().collection(AppConstant.team)
//                            .document(child.getKey() + "");
//                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            x++;
//                            if (task.isSuccessful()) {
//                                DocumentSnapshot document = task.getResult();
//                                if (document.exists()) {
//                                    ArrayList<String> list = (ArrayList<String>) document.get(AppConstant.teamMember);
//                                    Set<String> hashSet = new HashSet<>(list);
//                                    SharedPreferences sharedpreferences = getSharedPreferences(child.getKey(), Context.MODE_PRIVATE);
//                                    SharedPreferences.Editor editors = sharedpreferences.edit();
//                                    editors.putString(AppConstant.teamName, document.getString(AppConstant.teamName));
//                                    editors.putStringSet(AppConstant.teamMember, hashSet);
//                                    editors.putString(AppConstant.myPicUrl, document.getString(AppConstant.myPicUrl) + "");
//                                    editors.apply();
//                                }
//                            }
//
//                        }
//                    });
//                }

                if (progressDialog != null) {
                    progressDialog.cancel();
                    progressDialog = null;
                 //   startToRunActivity();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


    public void startToRunActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        if (!userId.isEmpty() && !new AppConstant(this).getFriendCheck())
            intent = new Intent(this, MainActivity.class);//UserInfoCheck.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void getGameName() {
        gameNameHashmap.clear();
        String game_name = remoteConfig.getString(AppConstant.gameStreaming).equals("") ? new AppConstant(this).getDataFromShared(AppConstant.gameStreaming, "") : remoteConfig.getString(AppConstant.gameStreaming);
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
//            if (gameViewpager != null)
//                setupViewPager(gameViewpager);

        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

//    public void setupViewPager(ViewPager viewPager) {
//        ArrayList<String> titleList = new ArrayList<>();
//        ViewPagerAdapter adapter = new ViewPagerAdapter(supportFragmentManager);
//        if (gameNameHashmap.size() > 0) {
//            for (Map.Entry<String, Boolean> entry : gameNameHashmap.entrySet()) {
//                adapter.addFragment(new OneFragment(entry.getKey(), entry.getValue()), entry.getKey());
//                titleList.add(entry.getKey());
//                // do something with key and/or tab
//            }
//            viewPager.setAdapter(adapter);
//            Intent intent = new Intent("custom-event-name");
//            intent.putExtra(AppConstant.title, titleList.get(0));
//            LocalBroadcastManager.getInstance(instance).sendBroadcast(intent);
//        }
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            public void onPageScrollStateChanged(int state) {}
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
//
//            public void onPageSelected(int position) {
//                Intent intent = new Intent("custom-event-name");
//                intent.putExtra(AppConstant.title, titleList.get(position));
//                LocalBroadcastManager.getInstance(instance).sendBroadcast(intent);
//            }
//        });
//    }

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

//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }

    public void getTournamentTime() {
        timeArray.clear();
        String game_name = remoteConfig.getString(AppConstant.game_slot).equals("") ? new AppConstant(this).getDataFromShared(AppConstant.game_slot, "") : remoteConfig.getString(AppConstant.game_slot);
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
        builder.setMessage("Please turn on the automatic time from setting").setTitle("Automatic time");

        //Setting message manually and performing action on button click
        builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "you choose yes action for alertbox",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "you choose no action for alertbox",
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
