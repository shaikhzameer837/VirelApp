package com.intelj.y_ral_gaming;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static com.google.firebase.messaging.Constants.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.Activity.NotificationActivity;
import com.intelj.y_ral_gaming.Activity.NotificationModel;
import com.intelj.y_ral_gaming.Activity.SearchActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.model.TournamentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AppController extends Application implements Application.ActivityLifecycleCallbacks {
    public static AppController instance;
    public String userId = "";
    AppConstant appConstant;
    DatabaseReference mDatabase;
    public ArrayList<String> timeArray = new ArrayList<>();
    public DataSnapshot mySnapShort;
    public DataSnapshot notification;
    public ArrayList<DataSnapshot> userInfoList;
    public DataSnapshot follow;
    public HashMap<String, Boolean> gameNameHashmap = new HashMap<>();
    public ProgressDialog progressDialog = null;
    public Intent mIntent;
    public String uploadUrl;
    public FirebaseRemoteConfig remoteConfig;
    public DataSnapshot dataSnapshot;
    public String is_production;
    public AlertDialog.Builder builder;
    public String subscription_package = "";
    public int amount = 0;
    public List<GameItem> movieList = new ArrayList<>();
    public GameItem gameItem;
    public TournamentModel tournamentModel;
    public HashMap<String, Integer> popularList = new HashMap<>();

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
        mDatabase.keepSynced(true);
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
                if (dataSnapshot.child(AppConstant.userName).getValue() == null) {
                    String userName = "Player" + System.currentTimeMillis();
                    mDatabase.child(AppConstant.userName).setValue("Player" + System.currentTimeMillis());
                    editor.putString(AppConstant.userName, userName).apply();
                } else
                    editor.putString(AppConstant.userName, dataSnapshot.child(AppConstant.userName).getValue(String.class)).apply();
                if (mySnapShort.child(AppConstant.bio).getValue() != null) {
                    editor.putString(AppConstant.bio, mySnapShort.child(AppConstant.bio).getValue().toString()).apply();
                }
                if (mySnapShort.child(AppConstant.title).getValue() != null) {
                    editor.putString(AppConstant.title, mySnapShort.child(AppConstant.title).getValue().toString()).apply();
                } else
                    mDatabase.child(AppConstant.pinfo).child(AppConstant.title).setValue(AppConstant.player_title[new Random().nextInt(AppConstant.player_title.length)]);
                if (!dataSnapshot.child(AppConstant.phoneNumber).getValue(String.class).startsWith("+")) {
                    HashMap<String, Object> phoneNumberUpdate = new HashMap<>();
                    phoneNumberUpdate.put(AppConstant.phoneNumber, appConstant.getCountryCode() + appConstant.getPhoneNumber());
                    mDatabase.updateChildren(phoneNumberUpdate);
                }
                notification = dataSnapshot.child(AppConstant.realTime).child(AppConstant.noti);
                if (notification.getChildrenCount() != 0) {
                    for (DataSnapshot dataSnapshots : notification.getChildren()) {
                        String subtitle = "";
                        if (dataSnapshots.child("subject").getValue(String.class).equals("follow"))
                            subtitle = "Followed you";
                        SharedPreferences notificationPref = getSharedPreferences("notificationPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = notificationPref.edit();
                        if (!notificationPref.getBoolean(dataSnapshots.getKey(), false)) {
                            showNotification(subtitle, dataSnapshots.child(AppConstant.name).getValue(String.class), null, dataSnapshots.getKey());
                            myEdit.putBoolean(dataSnapshots.getKey(), true);
                            myEdit.apply();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void showNotification(String msg, String title, Bitmap bitImage, String url) {
        Intent intent = new Intent(AppController.this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                676, intent, PendingIntent.FLAG_ONE_SHOT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH; //Important for heads-up notification
            NotificationChannel channel = new NotificationChannel("1", "name", importance);
            channel.setDescription(msg);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.common_google_signin_btn_text_light_focused)
                .setContentTitle(title)
                .setContentText(msg)
                .setContentIntent(pendingIntent)

                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE) //Important for heads-up notification
                .setPriority(Notification.PRIORITY_MAX);
        if (bitImage != null) {
            mBuilder.setLargeIcon(bitImage);
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                    .bigPicture(bitImage));
        }
        Notification buildNotification = mBuilder.build();
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify((int) (System.currentTimeMillis() / 1000), buildNotification);
    }

    public void startToRunActivity() {
        Intent intent = new Intent(this, MainActivity.class);
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
