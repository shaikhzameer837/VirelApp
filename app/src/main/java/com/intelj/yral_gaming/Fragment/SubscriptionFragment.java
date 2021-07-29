package com.intelj.yral_gaming.Fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.intelj.yral_gaming.R;

import org.json.JSONObject;

public class SubscriptionFragment extends Fragment {
    View rootView;
    String colorCode;

    public SubscriptionFragment(String s) {
        colorCode = s;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.subscription, container, false);
        rootView.findViewById(R.id.price).setBackgroundColor(Color.parseColor(colorCode));
        rootView.findViewById(R.id.subscription).setBackgroundColor(Color.parseColor(colorCode));
        if (colorCode.equals("#7e241c"))
            setViews("35Rs \n Per Match");
        if (colorCode.equals("#cb7069"))
            setViews("1200rs \n Per Month");
        if (colorCode.equals("#000000"))
            setViews("1500rs\n Per Month");
        return rootView;
    }

    private void setViews(String strPrice) {
        TextView price = rootView.findViewById(R.id.price);
        price.setText(strPrice);
        LinearLayout linbox = rootView.findViewById(R.id.linbox);
        Drawable img = getContext().getResources().getDrawable(R.drawable.ic_check);
        for (int x = 0; x < 8; x++) {
            TextView tv = new TextView(getActivity());
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, 0,
                    1.0f);
            lparams.setMargins(30, 0, 0, 0);
            tv.setLayoutParams(lparams);
            tv.setLayoutParams(lparams);
            tv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            tv.setText(" Daily free Custom matches Worth " + strPrice);
            tv.setGravity(Gravity.CENTER);
            linbox.addView(tv);
        }
    }
}