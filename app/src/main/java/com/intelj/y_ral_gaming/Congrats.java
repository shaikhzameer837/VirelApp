package com.intelj.y_ral_gaming;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.Activity.SplashScreen;

public class Congrats extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.congrats);


    }


    public void nextPage(View view) {
        Intent intent = new Intent(Congrats.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
