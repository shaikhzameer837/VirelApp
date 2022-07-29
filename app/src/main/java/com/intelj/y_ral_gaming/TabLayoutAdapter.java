package com.intelj.y_ral_gaming;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabLayoutAdapter extends FragmentPagerAdapter {

    Context mContext;
    int mTotalTabs;

    public TabLayoutAdapter(Context context , FragmentManager fragmentManager , int totalTabs) {
        super(fragmentManager);
        mContext = context;
        mTotalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d("asasas" , position + "");
        switch (position) {
            case 0:
                return new ShowCoins();
            case 1:
                return new ShowCoins();
            case 2:
                return new ShowCoins();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }
}
