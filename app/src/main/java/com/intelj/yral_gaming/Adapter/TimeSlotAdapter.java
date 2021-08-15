package com.intelj.yral_gaming.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.intelj.yral_gaming.AppController;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.Utils.RecyclerTouchListener;
import com.intelj.yral_gaming.model.UserListModel;
import com.intelj.yral_gaming.model.UserModel;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {
    DatabaseReference mDatabase;
    ArrayList<UserModel> allData;
    Context mContext;
    AppConstant appConstant;
    long miliSec = 0;
    String title = "";
    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    long endslot = 0;
    BottomSheetDialog bottomSheetDialog;
    SharedPreferences sharedPreferences;
    ArrayList<UserListModel> teamModel;

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
        sharedPreferences =
                mContext.getSharedPreferences
                        (AppConstant.AppName, 0);
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
        String strDate = title + " " + date + " " + allData.get(position).getTime().replace("pm", ":00:00 pm")
                .replace("am", ":00:00 am");
        if (sharedPreferences.getBoolean(strDate, false)) {
            holder.reg.setImageResource(R.drawable.check);
            holder.reg.setBackgroundResource(0);
            holder.reg.setOnClickListener(null);
        } else {
            holder.reg.setImageResource(R.drawable.arrow);
            holder.reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (new AppConstant(mContext).checkLogin())
                        showTeamList(strDate);
                    else {
                        Intent intent = new Intent("custom-event-name");
                        intent.putExtra(AppConstant.AppName, true);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    }
                }
            });
        }
        holder.info.setText(allData.get(position).getTotalCount() + "/100 members");
    }

    private void showTeamList(String strDate) {
        bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
        bottomSheetDialog.findViewById(R.id.newTeam).setVisibility(View.GONE);
        bottomSheetDialog.findViewById(R.id.bott_button).setVisibility(View.GONE);
        bottomSheetDialog.findViewById(R.id.create_team).setVisibility(View.GONE);
        TextView title = bottomSheetDialog.findViewById(R.id.title);
        title.setText("Select your team");
        TextView txt = bottomSheetDialog.findViewById(R.id.subtitle);
        txt.setText("only one team can be seelcted");
        RecyclerView recyclerview = bottomSheetDialog.findViewById(R.id.recyclerview);
        teamModel = new ArrayList<>();
        for (DataSnapshot snapshot : AppController.getInstance().mySnapShort.child(AppConstant.team).getChildren()) {
            SharedPreferences prefs = mContext.getSharedPreferences(snapshot.getKey(), Context.MODE_PRIVATE);
            teamModel.add(new UserListModel(prefs.getString(AppConstant.teamName, ""),
                    prefs.getString(AppConstant.myPicUrl, ""),
                    snapshot.getKey(),
                    prefs.getStringSet(AppConstant.teamMember, null)));
        }
        MemberListAdapter userAdapter = new MemberListAdapter(mContext, teamModel, AppConstant.team);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(userAdapter);
        bottomSheetDialog.show();
        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(mContext, recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                sendRequest(position, strDate);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void sendRequest(int position, String strDate) {
        Set<String> teamMember = teamModel.get(position).getTeamMember();
        ArrayList<String> discordId = new ArrayList<>();
        ArrayList<String> igid = new ArrayList<>();
        ArrayList<String> ign = new ArrayList<>();
        String error = "";
        for (String s : teamMember) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(s, 0);
            if(sharedPreferences.getString(AppConstant.discordId, "").equals("")){
                error = error + " Discord id not found for "+sharedPreferences.getString(AppConstant.userName, "") +"\n";
            }
            if(sharedPreferences.getString(title, "").equals("")){
                error = error + title + " Game id not found for "+sharedPreferences.getString(AppConstant.userName, "")+"\n";
            }
            if(sharedPreferences.getString(title+ "_" + AppConstant.userName, "").equals("")){
                error = error + title + " Game name not found for "+sharedPreferences.getString(AppConstant.userName, "")+"\n";
            }
            discordId.add(sharedPreferences.getString(AppConstant.discordId, ""));
            ign.add(sharedPreferences.getString(title, ""));
            igid.add(sharedPreferences.getString(title + "_" + AppConstant.userName, ""));
        }
        if(!error.equals("")){
            showDialog(error);
            return;
        }
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Registering for App, please wait.");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "http://y-ral-gaming.com/admin/api/register_matches.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean(AppConstant.success)) {
                                Toast.makeText(mContext,obj.getString(AppConstant.message),Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editShared = sharedPreferences.edit();
                                editShared.putBoolean(strDate,true);
                                editShared.apply();
                                notifyDataSetChanged();
                                bottomSheetDialog.cancel();
                            }else{
                                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                                alertDialog.setMessage(obj.getString(AppConstant.message));
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }catch (Exception e){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", teamMember.toString().replace("[", "").replace("]", ""));
                params.put("strDate", strDate);
                params.put("teamName", teamModel.get(position).getTeamName());
                params.put("teamUrl", teamModel.get(position).getTeamUrl());
                params.put("discordId", discordId.toString().replace("[", "").replace("]", ""));
                params.put("teamId", teamModel.get(position).getTeamId());
                params.put("game_name", title);
                params.put("ign", ign.toString().replace("[", "").replace("]", ""));
                params.put("igid", igid.toString().replace("[", "").replace("]", ""));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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