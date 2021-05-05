package com.intelj.yral_gaming;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.intelj.yral_gaming.Utils.AppConstant;

public class ComingSoon extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coming_soon);
        Glide.with(this).load(R.drawable.coming_soon).circleCrop().into((ImageView)findViewById(R.id.img));
    }
}
