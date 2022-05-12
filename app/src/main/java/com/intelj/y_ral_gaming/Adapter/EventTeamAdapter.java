package com.intelj.y_ral_gaming.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.intelj.y_ral_gaming.Activity.ContactListAdapter;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.model.EventTeamModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventTeamAdapter extends RecyclerView.Adapter<EventTeamAdapter.MyViewHolder> {
    private List<EventTeamModel> eventTeamModelList;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        ImageView imgs;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            imgs = view.findViewById(R.id.imgs);
        }
    }


    public EventTeamAdapter(Context mContext, List<EventTeamModel> eventTeamModelList) {
        this.eventTeamModelList = eventTeamModelList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_team_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EventTeamModel eventTeamModel = eventTeamModelList.get(position);
        holder.title.setText(eventTeamModel.getImg_name());
        holder.imgs.setTag(position);
        try {
            JSONObject jsonObject = new JSONObject(eventTeamModel.getTeamMember());
            holder.imgs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                    bottomSheetDialog.setContentView(R.layout.contacts);
                    TextView title = bottomSheetDialog.findViewById(R.id.refresh);
                    title.setText(eventTeamModel.getImg_name());
                    title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    bottomSheetDialog.findViewById(R.id.la_contact).setVisibility(View.GONE);
                    RecyclerView userRecyclerView = bottomSheetDialog.findViewById(R.id.rv_contact);
                    ArrayList<ContactListModel> contactModel = new ArrayList<>();
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        try {
                            if (jsonObject.get(key) instanceof JSONObject) {
                                Log.e("lcat teamObj", ((JSONObject) jsonObject.get(key)).getString("ingName"));
                                contactModel.add(new ContactListModel("http://y-ral-gaming.com/admin/api/images/" + key + ".png?u=" + AppConstant.imageExt(), ((JSONObject) jsonObject.get(key)).getString("ingName"), key, ""));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ContactListAdapter userListAdapter = new ContactListAdapter(mContext, contactModel);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                    userRecyclerView.setLayoutManager(mLayoutManager);
                    userRecyclerView.setAdapter(userListAdapter);
                    bottomSheetDialog.show();
                }
            });
        } catch (Exception e) {

        }
        Glide.with(mContext).load(eventTeamModel.getImg_url()).placeholder(R.drawable.placeholder).into(holder.imgs);
    }

    @Override
    public int getItemCount() {
        return eventTeamModelList.size();
    }
}
