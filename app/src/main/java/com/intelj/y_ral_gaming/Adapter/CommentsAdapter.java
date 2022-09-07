package com.intelj.y_ral_gaming.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.intelj.y_ral_gaming.Comments;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {
    private List<Comments> commentsArrayList;
    private Context mContext;
    private String myId;

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

    public CommentsAdapter(Context mContext, List<Comments> commentsArrayList) {
        this.mContext = mContext;
        this.commentsArrayList = commentsArrayList;
    }

    @Override
    public CommentsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);
        return new CommentsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentsAdapter.MyViewHolder holder, int position) {
        Comments comments = commentsArrayList.get(position);
        Glide.with(mContext)
                .load(AppConstant.AppUrl + "images/" + comments.getUser_id() + ".png?u=" +AppConstant.imageExt())
                .apply(new RequestOptions().circleCrop()).placeholder(R.drawable.game_avatar).into(holder.iv_profile);

        holder.name.setText(comments.getName());
        holder.lastMess.setText(comments.getComments());
    }

    @Override
    public int getItemCount() {
        return commentsArrayList.size();
    }
}
