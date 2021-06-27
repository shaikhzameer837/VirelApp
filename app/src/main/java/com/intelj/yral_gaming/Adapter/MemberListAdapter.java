package com.intelj.yral_gaming.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intelj.yral_gaming.AppController;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.model.UserListModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MyViewHolder> {
    private List<UserListModel> moviesList;
    private Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre,add_user;
        public ImageView imgs;
        public MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.title);
            genre =  view.findViewById(R.id.genre);
            imgs =  view.findViewById(R.id.imgs);
            add_user =  view.findViewById(R.id.add_user);
        }
    }


    public MemberListAdapter(Context mContext,List<UserListModel> moviesList) {
        this.moviesList = moviesList;
        this.mContext = mContext;
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
        Glide.with(mContext).load(movie.getTeamUrl()).centerCrop().circleCrop().placeholder(R.drawable.game_avatar).fitCenter().into(holder.imgs);
        holder.title.setText(movie.getTeamName());
        holder.add_user.setCompoundDrawables(null, ContextCompat.getDrawable(mContext,R.drawable.arrow), null, null);
        holder.add_user.setText(movie.getTeamMember() == null ? "0" : movie.getTeamMember().size() +" Member");
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}

