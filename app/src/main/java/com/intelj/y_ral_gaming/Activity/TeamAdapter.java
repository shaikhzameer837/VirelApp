package com.intelj.y_ral_gaming.Activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.ArrayList;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.MyViewHolder> {
    private final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference(AppConstant.users);
    Context mContext;
    ArrayList<DataSnapshot> userInfoList;
    ArrayList<String> idList ;
    String type;
    public TeamAdapter(Context mContext, ArrayList<DataSnapshot> userInfoList, String type) {
        this.mContext = mContext;
        this.userInfoList = userInfoList;
        this.type = type;
        idList = new ArrayList<>();
        for (DataSnapshot dataSnapshot : AppController.getInstance().userInfoList) {
            idList.add(dataSnapshot.getKey()+"");
            Log.d("dataSnapshot",dataSnapshot.getKey()+"");
        }
     }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, info;
        TextView reg;
        ImageView profilePic;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            reg = view.findViewById(R.id.reg);
            info = view.findViewById(R.id.info);
            profilePic = view.findViewById(R.id.profilePic);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list, parent, false);

        return new TeamAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamAdapter.MyViewHolder holder, final int position) {
        holder.title.setText(userInfoList.get(position).child(AppConstant.userName).getValue() + "");
        Glide.with(mContext)
                .load(userInfoList.get(position).child(AppConstant.myPicUrl).getValue() + "")
                .placeholder(R.drawable.game_avatar).circleCrop()
                .into(holder.profilePic);
        if (userInfoList.get(position).getKey().equals(new AppConstant(mContext).getUserId())) {
            holder.reg.setText("my profile");
            holder.reg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else if (idList.contains(userInfoList.get(position).getKey())) {
            holder.reg.setText("remove from team");
            holder.reg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.close, 0, 0, 0);
        } else {
            holder.reg.setText("Add to team");
            holder.reg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.add, 0, 0, 0);
        }
        holder.reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idList.contains(userInfoList.get(position).getKey())) {
                    mDatabase.child(AppController.getInstance().userId).child(AppConstant.myTeam).child(userInfoList.get(position).getKey()).
                            removeValue();
                    mDatabase.child(userInfoList.get(position).getKey()).child(AppConstant.myTeam).child(AppController.getInstance().userId).
                            removeValue();
                    idList.remove(position);
                }else{
                    mDatabase.child(AppController.getInstance().userId).child(AppConstant.myTeam).child(userInfoList.get(position).getKey()).
                            setValue(AppController.getInstance().userId);
                    mDatabase.child(userInfoList.get(position).getKey()).child(AppConstant.myTeam).child(AppController.getInstance().userId).
                            setValue(0);
                    idList.add(userInfoList.get(position).getKey());
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userInfoList.size();
    }
}
