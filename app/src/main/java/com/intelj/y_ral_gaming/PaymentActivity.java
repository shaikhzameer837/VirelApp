package com.intelj.y_ral_gaming;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        WebView webView = findViewById(R.id.webview);
        String amount = getIntent().getStringExtra("amount");
        String user_id = new AppConstant(this).getUserId();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportMultipleWindows(true); // This forces ChromeClient enabled.

        webView.setWebChromeClient(new WebChromeClient() {
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
                        if (html.contains("Congrats Transaction Successful")) {
                            new AlertDialog.Builder(PaymentActivity.this).setTitle("HTML").setMessage(html)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            AppController.getInstance().amount = AppController.getInstance().amount + Integer.parseInt(amount);
                                            finish();
                                        }
                                    }).setCancelable(false).create().show();
                        }

                    }
                });
        webView.loadUrl("http://y-ral-gaming.com/paytm/paytm-main/pgRedirect.php?amount=" + amount + "&&user_id=" + user_id);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        reloadData();
    }

    String key;

    private void reloadData() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("loading...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/load_free_matches.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick3", response);
                        progressDialog.cancel();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success") && !json.has("msg")) {
                                AppController.getInstance().amount = Integer.parseInt(json.getString("amount"));
                                JSONArray ja_data = json.getJSONArray("match_info");
                                AppController.getInstance().movieList.clear();
                                for (int i = 0; i < ja_data.length(); i++) {
                                    JSONObject jObj = ja_data.getJSONObject(i);
//                                    AppController.getInstance().movieList.add(new GameItem("BGMI match " + (1 + i),
//                                            jObj.getString("status"), jObj.getString("perKill"), jObj.getString("entryFees"),
//                                            jObj.getString("type"), jObj.getString("map"),
//                                            jObj.getString("time"), jObj.getString("isExist")
//                                            , jObj.getString("total"), jObj.getString("id").equals("") ? "****" : jObj.getString("id")
//                                            , jObj.getString("password").equals("") ? "****" : jObj.getString("password"),
//                                            jObj.getString("result_url"), jObj.getInt("max"), jObj.getString("yt_url")));
                                }
                                Intent intent = new Intent("custom-event-name");
                                LocalBroadcastManager.getInstance(PaymentActivity.this).sendBroadcast(intent);
                                finish();
                            } else {
                                key = "OK";
                                if (json.getInt("key") == 1)
                                    key = "Click Download new version";
                                new AlertDialog.Builder(PaymentActivity.this)
                                        .setTitle("Alert")
                                        .setCancelable(false)
                                        .setMessage(json.getString("msg"))
                                        .setPositiveButton(key, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if (key.equals("Click Download new version")) {
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://y-ral-gaming.com/scrims.apk"));
                                                    startActivity(browserIntent);
                                                } else {
                                                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                                                    startMain.addCategory(Intent.CATEGORY_HOME);
                                                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(startMain);
                                                }
                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                                startMain.addCategory(Intent.CATEGORY_HOME);
                                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(startMain);
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        } catch (Exception e) {
                            Log.e("logMess", e.getMessage());

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", new AppConstant(PaymentActivity.this).getUserId());
                int versionCode = BuildConfig.VERSION_CODE;
                params.put("version", versionCode + "");
                params.put("match_id", date);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
