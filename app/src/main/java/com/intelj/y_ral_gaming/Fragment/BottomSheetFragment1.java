package com.intelj.y_ral_gaming.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelj.y_ral_gaming.R;


public class BottomSheetFragment1 extends Fragment {



    public BottomSheetFragment1() {
        // Required empty public constructor
    }


    /*public static BottomSheetFragment1 newInstance(String param1, String param2) {
        BottomSheetFragment1 fragment = new BottomSheetFragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet1, container, false);
    }
}