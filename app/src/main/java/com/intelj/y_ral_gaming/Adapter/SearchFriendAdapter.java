package com.intelj.y_ral_gaming.Adapter;

import android.content.Context;
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

        public MyViewHolder(View view) {
            super(view);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rank_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return dataSnapshots.size();
    }
}

