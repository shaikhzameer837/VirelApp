package com.intelj.y_ral_gaming.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.databinding.CreateTeamBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class CreateTeam extends AppCompatActivity {
    private CreateTeamBinding binding;
    AppConstant appConstant;
    ContactListAdapter contactListAdapter;
    ArrayList<ContactListModel> contactModel;
    SharedPreferences shd;

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

    private void loadChats() {
        Set<String> set = shd.getStringSet(AppConstant.contact, null);
        try {
            if (set != null) {
                for (String s : set) {
                    SharedPreferences userInfo = getSharedPreferences(s, Context.MODE_PRIVATE);
                    contactModel.add(new ContactListModel(s, userInfo.getString(AppConstant.myPicUrl, ""), appConstant.getContactName(userInfo.getString(AppConstant.phoneNumber, "")), userInfo.getString(AppConstant.id, ""), userInfo.getString(AppConstant.bio, "")));
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
