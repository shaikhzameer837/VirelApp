package com.intelj.yral_gaming;

/**
 * Created by ravi on 20/02/18.
 */

import android.content.Context;
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
        public TextView note;
        public TextView time;
        ImageView images;
        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.note);
            time = view.findViewById(R.id.time);
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

        holder.note.setText(notificationModel.getStatus());

        // Displaying dot from HTML character code
        holder.time.setText(notificationModel.getDate());

        Glide.with(context)
                .load(notificationModel.getImage_url())
                .placeholder(R.drawable.game_avatar)
                .into(holder.images);
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        return "";
    }
}
