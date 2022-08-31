package com.intelj.y_ral_gaming.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.intelj.y_ral_gaming.Activity.ProFileActivity;
import com.intelj.y_ral_gaming.PopularModel;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.MyViewHolder> {

    private List<PopularModel> moviesList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, genre,count;
        ImageView imgs;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            genre = view.findViewById(R.id.genre);
            imgs = view.findViewById(R.id.imgs);
            count = view.findViewById(R.id.count);
        }
    }


    public PopularAdapter(Context mContext, List<PopularModel> moviesList) {
        this.moviesList = moviesList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.popular_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PopularModel movie = moviesList.get(position);
        holder.title.setText(movie.getImg_name());
        holder.genre.setText(movie.getTotal_coins() + "");
        holder.count.setText(" #"+(position + 1)+ "");
        holder.imgs.setTag(position);
        holder.imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProFileActivity.class);
                String transitionName = "fade";
                View transitionView = holder.imgs;
                ViewCompat.setTransitionName(transitionView, transitionName);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) mContext, transitionView, transitionName);
                intent.putExtra("userid", moviesList.get(position).getUser_id());
                mContext.startActivity(intent, options.toBundle());
            }
        });
        Glide.with(mContext).load("http://y-ral-gaming.com/admin/api/images/"+movie.getUser_id()+".png?u=" + AppConstant.imageExt()).apply(new RequestOptions().circleCrop()).placeholder(R.drawable.game_avatar).into(holder.imgs);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
