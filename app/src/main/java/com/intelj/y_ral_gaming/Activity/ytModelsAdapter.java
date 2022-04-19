package com.intelj.y_ral_gaming.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.intelj.y_ral_gaming.R;

import java.util.List;

public class ytModelsAdapter extends RecyclerView.Adapter<ytModelsAdapter.MyViewHolder> {
    private List<ytModel> moviesList;
    Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgs;
        public MyViewHolder(View view) {
            super(view);
            imgs = view.findViewById(R.id.imgs);
        }
    }


    public ytModelsAdapter(Context context,List<ytModel> moviesList) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.popular_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ytModel movie = moviesList.get(position);
        Glide.with(context).load(movie.getGenre()).circleCrop().into(holder.imgs);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}