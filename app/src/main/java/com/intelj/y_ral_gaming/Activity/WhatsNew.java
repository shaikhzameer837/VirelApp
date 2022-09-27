package com.intelj.y_ral_gaming.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.intelj.y_ral_gaming.databinding.WhatsNewBinding;

public class WhatsNew extends AppCompatActivity {
    WhatsNewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WhatsNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
