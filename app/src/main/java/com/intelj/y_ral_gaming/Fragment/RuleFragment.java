package com.intelj.y_ral_gaming.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.intelj.y_ral_gaming.Adapter.TeamDisplayList;
import com.intelj.y_ral_gaming.Base64Util;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.RoundedBottomSheetDialog;
import com.intelj.y_ral_gaming.model.TeamListPOJO;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class RuleFragment extends Fragment {
    View rootView;
    String url = "";
    WebView browser;

    public RuleFragment(String url) {
        this.url = url;
        Log.e("apiUrl", url);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("refreshWeb"));
        rootView = inflater.inflate(R.layout.rules, container, false);
        browser = rootView.findViewById(R.id.webview);
        Log.e("webviewUrl",url);
        browser.loadUrl(url);
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        browser.setWebViewClient(new MyBrowser());
        browser.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                rootView.findViewById(R.id.pBar3).setVisibility(View.VISIBLE);
                if (progress == 100)
                    rootView.findViewById(R.id.pBar3).setVisibility(View.GONE);
            }
        });
        return rootView;
    }

    ArrayList<TeamListPOJO> myListData = new ArrayList<>();

    public class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("WebViewRes", url);
            if (url.startsWith("!http://redirect_to_app")) {
                view.loadUrl(url);
            } else {
                Log.e("WebViewRes", url);
                String[] urlSplit = url.split("/");
                if (urlSplit[3].equals("team")) {
                    String base64 = urlSplit[5];
                    String[] nameList = urlSplit[6].split(",");
                    byte[] data = Base64.decode(base64, Base64.DEFAULT);
                    String text = new String(data, StandardCharsets.UTF_8);
                    Log.e("textWeb", text);
                    View inflated = getLayoutInflater().inflate(R.layout.team_member, null);
                    final BottomSheetDialog dialogBottom = new RoundedBottomSheetDialog(getActivity());
                    RecyclerView teamRecyclerView = inflated.findViewById(R.id.rv_teamlist);
                    TeamDisplayList teamAdapter;
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    teamRecyclerView.setLayoutManager(layoutManager);
                    teamRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    teamAdapter = new TeamDisplayList(myListData);
                    teamRecyclerView.setAdapter(teamAdapter);
                    try {
                        JSONObject jsonObject = new JSONObject(text);
                        myListData.clear();
                        Iterator<String> keys = jsonObject.keys();
                        int x = 0;
                        while (keys.hasNext()) {
                            String key = keys.next();
                            Object value = jsonObject.get(key);
                            if(value instanceof JSONObject){
                                JSONObject nestedJsonObject = jsonObject.getJSONObject(key);
                                Log.e("error Rec key", nestedJsonObject.getString("ingName"));
                                String userNames =  nestedJsonObject.getString("ingName");
                                if(Base64Util.isBase64(userNames)){
                                    byte[] decodedBytes = Base64.decode(userNames, Base64.DEFAULT);
                                    userNames = new String(decodedBytes, StandardCharsets.UTF_8);
                                }
                                myListData.add(new TeamListPOJO(userNames , key, nestedJsonObject.getString("ingName"),nameList[x]));
                            }else{
                                Log.e("error Rec key", "errors");
                            }
                            x = x + 1;
                        }
                        teamAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("error Rec", e.getMessage());
                    }
                    dialogBottom.setContentView(inflated);
                    dialogBottom.show();
                }

            }
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {

        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            browser.reload();
        }
    };

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
