package com.intelj.y_ral_gaming.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.model.TeamListPOJO;

import java.util.ArrayList;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    private ArrayList<TeamListPOJO> listdata;

    // RecyclerView recyclerView;
    public MyListAdapter(ArrayList<TeamListPOJO> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(listdata.get(position).getName());
        Log.e("holder.name",listdata.get(position).getName());
        holder.amount.setVisibility(!listdata.get(position).getPlaying_status().equals("0") ? View.VISIBLE : View.GONE);
        holder.amount.setText(listdata.get(position).getPlaying_status());
        holder.status.setText(!listdata.get(position).getPlaying_status().equals("0") ? "Played" : "Regsitered");
        Glide.with(holder.imgs.getContext()).load(AppConstant.AppUrl + "images/"+listdata.get(position).getUserId()+".png?u=" + AppConstant.imageExt()).apply(new RequestOptions().circleCrop()).placeholder(R.drawable.game_avatar).into(holder.imgs);
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name,status,amount;
        ImageView imgs;
        public ViewHolder(View itemView) {
            super(itemView);
            this.name =  itemView.findViewById(R.id.name);
            this.amount =  itemView.findViewById(R.id.amount);
            this.status =  itemView.findViewById(R.id.status);
            this.imgs =  itemView.findViewById(R.id.imgs);
        }
    }
}
