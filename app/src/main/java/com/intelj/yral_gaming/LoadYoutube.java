package com.intelj.yral_gaming;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LoadYoutube extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intents = getIntent();
        String youtubeUrl = intents.getStringExtra("youtubeUrl");
        Intent intent = new Intent(this,LoadYoutube.class);
        intent.setData(Uri.parse(youtubeUrl));
        intent.setPackage("com.google.android.youtube");
        startActivity(intent);
    }
}
