package com.intelj.y_ral_gaming.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.ArrayList;

public class SuggestionViewAdapter extends RecyclerView.Adapter<SuggestionViewAdapter.RecyclerViewHolder> {

    private ArrayList<SuggesstionModel> courseDataArrayList;
    private Context mcontext;

    public SuggestionViewAdapter(ArrayList<SuggesstionModel> suggesstionModelArrayList, Context mcontext) {
        this.courseDataArrayList = suggesstionModelArrayList;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.
        SuggesstionModel suggesstionModel = courseDataArrayList.get(position);
        holder.courseTV.setText(suggesstionModel.getTitle());
        Glide.with(mcontext).load(suggesstionModel.getImgid()).placeholder(R.drawable.game_avatar).apply(new RequestOptions().circleCrop()).into(holder.courseIV);
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return courseDataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView courseTV;
        private ImageView courseIV;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTV = itemView.findViewById(R.id.idTVCourse);
            courseIV = itemView.findViewById(R.id.idIVcourseIV);
        }
    }
}
