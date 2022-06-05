package com.intelj.y_ral_gaming.Activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.R;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {
    private List<ContactListModel> contactModel;
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,lastMess;
        ImageView iv_profile;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            lastMess = view.findViewById(R.id.lastMess);
            iv_profile = view.findViewById(R.id.profile);
        }
    }


    public ContactListAdapter(Context mContext, List<ContactListModel> contactModel) {
        this.contactModel = contactModel;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(contactModel.get(position).getName());
        //holder.lastMess.setText(contactModel.get(position).getUserid());
        Log.e("contactModel",contactModel.get(position).getProfile());
        Glide.with(mContext).load(contactModel.get(position).getProfile()).placeholder(R.drawable.game_avatar).apply(new RequestOptions().circleCrop()).into(holder.iv_profile);
//        holder.iv_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, ProFileActivity.class);
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, holder.iv_profile, ViewCompat.getTransitionName(holder.iv_profile));
//                intent.putExtra("userid", new AppConstant(mContext).getId());
//                mContext.startActivity(intent, options.toBundle());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return contactModel.size();
    }




}

