package com.intelj.y_ral_gaming.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.BuildConfig;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SigninActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;

public class SplashScreen extends AppCompatActivity {
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        img = findViewById(R.id.img);
        Glide.with(this).load(R.drawable.intro)
                .placeholder(R.mipmap.app_logo).into(img);
        Log.e("useriD", new AppConstant(this).getId());
    }


    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String versionCode = BuildConfig.VERSION_CODE + "";
                Intent intent = new Intent(SplashScreen.this,
                        AppController.getInstance().videoDataBase.videosDao().getAllVideo().size() != 0 ? MainActivity.class
                                : ViralWeb.class);
                if (new AppConstant(SplashScreen.this).getDataFromShared(versionCode, "0").equals("0")) {
                    intent = new Intent(SplashScreen.this, WhatsNew.class);
                }
                intent.putExtra("isFirstTime", new AppConstant(SplashScreen.this).checkLogin());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }, 3500);
    }
}
