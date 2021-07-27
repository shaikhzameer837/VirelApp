package com.intelj.yral_gaming.Activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intelj.yral_gaming.Adapter.MemberListAdapter;
import com.intelj.yral_gaming.Adapter.UserListAdapter;
import com.intelj.yral_gaming.AppController;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.model.UserListModel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchFriendActivity extends AppCompatActivity {
    private EditText et_search;
    private RecyclerView recyclerview;
    private ArrayList<String> contactList;
    private ArrayList<UserListModel> userListModel;
    private ArrayList<UserListModel> userListModel2;
    private DatabaseReference mFirebaseDatabaseReference;
    private UserListAdapter userAdapter;
    private ProgressBar progress;
    SharedPreferences prefs;
    BottomSheetDialog bottomSheetDialog;
    ArrayList<UserListModel> teamModel;
    MemberListAdapter userListAdapter;
    RecyclerView userRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_friend);
        et_search = findViewById(R.id.et_search);
        recyclerview = findViewById(R.id.recyclerview);
        progress = findViewById(R.id.progress);
        teamModel = new ArrayList<>();
        userListModel = new ArrayList<>();
        /*ArrayList<String> myList = new ArrayList<>();
        if (getIntent() != null)
        myList = (ArrayList<String>) getIntent().getSerializableExtra("member_list");
        userAdapter = new UserListAdapter(SearchFriendActivity.this, userListModel, AppConstant.user, myList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchFriendActivity.this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(userAdapter);*/
        prefs = getSharedPreferences(AppConstant.AppName, Context.MODE_PRIVATE);
        displayFriends();
        findViewById(R.id.floating_action_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadContact().execute();
            }
        });
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.users);

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userListModel.clear();
                if (s.length() == 0) {
                    userListModel.addAll(userListModel2);
                } else {
                    for (UserListModel listModel : userListModel2) {
                        if (listModel.getGenre().toLowerCase().contains(s.toString().toLowerCase())) {
                            userListModel.add(listModel);
                        }
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void showBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
        EditText teamName = bottomSheetDialog.findViewById(R.id.newTeam);
        bottomSheetDialog.findViewById(R.id.create_team).setVisibility(View.GONE);
        userRecyclerView = bottomSheetDialog.findViewById(R.id.recyclerview);
        teamModel = new ArrayList<>();
        for (DataSnapshot snapshot : AppController.getInstance().mySnapShort.child(AppConstant.team).getChildren()) {
            SharedPreferences prefs = getSharedPreferences(snapshot.getKey(), Context.MODE_PRIVATE);
            teamModel.add(new UserListModel(prefs.getString(AppConstant.teamName, ""),
                    prefs.getString(AppConstant.myPicUrl, ""),
                    snapshot.getKey(),
                    prefs.getStringSet(AppConstant.teamMember, null)));
        }
        userListAdapter = new MemberListAdapter(this, teamModel, AppConstant.team);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        userRecyclerView.setLayoutManager(mLayoutManager);
        userRecyclerView.setAdapter(userListAdapter);
        bottomSheetDialog.findViewById(R.id.addTeam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (teamName.getText().toString().equals("")) {
                    Toast.makeText(SearchFriendActivity.this, "Please Add new Team or select existing Team", Toast.LENGTH_LONG).show();
                    return;
                }
                saveToFireStoreData(teamName.getText().toString());
            }
        });
        bottomSheetDialog.show();
    }

    private void saveToFireStoreData(String teamName) {

        List<String> teamUserList = userAdapter.getArrayList();
        teamUserList.add(AppController.getInstance().userId);
        FirebaseFirestore.getInstance().enableNetwork();
        CollectionReference myTeam = db.collection(AppConstant.team);
        HashMap<String, Object> teamHash = new HashMap<>();
        teamHash.put(AppConstant.teamName, teamName);
        teamHash.put(AppConstant.teamMember, teamUserList);
        String uniqueId = System.currentTimeMillis() + "";
        for (String s : teamUserList) {
            FirebaseDatabase.getInstance().getReference(AppConstant.users).child(s)
                    .child(AppConstant.pinfo).child(AppConstant.team)
                    .child(uniqueId).setValue(s.equals(AppController.getInstance().userId) ? "1" : "0");
        }
        Set<String> hashSet = new HashSet<>(teamUserList);
        SharedPreferences sharedpreferences = getSharedPreferences(uniqueId, Context.MODE_PRIVATE);
        SharedPreferences.Editor editors = sharedpreferences.edit();
        editors.putString(AppConstant.teamName, teamName);
        editors.putStringSet(AppConstant.teamMember, hashSet);
        editors.putString(AppConstant.myPicUrl, "");
        editors.apply();
        myTeam.document(uniqueId).set(teamHash);
        teamModel.add(new UserListModel(teamName,
                "",
                uniqueId,
                hashSet));
        userListAdapter.notifyDataSetChanged();
        userRecyclerView.scrollToPosition(teamModel.size() - 1);
        Toast.makeText(this, "Team Created", Toast.LENGTH_LONG).show();
    }

    private void displayFriends() {
        userListModel2 = new ArrayList<>();
        userListModel.clear();
        Set<String> set = prefs.getStringSet(AppConstant.users, null);
        if (set == null)
            return;
        for (String s : set) {
            SharedPreferences sharedpreferences = getSharedPreferences(s, Context.MODE_PRIVATE);
            userListModel.add(new UserListModel(sharedpreferences.getString(AppConstant.myPicUrl, ""),
                    sharedpreferences.getString(AppConstant.userName, ""),
                    sharedpreferences.getString(AppConstant.phoneNumber, ""), s));
        }
        userListModel2.addAll(userListModel);
        ArrayList<String> myList = new ArrayList<>();
        if (getIntent() != null)
            myList = (ArrayList<String>) getIntent().getSerializableExtra("member_list");
        userAdapter = new UserListAdapter(SearchFriendActivity.this, userListModel, AppConstant.user, myList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchFriendActivity.this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(userAdapter);

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

    public void AddUser(View view) {
        List<String> teamUserList = userAdapter.getArrayList();
        if (getIntent().getExtras() == null)
            showBottomSheetDialog();
        else {
            String group_id = getIntent().getExtras().getString("group_id");
            teamUserList.add(AppController.getInstance().userId);
            CollectionReference myTeam = db.collection(AppConstant.team);
            HashMap<String, Object> teamHash = new HashMap<>();
            teamHash.put(AppConstant.teamName, getIntent().getExtras().getString("team_name"));
            teamHash.put(AppConstant.teamMember, teamUserList);
            myTeam.document(group_id).set(teamHash);
            Set<String> hashSet = new HashSet<>(teamUserList);
            SharedPreferences sharedpreferences = getSharedPreferences(group_id, Context.MODE_PRIVATE);
            SharedPreferences.Editor editors = sharedpreferences.edit();
            editors.putStringSet(AppConstant.teamMember, hashSet);
            editors.apply();
            Toast.makeText(SearchFriendActivity.this, "Member updated successfully", Toast.LENGTH_LONG).show();
            finish();
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
                        editors.putString(AppConstant.discordId, childDataSnap.child(AppConstant.pinfo).child(AppConstant.discordId).getValue() + "");
                        for (Map.Entry<String, Boolean> entry : AppController.getInstance().gameNameHashmap.entrySet()) {
                            editors.putString(entry.getKey(), childDataSnap.child(AppConstant.pinfo).child(entry.getKey()).getValue() + "");
                        }
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