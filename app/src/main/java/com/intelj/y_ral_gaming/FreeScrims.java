package com.intelj.y_ral_gaming;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FreeScrims extends Fragment {
    View rootView;
    private RecyclerView recyclerView;
    private MatchAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ShimmerFrameLayout shimmerFrameLayout;

    public FreeScrims() {
        // Required empty public constructor
    }

    public FreeScrims(String key, Boolean value) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        shimmerFrameLayout = rootView.findViewById(R.id.shimmer_layout);
        shimmerFrameLayout.startShimmer();
        mAdapter = new MatchAdapter(getActivity(), AppController.getInstance().movieList);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));
        mSwipeRefreshLayout = rootView.findViewById(R.id.swiperefresh_items);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                reloadData();
            }
        });
        reloadData();
        return rootView;
    }

    String key;

    private void reloadData() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//        ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setTitle("loading...");
//        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppConstant.AppUrl + "load_free_matches.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick3", response);
                       // progressDialog.cancel();
                        shimmerFrameLayout.hideShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success") && !json.has("msg")) {
                                AppController.getInstance().amount = Integer.parseInt(json.getString("amount"));
                                JSONArray ja_data = json.getJSONArray("match_info");
                                AppController.getInstance().movieList.clear();
                                for (int i = 0; i < ja_data.length(); i++) {
                                    JSONObject jObj = ja_data.getJSONObject(i);
//                                    AppController.getInstance().movieList.add(new GameItem("BGMI match " + (1 + i),
//                                            jObj.getString("status"), jObj.getString("perKill"), jObj.getString("entryFees"),
//                                            jObj.getString("type"), jObj.getString("map"),
//                                            jObj.getString("time"), jObj.getString("isExist")
//                                            , jObj.getString("total"),
//                                            jObj.getString("id").equals("") ? "****" : jObj.getString("id")
//                                            , jObj.getString("password").equals("") ? "****" : jObj.getString("password"),
//                                            jObj.getString("result_url"),jObj.getInt("max")));
                                }
                                Intent intent = new Intent("custom-event-name");
                                intent.putExtra(AppConstant.amount, true);
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
//                                BroadCast
//                                ((TextView) findViewById(R.id.coins)).setText(AppController.getInstance().amount + "");
                                mAdapter.notifyDataSetChanged();
                            } else {
                                key = "OK";
                                if (json.getInt("key") == 1)
                                    key = "Click Download new version";
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Alert")
                                        .setCancelable(false)
                                        .setMessage(json.getString("msg"))
                                        .setPositiveButton(key, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if (key.equals("Click Download new version")) {
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://y-ral-gaming.com/scrims.apk"));
                                                    startActivity(browserIntent);
                                                } else {
                                                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                                                    startMain.addCategory(Intent.CATEGORY_HOME);
                                                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(startMain);
                                                }
                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                                startMain.addCategory(Intent.CATEGORY_HOME);
                                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(startMain);
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        } catch (Exception e) {
                            Log.e("logMess", e.getMessage());

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  progressDialog.cancel();
                shimmerFrameLayout.hideShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", new AppConstant(getActivity()).getId());
                int versionCode = BuildConfig.VERSION_CODE;
                params.put("version", versionCode + "");
                params.put("match_id", date);
                params.put("game_type", "BGMI");
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