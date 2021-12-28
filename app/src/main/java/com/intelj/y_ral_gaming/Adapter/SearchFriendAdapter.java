package com.intelj.y_ral_gaming.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.intelj.y_ral_gaming.Activity.NewProfileActivity;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.ArrayList;

public class SearchFriendAdapter extends RecyclerView.Adapter<SearchFriendAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<DataSnapshot> dataSnapshots;
    public SearchFriendAdapter(Context mContext, ArrayList<DataSnapshot> dataSnapshots) {
        this.dataSnapshots = dataSnapshots;
        this.mContext = mContext;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView desc,name,count_win;
        public RelativeLayout relative_layout;
        ImageView imgs;
        public MyViewHolder(View view) {
            super(view);
            desc = view.findViewById(R.id.desc);
            name = view.findViewById(R.id.name);
            imgs = view.findViewById(R.id.imgs);
            count_win = view.findViewById(R.id.count_win);
            relative_layout = view.findViewById(R.id.relative_layout);
        }
    }

    @NonNull
    @Override
    public SearchFriendAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rank_row, parent, false);

        return new SearchFriendAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.desc.setText(dataSnapshots.get(position).child(AppConstant.phoneNumber).getValue()+"");
//        holder.desc.setText(dataSnapshots.get(position).child(AppConstant.pinfo).child("pubg").getValue()+"");
        holder.name.setText(dataSnapshots.get(position).child(AppConstant.username_search).getValue()  == null ? "User" : dataSnapshots.get(position).child(AppConstant.username_search).getValue()+"");
        holder.count_win.setText(dataSnapshots.get(position).child(AppConstant.count_win).getValue() == null ? "0" :dataSnapshots.get(position).child(AppConstant.count_win).getValue()+"");
        Glide.with(mContext).load(dataSnapshots.get(position).child(AppConstant.pinfo).child(AppConstant.myPicUrl).getValue()+"").placeholder(R.drawable.profile_icon).circleCrop().into(holder.imgs);
        holder.relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(mContext, NewProfileActivity.class);
//                AppController.getInstance().dataSnapshot = dataSnapshots.get(position);
//                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSnapshots.size();
    }
}

