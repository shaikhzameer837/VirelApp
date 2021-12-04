package com.intelj.yral_gaming.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.intelj.yral_gaming.Fragment.BottomSheetDilogFragment;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;

import java.util.Set;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    ViewPager mViewPager;
    ViewPagerAdapter mViewPagerAdapter;
    View rootView;
    private Context context;
    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_main2, container, false);
        //Initializing the ViewPager Object
        mViewPager = (ViewPager)rootView.findViewById(R.id.viewPagerMain);

        //Initializing the ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapter(getActivity(), mViewPager);


        //Adding the Adapter to the ViewPager
        mViewPager.setOffscreenPageLimit(0);
        mViewPager.setAdapter(mViewPagerAdapter);
        return rootView;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(AppConstant.teamMember, false) == true) {
                Set<String> myList = (Set<String>) intent.getSerializableExtra("teammeber");
                Log.i("mylist",myList+"");
//                mViewPagerAdapter = new ViewPagerAdapter(getActivity(), myList);
                mViewPager.setCurrentItem(1);
//                return;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver,new IntentFilter("call_position2"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        dialog.dismiss();
    }
}
