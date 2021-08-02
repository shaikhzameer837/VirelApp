package com.intelj.yral_gaming.Fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.intelj.yral_gaming.AppController;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;

import org.json.JSONException;
import org.json.JSONObject;

public class SubscriptionFragment extends Fragment {
    View rootView;
    String colorCode;
    private Button subscription;
    private int[] rs = {120,450,650};
    private int coins;
    private JSONObject subscription_amount;

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
//        rootView.findViewById(R.id.subscription).setBackgroundColor(Color.parseColor(colorCode));
        subscription = rootView.findViewById(R.id.subscription);
        subscription.setBackgroundColor(Color.parseColor(colorCode));
        try {
            subscription_amount = new JSONObject(AppController.getInstance().subscription_amount);
//            rs = new int[]{subscription_amount.getInt("normal"), subscription_amount.getInt("pro"), subscription_amount.getInt("ultra_pro")};
        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        if (colorCode.equals("#7e241c")) {
            coins = rs[0];
            setViews(coins + "Rs \n Per Match");
        }
        if (colorCode.equals("#cb7069")) {
            coins = rs[1];
            setViews(coins + "Rs \n Per Month");
        }
        if (colorCode.equals("#000000")) {
            coins = rs[2];
            setViews(coins + "Rs\n Per Month");
        }
        subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applySubscription();
            }
        });
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

    private void applySubscription() {
        if (coins > new AppConstant(getContext()).getCoins()) {
            showBottomSheetDialog();
        } else {
            Toast.makeText(getContext(), "Subscription successful", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_purchase_sub);
        TextView copy = bottomSheetDialog.findViewById(R.id.tv_purchase);
        TextView share = bottomSheetDialog.findViewById(R.id.tv_coins_rate);
        bottomSheetDialog.show();
    }
}