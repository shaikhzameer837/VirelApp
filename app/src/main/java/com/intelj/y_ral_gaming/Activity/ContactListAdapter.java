package com.intelj.y_ral_gaming.Activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.ArrayList;
import java.util.List;

import soup.neumorphism.NeumorphCardView;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {
    private List<ContactListModel> contactModel;
    private List<String> checkBoxList = new ArrayList<>();
    Context mContext;
    boolean isCheckBoxVisible = false;
    boolean isButtonVisible = false;
    ArrayList<String> teamMember = new ArrayList<>();
    public int setChecBox(int position) {
        if (position != -1 && !checkBoxList.contains(contactModel.get(position).getUserid())) {
            checkBoxList.add(contactModel.get(position).getUserid());
        }
        else if(position != -1) {
            checkBoxList.remove(contactModel.get(position).getUserid());
        }
        notifyDataSetChanged();
        return  position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, lastMess,nCount,addButton;
        ImageView iv_profile;
        NeumorphCardView arrow;
        CheckBox checkbox;
        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            addButton = view.findViewById(R.id.addButton);
            lastMess = view.findViewById(R.id.lastMess);
            iv_profile = view.findViewById(R.id.profile);
            arrow = view.findViewById(R.id.arrow);
            checkbox = view.findViewById(R.id.checkbox);
            nCount = view.findViewById(R.id.nCount);
        }
    }


    public ContactListAdapter(Context mContext, List<ContactListModel> contactModel) {
        this.contactModel = contactModel;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list, parent, false);

        return new MyViewHolder(itemView);
    }
    public void checkVisible(){
        isCheckBoxVisible = true;
        notifyDataSetChanged();
    }
  public void checkButtonVisible(){
        isButtonVisible = true;
      isCheckBoxVisible = false;
      notifyDataSetChanged();
    }
    public List<String> getSelectedList(){

        return checkBoxList;
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.name.setText(contactModel.get(position).getName());
        holder.lastMess.setText(contactModel.get(position).getBio());
        Log.e("contactModel", contactModel.get(position).getProfile());
        holder.checkbox.setChecked(checkBoxList.contains(contactModel.get(position).getUserid()));
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxList.contains(contactModel.get(position).getUserid())){
                    checkBoxList.remove(contactModel.get(position).getUserid());
                }else{
                    checkBoxList.add(contactModel.get(position).getUserid());
                }
                notifyDataSetChanged();
            }
        });
        Log.e("isCheckBoxVisible",isCheckBoxVisible+"");
        holder.checkbox.setVisibility(isCheckBoxVisible ? View.VISIBLE : View.GONE);
        holder.addButton.setVisibility(isButtonVisible ? View.VISIBLE : View.GONE);
        holder.addButton.setText(teamMember.contains(contactModel.get(position).getBio()) ? "Remove" : "Add");
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teamMember.add(contactModel.get(position).getBio());
                notifyDataSetChanged();
            }
        });
//        holder.arrow.setVisibility(isCheckBoxVisible ? View.GONE : View.VISIBLE);
//        holder.arrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(checkBoxList.contains(contactModel.get(position).getUserid())){
//                    checkBoxList.remove(contactModel.get(position).getUserid());
//                }else{
//                    checkBoxList.add(contactModel.get(position).getUserid());
//                }
//                notifyDataSetChanged();
//            }
//        });
        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        Glide.with(mContext).load(AppConstant.AppUrl + "images/" + contactModel.get(position).getUserid() + ".png?u=" + AppConstant.imageExt()).placeholder(R.drawable.game_avatar).apply(new RequestOptions().circleCrop()).into(holder.iv_profile);
    }

    @Override
    public int getItemCount() {
        return contactModel.size();
    }


}

