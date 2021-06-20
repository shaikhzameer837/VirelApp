package com.intelj.yral_gaming.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intelj.yral_gaming.Adapter.RankAdapter;
import com.intelj.yral_gaming.Adapter.SearchFriendAdapter;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;

import java.util.ArrayList;

public class SearchFriendActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerview;
    private ArrayList<DataSnapshot> dataSnapshots = new ArrayList<>();
    private ArrayList<String> contactList;
    private DatabaseReference mFirebaseDatabaseReference;
    private SearchFriendAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);
        searchView = findViewById(R.id.searchview);
        recyclerview = findViewById(R.id.recyclerview);
        pAdapter = new SearchFriendAdapter(SearchFriendActivity.this, dataSnapshots);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchFriendActivity.this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(pAdapter);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.users);
        getContactList();
        startingList();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dataSnapshots.clear();
                if (newText.length() > 0) {
//                Query query = mFirebaseDatabaseReference.child(AppConstant.pinfo).child(AppConstant.userName).startAt(newText).endAt(newText+"\uf8ff");
                    Query query = mFirebaseDatabaseReference.orderByChild(AppConstant.username_search).startAt(newText).endAt(newText + "\uf8ff");

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                dataSnapshots.add(child);
                            }
                            pAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    startingList();
                }

                pAdapter.notifyDataSetChanged();

                return true;
            }
        });
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
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i("Name: ", name);
                        Log.i("Phone Number with: ", phoneNo);
                        phoneNo = phoneNo.replace(" ", "");
                        phoneNo = phoneNo.replace("+91", "");
                        phoneNo = phoneNo.replace("0", "");
                        /*if (phoneNo.contains(" "))
                            phoneNo = phoneNo.replace(" ","");
                        if (phoneNo.contains("+91"))
                            phoneNo = phoneNo.replace("+91","");
                        if (phoneNo.startsWith("0"))
                            phoneNo = phoneNo.replace("0","");*/
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

    private void startingList() {
        dataSnapshots.clear();
        for (String s : contactList) {
            Query query = mFirebaseDatabaseReference.orderByChild(AppConstant.phoneNumber).equalTo(s);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        dataSnapshots.add(child);
                    }
                    pAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}