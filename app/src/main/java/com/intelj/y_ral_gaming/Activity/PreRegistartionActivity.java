package com.intelj.y_ral_gaming.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.intelj.y_ral_gaming.R;

public class PreRegistartionActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_registartion);
        textView = findViewById(R.id.tvPre_Registration);
        textView.setText("Thank you for pre registration. Very soon you will be able to play the tournament.");
    }
}