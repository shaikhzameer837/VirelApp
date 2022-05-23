package com.intelj.y_ral_gaming;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ResultActivity extends YouTubeBaseActivity {
    TextView question, timeInfo;
    Context mContext;
   // String gameKey = "F8Jwn2DtEkM";
    String gameKey = "XjbbRCjQNKM";
    ProgressBar progress;
    LockableViewPager viewPager;
    ArrayList<DataSnapshot> dataSnapshots = new ArrayList<>();
    CustomPagerAdapter customPagerAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        question = findViewById(R.id.question);
        timeInfo = findViewById(R.id.timeInfo);
        progress = findViewById(R.id.progress);
        mContext = this;
//        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                boolean connected = snapshot.getValue(Boolean.class);
//                if (connected) {
//                    db.collection("users")
//                            .document("online")
//                            .update(gameKey, FieldValue.increment(1));
//                } else {
//                    db.collection("users")
//                            .document("online")
//                            .update(gameKey, FieldValue.increment(-1));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w(TAG, "Listener was cancelled");
//            }
//        });
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
        HashMap<String,Object> hashMap = new HashMap<>();
        FirebaseDatabase.getInstance().getReference("quiz").child(gameKey).child("3")
                        .setValue(hashMap);

        FirebaseDatabase.getInstance().getReference("quiz").child(gameKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            dataSnapshots.add(snapshot);
                        }
                        customPagerAdapter = new CustomPagerAdapter(ResultActivity.this, dataSnapshots);
                        viewPager.setAdapter(customPagerAdapter);
                        if (dataSnapshots.size() != 0)
                            startTriviaGame();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
        viewPager.setOffscreenPageLimit(1);
    }

    private void startTriviaGame() {
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                progress.setProgress((int) (millisUntilFinished / 1000));
                timeInfo.setText(((int) (millisUntilFinished / 1000)) + "");
            }

            public void onFinish() {
                customPagerAdapter.setCorrectAnswer(viewPager.getCurrentItem());
                if(!customPagerAdapter.isCorrectAnswer()){
                    Toast.makeText(ResultActivity.this,"OOPs lost" , Toast.LENGTH_LONG).show();
                    return;
                }else{
                    Toast.makeText(ResultActivity.this,"you won" , Toast.LENGTH_LONG).show();
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
