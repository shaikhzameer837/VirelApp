package com.intelj.y_ral_gaming.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.intelj.y_ral_gaming.R;

public class GameInfo extends BottomSheetDialogFragment {
    String dateTime;
    View rootView;

    {
    }

    public GameInfo(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.game_info, container, false);
        ((TextView) rootView.findViewById(R.id.title)).setText("Id password for the game will be given on Youtube's Live chat at " + dateTime);
        rootView.findViewById(R.id.yt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "https://www.youtube.com/c/YRALGaming";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        ((TextView) rootView.findViewById(R.id.link)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.youtube.com/c/YRALGaming";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        return rootView;
    }
}
