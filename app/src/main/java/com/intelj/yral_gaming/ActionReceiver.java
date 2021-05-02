package com.intelj.yral_gaming;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.intelj.yral_gaming.Activity.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class ActionReceiver extends BroadcastReceiver {
    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();
        mContext = context;
        String action=intent.getStringExtra("action");
        if(action.equals("action1")){
            performAction1();
        }
//        else if(action.equals("action2")){
//            performAction2();
//
//        }
        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void performAction1(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ScreenshotManager.INSTANCE.takeScreenshot(mContext);
            }
        }, 3000);
    }


}
