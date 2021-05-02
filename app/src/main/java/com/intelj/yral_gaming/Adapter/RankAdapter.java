package com.intelj.yral_gaming.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;

import java.util.ArrayList;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<DataSnapshot> dataSnapshots;
    public RankAdapter(Context mContext, ArrayList<DataSnapshot> dataSnapshots) {
        this.dataSnapshots = dataSnapshots;
        this.mContext = mContext;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView desc,name,count_win;
        ImageView imgs;
        public MyViewHolder(View view) {
            super(view);
            desc = view.findViewById(R.id.desc);
            name = view.findViewById(R.id.name);
            imgs = view.findViewById(R.id.imgs);
            count_win = view.findViewById(R.id.count_win);
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
        holder.desc.setText("#"+(position +1)+" rank");
        holder.name.setText(dataSnapshots.get(position).child(AppConstant.pinfo).child(AppConstant.userName).getValue()+"");
        holder.count_win.setText(dataSnapshots.get(position).child(AppConstant.count_win).getValue() == null ? "0" :dataSnapshots.get(position).child(AppConstant.count_win).getValue()+"");
        Glide.with(mContext).load(dataSnapshots.get(position).child(AppConstant.pinfo).child(AppConstant.myPicUrl).getValue()+"").placeholder(R.drawable.profile_icon).circleCrop().into(holder.imgs);
    }

    @Override
    public int getItemCount() {
         return dataSnapshots.size();
    }
}
