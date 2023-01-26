package com.intelj.y_ral_gaming.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import com.intelj.y_ral_gaming.Adapter.CreateTeamAdapter;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.databinding.CreateTeamBinding;

import org.apache.commons.net.util.Base64;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CreateTeam extends AppCompatActivity {
    private CreateTeamBinding binding;
    AppConstant appConstant;
    CreateTeamAdapter createTeamAdapter;
    ArrayList<ContactListModel> contactModel;
    ArrayList<ContactListModel> contactModel2 = new ArrayList<>();
    SharedPreferences shd;
    List<String> list = new ArrayList<>();
    HashMap<String, String> contactHashList = new HashMap<>();
    HashSet<String> originalContact = new HashSet<>();
    private static final int REQUEST = 112;
    String loyalFriends = "";
    private ArrayList<String> contactStrList;
    int SELECT_PHONE_NUMBER = 90;
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CreateTeamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        contactModel = new ArrayList<>();
        shd = getSharedPreferences(AppConstant.id, MODE_PRIVATE);
        appConstant = new AppConstant(this);
        SharedPreferences sharedAppName = getSharedPreferences(AppConstant.AppName, 0);
        loyalFriends = sharedAppName.getString(AppConstant.contact, "");
        Log.e("loyalFriends: ", loyalFriends);
        createTeamAdapter = new CreateTeamAdapter(CreateTeam.this, contactModel, loyalFriends);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (loyalFriends.contains(",")) {
            String loyalFriendList[] = loyalFriends.split(",");
            for (String s : loyalFriendList) {
                SharedPreferences userInfo = getSharedPreferences(s, Context.MODE_PRIVATE);
                Log.e("loadChats: ", userInfo.getString(AppConstant.id, ""));
                contactModel.add(0, new ContactListModel(s.replace(appConstant.getCountryCode(), ""),
                        AppConstant.AppUrl + "images/" + userInfo.getString(AppConstant.id, "") + ".png?u=" + AppConstant.imageExt(),
                        getContactName(s),
                        userInfo.getString(AppConstant.id, ""),
                        s));
            }
            contactModel2.addAll(contactModel);
        }
        binding.recyclerView.setAdapter(createTeamAdapter);
    }

    public void createTeam(View view) {
        if (binding.teamName.getText().toString().trim().equals("")) {
            binding.teamName.setError("Team name cannot be empty");
            binding.teamName.requestFocus();
            return;
        }
        if (createTeamAdapter.getSelectedList().size() == 0) {
            Toast.makeText(CreateTeam.this, "Please select member for Your team", Toast.LENGTH_LONG).show();
            return;
        }
        if (createTeamAdapter.getSelectedList().size() > 7) {
            Toast.makeText(CreateTeam.this, "Only 8 members are allowed including You", Toast.LENGTH_LONG).show();
            return;
        }
        postTeam();
    }

    String joined = "";
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
                        Log.e("getParams:1 ", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(CreateTeam.this, "Team Created", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent("team-event-name");
                                // You can also include some extra data.
                                AppController.getInstance().teamList = AppController.getInstance().teamList + jsonObject.getString("id") + ",";

                                finish();
                            } else
                                Toast.makeText(CreateTeam.this, jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("getParams:1 ", e.getMessage());
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Log.e("getParams: ", error.getMessage());
                error.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("owner", appConstant.getId());
                list = createTeamAdapter.getSelectedList();
                list.add(appConstant.getId());
                HashSet<String> hset = new HashSet<>(list);
                joined = TextUtils.join(",", hset);
                params.put("teamMember", joined);
                byte[] bytesEncoded = Base64.encodeBase64(binding.teamName.getText().toString().getBytes());
                params.put("teamName", new String(bytesEncoded));
                Log.e("getParams: ", params.toString());
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

    public String getContactName(final String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String contactName = "";
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }
        return contactName;
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void pickNumber(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Contacts access needed");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage("please confirm Contacts access");// explain here why the app needs this permission
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(
                                new String[]
                                        {Manifest.permission.READ_CONTACTS}
                                , MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                });
                builder.show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(i, SELECT_PHONE_NUMBER);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Cursor cursor = null;
            try {
                String phoneNo = null;
                String name = null;

                Uri uri = data.getData();
                cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                phoneNo = cursor.getString(phoneIndex);
                name = cursor.getString(nameIndex);
                if (phoneNo.length() > 8) {
                    String original = phoneNo.replace(" ", "");
                    if (phoneNo.startsWith("0"))
                        phoneNo = phoneNo.substring(1);
                    if (!phoneNo.startsWith("+"))
                        phoneNo = appConstant.getCountryCode() + phoneNo;
                    Log.e("Phone_Number: ", phoneNo);
                    if (!phoneNo.equals(appConstant.getCountryCode() + appConstant.getPhoneNumber()) || !contactHashList.containsKey(original)) {
                        contactHashList.put(original, phoneNo);
                        originalContact.add(original);
                        Log.e("Phone_Number_add: ", original);
                    }
                    createTeamAdapter.checkIfNumberExist(phoneNo, name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.e("Failed", "Not able to pick contact");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

        }
    }
}
