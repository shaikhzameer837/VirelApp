package com.intelj.y_ral_gaming;

/**
 * Created by ravi on 20/02/18.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.intelj.y_ral_gaming.model.NotificationModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private Context context;
    private boolean isTour;
    private List<NotificationModel> notificationModelList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,time,status,gameName;
        ImageView images;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            time = view.findViewById(R.id.time);
            status = view.findViewById(R.id.status);
            images = view.findViewById(R.id.images);
            gameName = view.findViewById(R.id.gameName);
            if(isTour) {
                images.getLayoutParams().width = 180;
                images.getLayoutParams().height = 130;
            }else{
                images.getLayoutParams().width = 100;
                images.getLayoutParams().height = 100;
            }
        }
    }


    public NotificationAdapter(Context context, List<NotificationModel> notificationModelList,boolean isTour) {
        this.context = context;
        this.notificationModelList = notificationModelList;
        this.isTour = isTour;
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
        holder.gameName.setText(notificationModel.getGame_name());
        if(notificationModel.getStatus().equals("0")) {
            holder.status.setText("Upcoming");
            holder.status.setTextColor(Color.parseColor("#7e241c"));
        }else if(notificationModel.getStatus().equals("1")) {
            holder.status.setText("OnGoing");
            holder.status.setTextColor(Color.parseColor("#228B22"));
        }else if(notificationModel.getStatus().equals("2")){
            holder.status.setText("Completed");
            holder.status.setTextColor(Color.parseColor("#888888"));
        }else{
            holder.status.setText(notificationModel.getStatus());
            holder.status.setTextColor(Color.parseColor("#333333"));
        }
        Glide.with(context)
                .load(notificationModel.getImage_url())
                .placeholder(R.drawable.game_avatar)
                .into(holder.images);
        holder.images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView image = new ImageView(context);
                // image.setImageResource(R.drawable.YOUR_IMAGE_ID);
                Glide.with(context)
                        .load(notificationModel.getImage_url())
                        .placeholder(R.drawable.game_avatar)
                        .into(image);
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(context).
                                setMessage("").
                                setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).
                                setView(image);
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }


}
