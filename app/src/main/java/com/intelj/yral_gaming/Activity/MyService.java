package com.intelj.yral_gaming.Activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

     @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         startMyOwnForeground();

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        isForeground();
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000);
         return START_STICKY;
    }
    public  void isForeground(){
         getForegroundApp();
    }
    private void getForegroundApp() {

        getProcessName();
    }
    private void getProcessName() {
             String topPackageName;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService("usagestats");
                long currentTime = System.currentTimeMillis();
                // get usage stats for the last 10 seconds
                List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 3600000, currentTime);
                // search for app with most recent last used time
                if (stats != null) {
                    long lastUsedAppTime = 0;
                    for (UsageStats usageStats : stats) {
                        if (usageStats.getLastTimeUsed() > lastUsedAppTime) {
                            topPackageName = usageStats.getPackageName();
                            lastUsedAppTime = usageStats.getLastTimeUsed();
                            Log.e("topPackageName",topPackageName);
                        }
                    }
                }
            }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MYSERVICE STATUS: "," ONCREATED");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Anti - cheat is running")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
    }

    @Override
    public void onDestroy() {
        Log.d("MYSERVICE STATUS: "," ONDESTROYED");
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("MYSERVICE STATUS: "," onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }
}
