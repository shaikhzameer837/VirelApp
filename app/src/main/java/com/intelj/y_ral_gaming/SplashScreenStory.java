package com.intelj.y_ral_gaming;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashScreenStory extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    DatabaseReference mDatabase;
    private static final int PROGRESS_COUNT = 3;
    private StoriesProgressView storiesProgressView;
    private ImageView image;
    TextView desc;
    private TextView priceMoney;
    private int counter = 0;
    List<String> posterImage = new ArrayList<>();
    ArrayList<String> imageText = new ArrayList<>();
    long pressTime = 0L;
    long limit = 500L;
    private AppConstant appConstant;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashscreen_story);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        priceMoney = findViewById(R.id.priceMoney);
        priceMoney.setText("collect 5 coins For Daily login");
        appConstant = new AppConstant(this);
        mDatabase = FirebaseDatabase.getInstance().getReference(appConstant.users);
        priceMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        String splashscreen = AppController.getInstance().remoteConfig.getString(AppConstant.splashscreen);
//        try {
//            JSONObject jsonObject = new JSONObject(splashscreen);
//            JSONArray keys = jsonObject.names();
//            for (int i = 0; i < keys.length(); i++) {
//                String key = keys.getString(i); // Here's your key
//                String value = jsonObject.getString(key);
//                posterImage.add(key);
//                imageText.add(value);
//            }
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            FirebaseCrashlytics.getInstance().recordException(e);
//        }
        storiesProgressView = findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(PROGRESS_COUNT);
        storiesProgressView.setStoryDuration(3000L);
        storiesProgressView.setStoriesListener(this);
        storiesProgressView.startStories(counter);
        image = findViewById(R.id.image);
        desc = findViewById(R.id.desc);
      //  if (posterImage.size() == 0) {
            final String[] resources = new String[]{
                    "https://cdnb.artstation.com/p/assets/images/images/025/525/671/large/as-editor-pubg-poster.jpg?1586085669",
                    "https://cdn-products.eneba.com/resized-products/dEdZeooTmlzeNGBKPTKCbL7Ob_wvjzO4sNYvr1f-xYU_350x200_1x-0.jpg",
                    "https://m.media-amazon.com/images/M/MV5BNmNhM2NjMTgtNmIyZC00ZmVjLTk4YWItZmZjNGY2NThiNDhkXkEyXkFqcGdeQXVyODU4MDU1NjU@._V1_.jpg"};
            posterImage = Arrays.asList(resources);
       // }

        Glide.with(SplashScreenStory.this).load(posterImage.get(0)).fitCenter().into(image);
        desc.setText(imageText.size() > 0 ? imageText.get(0) : "");
        // bind reverse view
        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
        findViewById(R.id.skipScreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().startToRunActivity();
            }
        });
    }

    @Override
    public void onNext() {
        Glide.with(SplashScreenStory.this).load(posterImage.get(++counter)).into(image);
        desc.setText(imageText.size() > 0 ? imageText.get(counter) : "");
//        if (counter == 1 && appConstant.getNextCoinTime() < (System.currentTimeMillis() / 1000))
//            priceMoney.setVisibility(View.VISIBLE);
//        else
//            priceMoney.setVisibility(View.GONE);
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        Glide.with(SplashScreenStory.this).load(posterImage.get(--counter)).into(image);
      //  desc.setText(imageText.size() > 0 ? imageText.get(counter) : "");

    }

    @Override
    public void onComplete() {

        AppController.getInstance().startToRunActivity();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }

}
