package com.intelj.yral_gaming.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.model.UserListModel;

import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MyViewHolder> {
    private List<UserListModel> moviesList;
    private Context mContext;
    private String type;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre,add_user;
        public ImageView imgs;
        public MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.title);
            genre =  view.findViewById(R.id.genre);
            imgs =  view.findViewById(R.id.imgs);
            add_user =  view.findViewById(R.id.add_user);
            if(type.equals(AppConstant.team)){
                add_user.setVisibility(View.GONE);
            }
        }
    }


    public MemberListAdapter(Context mContext, List<UserListModel> moviesList, String type) {
        this.moviesList = moviesList;
        this.mContext = mContext;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        UserListModel movie = moviesList.get(position);
        Glide.with(mContext).load(movie.getTeamUrl()).centerCrop().circleCrop().placeholder(R.drawable.account_group).fitCenter().into(holder.imgs);
        holder.title.setText(movie.getTeamName());
        holder.add_user.setCompoundDrawables(null, ContextCompat.getDrawable(mContext,R.drawable.arrow), null, null);
        holder.genre.setText(movie.getTeamMember() == null ? "0" : movie.getTeamMember().size() +" Member");
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}

