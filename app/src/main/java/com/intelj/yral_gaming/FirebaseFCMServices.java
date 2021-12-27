



package com.intelj.yral_gaming;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.intelj.yral_gaming.Utils.AppConstant;

import org.json.JSONObject;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static android.content.ContentValues.TAG;

public class FirebaseFCMServices extends FirebaseMessagingService {
    private DatabaseHelper db;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       // Log.e("dataRecived","yes");
         if (remoteMessage.getData().size() > 0) {
             db = new DatabaseHelper(this, "notifications");
             try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                String title = json.getJSONObject("data").getString("title");
                String subject = json.getJSONObject("data").getString("subject");
                String image = json.getJSONObject("data").getString("image");
                String message = json.getJSONObject("data").getString("payload");//.getString("title");
                 getBitmapAsyncAndDoWork(message,title,image);
                if (subject.equals("match_result")){
                    String youtube_id = json.getJSONObject("data").getString("youtube_id");
                    String winner_id = json.getJSONObject("data").getString("winner_id");
                    String game_name = json.getJSONObject("data").getString("game_name");
                    long id = db.insertNote(title, youtube_id,winner_id,game_name);
                   // db.getNote(id);
                    Log.e("Payload", "Data Payload: " +youtube_id);
                }
            } catch (Exception e) {
                Log.e("Payload", "Exception: " + e.getMessage());
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }

      //  showNotification();
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: userMsg " + remoteMessage.getFrom());

        // Check if message cont    ains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
      // getBitmapAsyncAndDoWork( remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getImageUrl().toString());

        getBitmapAsyncAndDoWork( remoteMessage.getData().get("payload"),remoteMessage.getData().get("title"),remoteMessage.getData().get("image"));
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void getBitmapAsyncAndDoWork(String msg,String title,String imageUrl) {

        final Bitmap[] bitmap = {null};

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        bitmap[0] = resource;
                        String url = imageUrl.split("/")[4];
                        // TODO Do some work: pass this bitmap
                        showNotification(msg,title,bitmap[0],url);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
    public void showNotification (String msg,String title,Bitmap bitImage,String url){
       // Intent intent = new Intent(this,LoadYoutube.class);
       // intent.putExtra("youtubeUrl","https://www.youtube.com/watch?v=" + url);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + url));
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName,MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("url", url);
        myEdit.apply();
        //Intent webIntent = new Intent(Intent.ACTION_VIEW,
         //       Uri.parse("http://www.youtube.com/watch?v=" + id));

        PendingIntent pendingIntent =PendingIntent.getActivity(this,
                676,intent,PendingIntent.FLAG_ONE_SHOT);
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
                .setLargeIcon(bitImage)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitImage))
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE) //Important for heads-up notification
                .setPriority(Notification.PRIORITY_MAX);
        Notification buildNotification = mBuilder.build();
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify((int)(System.currentTimeMillis()/1000), buildNotification);

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel notificationChannel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_HIGH);
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(notificationChannel);
//        }
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"n")
//                .setContentText("yes dude")
//                .setSmallIcon(R.drawable.common_google_signin_btn_text_light_focused)
//                .setAutoCancel(true)
//                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentText("this is msg");
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//        notificationManagerCompat.notify(23432,builder.build());
    }
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseMessaging.getInstance().subscribeToTopic("push_yt").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SUBSCRIBED","SUCCESS");
            }
        });
    }
}
