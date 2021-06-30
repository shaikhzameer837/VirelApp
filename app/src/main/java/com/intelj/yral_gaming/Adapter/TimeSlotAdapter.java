package com.intelj.yral_gaming.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
                //Intent waIntent = new Intent(Intent.ACTION_SEND);
                //waIntent.setType("text/plain");
//                Intent waIntent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://discord.gg/9Shnr3nY"));
//                String text = "YOUR TEXT HERE @zacshooter#4354";
//                waIntent.setPackage("com.discord");
//                waIntent.putExtra(Intent.EXTRA_TEXT, text);
//                mContext.startActivity(Intent.createChooser(waIntent, "Share with"));
                showTeamList();

                // if(System.currentTimeMillis() < miliSec) {
//                if (!new AppConstant(mContext).checkLogin()) {
//                    Intent intent = new Intent("custom-event-name");
//                    intent.putExtra("message", "bottom_sheet_broadcast");
//                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
//                } else if (!allData.get(position).getRegisterd()) {
//                        try {
//                            String strDate = date + " " + allData.get(position).getTime().replace("pm", ":00:00 pm")
//                                    .replace("am", ":00:00 am");
//                            Date resultDate = AppConstant.dateFormat.parse(strDate);
//                            miliSec = resultDate.getTime();
//                            endslot = miliSec + 3600000;
//                            Log.e("miliSec", miliSec + "");
//                            Log.e("miliSec", System.currentTimeMillis() + "");
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                            FirebaseCrashlytics.getInstance().recordException(e);
//                        }
//                    if (miliSec < System.currentTimeMillis()) {
//
//                        Toast.makeText(mContext, "Time is up .Slot is not avialable.", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//
//                    showBottomSheet(position);
//                }

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
    private void showTeamList() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
        bottomSheetDialog.findViewById(R.id.newTeam).setVisibility(View.GONE);
        bottomSheetDialog.findViewById(R.id.bott_button).setVisibility(View.GONE);
        RecyclerView recyclerview = bottomSheetDialog.findViewById(R.id.recyclerview);
        teamModel = new ArrayList<>();
        for (DataSnapshot snapshot : AppController.getInstance().mySnapShort.child(AppConstant.team).getChildren()) {
            SharedPreferences prefs = mContext.getSharedPreferences(snapshot.getKey(), Context.MODE_PRIVATE);
            teamModel.add(new UserListModel(prefs.getString(AppConstant.teamName, null),
                    prefs.getString(AppConstant.myPicUrl, null),
                    snapshot.getKey(),
                    prefs.getStringSet(AppConstant.teamMember, null)));
        }
        MemberListAdapter userAdapter = new MemberListAdapter(mContext, teamModel,AppConstant.applyMatches);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(userAdapter);
        bottomSheetDialog.show();
        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(mContext, recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                sendRequest(position);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void sendRequest(int position) {
        Set<String> teamMember = teamModel.get(position).getTeamMember();
        JSONObject json = new JSONObject();
        try {
            json.put("set", teamMember);
        }catch (Exception e){

        }
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "http://y-ral-gaming.com/demo.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.e("Response is: ", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error Response is ", "That didn't work!");
                Log.e("error Response is ", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", json.toString());
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