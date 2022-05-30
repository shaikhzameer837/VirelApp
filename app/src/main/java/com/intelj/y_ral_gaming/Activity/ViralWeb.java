package com.intelj.y_ral_gaming.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;

import java.util.ArrayList;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class ViralWeb extends AppCompatActivity {
    private ViewsSliderAdapter mAdapter;
    ViewPager2 viewPager2;
    SimpleExoPlayer exoPlayer;
    MediaSource mediaSource;
    ArrayList<SimpleExoPlayerView> SimpleExoPlayerList = new ArrayList<SimpleExoPlayerView>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.viral_web);
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
    public void stopVideo(){
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.release();
            exoPlayer = null;
        }
    }
    public void getYoutubeVid(int position) {
//        String youtubeLink = "https://www.youtube.com/watch?v=UKTO7VCrCRs";
//
//        new YouTubeExtractor(this) {
//            @Override
//            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
//                if (ytFiles != null) {
        try {
            //bandwisthmeter is used for getting default bandwidth
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            // track selector is used to navigate between video using a default seekbar.
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            //we are ading our track selector to exoplayer.
            exoPlayer = ExoPlayerFactory.newSimpleInstance(ViralWeb.this, trackSelector);
            // we are parsing a video url and parsing its video uri.
            // we are creating a variable for datasource factory and setting its user agent as 'exoplayer_view'
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            // we are creating a variable for extractor factory and setting it to default extractor factory.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            //we are creating a media source with above variables and passing our event handler as null,
            SimpleExoPlayerList.get(position).setPlayer(exoPlayer);
            Uri videouri = Uri.parse(AppController.getInstance().shortsUrlList.get(position));
            mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(true);
        } catch (Exception e) {
            // below line is used for handling our errors.
            Log.e("TAG", "Error : " + e.toString());

        }
//                }
//            }
//        }.extract(youtubeLink);
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

            public SliderViewHolder(View view) {
                super(view);
                idExoPlayerVIew = view.findViewById(R.id.idExoPlayerVIew);
            }
        }
    }
}

