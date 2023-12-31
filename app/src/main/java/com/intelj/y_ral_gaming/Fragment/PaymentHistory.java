package com.intelj.y_ral_gaming.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.intelj.y_ral_gaming.Adapter.PayMentAdapter;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.databinding.HistoryBinding;
import com.intelj.y_ral_gaming.main.PageViewModel;
import com.intelj.y_ral_gaming.model.PaymentHistoryModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaymentHistory extends Fragment {
    SharedPreferences sharedPreferences;
    private static final String ARG_SECTION_NUMBER = "section_number";
    ArrayList<PaymentHistoryModel> paymentHistoryModels = new ArrayList<>();
    private PageViewModel pageViewModel;
    private HistoryBinding binding;
    AppConstant appConstant;

    public static PaymentHistory newInstance(int index) {
        PaymentHistory fragment = new PaymentHistory();
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


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        appConstant = new AppConstant(getActivity());
        binding = HistoryBinding.inflate(inflater, container, false);
        sharedPreferences = getActivity().getSharedPreferences(appConstant.getId(), 0);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        requestMoney();
        View root = binding.getRoot();
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void requestMoney() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppConstant.AppUrl + "get_payment_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("tokenResponse", response);
                        binding.shimmerLayout.hideShimmer();
                        binding.shimmerLayout.setVisibility(View.GONE);
                        try {
                            JSONArray array = new JSONArray(response);
                            if (array.length() == 0) {
                                binding.pBar3.setVisibility(View.VISIBLE);
                                binding.msg.setVisibility(View.VISIBLE);
                                return;
                            }
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                paymentHistoryModels.add(new PaymentHistoryModel(AppConstant.getTimeAgo(Integer.parseInt(obj.getString("date"))),
                                        obj.getString("transaction_id"), obj.getString("amount"),
                                        obj.getString("screenshort"), obj.getString("ticket_id"), obj.getInt("type")));
                            }
                            PayMentAdapter pAdapter = new PayMentAdapter(getActivity(), paymentHistoryModels);
                            binding.recyclerView.setAdapter(pAdapter);

                        } catch (Exception e) {
                            Log.e("error Rec", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
/*                shimmer_container.hideShimmer();
                shimmer_container.setVisibility(View.GONE);*/
                error.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", appConstant.getId());
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

