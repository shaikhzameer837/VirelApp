package com.intelj.y_ral_gaming;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.Activity.NotificationActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.Utils;
import com.intelj.y_ral_gaming.db.AppDataBase;
import com.intelj.y_ral_gaming.db.Chat;
import com.intelj.y_ral_gaming.db.VideoList;
import com.intelj.y_ral_gaming.model.TournamentModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AppController extends Application implements Application.ActivityLifecycleCallbacks {
    public static AppController instance;
    public String userId = "";
    AppConstant appConstant;
    DatabaseReference mDatabase;
    public DataSnapshot notification;
    public DataSnapshot follow;
    public ProgressDialog progressDialog = null;
    public Intent mIntent;
    public String uploadUrl;
    public DataSnapshot dataSnapshot;
    public AlertDialog.Builder builder;
    public int amount = 0;
    public int rank = 0;
    public String referral = "0";
    public List<GameItem> movieList = new ArrayList<>();
    public TournamentModel tournamentModel;
    AppDataBase appDataBase;
    public HashMap<String, Integer> popularList = new HashMap<>();
    public AppDataBase videoDataBase;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
        getReadyForCheckin();
        getVideo();
    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        AppController app = (AppController) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(Utils.getVideoCacheDir(AppController.this))
                .build();
    }

    public void getReadyForCheckin() {
        appConstant = new AppConstant(this);
        if (new AppConstant(this).checkLogin()) {
            userId = appConstant.getId();
            Log.e("userIdx", userId);
            getUserInfo();
        }
    }

    public void getUserInfo() {
        mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users)
                .child(userId).child(AppConstant.realTime);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.e("userIdx", task.getResult());
                        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userId).child(AppConstant.token).setValue(token);
                    }
                });


        DocumentReference docRef = FirebaseFirestore.getInstance().collection(AppConstant.realTime)
                .document(appConstant.getId());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> ds = (Map<String, Object>) documentSnapshot.get(AppConstant.noti);
                for (Object x: ds.values()) {
                    for (Object value: ((Map<String, Object>)x).values()){
                        System.out.println("values");
                        System.out.println(value);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child(AppConstant.deviceId).getValue(String.class).equals(Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID))) {
                    Log.e("statusLog", "Logout");
                    new AppConstant(AppController.this).logout();
                    return;
                }
                notification = dataSnapshot.child(AppConstant.noti);
                if (notification.getChildrenCount() != 0) {
                    for (DataSnapshot dataSnapshots : notification.getChildren()) {
                        String subtitle = "";
                        if (dataSnapshots.child("subject").getValue(String.class).equals("follow"))
                            subtitle = "Followed you";
                        SharedPreferences notificationPref = getSharedPreferences("notificationPref", MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = notificationPref.edit();
                        if (!notificationPref.getBoolean(dataSnapshots.getKey(), false)) {
                            Intent intent = new Intent(AppController.this, NotificationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            String finalSubtitle = subtitle;
                            Glide.with(getApplicationContext())
                                    .asBitmap()
                                    .load(AppConstant.AppUrl + "images/" + dataSnapshots.getKey() + ".png?u=" + AppConstant.imageExt())
                                    .into(new CustomTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                            showNotification(intent, finalSubtitle, dataSnapshots.child(AppConstant.name).getValue(String.class), resource, dataSnapshots.child("owner").getValue(String.class));
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                                    R.drawable.game_avatar);
                                            showNotification(intent, finalSubtitle, dataSnapshots.child(AppConstant.name).getValue(String.class), icon, dataSnapshots.child("owner").getValue(String.class));
                                        }
                                    });
                            myEdit.putBoolean(dataSnapshots.getKey(), true);
                            myEdit.apply();
                        }
                    }
                }
                DataSnapshot msgData = dataSnapshot.child(AppConstant.msg);
                if (msgData.getChildrenCount() != 0) {
                    for (DataSnapshot dataSnapshots : msgData.getChildren()) {
                        Intent intent = new Intent(AppController.this, ChatActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        appDataBase = AppDataBase.getDBInstance(AppController.this, dataSnapshots.child("owner").getValue() + "_chats");
                        intent.putExtra(AppConstant.phoneNumber, dataSnapshots.child(AppConstant.phoneNumber).getValue(String.class));
                        intent.putExtra(AppConstant.id, dataSnapshots.child("owner").getValue(String.class));
                        Glide.with(getApplicationContext())
                                .asBitmap()
                                .load(AppConstant.AppUrl + "images/" + dataSnapshots.child("owner").getValue(String.class) + ".png?u=" + AppConstant.imageExt())
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        showNotification(intent, dataSnapshots.child("messages").getValue(String.class), dataSnapshots.child(AppConstant.phoneNumber).getValue(String.class), resource, dataSnapshots.child("owner").getValue(String.class));
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                                R.drawable.game_avatar);
                                        showNotification(intent, dataSnapshots.child("messages").getValue(String.class), dataSnapshots.child(AppConstant.phoneNumber).getValue(String.class), icon, dataSnapshots.child("owner").getValue(String.class));
                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        super.onLoadFailed(errorDrawable);
                                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                                R.drawable.game_avatar);
                                        showNotification(intent, dataSnapshots.child("messages").getValue(String.class), dataSnapshots.child(AppConstant.phoneNumber).getValue(String.class), icon, dataSnapshots.child("owner").getValue(String.class));
                                    }
                                });
                        appDataBase.chatDao().insertUser(dataSnapshots.getValue(Chat.class));
                    }
                    Intent intent = new Intent("chat");
                    LocalBroadcastManager.getInstance(AppController.this).sendBroadcast(intent);
                    FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userId).child(AppConstant.realTime).child(AppConstant.msg).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void showNotification(Intent intent, String msg, String title, Bitmap bitImage, String owner) {

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        } else {
            pendingIntent = PendingIntent.getActivity(this,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }
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
                .setSmallIcon(R.drawable.ic_stat_logo)
                .setContentTitle(title)
                .setContentText(msg)
                .setLargeIcon(bitImage)
                .setContentIntent(pendingIntent)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE) //Important for heads-up notification
                .setPriority(Notification.PRIORITY_MAX);

        Notification buildNotification = mBuilder.build();
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(Integer.parseInt(owner.replaceAll("[\\D]", "")), buildNotification);
    }

    public void startToRunActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
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

    public void getVideo() {
        videoDataBase = AppDataBase.getDBInstance(AppController.this, AppConstant.AppName);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "get_video.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick3", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                //appDataBase.videoDao().insertUser();
                                JSONArray jsonArr = json.getJSONArray("value");
                                for (int x = 0; x < jsonArr.length(); x++) {
                                    JSONObject jsonObj = (JSONObject) jsonArr.get(x);
                                    Log.e("onResponse: ", jsonObj.getString("uid"));
                                   if (videoDataBase.videosDao().getLastVideo(jsonObj.getString("uid")).size() == 0)
                                       videoDataBase.videosDao().insertVideo(new VideoList(jsonObj.getString("uid"), jsonObj.getString("owner"), jsonObj.getString("time")));
                                }
                            }
                        } catch (Exception e) {
                            Log.e("logMess", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", new AppConstant(AppController.this).getId());
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
