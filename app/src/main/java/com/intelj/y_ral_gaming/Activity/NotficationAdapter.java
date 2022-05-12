package com.intelj.y_ral_gaming.Activity;

import android.content.Context;
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

import java.util.ArrayList;

public class NotficationAdapter extends RecyclerView.Adapter<NotficationAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<NotificationModel> notificationModelArrayList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, sub_title,time;
        ImageView profile;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            sub_title = view.findViewById(R.id.sub_title);
            time = view.findViewById(R.id.time);
            profile = view.findViewById(R.id.profile);
        }
    }

    public NotficationAdapter(Context mContext, ArrayList<NotificationModel> notificationModelArrayList) {
        this.mContext = mContext;
        this.notificationModelArrayList = notificationModelArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotficationAdapter.MyViewHolder holder, int position) {
        NotificationModel movie = notificationModelArrayList.get(position);
        holder.name.setText(movie.getName());
        holder.sub_title.setText(movie.getSubtitle());
        holder.time.setText(movie.getTime() == null ? "" : AppConstant.getTimeAgo(Long.parseLong(movie.getTime())) + "");
        Glide.with(mContext)
                .load("http://y-ral-gaming.com/admin/api/images/" + movie.getUserId() + ".png?u=" +AppConstant.imageExt())
                .apply(new RequestOptions().circleCrop()).placeholder(R.drawable.game_avatar).into(holder.profile);
    }

    @Override
    public int getItemCount() {
        return notificationModelArrayList.size();
    }
}
