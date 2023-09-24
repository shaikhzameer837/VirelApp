package com.intelj.y_ral_gaming.bottomSheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

public class BrowserBottomSheet extends BottomSheetDialogFragment {
    AppConstant appConstant;
    String gameId;
    public BrowserBottomSheet(String gameId) {
        this.gameId = gameId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        WebView myWebView = view.findViewById(R.id.webview);
        appConstant = new AppConstant(getActivity());
        myWebView.loadUrl(AppConstant.AppUrl+"web/poll.php?u=" + appConstant.getId()+"&g="+gameId);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        return view;
    }
}