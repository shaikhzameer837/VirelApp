package com.intelj.y_ral_gaming;



import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.intelj.y_ral_gaming.model.TournamentModel;

import java.util.List;


public class TournamentAdapter extends RecyclerView.Adapter<TournamentAdapter.MyViewHolder> {
    private Context context;
    private boolean isTour;
    private List<TournamentModel> tournamentModelList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,time,status,gameName,slot;
        ImageView images;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            time = view.findViewById(R.id.time);
            status = view.findViewById(R.id.status);
            slot = view.findViewById(R.id.slot);
            images = view.findViewById(R.id.images);
            gameName = view.findViewById(R.id.gameName);
            if(isTour) {
                images.getLayoutParams().width = 180;
                images.getLayoutParams().height = 130;
            }else{
                images.getLayoutParams().width = 100;
                images.getLayoutParams().height = 100;
            }
        }
    }


    public TournamentAdapter(Context context, List<TournamentModel> tournamentModelList, boolean isTour) {
        this.context = context;
        this.tournamentModelList = tournamentModelList;
        this.isTour = isTour;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TournamentModel tournamentModel = tournamentModelList.get(position);
        holder.name.setText(tournamentModel.getName());
        holder.time.setText(tournamentModel.getDate());
        holder.slot.setText(!isTour? "" :tournamentModel.getTeam_count()+"/"+tournamentModel.getMax());
        holder.gameName.setText(tournamentModel.getGame_name());
        if(tournamentModel.getStatus().equals("0")) {
            holder.status.setText("Upcoming");
            holder.status.setTextColor(Color.parseColor("#7e241c"));
        }else if(tournamentModel.getStatus().equals("1")) {
            holder.status.setText("OnGoing");
            holder.status.setTextColor(Color.parseColor("#228B22"));
        }else if(tournamentModel.getStatus().equals("2")){
            holder.status.setText("Completed");
            holder.status.setTextColor(Color.parseColor("#888888"));
        }else{
            holder.status.setText(tournamentModel.getStatus());
            holder.status.setTextColor(Color.parseColor("#333333"));
        }
        Glide.with(context)
                .load(tournamentModel.getImage_url())
                .placeholder(R.drawable.placeholder)
                .into(holder.images);
    }

    @Override
    public int getItemCount() {
        return tournamentModelList.size();
    }


}
