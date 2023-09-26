package com.intelj.y_ral_gaming.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

public class GameInfo extends BottomSheetDialogFragment {
    String gameId;
    View rootView;

    {
    }

    public GameInfo(String gameId) {
        this.gameId = gameId;
    }
   public GameInfo() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    AppConstant appConstant;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.game_info, container, false);
        WebView myWebView = rootView.findViewById(R.id.webview);
        appConstant = new AppConstant(getActivity());
        myWebView.loadUrl(AppConstant.AppUrl+"web/poll.php?u=" + appConstant.getId()+"&g="+gameId);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        rootView.findViewById(              R.id.yt).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                String url = "https://www.youtube.com/c/YRALGaming";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//        });
//        ((TextView) rootView.findViewById(R.id.link)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = "https://www.youtube.com/c/YRALGaming";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//        });
        return rootView;
    }
}
