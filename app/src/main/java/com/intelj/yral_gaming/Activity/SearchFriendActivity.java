package com.intelj.yral_gaming.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intelj.yral_gaming.Adapter.UserListAdapter;
import com.intelj.yral_gaming.AppController;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.model.UserListModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SearchFriendActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerview;
    private ArrayList<String> contactList;
    private ArrayList<UserListModel> userListModel;
    private ArrayList<UserListModel> userListModel2;
    private DatabaseReference mFirebaseDatabaseReference;
    private UserListAdapter userAdapter;
    private ProgressBar progress;
    SharedPreferences prefs;
    int x = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        searchView = findViewById(R.id.searchview);
        recyclerview = findViewById(R.id.recyclerview);
        progress = findViewById(R.id.progress);
        userListModel = new ArrayList<>();
        userAdapter = new UserListAdapter(SearchFriendActivity.this, userListModel);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchFriendActivity.this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(userAdapter);
        prefs = getSharedPreferences(AppConstant.AppName, Context.MODE_PRIVATE);
        displayFriends();
        findViewById(R.id.floating_action_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadContact().execute();
            }
        });
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.users);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userListModel.clear();
                if (newText.length() > 0) {
                    startingList();
                } else {
                  //  userModel =
                            startingList();
                }

                userAdapter.notifyDataSetChanged();

                return true;
            }
        });
    }

    private void displayFriends() {
        Set<String> set = prefs.getStringSet(AppConstant.users, null);
        if(set == null)
            return;
        for (String s : set) {
            SharedPreferences sharedpreferences = getSharedPreferences(s, Context.MODE_PRIVATE);
            userListModel.add(new UserListModel(sharedpreferences.getString(AppConstant.myPicUrl, ""),
                    sharedpreferences.getString(AppConstant.userName, ""),
                    sharedpreferences.getString(AppConstant.phoneNumber, ""),s));
        }
        userAdapter.notifyDataSetChanged();
    }

    private void getContactList() {
        contactList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo = phoneNo.replace(" ", "");
                        phoneNo = phoneNo.replace("+91", "");
                        if (phoneNo.startsWith("0"))
                            phoneNo = phoneNo.substring(1);
                        if (phoneNo.length() > 7 && !phoneNo.equals(AppController.getInstance().userId.replace("+91", "")) && !contactList.contains(phoneNo))
                            contactList.add(phoneNo);
                        Log.i("Phone Number: ", phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }

    class LoadContact extends AsyncTask<Void, Integer, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... arg0) {
            getContactList();
            return null;
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            startingList();
        }
    }
    private void startingList() {
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = new HashSet<>();
        x = 0;
         for (String s : contactList) {
            Query query = mFirebaseDatabaseReference.orderByChild(AppConstant.phoneNumber).equalTo(s);
            query.keepSynced(false);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    x++;
                    for (DataSnapshot childDataSnap : snapshot.getChildren()) {
                        set.add(childDataSnap.getKey());
                        SharedPreferences sharedpreferences = getSharedPreferences(childDataSnap.getKey(), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editors = sharedpreferences.edit();
                        editors.putString(AppConstant.userName, childDataSnap.child(AppConstant.pinfo).child(AppConstant.userName).getValue() + "");
                        editors.putString(AppConstant.phoneNumber, childDataSnap.child(AppConstant.phoneNumber).getValue() + "");
                        editors.putString(AppConstant.myPicUrl, childDataSnap.child(AppConstant.pinfo).child(AppConstant.myPicUrl).getValue() + "");
                        editors.apply();
                        Log.e("xyz123myPicUrl", childDataSnap.child(AppConstant.pinfo).child(AppConstant.userName).getValue() + "");
                    }
                    if (contactList.size() == x) {
                        editor.putStringSet(AppConstant.users, set);
                        editor.apply();
                        progress.setVisibility(View.GONE);
                        userAdapter.notifyDataSetChanged();
                        displayFriends();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}