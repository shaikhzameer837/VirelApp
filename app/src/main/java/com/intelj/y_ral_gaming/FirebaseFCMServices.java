package com.intelj.y_ral_gaming;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONObject;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static android.content.ContentValues.TAG;

import java.util.HashMap;
import java.util.Map;

public class FirebaseFCMServices extends FirebaseMessagingService {
    //  private DatabaseHelper db;
    String subject;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Log.e("dataRecived","yes");
        if (remoteMessage.getData().size() > 0) {
            // db = new DatabaseHelper(this, "notifications");
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                subject = json.getJSONObject("data").getString("subject");
                if(subject.equals("ping")){
                    Log.e("ping","ping");
                    AppController.getInstance().getUserInfo();
                    return;
                }
                String title = json.getJSONObject("data").getString("title");
                String image = json.getJSONObject("data").getString("image");
                String message = json.getJSONObject("data").getString("payload");//.getString("title");
                if (subject.equals("notification")) {
                    showNotification(message, title, null, "");
                } else
                    getBitmapAsyncAndDoWork(message, title, image);
            } catch (Exception e) {
                Log.e("Payload", "Exception: " + e.getMessage());
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }

    }

    private void getBitmapAsyncAndDoWork(String msg, String title, String imageUrl) {

        final Bitmap[] bitmap = {null};
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        bitmap[0] = resource;
                        String url = "";
                        if (subject.equals("yt_videos"))
                           url = imageUrl.split("/")[4];
                        // TODO Do some work: pass this bitmap
                        showNotification(msg, title, bitmap[0], url);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    public void showNotification(String msg, String title, Bitmap bitImage, String url) {

        Intent intent = new Intent(this, MainActivity.class);
        if (!url.equals(""))
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + url));
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("url", url);
        myEdit.apply();
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent =PendingIntent.getActivity(this,
                    0, intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }else {
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

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (new AppConstant(FirebaseFCMServices.this).checkLogin())
            updateFcm(s);
        AppConstant.setSubscription();
    }

    public void updateFcm(String token) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/update_fcm.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tokenResponse", response);

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
                params.put("user_id", new AppConstant(FirebaseFCMServices.this).getId());
                params.put("token", token);
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
}
