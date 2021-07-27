package com.intelj.yral_gaming.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.model.UserListModel;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
    private List<UserListModel> moviesList;
    private List<String> userCheck = new ArrayList<>();
    private Context mContext;
    String subject = "0";

    public List<String> getArrayList() {
        return userCheck;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre;
        public ImageView imgs;
        private CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            genre = view.findViewById(R.id.genre);
            imgs = view.findViewById(R.id.imgs);
            checkBox = view.findViewById(R.id.checkbox);
            if(subject.equals(AppConstant.user))
                checkBox.setVisibility(View.VISIBLE);
        }
    }


    public UserListAdapter(Context mContext, List<UserListModel> moviesList) {
        this.moviesList = moviesList;
        this.mContext = mContext;
    }

    public UserListAdapter(Context mContext, List<UserListModel> moviesList, String subject, ArrayList<String> checked_array) {
        this.moviesList = moviesList;
        this.mContext = mContext;
        this.subject = subject;
        if (this.moviesList.size() > 0 && checked_array != null && checked_array.size() > 0) {
            userCheck.addAll(checked_array);
        }
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
        Glide.with(mContext).load(movie.getTitle()).placeholder(R.drawable.game_avatar).apply(new RequestOptions().circleCrop()).into(holder.imgs);
        holder.title.setText(movie.getGenre());
        holder.genre.setText(movie.getUserId());
        holder.checkBox.setChecked(userCheck.contains(movie.getUserId()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (userCheck.contains(movie.getUserId()))
                    userCheck.remove(movie.getUserId());
                else
                    userCheck.add(movie.getUserId());
            }
        });

    }



    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
