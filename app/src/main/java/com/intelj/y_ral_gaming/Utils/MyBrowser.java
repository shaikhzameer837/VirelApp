package com.intelj.y_ral_gaming.Utils;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyBrowser extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(url.startsWith("!http://redirect_to_app") ) {
            view.loadUrl(url);
        }else{
            String[] urlSplit = url.split("&&");
            Log.e("WebView",url);
        }
        return true;
    }
    @Override
    public void onLoadResource(WebView  view, String  url){

    }
}