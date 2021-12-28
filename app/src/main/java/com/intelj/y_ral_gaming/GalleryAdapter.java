package com.intelj.y_ral_gaming;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<String> movieList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgs;

        public MyViewHolder(View view) {
            super(view);
            imgs =  view.findViewById(R.id.imgs);
        }
    }


    public GalleryAdapter(Context mContext, List<String> moviesList) {
        this.movieList = moviesList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(mContext).load(movieList.get(position))
                .apply(new RequestOptions()).into(holder.imgs);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}
