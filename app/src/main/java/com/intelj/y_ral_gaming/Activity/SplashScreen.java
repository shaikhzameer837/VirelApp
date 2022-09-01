package com.intelj.y_ral_gaming.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SigninActivity;

public class SplashScreen extends AppCompatActivity {
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        img = findViewById(R.id.img);
        Glide.with(this).load(R.drawable.intro)
                .placeholder(R.mipmap.app_logo).into(img);
    }


    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        }, 3500);

    }
}
