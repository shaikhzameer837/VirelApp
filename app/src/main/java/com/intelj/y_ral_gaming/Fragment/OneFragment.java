package com.intelj.y_ral_gaming.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.intelj.y_ral_gaming.Adapter.TimeSlotAdapter;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.BuildConfig;
import com.intelj.y_ral_gaming.GameItem;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OneFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tv_coming_soon;
    View rootView;
    private TimeSlotAdapter mAdapter;
    public ArrayList<GameItem> GameItem = new ArrayList<>();
    ShimmerFrameLayout shimmerFrameLayout;
    String title;
    String key;
    long show_listview = 0;
    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

    public OneFragment() {
        // Required empty public constructor
    }


    public OneFragment(String title, long show_listview) {
        this.title = title;
        this.show_listview = show_listview;
        Log.e("show_listview", show_listview + "");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_one, container, false);
        onResumeView();
        return rootView;
    }

    public void onResumeView() {
        shimmerFrameLayout = rootView.findViewById(R.id.shimmer_layout);
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        recyclerView = rootView.findViewById(R.id.recycler_view);
        tv_coming_soon = rootView.findViewById(R.id.tv_coming_soon);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        initializedata();
        Log.e("show_listview", show_listview + "--" + title);
        if (show_listview != 0) {
            shimmerFrameLayout.startShimmer();
            recyclerView.setVisibility(View.VISIBLE);
            tv_coming_soon.setVisibility(View.GONE);
            rootView.findViewById(R.id.coming_soon).setVisibility(View.GONE);
            reloadData();
        } else {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            tv_coming_soon.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.coming_soon).setVisibility(View.VISIBLE);
        }
    }

    private void reloadData() {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = AppConstant.AppUrl + "free_give_away.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick3-free_give_away", response);
                        // progressDialog.cancel();
                        shimmerFrameLayout.hideShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success") && !json.has("msg")) {
                                AppController.getInstance().amount = Integer.parseInt(json.getString("amount"));
                                AppController.getInstance().rank = Integer.parseInt(json.getString("rank"));
                                AppController.getInstance().referral = json.getString("referral");
                                AppController.getInstance().teamList = json.getString("teamList");
                                Log.e("onReceive: R ", AppController.getInstance().teamList);
                                JSONArray ja_data = json.getJSONArray("match_info");
                                GameItem.clear();
                                for (int i = 0; i < ja_data.length(); i++) {
                                    JSONObject JdataObject = ja_data.getJSONObject(i);
                                    JSONObject jObj = JdataObject.getJSONObject("row_result");
                                    GameItem.add(new GameItem("BGMI match " + (1 + i),
                                            jObj.getString("status"), jObj.getString("perKill"), jObj.getString("entryFees"),
                                            jObj.getString("type"), jObj.getString("map"),
                                            jObj.getString("time"), JdataObject.getString("isExist")
                                            , jObj.getString("player_count"),
                                            jObj.getString("g_id")
                                            , jObj.getString("g_password"), jObj.getInt("max"), jObj.getString("yt_url"), jObj.getString("id"), jObj.getString("g_password")));
                                }
                                Log.e("titlessss", GameItem.size() + " " + title);
                                Intent intent = new Intent("custom-event-name");
                                intent.putExtra(AppConstant.amount, true);
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
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
                                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.intelj.y_ral_gaming"));
                                                    startActivity(browserIntent);
                                                } else {
                                                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                                                    startMain.addCategory(Intent.CATEGORY_HOME);
                                                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(startMain);
                                                }
                                            }
                                        })

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
                                AppController.getInstance().amount = 0;
                                Intent intent = new Intent("custom-event-name");
                                intent.putExtra(AppConstant.amount, true);
                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            Log.e("logMess", e.getMessage());

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                params.put("debug", BuildConfig.DEBUG + "");
                params.put("match_id", date);
                params.put("game_type", title);
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


    private void initializedata() {
        mAdapter = new TimeSlotAdapter(getActivity(), GameItem, title);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}