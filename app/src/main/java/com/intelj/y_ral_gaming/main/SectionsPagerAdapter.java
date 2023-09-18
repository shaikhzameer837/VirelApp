package com.intelj.y_ral_gaming.main;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.Fragment.PaymentHistory;
import com.intelj.y_ral_gaming.Fragment.PaymentStatus;
import com.intelj.y_ral_gaming.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    String coins = "";
    String reward_id = "";
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm,String coin,String rewardId) {
        super(fm);
        mContext = context;
        coins = coin;
        reward_id = rewardId;
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("AppController-amount-1", AppController.getInstance().amount+"");
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){

            case 2 :
                return  PaymentStatus.newInstance(position + 1);
            case 1 :
                return  PaymentHistory.newInstance(position + 1);
            default:
                Log.d("shouldLoading2: ", reward_id);
                return   PlaceholderFragment.newInstance(position + 1,coins,reward_id);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}