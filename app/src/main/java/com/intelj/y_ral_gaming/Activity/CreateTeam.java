package com.intelj.y_ral_gaming.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
    String loyalFriends;
    String isContact = "";
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
        Log.e( "loyalFriends: ", loyalFriends);
        createTeamAdapter = new CreateTeamAdapter(CreateTeam.this, contactModel, loyalFriends);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(createTeamAdapter);
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS};
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, REQUEST);
            } else {
                new readContactTask().execute();
            }
        } else {
            new readContactTask().execute();
        }
        binding.searchName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                contactModel.clear();
                if (s.toString().trim().equals("")) {
                    contactModel.addAll(contactModel2);
                    createTeamAdapter.notifyDataSetChanged();
                    return;
                }
                for (ContactListModel contactListModel : contactModel2) {
                    if (contactListModel.getName().toLowerCase(Locale.ROOT).contains(s.toString().toLowerCase(Locale.ROOT))) {
                        contactModel.add(contactListModel);
                    }
                }
                createTeamAdapter.notifyDataSetChanged();
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

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
        if (createTeamAdapter.getSelectedList().size() > 5) {
            Toast.makeText(CreateTeam.this, "Only 6 members are allowed including You", Toast.LENGTH_LONG).show();
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
                        Log.e("lcat_response", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(CreateTeam.this, "Team Created", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent("team-event-name");
                                // You can also include some extra data.
                                intent.putExtra("id", jsonObject.getString("id"));
                                intent.putExtra("name", binding.teamName.getText().toString());
                                intent.putExtra("teamMember", joined);
                                LocalBroadcastManager.getInstance(CreateTeam.this).sendBroadcast(intent);
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
                list = createTeamAdapter.getSelectedList();
                list.add(appConstant.getId());
                HashSet<String> hset = new HashSet<>(list);
                 joined = TextUtils.join(",", hset);
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
                    contactModel.add(new ContactListModel(s.replace(appConstant.getCountryCode(), ""),
                            AppConstant.AppUrl + "images/" + userInfo.getString(AppConstant.id, "") + ".png?u=" + AppConstant.imageExt(),
                            getContactName(s),
                            userInfo.getString(AppConstant.id, ""),
                            s));
                }
                Collections.sort(contactModel, new Comparator<ContactListModel>() {
                    @Override
                    public int compare(final ContactListModel object1, final ContactListModel object2) {
                        Log.e("Collections", object1.getName() + " " + object2.getName());
                        return object1.getName().compareTo(object2.getName());
                    }
                });
                String loyalFriendList[] = loyalFriends.split(",");
                for (String s : loyalFriendList) {
                    SharedPreferences userInfo = getSharedPreferences(s, Context.MODE_PRIVATE);
                    Log.e( "loadChats: ", userInfo.getString(AppConstant.id, ""));
                    contactModel.add(0,new ContactListModel(s.replace(appConstant.getCountryCode(), ""),
                            AppConstant.AppUrl + "images/" + userInfo.getString(AppConstant.id, "") + ".png?u=" + AppConstant.imageExt(),
                            getContactName(s),
                            userInfo.getString(AppConstant.id, ""),
                            s));
                }
                contactModel2.addAll(contactModel);
                createTeamAdapter.notifyDataSetChanged();
            } else
                new readContactTask().execute();
        } catch (Exception e) {
            new readContactTask().execute();
        }
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

    class readContactTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute() {
            super.onPreExecute();
            contactModel.clear();
            binding.progress.setVisibility(View.VISIBLE);
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(Void... arg0) {
            readContacts();

            return "You are at PostExecute";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            binding.progress.setVisibility(View.GONE);
            loadChats();
        }
    }

//    public void serverContact(String number, String original) {
//        Log.e("userNum", number);
//        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        Query query = mFirebaseDatabaseReference.child("users").orderByChild("phoneNumber").equalTo(number);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                returnPhone++;
//                if (dataSnapshot != null) {
//                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                        Log.e("number//", original);
//                        Log.e("number//---", postSnapshot.getKey());
//                        originalContact.add(postSnapshot.getKey());
//                        appConstant.saveUserInfo(original, postSnapshot.getKey(), postSnapshot.child(AppConstant.token).exists() ? postSnapshot.child(AppConstant.token).getValue(String.class) : "", null, "", postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue() != null ? postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue().toString() : null, postSnapshot.child(AppConstant.userName).getValue() != null ? postSnapshot.child(AppConstant.userName).getValue().toString() : System.currentTimeMillis() + "");
//                    }
//                }
//                Log.e("originalContact", contactArrayList.size() + " " + returnPhone);
//                if (contactArrayList.size() == returnPhone) {
//                    SharedPreferences.Editor setEditor = shd.edit();
//                    setEditor.putStringSet(AppConstant.contact, originalContact);
//                    setEditor.apply();
//                    Intent intent = new Intent("refresh");
//                    LocalBroadcastManager.getInstance(ChatList.this).sendBroadcast(intent);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }


    public void readContacts() {
        contactHashList.clear();
        originalContact.clear();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
                        if (phoneNo.length() > 8) {
                            String original = phoneNo;
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
                        }
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        SharedPreferences.Editor setEditor = shd.edit();
        setEditor.putStringSet(AppConstant.contact, originalContact);
        setEditor.apply();
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
            case REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new readContactTask().execute();
                } else {
                    Toast.makeText(this, "The app was not allowed to read your contact", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
