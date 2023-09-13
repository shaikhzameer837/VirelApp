package com.intelj.y_ral_gaming.bottomSheet;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.BuildConfig;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RulesBottomSheet extends BottomSheetDialogFragment {
    private ChallengeAcceptedListener mListener;
    public interface ChallengeAcceptedListener {
        void onChallengeAccepted();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (ChallengeAcceptedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ChallengeAcceptedListener");
        }
    }

    public static RulesBottomSheet newInstance(String id,String count,String type) {
        RulesBottomSheet fragment = new RulesBottomSheet();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("count", count);
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }
    String cid = "";
    String count = "";
    String type = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            cid = getArguments().getString("id");
            count = getArguments().getString("count");
            type = getArguments().getString("type");
            // Do something with the id
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        Button btnAccept = view.findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptChallenge(AppConstant.AppUrl+"register_challenges.php");
                dismiss();
            }
        });

        return view;
    }
    private void acceptChallenge(String url) {
        Log.e("AppConstant.AppUrl4", "start");
        String time = ""+((System.currentTimeMillis()/1000)  + (24 * 60 * 60));
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?userid=" + new AppConstant(getActivity()).getId()+"&&stime="+time+"&&cid="+cid+"&&target="+count+"&&type="+type,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("AppConstant.AppUrl3", response);
                        if (mListener != null) {
                            mListener.onChallengeAccepted();
                        }
//                        try {
//                            JSONObject json = new JSONObject(response);
//                            if (json.getBoolean("success")) {
//                                Toast.makeText(MainActivity.this, json.getString("msg"), Toast.LENGTH_LONG).show();
//                            } else {
//                                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
//                            }
//                        } catch (Exception e) {
//                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
//
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("AppConstant.AppUrl1", error.getMessage()+"");
            }
        }) {
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
