package com.intelj.y_ral_gaming.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.intelj.y_ral_gaming.Activity.EventInfo;
import com.intelj.y_ral_gaming.Activity.GameInfo;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.RoundedBottomSheetDialog;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.MyBrowser;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import soup.neumorphism.NeumorphCardView;

public class HomeFragment extends Fragment {
    String title;
    AppConstant appConstant;
    String gameId = "";
    String webUrl = "";

    public HomeFragment() {
        // Required empty public constructor
    }


    public HomeFragment(String title, String webUrl) {
        this.title = title;
        this.webUrl = webUrl;
    }


    public class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("!http://redirect_to_app")) {
                view.loadUrl(url);
            } else {
                Log.e("WebViewRes", url);
                if (appConstant.checkLogin()) {
                    String[] urlSplit = url.split("/");
                    gameId = urlSplit[4];
                    if (urlSplit[3].equals("giveaway")) {
                        showRoundedBottomSheetDialog();
                    }
                    if (urlSplit[3].equals("events")) {
                        Intent intent = new Intent(getActivity(), EventInfo.class);
                        intent.putExtra("eId", urlSplit[4]);
                        intent.putExtra("name", urlSplit[5]);
                        intent.putExtra("gameName", urlSplit[6]);
                        intent.putExtra("isRegistered", urlSplit[7]);
                        intent.putExtra("max", urlSplit[8]);
                        intent.putExtra("status", urlSplit[9]);
                        //String transitionName = "fade";
//                        View transitionView = view.findViewById(R.id.images);
//                        ViewCompat.setTransitionName(transitionView, transitionName);
                        //  ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), transitionView, transitionName);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent("custom-event-name");
                    intent.putExtra(AppConstant.AppName, true);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                }
            }
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;
    WebView browser;
    BottomSheetDialog bottomSheetDialog;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.rules, container, false);
        appConstant = new AppConstant(getContext());
        browser = rootView.findViewById(R.id.webview);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        browser.loadUrl(webUrl + "?n=" + title + "&&d=" + date + "&u=" + appConstant.getId());
        Log.e("appConstant", webUrl + "?n=" + title + "&&d=" + date + "&u=" + appConstant.getId());
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
//        browser.setScrollbarFadingEnabled(true);
//        browser.setVerticalScrollBarEnabled(true);
//        browser.setHorizontalScrollBarEnabled(true);
//        browser.getSettings().setSupportZoom(true);
//        browser.getSettings().setBuiltInZoomControls(true);
//        browser.getSettings().setDisplayZoomControls(false);
//        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        browser.getSettings().setLoadWithOverviewMode(true);
//        browser.getSettings().setUseWideViewPort(true);
        browser.setWebViewClient(new MyBrowser());
        browser.setInitialScale(0);
        browser.setVerticalScrollBarEnabled(false);
        browser.setHorizontalScrollBarEnabled(false);
        browser.setScrollbarFadingEnabled(false);
        browser.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                rootView.findViewById(R.id.pBar3).setVisibility(View.VISIBLE);
                if (progress == 100)
                    rootView.findViewById(R.id.pBar3).setVisibility(View.GONE);
            }
        });
        return rootView;
    }

    private void showRoundedBottomSheetDialog() {
        bottomSheetDialog = new RoundedBottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.register_match);
        NeumorphCardView btn_next = bottomSheetDialog.findViewById(R.id.btn_next);
        EditText gameName = bottomSheetDialog.findViewById(R.id.ingameName);
        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.AppName, 0);
        gameName.setHint("Enter your " + title + " player 1 in game name");
        gameName.setText(prefs.getString("GameName", ""));
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameName.getText().toString().trim().equals("")) {
                    gameName.requestFocus();
                    gameName.setError("This cannot be empty");
                    return;
                }
                registerForMatch(gameName.getText().toString());

            }
        });
        bottomSheetDialog.show();
    }

    private void registerForMatch(String inGameName) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("loading...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppConstant.AppUrl + "join_game.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick3", response);
                        progressDialog.cancel();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                appConstant.savePackage(appConstant.getId(), "");
                                bottomSheetDialog.dismiss();
                                browser.reload();
                                GameInfo BottomSheetFragment = new GameInfo();
                                BottomSheetFragment.show((getActivity()).getSupportFragmentManager(), "");
                            } else
                                Toast.makeText(getActivity(), json.getString("msg"), Toast.LENGTH_LONG).show();
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
                params.put("inGameName", inGameName);
                params.put("gameId", gameId);
                params.put("userId", appConstant.getId());
                if (!appConstant.getReferal().equals(""))
                    params.put("referral", appConstant.getReferal());
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