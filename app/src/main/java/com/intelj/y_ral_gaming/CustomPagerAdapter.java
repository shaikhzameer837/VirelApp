package com.intelj.y_ral_gaming;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.ArrayList;

public class CustomPagerAdapter extends PagerAdapter {
    AdView v = null;
    private Context mContext;
    ArrayList<String> dataSnapshots;

    public CustomPagerAdapter(Context context, ArrayList<String> dataSnapshots) {
        mContext = context;
        this.dataSnapshots = dataSnapshots;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.img_swipe, collection, false);
        ImageView imgs = layout.findViewById(R.id.imgs);
//        if (position == 1) {
//            imgs.setVisibility(View.GONE);
//            MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
//                @Override
//                public void onInitializationComplete(InitializationStatus initializationStatus) {
//                }
//            });
//            AdView mAdView = layout.findViewById(R.id.adView);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(adRequest);
//
//            mAdView.setAdListener(new AdListener() {
//                @Override
//                public void onAdLoaded() {
//                    // Code to be executed when an ad finishes loading.
//                    super.onAdLoaded();
//                    Toast.makeText(mContext, "Add loaded", Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onAdFailedToLoad(LoadAdError adError) {
//                    // Code to be executed when an ad request fails.
//                    super.onAdFailedToLoad(adError);
//                    mAdView.loadAd(adRequest);
//                }
//
//                @Override
//                public void onAdOpened() {
//                    // Code to be executed when an ad opens an overlay that
//                    // covers the screen.
//                    super.onAdOpened();
//                }
//
//                @Override
//                public void onAdClicked() {
//                    // Code to be executed when the user clicks on an ad.
//                    super.onAdClicked();
//                }
//
//                @Override
//                public void onAdClosed() {
//                    // Code to be executed when the user is about to return
//                    // to the app after tapping on an ad.
////            super.onAdClosed();
//                }
//            });
//        } else {
            imgs.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(dataSnapshots.get(position))
                    .placeholder(R.drawable.game_avatar)
                    .into(imgs);
            collection.addView(layout);
      //  }
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return "";
    }

}
