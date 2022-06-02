package com.intelj.y_ral_gaming.Activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViralWeb extends AppCompatActivity {
    private ViewsSliderAdapter mAdapter;
    ViewPager2 viewPager2;
    SimpleExoPlayer exoPlayer;
    MediaSource mediaSource;
    ArrayList<SimpleExoPlayerView> SimpleExoPlayerList = new ArrayList<>();
    Object[] keys = AppController.getInstance().shortsUrlList.keySet().toArray();
    AppConstant appConstant;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.viral_web);
        appConstant = new AppConstant(this);
        viewPager2 = findViewById(R.id.view_pager);
        init();
    }

    private void init() {
        mAdapter = new ViewsSliderAdapter();
        viewPager2.setAdapter(mAdapter);
        viewPager2.registerOnPageChangeCallback(pageChangeCallback);
    }


    ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            stopVideo();
            getYoutubeVid(position);
        }
    };

    public void stopVideo() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    public void getYoutubeVid(int position) {
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(ViralWeb.this, trackSelector);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            SimpleExoPlayerList.get(position).setPlayer(exoPlayer);
            Uri videouri = Uri.parse("https://cdn.discordapp.com/attachments/911308156855005195/" + keys[position] + "/1.mp4");
            mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
            exoPlayer.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity() {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }
            });

        } catch (Exception e) {
            // below line is used for handling our errors.
            Log.e("TAG", "Error : " + e.toString());

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopVideo();
    }

    public class ViewsSliderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public ViewsSliderAdapter() {
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(viewType, parent, false);
            return new SliderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            SliderViewHolder buttonViewHolder = (SliderViewHolder) holder;
            SimpleExoPlayerList.add(buttonViewHolder.idExoPlayerVIew);
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, 0);
            if (sharedPreferences.getString(keys[position].toString(), null) == null) {
                FirebaseDatabase.getInstance().getReference().child(AppConstant.yralWeb).child(keys[position].toString()).child(AppConstant.likes).child(appConstant.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                            buttonViewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cards_heart, 0, 0);
                        else
                            buttonViewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cards_heart_outline, 0, 0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                buttonViewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(0, sharedPreferences.getString(keys[position].toString(), null).equals("0") ? R.drawable.cards_heart : R.drawable.cards_heart_outline, 0, 0);
            }
            Map<String, Object> city = new HashMap<>();
            Map<String, Object> details = new HashMap<>();
            buttonViewHolder.likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (sharedPreferences.getString(keys[position].toString(), null) == null || sharedPreferences.getString(keys[position].toString(), null).equals("1")) {
                        buttonViewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cards_heart, 0, 0);
                        FirebaseDatabase.getInstance().getReference().child(AppConstant.yralWeb).child(keys[position].toString()).child(AppConstant.likes).child(appConstant.getId()).setValue(System.currentTimeMillis() / 1000);
                        editor.putString(keys[position].toString(), "0").apply();
                        details.put("likes", FieldValue.increment(1));
                        city.put(keys[position].toString(), details);
                        FirebaseFirestore.getInstance().collection(AppConstant.yralWeb)
                                .document("video").set(city, SetOptions.merge());

                    } else {
                        buttonViewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cards_heart_outline, 0, 0);
                        editor.putString(keys[position].toString(), "1").apply();
                        FirebaseDatabase.getInstance().getReference().child(AppConstant.yralWeb).child(keys[position].toString()).child(AppConstant.likes).child(appConstant.getId()).removeValue();
                        details.put("likes", FieldValue.increment(-1));
                        city.put(keys[position].toString(), details);
                        FirebaseFirestore.getInstance().collection(AppConstant.yralWeb)
                                .document("video").set(city, SetOptions.merge());

                    }
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.viral_videos;
        }

        @Override
        public int getItemCount() {
            return AppController.getInstance().shortsUrlList.size();
        }

        public class SliderViewHolder extends RecyclerView.ViewHolder {
            SimpleExoPlayerView idExoPlayerVIew;
            TextView likes;

            public SliderViewHolder(View view) {
                super(view);
                idExoPlayerVIew = view.findViewById(R.id.idExoPlayerVIew);
                likes = view.findViewById(R.id.likes);
            }
        }
    }
}

