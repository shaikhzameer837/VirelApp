package com.intelj.y_ral_gaming;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

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

import java.util.ArrayList;

public class ResultActivity extends YouTubeBaseActivity {
    TextView question, timeInfo;
    Context mContext;
    String gameKey = "XjbbRCjQNKM";
    ProgressBar progress;
    LockableViewPager viewPager;
    ArrayList<DataSnapshot> dataSnapshots = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        question = findViewById(R.id.question);
        timeInfo = findViewById(R.id.timeInfo);
        progress = findViewById(R.id.progress);
        mContext = this;
        YouTubePlayerView youTubePlayerView =
                findViewById(R.id.player);
        youTubePlayerView.initialize(AppConstant.youtubeApiKey,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        startTriviaGame();
                        youTubePlayer.cueVideo(gameKey);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
        viewPager = findViewById(R.id.viewpager);

    }

    private void startTriviaGame() {
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                progress.setProgress((int) (millisUntilFinished / 1000));
                timeInfo.setText(((int) (millisUntilFinished / 1000)) + "");
            }

            public void onFinish() {
                if (viewPager.getCurrentItem() != (dataSnapshots.size() - 1)) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    startTriviaGame();
                }
            }

        }.start();
        FirebaseDatabase.getInstance().getReference("quiz").child(gameKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            dataSnapshots.add(snapshot);
                        }
                        viewPager.setAdapter(new CustomPagerAdapter(ResultActivity.this, dataSnapshots));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }
}
