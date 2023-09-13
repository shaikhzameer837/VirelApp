package com.intelj.y_ral_gaming.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.intelj.y_ral_gaming.Activity.ProFileActivity;
import com.intelj.y_ral_gaming.PopularModel;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AmazonUrlOpener;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.List;

import soup.neumorphism.NeumorphCardView;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.MyViewHolder> {

    private List<PopularModel> moviesList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, genre;
        ImageView imgs;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            genre = view.findViewById(R.id.genre);
            // byNow = view.findViewById(R.id.byNow);
            imgs = view.findViewById(R.id.imgs);
            count = view.findViewById(R.id.count);
            cardView = view.findViewById(R.id.cardView);
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
        Gson gson = new Gson();
        PopularModel movie = moviesList.get(position);
        String jsonString = gson.toJson(movie);
        Log.e("jsonString", jsonString);
        holder.title.setText(movie.getName());
        holder.count.setText("" + (position + 1));
//        holder.rating.setMax(5 - position);
//        holder.rating.setRating(5 - position);
        //   holder.count.setText(""+(position+1)+"");
        holder.genre.setText(movie.getAmount());
        //       holder.byNow.setText(movie.getTotal_coins() + "");
        holder.imgs.setTag(position);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("jsonString-userid", moviesList.get(position).getUser_id()+"");
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
        Glide.with(mContext).load(movie.getUrl()+ ".png?u=" + AppConstant.imageExt()).apply(new RequestOptions().circleCrop()).placeholder(R.drawable.game_avatar).into(holder.imgs);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
