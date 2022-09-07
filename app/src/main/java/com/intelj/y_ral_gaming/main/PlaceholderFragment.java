package com.intelj.y_ral_gaming.main;

import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.databinding.FragmentMainBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment implements View.OnClickListener {
    SharedPreferences sharedPreferences;
    private static final String ARG_SECTION_NUMBER = "section_number";
    LinearLayout moneyList;
    LinearLayout payOptionList;
    String wAmount = "25";
    String gameplay = "";
    String paymentType = "Google Pay";
    private PageViewModel pageViewModel;
    private FragmentMainBinding binding;
    AppConstant appConstant;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    String[] gameplayTypeList = {"Free Fire", "BGMI"};

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        appConstant = new AppConstant(getActivity());
        binding = FragmentMainBinding.inflate(inflater, container, false);
        sharedPreferences = getActivity().getSharedPreferences(appConstant.getId(), 0);

        TextView user_name = binding.userName;
        TextView coins = binding.coins;
//        TextView ranks = binding.ranks;
        coins.setText(AppController.getInstance().amount + "");
//        ranks.setText(Html.fromHtml("<img src='"+AppConstant.getRank(AppController.getInstance().rank) + "'/> " , new Html.ImageGetter() {
//            @Override
//            public Drawable getDrawable(String source) {
//                int resourceId = getResources().getIdentifier(source, "drawable", getActivity().getPackageName());
//                Drawable drawable = getResources().getDrawable(resourceId);
//                drawable.setBounds(0, 0, 40, 30);
//                return drawable;
//            }
//        }, null));
        user_name.setText(sharedPreferences.getString(AppConstant.name, "Player"));
        EditText upi = binding.upi;
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
        Spinner sp_gameplay = binding.gameplay;
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
        moneyList = binding.moneyList;
        for (int i = 0; i < moneyList.getChildCount(); i++) {
            moneyList.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WidthDrawAmount((TextView) v);
                }
            });
        }
        payOptionList = binding.payOptionList;
        binding.btnNext.setOnClickListener(new View.OnClickListener() {
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
                        long currentTime = (System.currentTimeMillis() / 1000);
                        long lastRequest = prefs.getLong(AppConstant.payment, currentTime);//"No name defined" is the default value.
                        if (currentTime >= lastRequest) {
                            requestMoney(upi.getText().toString());
                        } else {
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
        binding.gpay.setOnClickListener(this);
        binding.addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppConstant(getActivity()).addMoney(getActivity());
            }
        });

        for (int i = 0; i < binding.payOptionList.getChildCount(); i++) {
            binding.payOptionList.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPayM(v);
                }
            });
        }
        View root = binding.getRoot();
        return root;
    }

    public void WidthDrawAmount(View view) {
        TextView selected = ((TextView) view);
        if (Integer.parseInt(selected.getTag() + "") < AppController.getInstance().amount) {
            selected.setBackgroundResource(R.drawable.outline);
            selected.setTextColor(Color.parseColor("#ffffff"));
            for (int i = 0; i < moneyList.getChildCount(); i++) {
                TextView unselected = (TextView) moneyList.getChildAt(i);
                if (selected != moneyList.getChildAt(i)) {
                    unselected.setBackground(null);
                    unselected.setTextColor(Color.parseColor("#000000"));
                    unselected.setTextColor(Color.parseColor("#ffffff"));
                }
            }
            wAmount = selected.getText().toString();
        } else {
            Toast.makeText(getActivity(), "You need more than " + selected.getContentDescription() + " coins to withdraw this amount", Toast.LENGTH_LONG).show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void requestMoney(String upi) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("loading...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppConstant.AppUrl + "request_payment.php";
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
                            Intent intent = new Intent("payment");
                            intent.putExtra("ticked_id",obj.getString("ticked_id"));
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

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

    @Override
    public void onClick(View v) {
        switch (v.getTag().toString()) {
            case "1":
                selectPayM(v);
                break;
            case "2":
                WidthDrawAmount(v);
                break;
        }
    }
}