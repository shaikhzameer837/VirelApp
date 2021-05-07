package com.intelj.yral_gaming.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.intelj.yral_gaming.AppController;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.model.UserModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {
    DatabaseReference mDatabase;
    ArrayList<UserModel> allData;
    Context mContext;
    AppConstant appConstant;
    long miliSec = 0;
    String title = "";
    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    long endslot = 0 ;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, info;
        ImageView reg;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            reg = view.findViewById(R.id.reg);
            info = view.findViewById(R.id.info);
        }
    }


    public TimeSlotAdapter(Context mContext, ArrayList<UserModel> allData, String title) {
        this.mContext = mContext;
        this.allData = allData;
        this.title = title;
        appConstant = new AppConstant(mContext);
        mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.live_stream);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.title.setText(allData.get(position).getTime());

        holder.reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if(System.currentTimeMillis() < miliSec) {
                if (!new AppConstant(mContext).checkLogin()) {
                    Intent intent = new Intent("custom-event-name");
                    intent.putExtra("message", "bottom_sheet_broadcast");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                } else if (!allData.get(position).getRegisterd()) {
                        try {
                            String strDate = date + " " + allData.get(position).getTime().replace("pm", ":00:00 pm")
                                    .replace("am", ":00:00 am");
                            Date resultDate = AppConstant.dateFormat.parse(strDate);
                            miliSec = resultDate.getTime();
                            endslot = miliSec + 3600000;
                            Log.e("miliSec", miliSec + "");
                            Log.e("miliSec", System.currentTimeMillis() + "");
                        } catch (ParseException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    if (miliSec < System.currentTimeMillis()) {
                       /* Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                        Date tomorrow = calendar.getTime();
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        date = dateFormat.format(tomorrow);*/
                        Toast.makeText(mContext, "Time is up .Slot is not avialable.", Toast.LENGTH_SHORT).show();
                        return;
                    }

//                    if (appConstant.getbooking() == 0 || appConstant.getbooking() < System.currentTimeMillis()) {
//                            showBottomSheet(position);
//                    }
//                    else
//                    {
//                        Toast.makeText(mContext,"First play the game in first slot",Toast.LENGTH_LONG).show();
//                        return;
//                    }

                    showBottomSheet(position);
                }
                // }else
                //    Toast.makeText(mContext,"sorry",Toast.LENGTH_LONG).show();
            }
        });
        if (allData.get(position).getRegisterd()) {
            holder.reg.setImageResource(R.drawable.check);
            holder.reg.setBackgroundResource(0);
        } else {
            holder.reg.setImageResource(R.drawable.arrow);
        }
        holder.info.setText(allData.get(position).getTotalCount() + "/100 members");
    }

    private void showBottomSheet(final int position) {
        final SharedPreferences shd = mContext.getSharedPreferences(AppConstant.saveYTid, 0);
        View myView = LayoutInflater.from(mContext).inflate(R.layout.my_view, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        final EditText gameId = myView.findViewById(R.id.gameId);
        TextView textId = myView.findViewById(R.id.textId);
        textId.setText("Enter your " + title + " id");
        gameId.setText(AppController.getInstance().mySnapShort == null ? "" : AppController.getInstance().mySnapShort.child(
                AppConstant.pinfo).child(title).getValue() == null ? "" :
                AppController.getInstance().mySnapShort.child(AppConstant.pinfo).child(title).getValue() + "");
        myView.findViewById(R.id.books).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appConstant.savebooking(endslot);
                if (!gameId.getText().toString().equals("")) {
                    FirebaseDatabase.getInstance().getReference(AppConstant.users)
                            .child(AppController.getInstance().userId).child(AppConstant.pinfo).child(title).setValue(gameId.getText().toString());
                    if (allData.get(position).getRegisterd()) {
                        mDatabase.child(AppController.getInstance().channelId).child(date).child(title).child(allData.get(position).getTime()).child(AppConstant.member).child(AppController.getInstance().userId).
                                removeValue();
                        allData.get(position).setRegisterd(false);
                        allData.get(position).setTotalCount(allData.get(position).getTotalCount() - 1);
                        shd.edit().putString(AppConstant.saveYTid, "").apply();
                    } else {
                        allData.get(position).setTotalCount(allData.get(position).getTotalCount() + 1);
                        mDatabase.child(AppController.getInstance().channelId).child(date).child(title).child(allData.get(position).getTime()).child(AppConstant.member).child(AppController.getInstance().userId).
                                setValue(miliSec);
                        allData.get(position).setRegisterd(true);
                        shd.edit().putString(AppConstant.saveYTid, allData.get(position).getStrDate()).apply();
                    }
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        dialog.setContentView(myView);
        dialog.show();
        ((View) myView.getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return allData.size();
    }
}