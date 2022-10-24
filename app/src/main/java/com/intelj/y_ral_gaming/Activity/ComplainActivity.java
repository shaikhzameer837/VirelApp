package com.intelj.y_ral_gaming.Activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.databinding.ComplainBinding;

public class ComplainActivity extends AppCompatActivity {
    private ComplainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ComplainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        WebView webv = binding.webview;
        webv.getSettings().setJavaScriptEnabled(true);
        webv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.progress.setVisibility(View.VISIBLE);
        String url = "https://form.jotform.com/222868294665471?name=" + new AppConstant(this).getName() + "&phoneNumber=" + new AppConstant(this).getCountryCode() + new AppConstant(this).getPhoneNumber()  + "&userId=" + new AppConstant(this).getId();
        webv.loadUrl(url);
        binding.webview.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                binding.progress.setVisibility(View.GONE);
            }
        });
    }
}
