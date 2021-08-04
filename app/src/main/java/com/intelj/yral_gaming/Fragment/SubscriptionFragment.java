package com.intelj.yral_gaming.Fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;

import java.util.HashMap;
import java.util.Map;

public class SubscriptionFragment extends Fragment {
    public View rootView;
    public String colorCode,time_of_expired;
    int package_price;
    int position;
    public SubscriptionFragment(String s, int position, String time_of_expired, int package_price) {
        colorCode = s;
        this.position = position;
        this.time_of_expired = time_of_expired;
        this.package_price = package_price;
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
        //if (colorCode.equals("#7e241c"))
            setViews(package_price+" Coins \n Per Match");
//        if (colorCode.equals("#cb7069"))
//            setViews("1200 Coins \n Per Month");
//        if (colorCode.equals("#000000"))
//            setViews("1500 Coins \n Per Month");
        rootView.findViewById(R.id.subscription).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSubscription();
            }
        });
        return rootView;
    }

    private void updateSubscription() {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Registering for App, please wait.");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://y-ral-gaming.com/admin/api/save_subscription.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", new AppConstant(getActivity()).getUserId());
                params.put("package_id", position+"");
                params.put("time_of_purchase", (System.currentTimeMillis()/1000)+"");
                params.put("time_of_expired", time_of_expired);
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