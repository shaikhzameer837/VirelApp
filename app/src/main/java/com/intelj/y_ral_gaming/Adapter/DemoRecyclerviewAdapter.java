package com.intelj.y_ral_gaming.Adapter;

import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.intelj.y_ral_gaming.Activity.NewProfileActivity;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.model.DemoRecyclerviewModel;

import java.util.List;

public class DemoRecyclerviewAdapter extends RecyclerView.Adapter<DemoRecyclerviewAdapter.MyViewHolder> {

    private List<DemoRecyclerviewModel> moviesList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, gameId, discordId, gameName;
        public ImageView imgView;

        public MyViewHolder(View view) {
            super(view);
            title =  view.findViewById(R.id.title);
            gameId =view.findViewById(R.id.gameId);
            discordId =  view.findViewById(R.id.discordId);
            gameName = view.findViewById(R.id.gameName);
            imgView = view.findViewById(R.id.imgs);
        }
    }


    public DemoRecyclerviewAdapter(List<DemoRecyclerviewModel> moviesList, Context context) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.demo_recyclerview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        DemoRecyclerviewModel movie = moviesList.get(position);
        holder.title.setText(movie.getTitle());
        holder.gameId.setText(movie.getGameId());
        holder.discordId.setText(movie.getDiscordId());
        holder.gameName.setText(movie.getGameName());
        Glide.with(context).load(movie.getImage()).placeholder(R.drawable.profile_icon).into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}

