package com.intelj.y_ral_gaming;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.intelj.y_ral_gaming.Utils.AppConstant;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        WebView webView =  findViewById(R.id.webview);
        String amount = getIntent().getStringExtra("amount");
        String user_id = new AppConstant(this).getUserId();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportMultipleWindows(true); // This forces ChromeClient enabled.

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                getWindow().setTitle(title); //Set Activity tile to page title.
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        webView.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String html) {
                        Log.d("HTML", html);
                        if(html.contains("Congrats Transaction Successful")) {
                            new AlertDialog.Builder(PaymentActivity.this).setTitle("HTML").setMessage(html)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //AppController.getInstance().amount = AppController.getInstance().amount + Integer.parseInt(amount);
                                            finish();
                                        }
                                    }).setCancelable(false).create().show();
                        }

                    }
                });
        webView.loadUrl("http://y-ral-gaming.com/paytm/paytm-main/pgRedirect.php?amount="+amount+"&&user_id="+user_id);
     }

}
