package com.intelj.y_ral_gaming.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.intelj.y_ral_gaming.TournamentAdapter;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.databinding.RankBinding;
import com.intelj.y_ral_gaming.main.PageViewModel;
import com.intelj.y_ral_gaming.model.TournamentModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentStatus extends Fragment  {
    SharedPreferences sharedPreferences;
    private static final String ARG_SECTION_NUMBER = "section_number";
    List<TournamentModel> tournamentModelList = new ArrayList<>();
    private PageViewModel pageViewModel;
    private RankBinding binding;
    AppConstant appConstant;

    public static PaymentStatus newInstance(int index) {
        PaymentStatus fragment = new PaymentStatus();
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
        binding = RankBinding.inflate(inflater, container, false);
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
        String url = "http://y-ral-gaming.com/admin/api/get_status.php";
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
                                binding.not.setVisibility(View.VISIBLE);
                                return;
                            }
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                tournamentModelList.add(
                                        new TournamentModel(obj.getString("name"),
                                                "",
                                                obj.getString("image_url"),
                                                obj.getString("date"),
                                                obj.getString("status"),
                                                obj.getString("comment"), "", "", "", 0, 0));
                            }
                            TournamentAdapter pAdapter = new TournamentAdapter(getActivity(), tournamentModelList, false);
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
