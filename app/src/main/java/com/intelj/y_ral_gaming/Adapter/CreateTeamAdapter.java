package com.intelj.y_ral_gaming.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.RoundedBottomSheetDialog;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTeamAdapter extends RecyclerView.Adapter<CreateTeamAdapter.MyViewHolder> {
    private List<ContactListModel> contactModel;
    Context mContext;
    ArrayList<String> teamMember = new ArrayList<>();
    SharedPreferences sharedPreferences;
    String contactList;
    String loyalFriends = "";
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, phoneNumber;
        Button addButton;
        ImageView iv_profile;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            phoneNumber = view.findViewById(R.id.number);
            addButton = view.findViewById(R.id.addButton);
            iv_profile = view.findViewById(R.id.profile);
        }
    }

    public List<String> getSelectedList() {

        return teamMember;
    }

    @NonNull
    @Override
    public CreateTeamAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_team, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateTeamAdapter.MyViewHolder holder, int position) {
        holder.name.setText(contactModel.get(position).getName());
        holder.phoneNumber.setText(contactModel.get(position).getPhoneNumber());
        holder.addButton.setText(teamMember.contains(contactModel.get(position).getUserid()) ? "Added" : "Add");
        holder.addButton.setTextColor(teamMember.contains(contactModel.get(position).getUserid()) ? Color.parseColor("#ffffff") : Color.parseColor("#000000"));
        holder.addButton.setBackgroundResource(teamMember.contains(contactModel.get(position).getUserid()) ? R.drawable.curved_red : R.drawable.curved_white);
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loyalFriends.contains(contactModel.get(position).getPhoneNumber()+",")){
                    checkIfNumberExist(position);
                } else {
                    if (!teamMember.contains(contactModel.get(position).getUserid())) {
                        teamMember.add(contactModel.get(position).getUserid());
                    }else {
                        teamMember.remove(contactModel.get(position).getUserid());
                    }
                    notifyDataSetChanged();
                }
            }
        });
        Glide.with(mContext).load(contactModel.get(position).getProfile()).placeholder(R.drawable.game_avatar).circleCrop().into(holder.iv_profile);
    }

    public void checkIfNumberExist(int position) {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Creating Team...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = AppConstant.AppUrl + "check_phone_number.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.cancel();
                        Log.e("locat_response", response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(!jsonObject.getBoolean("success")){
                                inviteFriend(position);
                            }else{
                                SharedPreferences userInfo = mContext.getSharedPreferences(contactModel.get(position).getPhoneNumber(), Context.MODE_PRIVATE);
                                userInfo.edit().putString(AppConstant.id,jsonObject.getString("userId")).apply();
                                loyalFriends = contactModel.get(position).getPhoneNumber()  + "," +loyalFriends;
                                sharedPreferences.edit().putString(AppConstant.contact,loyalFriends).apply();
                                teamMember.add(contactModel.get(position).getPhoneNumber());
                                contactModel.get(position).setUserid(jsonObject.getString("userId"));
                                notifyDataSetChanged();
                            }
                        }catch (Exception e){
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
/*                shimmer_container.hideShimmer();
                shimmer_container.setVisibility(View.GONE);*/
                error.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("number",contactModel.get(position).getPhoneNumber());
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

    private void inviteFriend(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.invite, null);
        final BottomSheetDialog dialogBottom = new RoundedBottomSheetDialog(mContext);
         dialogBottom.setContentView(view);
        dialogBottom.show();
        TextView subText = view.findViewById(R.id.subText);
        TextView titleText = view.findViewById(R.id.titleText);
        titleText.setText(contactModel.get(position).getName() + " is not on Y-ral Gaming \n Why not invite "+contactModel.get(position).getName()+"  \uD83D\uDE09");
        subText.setText("Earn 10rs Instantly by inviting");
        view.findViewById(R.id.inviteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out Y-ral Gaming app play BGMI , Free Fire & Valorant game with Free entry and earn per kill and use 'YRAL" + new AppConstant(mContext).getId() + "' as a Referral code : https://play.google.com/store/apps/details?id=com.intelj.y_ral_gaming");
                sendIntent.setType("text/plain");
                mContext.startActivity(sendIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactModel.size();
    }

    public CreateTeamAdapter(Context mContext, List<ContactListModel> contactModel,String loyalFriends) {
        this.contactModel = contactModel;
        this.loyalFriends = loyalFriends;
        this.mContext = mContext;
        sharedPreferences = mContext.getSharedPreferences(AppConstant.AppName,0);
        contactList = sharedPreferences.getString(AppConstant.contact,"");
    }
}
