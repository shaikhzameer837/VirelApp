package com.intelj.yral_gaming;

/**
 * Created by ravi on 20/02/18.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.intelj.yral_gaming.model.NotificationModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private Context context;
    private List<NotificationModel> notificationModelList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,time,status;
        ImageView images;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            time = view.findViewById(R.id.time);
            status = view.findViewById(R.id.status);
            images = view.findViewById(R.id.images);
        }
    }


    public NotificationAdapter(Context context, List<NotificationModel> notificationModelList) {
        this.context = context;
        this.notificationModelList = notificationModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotificationModel notificationModel = notificationModelList.get(position);
        holder.name.setText(notificationModel.getName());
        holder.time.setText(notificationModel.getDate());
        if(notificationModel.getStatus().equals("0")) {
            holder.status.setText("Upcoming");
            holder.status.setTextColor(Color.parseColor("#7e241c"));
        }else if(notificationModel.getStatus().equals("1")) {
            holder.status.setText("OnGoing");
            holder.status.setTextColor(Color.parseColor("#228B22"));
        }else if(notificationModel.getStatus().equals("2")){
            holder.status.setText("Completed");
            holder.status.setTextColor(Color.parseColor("#dddddd"));
        }else{
            holder.status.setText(notificationModel.getStatus());
            holder.status.setTextColor(Color.parseColor("#bbbbbb"));
        }
        Glide.with(context)
                .load(notificationModel.getImage_url())
                .placeholder(R.drawable.game_avatar)
                .into(holder.images);
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }


}
