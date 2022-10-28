package com.intelj.y_ral_gaming.Activity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.ChatActivity;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SigninActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ChatList extends AppCompatActivity {
    ArrayList<ContactListModel> contactModel;
    HashMap<String, String> contactHashList = new HashMap<>();
    HashSet<String> originalContact = new HashSet<>();
    SharedPreferences shd;
    RecyclerView rv_contact;
    private static final int REQUEST = 112;
    ContactListAdapter contactListAdapter;
    AppConstant appConstant;
    ProgressBar progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);
        shd = getSharedPreferences(AppConstant.id, MODE_PRIVATE);
        rv_contact = findViewById(R.id.rv_contact);
        progress = findViewById(R.id.progress);
        appConstant = new AppConstant(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ChatList.this);
        rv_contact.setLayoutManager(mLayoutManager);
        contactModel = new ArrayList<>();
        contactListAdapter = new ContactListAdapter(ChatList.this, contactModel);
        rv_contact.setAdapter(contactListAdapter);
        rv_contact.addOnItemTouchListener(new RecyclerTouchListener(ChatList.this, rv_contact, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (!contactModel.get(position).getUserid().equals("")) {
                    sendIntent(contactModel.get(position).getUserid(),view);
                    return;
                }
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                ref.orderByChild("phoneNumber").equalTo(contactModel.get(position).getPhoneNumber().trim()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshots) {
                        if (dataSnapshots.getChildrenCount() == 0) {
                            startActivity(new Intent(ChatList.this, ReferralActivity.class));
                            return;
                        }
                        for (DataSnapshot childSnapshot : dataSnapshots.getChildren()) {
                            String clubkey = childSnapshot.getKey();
                            Log.e("clubkey", clubkey);
                            appConstant.saveUserInfo(contactModel.get(position).getPhoneNumber(), clubkey, "", null, "", childSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue() != null ? childSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue().toString() : null, childSnapshot.child(AppConstant.userName).getValue() != null ? childSnapshot.child(AppConstant.userName).getValue().toString() : System.currentTimeMillis() + "");
                            contactModel.get(position).setUserid(clubkey);
                            sendIntent(contactModel.get(position).getUserid(),view);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed, how to handle?

                    }

                });
                if (!appConstant.checkLogin()) {
                    startActivity(new Intent(ChatList.this, SigninActivity.class));
                    return;
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS};
            if (!hasPermissions(ChatList.this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(ChatList.this, PERMISSIONS, REQUEST);
            } else {
                loadChats();
            }
        } else {
            loadChats();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("refresh"));
    }

    private void sendIntent(String userid,View view) {
        Intent intent = new Intent(ChatList.this, ChatActivity.class);
        String transitionName = "fade";
        View transitionView = view.findViewById(R.id.profile);
        ViewCompat.setTransitionName(transitionView, transitionName);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(ChatList.this, transitionView, transitionName);
        intent.putExtra(AppConstant.id, userid);
        startActivity(intent, options.toBundle());
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progress.setVisibility(View.GONE);
            loadChats();
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private void loadChats() {
        Set<String> set = shd.getStringSet(AppConstant.contact, null);
        try {
            if (set != null) {
                for (String s : set) {
                    SharedPreferences userInfo = getSharedPreferences(s, Context.MODE_PRIVATE);
                    contactModel.add(new ContactListModel(s,
                            AppConstant.AppUrl + "images/" + userInfo.getString(AppConstant.id, "") + ".png?u=" + AppConstant.imageExt(),
                            getContactName(s),
                            userInfo.getString(AppConstant.id, ""),
                            userInfo.getString(AppConstant.bio, "")));
                }
                Collections.sort(contactModel, new Comparator<ContactListModel>() {
                    @Override
                    public int compare(final ContactListModel object1, final ContactListModel object2) {
                        Log.e("Collections", object1.getName() + " " + object2.getName());
                        return object1.getName().compareTo(object2.getName());
                    }
                });
                contactListAdapter.notifyDataSetChanged();
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
            progress.setVisibility(View.VISIBLE);
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(Void... arg0) {
            readContacts();

            return "You are at PostExecute";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progress.setVisibility(View.GONE);
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
                                contactHashList.put(phoneNo, original);
                                originalContact.add(original);
                                Log.e("Phone_Number_add: ", phoneNo);
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
                    loadChats();
                } else {
                    Toast.makeText(this, "The app was not allowed to read your contact", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}
