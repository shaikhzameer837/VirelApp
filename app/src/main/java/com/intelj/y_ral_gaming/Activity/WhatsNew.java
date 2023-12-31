package com.intelj.y_ral_gaming.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.BuildConfig;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.databinding.WhatsNewBinding;

import java.util.ArrayList;

public class WhatsNew extends AppCompatActivity {
    WhatsNewBinding binding;
    ArrayList<Integer> contentList = new ArrayList<>();
    ArrayList<String> jsonFiles = new ArrayList<>();
    String versionCode = BuildConfig.VERSION_CODE + "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AppConstant(this).setDataFromShared(versionCode + "a", "1");
        binding = WhatsNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        contentList.add(R.drawable.poster_a);
        contentList.add(R.drawable.poster_b);
        contentList.add(R.drawable.poster_c);
        jsonFiles.add("Refer A Friend and Earn Money");
        jsonFiles.add("Introducing Viral Web \n Watch Short Gaming Videos online");
        jsonFiles.add("Have complain or suggestion Write to us");
        binding.viewpager.setAdapter(new CustomPagerAdapter(WhatsNew.this));
        binding.viewpager.setOffscreenPageLimit(3);
        setProgress();
    }

    int x = 0;
    ProgressBar progressBar;
    public void setProgress() {
        progressBar = (ProgressBar) binding.lin.getChildAt(x);
        new CountDownTimer(6000, 500) {

            public void onTick(long millisUntilFinished) {
                Log.e("setProgress", ((10000 - millisUntilFinished) / 1000) + "");
                progressBar.setProgress((int) ((10000 - millisUntilFinished) / 1000));
            }

            public void onFinish() {
                x = x + 1;
                binding.viewpager.setCurrentItem(x);
                if (x != binding.lin.getChildCount())
                    setProgress();
                else
                    launchActivity();
            }

        }.start();
    }

    public void StartActivity(View view) {
        launchActivity();
    }

    public void nextJson(View view) {
        Log.e("viewpager", binding.viewpager.getCurrentItem() + "---" + contentList.size());
        if (binding.viewpager.getCurrentItem() == (contentList.size() - 1)) {
            launchActivity();
        } else {
            binding.viewpager.setCurrentItem(binding.viewpager.getCurrentItem() + 1);
        }
    }

    private void launchActivity() {
        Intent intent = new Intent(WhatsNew.this,
                AppController.getInstance().videoDataBase.videosDao().getAllVideo().size() != 0 ? MainActivity.class
                        : ViralWeb.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.whats_new_image, collection, false);
//            TextView textView = layout.findViewById(R.id.titles);
//            textView.setText(jsonFiles.get(position));
            ImageView imgs = layout.findViewById(R.id.imgs);
            imgs.setVisibility(View.VISIBLE);
           // Glide.with(mContext).load(contentList.get(position)).into(imgs);
            Glide.with(mContext)
                    .asBitmap().load(contentList.get(position))
                    .listener(new RequestListener<Bitmap>() {
                                  @Override
                                  public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                      Log.e("glideerror",e.getMessage()) ;
                                      return false;
                                  }

                                  @Override
                                  public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                      imgs.setBackground( new BitmapDrawable(getResources(), bitmap));
                                      return false;
                                  }
                              }
                    ).submit();
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return contentList.size();
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
}
