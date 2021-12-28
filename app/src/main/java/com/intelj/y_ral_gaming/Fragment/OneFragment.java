package com.intelj.y_ral_gaming.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.Adapter.TimeSlotAdapter;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.model.UserModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OneFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tv_coming_soon;
    private ImageView imageView;
    View rootView;
    private TimeSlotAdapter mAdapter;
    ArrayList<UserModel> allData;
    DatabaseReference mDatabase;
    long miliSec = 0;
    String title;
    boolean show_listview = true;
    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    Date resultDate;
    public OneFragment() {
        // Required empty public constructor
    }


    public OneFragment(String title, Boolean show_listview) {
        this.title = title;
        this.show_listview = show_listview;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    public void onResumeView() {
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        allData = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recycler_view);
        tv_coming_soon = rootView.findViewById(R.id.tv_coming_soon);
        imageView = rootView.findViewById(R.id.imageview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        Glide.with(this).load(R.drawable.coming_soon).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(imageView);
        if (show_listview) {
            recyclerView.setVisibility(View.VISIBLE);
            tv_coming_soon.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tv_coming_soon.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }
        initializedata();
        for (String s : AppController.getInstance().timeArray) {
//            String strDate = date + " " + s.replace("pm", ":00:00 pm")
//                    .replace("am", ":00:00 am");
            if (AppController.getInstance().userId != null) {
//                        if (miliSec == 0 && dataSnapshot.child(s).child(AppConstant.member).child(AppController.getInstance().userId).exists()) {
//                            try {
//                                resultDate = AppConstant.dateFormat.parse(strDate);
//                                miliSec = resultDate.getTime();
//                                Log.e("miliSeRec:-", date + "/" + title + "/" + s);
//                                Intent intent = new Intent("custom-event-name");
//                                intent.putExtra("message", (miliSec - System.currentTimeMillis()) + "");
//                                intent.putExtra("roomPlan", date + "/" + title + "/" + s);
//                                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                FirebaseCrashlytics.getInstance().recordException(e);
//                            }
//                        }
                UserModel userModel = new UserModel()
                        .setRegisterd(false)
                        .setTotalCount(0)
                        .setTime(s).setuniqueDate(date + "/" + title + "/" + s).userModelBuilder();
                allData.add(userModel);
            }
        }
//                mAdapter.notifyDataSetChanged();
        initializedata();
//        mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.live_stream);
//        mDatabase.child(AppController.getInstance().channelId).child(date).child(title).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//
//            }
//        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_one, container, false);
        onResumeView();
        return rootView;
    }

    private void initializedata() {
        mAdapter = new TimeSlotAdapter(getActivity(), allData, title);
        recyclerView.setAdapter(mAdapter);
    }

}