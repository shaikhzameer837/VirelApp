package com.intelj.y_ral_gaming.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intelj.y_ral_gaming.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetFragment2 extends Fragment {


    public BottomSheetFragment2() {
        // Required empty public constructor
    }


    /*public static BottomSheetFragment2 newInstance(String param1, String param2) {
        BottomSheetFragment2 fragment = new BottomSheetFragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet2, container, false);
    }
}