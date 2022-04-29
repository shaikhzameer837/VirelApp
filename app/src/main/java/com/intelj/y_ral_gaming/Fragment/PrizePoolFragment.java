package com.intelj.y_ral_gaming.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SplashScreenStory;

public class PrizePoolFragment extends Fragment {
    View rootView;
    String subj;
    public PrizePoolFragment(String subj) {
        this.subj = subj;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.prize_pool, container, false);
        Glide.with(getActivity()).load(subj).fitCenter().into((ImageView) rootView.findViewById(R.id.imgs));
        return rootView;
    }
}
