package com.intelj.y_ral_gaming.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.intelj.y_ral_gaming.Adapter.EventTeamAdapter;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.PopularModel;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.model.EventTeamModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TeamFragment extends Fragment {
    View rootView;
    RecyclerView rv_teamlist;
    EventTeamAdapter eventTeamAdapter;
    List<EventTeamModel> eventTeamList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventTeamList.clear();
        rootView = inflater.inflate(R.layout.team_list, container, false);
        rv_teamlist = rootView.findViewById(R.id.rv_teamlist);
        loadTeam();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("register_event"));
        return rootView;
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            String teamName = intent.getStringExtra("teamName");
            String teams = intent.getStringExtra("teams");
            eventTeamList.add(new EventTeamModel("http://y-ral-gaming.com/admin/api/images/"+key+".png?u=" + (System.currentTimeMillis() / 1000), teamName, teams, key));
            eventTeamAdapter.notifyDataSetChanged();
        }
    };
    private void loadTeam() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://y-ral-gaming.com/admin/api/test.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean joined_event = false;
                        Log.e("lcat_response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Iterator<String> keys = jsonObject.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                if (jsonObject.get(key) instanceof JSONObject) {
                                    JSONObject teamJson = ((JSONObject) jsonObject.get(key));
                                    String teamName = teamJson.getString("teamName");
                                    if (((JSONObject)teamJson.get("teams")).has(new AppConstant(getActivity()).getId())) {
                                        joined_event = true;
                                    }
                                    eventTeamList.add(new EventTeamModel("http://y-ral-gaming.com/admin/api/images/"+key+".png?u=" + (System.currentTimeMillis() / 1000), teamName, teamJson.getString("teams"), key));
                                }
                            }

                        } catch (Exception e) {

                        }
                        Intent intent = new Intent("joined_event");
                        intent.putExtra("message", joined_event);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                        eventTeamAdapter = new EventTeamAdapter(getActivity(), eventTeamList);
                        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
                        rv_teamlist.setLayoutManager(mLayoutManager);
                        rv_teamlist.setAdapter(eventTeamAdapter);
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
                params.put("tid", AppController.getInstance().tournamentModel.getId());
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }



    public TeamFragment() {

    }



}