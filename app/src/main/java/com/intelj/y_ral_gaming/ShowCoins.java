package com.intelj.y_ral_gaming;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShowCoins extends Fragment {
    View paymentBottomSheet;
    String[] gameplayTypeList = {"Free Fire", "BGMI"};
    SharedPreferences sharedPreferences;
    AppConstant appConstant;
    LinearLayout moneyList;
    LinearLayout payOptionList;
    String gameplay = "";
    String wAmount = "25";
    String paymentType = "Google Pay";
    public ShowCoins() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        paymentBottomSheet = inflater.inflate(R.layout.coins, container, false);
        appConstant = new AppConstant(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(appConstant.getId(), 0);
        TextView user_name = paymentBottomSheet.findViewById(R.id.user_name);
        TextView coins = paymentBottomSheet.findViewById(R.id.coins);
        TextView ranks = paymentBottomSheet.findViewById(R.id.ranks);
        coins.setText(AppController.getInstance().amount + "");
        ranks.setText(Html.fromHtml("<b><font size='14' color='#000000'>" + AppConstant.getRank(AppController.getInstance().rank) + "", new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int resourceId = getResources().getIdentifier(source, "drawable", getActivity().getPackageName());
                Drawable drawable = getResources().getDrawable(resourceId);
                drawable.setBounds(0, 0, 40, 30);
                return drawable;
            }
        }, null));
        user_name.setText(sharedPreferences.getString(AppConstant.name, "Player"));
        EditText upi = paymentBottomSheet.findViewById(R.id.upi);
        upi.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });
        Spinner sp_gameplay = paymentBottomSheet.findViewById(R.id.gameplay);
        ArrayAdapter sp_gameplayAdp = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, gameplayTypeList);
        sp_gameplay.setSelection(0);
        sp_gameplayAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_gameplay.setAdapter(sp_gameplayAdp);
        sp_gameplay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gameplay = gameplayTypeList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        moneyList = paymentBottomSheet.findViewById(R.id.moneyList);
        payOptionList = paymentBottomSheet.findViewById(R.id.payOptionList);
        paymentBottomSheet.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wAmount.equals("")) {
                    Toast.makeText(getActivity(), "Please Select amount to withdraw", Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(wAmount) <= AppController.getInstance().amount) {
                    if (upi.getText().toString().trim().equals("")) {
                        upi.setError("Upi id cannot be empty");
                        upi.requestFocus();
                        return;
                    }
                    if (upi.getText().toString().trim().contains("@") || android.text.TextUtils.isDigitsOnly(upi.getText().toString())) {
                        SharedPreferences prefs = getActivity().getSharedPreferences(AppConstant.AppName, MODE_PRIVATE);
                        long currentTime = (System.currentTimeMillis()/1000);
                        long lastRequest = prefs.getLong(AppConstant.payment, currentTime);//"No name defined" is the default value.
                        if(currentTime >= lastRequest) {
                            requestMoney(upi.getText().toString());
                        }else{
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Alert")
                                    .setMessage("Payment Already requested try again after 24hrs")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    } else {
                        upi.setError("Invalid upi id / paytm number");
                        upi.requestFocus();
                    }
                } else {
                    Toast.makeText(getActivity(), "Insufficient Balance", Toast.LENGTH_LONG).show();
                }
            }
        });
        paymentBottomSheet.findViewById(R.id.add_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppConstant(getActivity()).addMoney(getActivity());
            }
        });
        return  paymentBottomSheet;
    }
    public void WidthDrawAmount(View view) {
        TextView selected = ((TextView) view);
        if (Integer.parseInt(selected.getTag() + "") < AppController.getInstance().rank) {
            selected.setBackgroundColor(Color.parseColor("#000000"));
            selected.setTextColor(Color.parseColor("#ffffff"));
            for (int i = 0; i < moneyList.getChildCount(); i++) {
                TextView unselected = (TextView) moneyList.getChildAt(i);
                if (selected != moneyList.getChildAt(i)) {
                    unselected.setBackgroundResource(R.drawable.outline);
                    unselected.setTextColor(Color.parseColor("#000000"));
                }
            }
            wAmount = selected.getText().toString();
        } else {
            Toast.makeText(getActivity(), "You need " + selected.getContentDescription() + " rank to withdraw this amount", Toast.LENGTH_LONG).show();
        }
    }
    public void selectPayM(View view) {
        TextView selected = ((TextView) view);
        selected.setBackgroundResource(R.drawable.outline_black);
        for (int i = 0; i < payOptionList.getChildCount(); i++) {
            TextView unselected = (TextView) payOptionList.getChildAt(i);
            if (selected != payOptionList.getChildAt(i)) {
                unselected.setBackground(null);
            }
        }
        paymentType = selected.getText().toString();
    }
    private void requestMoney(String upi) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("loading...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://y-ral-gaming.com/admin/api/request_payment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick3", response);

                        progressDialog.cancel();
                         try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("success"))
                                return;
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(AppConstant.AppName, MODE_PRIVATE).edit();
                            editor.putLong(AppConstant.payment, (System.currentTimeMillis() / 1000) + 86400);
                            editor.apply();
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("success")
                                    .setMessage("Payment requested you will recieve payment in 24hrs \n Your Ticked id is " + obj.getString("ticked_id") + "\n click on status to check your payment request status")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Continue with delete operation
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
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
                params.put("upi", upi);
                params.put("userid", new AppConstant(getActivity()).getUserName());
                params.put("id", new AppConstant(getActivity()).getId());
                params.put("amount", wAmount);
                params.put("type", "Redeem Money");
                params.put("paymentType", paymentType);
                params.put("gameplay", gameplay);
                params.put("time", (System.currentTimeMillis()) + "");
                params.put("userName", sharedPreferences.getString(AppConstant.name, ""));
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