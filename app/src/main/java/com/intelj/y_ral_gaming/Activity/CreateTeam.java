package com.intelj.y_ral_gaming.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.databinding.CreateTeamBinding;

import org.apache.commons.net.util.Base64;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateTeam extends AppCompatActivity {
    private CreateTeamBinding binding;
    AppConstant appConstant;
    ContactListAdapter contactListAdapter;
    ArrayList<ContactListModel> contactModel;
    SharedPreferences shd;
    List<String> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CreateTeamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        contactModel = new ArrayList<>();
        shd = getSharedPreferences(AppConstant.id, MODE_PRIVATE);
        appConstant = new AppConstant(this);
        contactListAdapter = new ContactListAdapter(CreateTeam.this, contactModel);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(contactListAdapter);
        contactListAdapter.checkVisible();
        loadChats();
    }

    public void createTeam(View view) {
        if (binding.teamName.getText().toString().trim().equals("")) {
            binding.teamName.setError("Team name cannot be empty");
            binding.teamName.requestFocus();
            return;
        }
        if (contactListAdapter.getSelectedList().size() == 0) {
            Toast.makeText(CreateTeam.this, "Please select member for Your team", Toast.LENGTH_LONG).show();
            return;
        }
        if (contactListAdapter.getSelectedList().size() > 5) {
            Toast.makeText(CreateTeam.this, "Only 6 members are allowed including You", Toast.LENGTH_LONG).show();
            return;
        }
        postTeam();
    }

    private void postTeam() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Team...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(CreateTeam.this);
        String url = AppConstant.AppUrl + "create_team.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.hide();
                        Log.e("lcat_response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(CreateTeam.this, "Team Created", Toast.LENGTH_LONG).show();
                               finish();
                            } else
                                Toast.makeText(CreateTeam.this, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
/*                shimmer_container.hideShimmer();
                shimmer_container.setVisibility(View.GONE);*/
                error.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("owner", appConstant.getId());
                 list = contactListAdapter.getSelectedList();
                list.add(appConstant.getId());
                String joined = TextUtils.join(",", list);
                params.put("teamMember", joined);
                byte[] bytesEncoded = Base64.encodeBase64(binding.teamName.getText().toString().getBytes());
                params.put("teamName", new String(bytesEncoded));
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

    private void loadChats() {
        Set<String> set = shd.getStringSet(AppConstant.contact, null);
        try {
            if (set != null) {
                for (String s : set) {
                    SharedPreferences userInfo = getSharedPreferences(s, Context.MODE_PRIVATE);
                    if(!userInfo.getString(AppConstant.phoneNumber, "").equals(appConstant.getId())) {
                        contactModel.add(new ContactListModel(s, userInfo.getString(AppConstant.myPicUrl, ""), appConstant.getContactName(userInfo.getString(AppConstant.phoneNumber, "")), userInfo.getString(AppConstant.id, ""), userInfo.getString(AppConstant.bio, "")));
                    }
                }
                Collections.sort(contactModel, new Comparator<ContactListModel>() {
                    @Override
                    public int compare(final ContactListModel object1, final ContactListModel object2) {
                        Log.e("Collections", object1.getName() + " " + object2.getName());
                        return object1.getName().compareTo(object2.getName());
                    }
                });
                contactListAdapter.notifyDataSetChanged();
            } else {
                // readContactTask().execute();
            }
        } catch (Exception e) {
            // readContactTask().execute();
        }
    }
}
