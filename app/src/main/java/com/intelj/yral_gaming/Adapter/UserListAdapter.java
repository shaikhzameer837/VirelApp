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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
    private List<UserListModel> moviesList;
    private Context mContext;
    BottomSheetDialog bottomSheetDialog;
    private int positionClick;
    RecyclerView recyclerview;
    ArrayList<UserListModel> teamModel;
    MemberListAdapter userAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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


    public UserListAdapter(Context mContext,List<UserListModel> moviesList) {
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
        Glide.with(mContext).load(movie.getTitle()).centerCrop().circleCrop().placeholder(R.drawable.game_avatar).fitCenter().into(holder.imgs);
        holder.title.setText(movie.getGenre());
        holder.genre.setText(movie.getUserId());
        holder.add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionClick = position;
                showBottomSheetDialog();
            }
        });
    }
    public void showBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
        EditText teamName = bottomSheetDialog.findViewById(R.id.newTeam);
        recyclerview = bottomSheetDialog.findViewById(R.id.recyclerview);
        teamModel = new ArrayList<>();
        for (DataSnapshot snapshot : AppController.getInstance().mySnapShort.child(AppConstant.team).getChildren()) {
            SharedPreferences prefs = mContext.getSharedPreferences(snapshot.getKey(), Context.MODE_PRIVATE);
            teamModel.add(new UserListModel(prefs.getString(AppConstant.teamName, null),
                    prefs.getString(AppConstant.myPicUrl, null),
                    snapshot.getKey(),
                    prefs.getStringSet(AppConstant.teamMember, null)));
        }
        userAdapter = new MemberListAdapter(mContext, teamModel, AppConstant.applyMatches);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(userAdapter);
        bottomSheetDialog.findViewById(R.id.addTeam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(teamName.getText().toString().equals("")){
                    Toast.makeText(mContext,"Please Add new Team or select existing Team",Toast.LENGTH_LONG).show();
                    return;
                }
                saveToFireStoreData(teamName.getText().toString());
            }
        });
        bottomSheetDialog.show();
    }

    private void saveToFireStoreData(String teamName) {
        ArrayList<String> teamUserList = new ArrayList<>();
        teamUserList.add(moviesList.get(positionClick).getUserId());
        teamUserList.add(AppController.getInstance().userId);
        CollectionReference myTeam = db.collection(AppConstant.team);
        HashMap<String,Object> teamHash = new HashMap<>();
        teamHash.put(AppConstant.teamName,teamName);
        teamHash.put(AppConstant.teamMember,teamUserList);
        String uniqueId = System.currentTimeMillis()+"";
        for (String s : teamUserList) {
            FirebaseDatabase.getInstance().getReference(AppConstant.users).child(s)
                    .child(AppConstant.pinfo).child(AppConstant.team)
                    .child(uniqueId).setValue(s.equals(AppController.getInstance().userId) ? "1" : "0");
        }
        Set<String> hashSet = new HashSet<>(teamUserList);
        SharedPreferences sharedpreferences = mContext.getSharedPreferences(uniqueId, Context.MODE_PRIVATE);
        SharedPreferences.Editor editors = sharedpreferences.edit();
        editors.putString(AppConstant.teamName, teamName);
        editors.putStringSet(AppConstant.teamMember, hashSet);
        editors.putString(AppConstant.myPicUrl, "");
        editors.apply();
        myTeam.document(uniqueId).set(teamHash);
        teamModel.add(new UserListModel(teamName,
                "",
                uniqueId,
                hashSet));
        userAdapter.notifyDataSetChanged();
        recyclerview.scrollToPosition(teamModel.size()-1);
        Toast.makeText(mContext,"Team Created",Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
