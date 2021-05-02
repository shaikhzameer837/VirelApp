package com.intelj.yral_gaming.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;

import java.util.ArrayList;

public class PayMentAdapter extends RecyclerView.Adapter<PayMentAdapter.MyViewHolder> {
    Context mContext;
    AppConstant appConstant;
    ArrayList<DataSnapshot> dataSnapshots;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView  pinfo;
        ImageView reg;
        public MyViewHolder(View view) {
            super(view);
              pinfo = view.findViewById(R.id.pinfo);

        }
    }
    public PayMentAdapter(Context mContext, ArrayList<DataSnapshot> dataSnapshots) {
        this.mContext = mContext;
        this.dataSnapshots = dataSnapshots;
         appConstant = new AppConstant(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.pinfo.setText(" "+dataSnapshots.get(position).getKey().replace("_"," "));
    }
    @Override
    public int getItemCount() {
        return dataSnapshots.size();
    }
}
