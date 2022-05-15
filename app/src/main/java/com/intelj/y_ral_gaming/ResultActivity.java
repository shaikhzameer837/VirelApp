package com.intelj.y_ral_gaming;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

public class ResultActivity extends YouTubeBaseActivity {
    TextView question,timeInfo;
    Context mContext;

    String gameKey = "XjbbRCjQNKM";
    LinearLayout linearLayout;
    Boolean answer = null;
    ProgressBar progress;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        question = findViewById(R.id.question);
        timeInfo = findViewById(R.id.timeInfo);
        progress = findViewById(R.id.progress);
        linearLayout = findViewById(R.id.linearLayout);
        mContext = this;
        YouTubePlayerView youTubePlayerView =
                findViewById(R.id.player);
        youTubePlayerView.initialize(AppConstant.youtubeApiKey,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {

                        // do any work here to cue video, play video, etc.
                        youTubePlayer.cueVideo(gameKey);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                progress.setProgress((int)(millisUntilFinished / 1000));
                timeInfo.setText(((int)(millisUntilFinished / 1000))+"");
            }

            public void onFinish() {

            }

        }.start();
        FirebaseDatabase.getInstance().getReference("quiz").child(gameKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot Dsnapshot : dataSnapshot.getChildren()) {
                            int x = 0;
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            params.weight = 1.0f;
                            for (DataSnapshot snapshot : Dsnapshot.getChildren()) {
                                if (snapshot.getValue() instanceof Boolean) {
                                    x++;
                                    Button button = new Button(ResultActivity.this);
                                    button.setId(x);
                                    button.setTag(x);
                                    button.setText(snapshot.getKey());
                                    button.setLayoutParams(params);
                                    button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            v.setBackgroundResource(R.drawable.curved_red);
                                            ((Button) v).setTextColor(Color.parseColor("#ffffff"));
                                            answer = (Boolean) button.getTag();
                                        }
                                    });
                                    linearLayout.addView(button);
                                } else
                                    question.setText(snapshot.getValue(String.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }
}
