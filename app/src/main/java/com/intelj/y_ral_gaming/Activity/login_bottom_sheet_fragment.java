package com.intelj.y_ral_gaming.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.intelj.y_ral_gaming.R;

public class login_bottom_sheet_fragment extends BottomSheetDialogFragment {
    Button btn_ok;
    Button btn_cancel;

    public login_bottom_sheet_fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_sheet, container, false);
    }
}