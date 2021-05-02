package com.intelj.yral_gaming.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intelj.yral_gaming.Adapter.TimeSlotAdapter;
import com.intelj.yral_gaming.AppController;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.model.UserModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class OneFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tv_coming_soon;
    View rootView;
    private TimeSlotAdapter mAdapter;
    ArrayList<UserModel> allData = new ArrayList<>();
    DatabaseReference mDatabase;
    long miliSec = 0;
    String title;
    Boolean show_listview;
    String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
     Date resultDate;
    public OneFragment() {
        // Required empty public constructor
    }

    public OneFragment(String title) {
        this.title = title;
    }

    public OneFragment(String title , Boolean show_listview) {
        this.title = title;
        this.show_listview = show_listview;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
//            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            Log.i("IsRefresh", "Yes");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_one, container, false);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        tv_coming_soon = rootView.findViewById(R.id.tv_coming_soon);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TimeSlotAdapter(getActivity(), allData,title);

        if (show_listview) {
            recyclerView.setAdapter(mAdapter);
            mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.live_stream);
            mDatabase.child(AppController.getInstance().channelId).child(date).child(title).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (String s : AppController.getInstance().timeArray) {
                        String strDate = date + " " + s.replace("pm", ":00:00 pm")
                                .replace("am", ":00:00 am");
                        if (AppController.getInstance().userId != null) {
                            if (miliSec == 0 && dataSnapshot.child(s).child(AppConstant.member).child(AppController.getInstance().userId).exists()) {
                                try {
                                    resultDate = AppConstant.dateFormat.parse(strDate);
                                    miliSec = resultDate.getTime();
                                    Log.e("miliSeRec:-", date + "/" + title + "/" + s);
                                    Intent intent = new Intent("custom-event-name");
                                    intent.putExtra("message", (miliSec - System.currentTimeMillis()) + "");
                                    intent.putExtra("roomPlan", date + "/" + title + "/" + s);
                                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                                } catch (Exception e) {
                                    Log.e("miliSeRec:-", e.getMessage() + "");
                                    e.printStackTrace();
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                }
                            }
                            UserModel userModel = new UserModel()
                                    .setRegisterd(dataSnapshot.child(s).child(AppConstant.member).child(AppController.getInstance().userId).exists())
                                    .setTotalCount(dataSnapshot.child(s).child(AppConstant.member).getChildrenCount())
                                    .setTime(s).setuniqueDate(date + "/" + title + "/" + s).userModelBuilder();
                            allData.add(userModel);
                            tv_coming_soon.setVisibility(View.GONE);
                        }
                    }
                    mAdapter.notifyDataSetChanged();

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }
        else {
            recyclerView.setVisibility(View.GONE);
            tv_coming_soon.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

}