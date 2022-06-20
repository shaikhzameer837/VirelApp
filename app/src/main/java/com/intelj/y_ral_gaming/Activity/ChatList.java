package com.intelj.y_ral_gaming.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.ChatActivity;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ChatList extends AppCompatActivity {
    ArrayList<ContactListModel> contactModel;
    HashMap<String, String> contactArrayList = new HashMap<>();
    SharedPreferences shd;
    HashSet<String> originalContact = new HashSet<>();
    RecyclerView rv_contact;
    private static final int REQUEST = 112;
    ContactListAdapter contactListAdapter;
    AppConstant appConstant;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);
        shd = getSharedPreferences(AppConstant.id, MODE_PRIVATE);
        rv_contact = findViewById(R.id.rv_contact);
        appConstant = new AppConstant(this);
        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS};
                    if (!hasPermissions(ChatList.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(ChatList.this, PERMISSIONS, REQUEST);
                    } else {
                        new readContactTask().execute();
                    }
                } else {
                    new readContactTask().execute();
                }
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ChatList.this);
        rv_contact.setLayoutManager(mLayoutManager);
        contactModel = new ArrayList<>();
        contactListAdapter = new ContactListAdapter(ChatList.this, contactModel);
        rv_contact.setAdapter(contactListAdapter);
        rv_contact.addOnItemTouchListener(new RecyclerTouchListener(ChatList.this, rv_contact, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(ChatList.this, ChatActivity.class);
                String transitionName = "fade";
                View transitionView = view.findViewById(R.id.profile);
                ViewCompat.setTransitionName(transitionView, transitionName);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(ChatList.this, transitionView, transitionName);
                intent.putExtra(AppConstant.id, contactModel.get(position).getUserid());
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(Void... arg0) {
            readContacts();
            returnPhone = 0;
            originalContact.clear();
            for (String phoneNo : contactArrayList.keySet()) {
                serverContact(phoneNo, contactArrayList.get(phoneNo));
            }
            return "You are at PostExecute";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            contactListAdapter.notifyDataSetChanged();
        }
    }

    int returnPhone = 0;

    public void serverContact(String number, String original) {
        Log.e("userNum", number);
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child("users").orderByChild("phoneNumber").equalTo(number);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                returnPhone++;
                if (dataSnapshot != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.e("number//", original);
                        Log.e("number//---", postSnapshot.getKey());
                        originalContact.add(postSnapshot.getKey());
                        appConstant.saveUserInfo(original, postSnapshot.getKey(), "http://y-ral-gaming.com/admin/api/images/" + postSnapshot.getKey() + ".png?u=" + AppConstant.imageExt(), null, "", postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue() != null ? postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue().toString() : null, postSnapshot.child(AppConstant.userName).getValue() != null ? postSnapshot.child(AppConstant.userName).getValue().toString() : System.currentTimeMillis() + "");
                        contactModel.add(new ContactListModel("http://y-ral-gaming.com/admin/api/images/" + postSnapshot.getKey() + ".png?u=" + AppConstant.imageExt(), appConstant.getContactName(postSnapshot.child(AppConstant.phoneNumber).getValue(String.class)), postSnapshot.getKey(), postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue() != null ? postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue().toString() : ""));
                    }
                }
                if (contactArrayList.size() == returnPhone) {
                    SharedPreferences.Editor setEditor = shd.edit();
                    setEditor.putStringSet(AppConstant.contact, originalContact);
                    setEditor.apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void readContacts() {
        contactArrayList.clear();
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
                            Log.i("Phone Number: ", appConstant.getCountryCode());
                            contactArrayList.put(phoneNo, original);
                        }

                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
            if (!hasPermissions(ChatList.this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(ChatList.this, PERMISSIONS, REQUEST);
            } else {
                Set<String> set = shd.getStringSet(AppConstant.contact, null);
                if (set != null) {
                    for (String s : set) {
                        SharedPreferences userInfo = getSharedPreferences(s, Context.MODE_PRIVATE);
                        contactModel.add(new ContactListModel(userInfo.getString(AppConstant.myPicUrl, ""), appConstant.getContactName(userInfo.getString(AppConstant.phoneNumber, "")), userInfo.getString(AppConstant.id, ""), userInfo.getString(AppConstant.bio, "")));
                    }
                 } else
                    new readContactTask().execute();
            }
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
