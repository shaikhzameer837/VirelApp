package com.intelj.y_ral_gaming;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.model.TournamentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AppController extends Application implements Application.ActivityLifecycleCallbacks {
    public static AppController instance;
    public String userId = "";
    AppConstant appConstant;
    DatabaseReference mDatabase;
    public ArrayList<String> timeArray = new ArrayList<>();
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
    public String subscription_package = "";
    public int amount = 0;
    public List<GameItem> movieList = new ArrayList<>();
    public GameItem gameItem;
    public TournamentModel tournamentModel;
    public HashMap<String,Integer> popularList = new HashMap<>();
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
            userId = appConstant.getId();
            Log.e("userIdx", userId);
            getUserInfo();
        }
    }

    int x = 0;

    private void getUserInfo() {
        x = 0;
        Log.e("userId1s", userId);
        mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mySnapShort = dataSnapshot.child(AppConstant.pinfo);
                userInfoList = new ArrayList<>();
                if (!dataSnapshot.exists()) {
                    new AppConstant(AppController.this).logout();
                    return;
                }
                if (!dataSnapshot.child(AppConstant.realTime).child(AppConstant.deviceId).getValue(String.class).equals(Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID))) {
                    Log.e("statusLog", "Logout");
                    new AppConstant(AppController.this).logout();
                    return;
                }
                SharedPreferences sharedPreferences = getSharedPreferences(userId, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (dataSnapshot.child(AppConstant.userName).getValue() == null){
                    String userName = "Player"+System.currentTimeMillis();
                    mDatabase.child(AppConstant.userName).setValue("Player"+System.currentTimeMillis());
                    editor.putString(AppConstant.userName, userName).apply();
                }else
                    editor.putString(AppConstant.userName, dataSnapshot.child(AppConstant.userName).getValue(String.class)).apply();
                if (mySnapShort.child(AppConstant.bio).getValue() != null) {
                    editor.putString(AppConstant.bio, mySnapShort.child(AppConstant.bio).getValue().toString()).apply();
                }
                if (mySnapShort.child(AppConstant.title).getValue() != null) {
                    editor.putString(AppConstant.title, mySnapShort.child(AppConstant.title).getValue().toString()).apply();
                }else
                    mDatabase.child(AppConstant.pinfo).child(AppConstant.title).setValue(AppConstant.player_title[new Random().nextInt(AppConstant.player_title.length)]);
                if (!dataSnapshot.child(AppConstant.phoneNumber).getValue(String.class).startsWith("+")) {
                    HashMap<String, Object> phoneNumberUpdate = new HashMap<>();
                    phoneNumberUpdate.put(AppConstant.phoneNumber, appConstant.getCountryCode() + appConstant.getPhoneNumber());
                    mDatabase.updateChildren(phoneNumberUpdate);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


    public void startToRunActivity() {
        Intent intent = new Intent(this, MainActivity.class);
//        if (!userId.isEmpty() && !new AppConstant(this).getFriendCheck())
//            intent = new Intent(this, SearchActivity.class);//UserInfoCheck.class);
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
            }

        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
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
