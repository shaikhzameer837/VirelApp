package com.intelj.yral_gaming;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.intelj.yral_gaming.Activity.MainActivity;
import com.intelj.yral_gaming.Activity.PreRegistartionActivity;
import com.intelj.yral_gaming.Activity.SplashScreen;
import com.intelj.yral_gaming.Utils.AppConstant;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SplashScreenStory extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private static final int PROGRESS_COUNT = 3;

    private StoriesProgressView storiesProgressView;
    private ImageView image;
    private TextView priceMoney;

    private int counter = 0;
    private final String[] resources = new String[]{
            "https://cdnb.artstation.com/p/assets/images/images/025/525/671/large/as-editor-pubg-poster.jpg?1586085669",
            "https://www.thehindu.com/sci-tech/technology/w0t8kp/article33084076.ece/alternates/FREE_615/pubg-indiaJPG",
            "https://static.displate.com/857x1200/displate/2020-07-23/9deff3383d395493cdd7ea395cc7800b_ca0037f2de2d6f9aff59a7bd843df0b5.jpg",
    };


    long pressTime = 0L;
    long limit = 500L;

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
        priceMoney.setText("collect 200 coins");
        storiesProgressView =  findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(PROGRESS_COUNT);
        storiesProgressView.setStoryDuration(3000L);
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations);
        storiesProgressView.setStoriesListener(this);
//        storiesProgressView.startStories();
        // counter = 1;
          storiesProgressView.startStories(counter);

        image =  findViewById(R.id.image);
        Glide.with(SplashScreenStory.this).load(resources[0]).fitCenter().into(image);
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
       /* try {
            TimeTCPClient client = new TimeTCPClient();
            try {
                // Set timeout of 60 seconds
                client.setDefaultTimeout(1000000);
                client.connect("time.nist.gov");
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = formatter.format(client.getDate());
                try {
                    Date date = formatter.parse(dateString);
                    long timeInMillis = date.getTime();
                    Log.e("ServerValue", timeInMillis+"");
                    SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putLong("lastkey", timeInMillis);
                    editor.apply();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            } finally {
                client.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        findViewById(R.id.skipScreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();

                Runnable runnable = new Runnable() {
                    public void run() {
                        AppController.getInstance().mFirebaseRemoteConfig.fetchAndActivate();
                        if (AppController.getInstance().mFirebaseRemoteConfig.getString("Pre_registration").equalsIgnoreCase("yes")) {
                            Intent intent = null;
                            if (!new AppConstant(SplashScreenStory.this).checkLogin()) {
                                intent = new Intent(SplashScreenStory.this, SigninActivity.class);
                            }
                            else intent = new Intent(SplashScreenStory.this, PreRegistartionActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(SplashScreenStory.this, MainActivity.class);
                            startActivity(intent);
                        }
                        AppController.getInstance().getGameName();
                    }
                };
                worker.schedule(runnable, 4, TimeUnit.SECONDS);
            }
        });
    }

    @Override
    public void onNext() {
        if(counter == resources.length){
            Intent intent = new Intent(SplashScreenStory.this, MainActivity.class);
            startActivity(intent);
        }
            Glide.with(SplashScreenStory.this).load(resources[++counter]).into(image);
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0) return;
        Glide.with(SplashScreenStory.this).load(resources[--counter]).into(image);
    }

    @Override
    public void onComplete() {
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }
}
