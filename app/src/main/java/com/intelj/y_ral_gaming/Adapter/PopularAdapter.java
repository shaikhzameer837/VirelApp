package com.intelj.y_ral_gaming.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.intelj.y_ral_gaming.PopularModel;
import com.intelj.y_ral_gaming.R;

import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.MyViewHolder> {

    private List<PopularModel> moviesList;
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre;
        ImageView imgs;
        public MyViewHolder(View view) {
            super(view);
            title =   view.findViewById(R.id.title);
            genre =   view.findViewById(R.id.genre);
            imgs =   view.findViewById(R.id.imgs);
        }
    }


    public PopularAdapter(Context mContext,List<PopularModel> moviesList) {
        this.moviesList = moviesList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PopularModel movie = moviesList.get(position);
        holder.title.setText(movie.getImg_name());
        holder.genre.setText(movie.getTotal_coins()+"");
        Glide.with(mContext).load(movie.getImg_url()).placeholder(R.drawable.game_avatar).apply(new RequestOptions().circleCrop()).into(holder.imgs);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
