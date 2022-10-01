package com.intelj.y_ral_gaming.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.intelj.y_ral_gaming.Activity.ProFileActivity;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SigninActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;

public class SuggestionViewAdapter extends RecyclerView.Adapter<SuggestionViewAdapter.RecyclerViewHolder> {

    private ArrayList<SuggesstionModel> courseDataArrayList;
    private Context mcontext;
    AppConstant appConstant;

    public SuggestionViewAdapter(ArrayList<SuggesstionModel> suggesstionModelArrayList, Context mcontext) {
        this.courseDataArrayList = suggesstionModelArrayList;
        this.mcontext = mcontext;
        appConstant = new AppConstant(mcontext);
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
        SuggesstionModel suggesstionModel = courseDataArrayList.get(position);
        holder.courseTV.setText(suggesstionModel.getTitle() == null ? "Player" : suggesstionModel.getTitle());
        if (suggesstionModel.isVerified())
            holder.courseTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.verified, 0);
        else
            holder.courseTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        holder.courseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, ProFileActivity.class);
                String transitionName = "fade";
                View transitionView = v;
                ViewCompat.setTransitionName(transitionView, transitionName);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) mcontext, transitionView, transitionName);
                intent.putExtra("userid", suggesstionModel.getUserId());
                mcontext.startActivity(intent, options.toBundle());

            }
        });
        Glide.with(mcontext).load(suggesstionModel.getImgid()).placeholder(R.drawable.game_avatar).apply(new RequestOptions().circleCrop()).into(holder.courseIV);
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!appConstant.checkLogin()) {
                    mcontext.startActivity(new Intent(mcontext, SigninActivity.class));
                    return;
                }
                if (holder.follow.getText().toString().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference(AppConstant.users).child(suggesstionModel.getUserId()).child(AppConstant.profile).child(AppConstant.follower).child(appConstant.getId()).setValue((System.currentTimeMillis() / 1000));
                    FirebaseDatabase.getInstance().getReference(AppConstant.users).child(appConstant.getId()).child(AppConstant.profile).child(AppConstant.following).child(suggesstionModel.getUserId()).setValue((System.currentTimeMillis() / 1000));
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(AppConstant.subject, "follow");
                    hashMap.put(AppConstant.id, appConstant.getId());
                    hashMap.put(AppConstant.name, appConstant.getName());
                    FirebaseFirestore.getInstance().collection(AppConstant.realTime)
                            .document(suggesstionModel.getUserId()).collection(AppConstant.noti)
                            .document((System.currentTimeMillis() / 1000)+"").set(hashMap);
                    FirebaseDatabase.getInstance().getReference(AppConstant.users).child(suggesstionModel.getUserId()).child(AppConstant.realTime).child(AppConstant.noti).child((System.currentTimeMillis() / 1000) + "").setValue(hashMap);
                    holder.follow.setText("unfollow");
                    holder.follow.setTextColor(Color.parseColor("#333333"));
                    holder.follow.setBackgroundResource(R.drawable.curved_white);
                } else {
                    FirebaseDatabase.getInstance().getReference(AppConstant.users).child(suggesstionModel.getUserId()).child(AppConstant.profile).child(AppConstant.follower).child(appConstant.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference(AppConstant.users).child(appConstant.getId()).child(AppConstant.profile).child(AppConstant.following).child(suggesstionModel.getUserId()).removeValue();
                    holder.follow.setText("follow");
                    holder.follow.setTextColor(Color.WHITE);
                    holder.follow.setBackgroundResource(R.drawable.curved_blue);
                }
            }
        });
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
        Button follow;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            courseTV = itemView.findViewById(R.id.idTVCourse);
            courseIV = itemView.findViewById(R.id.idIVcourseIV);
            follow = itemView.findViewById(R.id.follow);
        }
    }
}
