package com.intelj.y_ral_gaming.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.intelj.y_ral_gaming.R;

public class PostFragment extends Fragment {
    String userid;
    View rootView;
    public PostFragment() {
    }
   public PostFragment(String userid) {
        this.userid =userid;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.post_fragment, container, false);

        return rootView;
    }
}
