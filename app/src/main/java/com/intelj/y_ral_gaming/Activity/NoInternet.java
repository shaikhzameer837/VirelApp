package com.intelj.y_ral_gaming.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.intelj.y_ral_gaming.databinding.NoInternetBinding;

public class NoInternet extends AppCompatActivity {
    private NoInternetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NoInternetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}