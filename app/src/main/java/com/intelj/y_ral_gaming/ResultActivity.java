package com.intelj.y_ral_gaming;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.LockableViewPager;
import com.sdsmdg.harjot.rotatingtext.RotatingTextWrapper;
import com.sdsmdg.harjot.rotatingtext.models.Rotatable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ResultActivity extends YouTubeBaseActivity {
    TextView question, timeInfo, startTime;
    Context mContext;
    // String gameKey = "F8Jwn2DtEkM";
    String gameKey = "XjbbRCjQNKM";
    ProgressBar progress;
    LockableViewPager viewPager;
    ArrayList<DataSnapshot> dataSnapshots = new ArrayList<>();
    CustomPagerAdapter customPagerAdapter;
    Long time = 0L;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        question = findViewById(R.id.question);
        timeInfo = findViewById(R.id.timeInfo);
        progress = findViewById(R.id.progress);
        startTime = findViewById(R.id.startTime);
        mContext = this;
        RotatingTextWrapper rotatingTextWrapper =  findViewById(R.id.custom_switcher);
        rotatingTextWrapper.setSize(35);

        Rotatable rotatable = new Rotatable(Color.parseColor("#FFA036"), 1000, "Word", "Word01", "Word02");
        rotatable.setSize(35);
        rotatable.setAnimationDuration(500);

        rotatingTextWrapper.setContent("This is ?", rotatable);
        YouTubePlayerView youTubePlayerView =
                findViewById(R.id.player);
        youTubePlayerView.initialize(AppConstant.youtubeApiKey,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {

                        youTubePlayer.loadVideo(gameKey);
                        //  youTubePlayer.cueVideo(gameKey);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
        viewPager = findViewById(R.id.viewpager);
        HashMap<String, Object> hashMap = new HashMap<>();
        FirebaseDatabase.getInstance().getReference("quiz").child(gameKey).child("3")
                .setValue(hashMap);

        FirebaseDatabase.getInstance().getReference("quiz").child(gameKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            dataSnapshots.add(snapshot);
                        }
                        time = dataSnapshot.child("time").getValue(Long.class);
                        if (time < (System.currentTimeMillis() / 1000))
                            showGamePlay();
                        else {
                            new CountDownTimer((time - (System.currentTimeMillis() / 1000)) * 1000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    startTime.setText("Live Game Show starts in \n" + (new SimpleDateFormat("hh:mm:ss")).format(new Date(millisUntilFinished)));
                                }

                                public void onFinish() {
                                    showGamePlay();
                                }
                            }.start();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
        viewPager.setOffscreenPageLimit(1);
    }

    private void showGamePlay() {
        findViewById(R.id.gameTime).setVisibility(View.VISIBLE);
        customPagerAdapter = new CustomPagerAdapter(ResultActivity.this, dataSnapshots);
        viewPager.setAdapter(customPagerAdapter);
        if (dataSnapshots.size() != 0)
            startTriviaGame();
    }

    private void startTriviaGame() {
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                progress.setProgress((int) (millisUntilFinished / 1000));
                timeInfo.setText(((int) (millisUntilFinished / 1000)) + "");
            }

            public void onFinish() {
                customPagerAdapter.setCorrectAnswer(viewPager.getCurrentItem());
                if (!customPagerAdapter.isCorrectAnswer()) {
                    Toast.makeText(ResultActivity.this, "OOPs lost", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Toast.makeText(ResultActivity.this, "you won", Toast.LENGTH_LONG).show();
                }
                if (viewPager.getCurrentItem() != (dataSnapshots.size() - 1)) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            startTriviaGame();
                        }
                    }, 2000);

                }
            }

        }.start();
    }
}
