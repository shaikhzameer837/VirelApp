package com.intelj.y_ral_gaming.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.intelj.y_ral_gaming.RoundedBottomSheetDialog;
import com.google.android.exoplayer2.ExoPlayerFactory;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.intelj.y_ral_gaming.Adapter.CommentsAdapter;
import com.intelj.y_ral_gaming.Comments;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.db.AppDataBase;
import com.intelj.y_ral_gaming.db.VideoList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViralWeb extends AppCompatActivity {
    ViewPager2 viewPager2;
    SimpleExoPlayer exoPlayer;
    int oldPosition = 0;
    AppConstant appConstant;
    ArrayList<SimpleExoPlayerView> SimpleExoPlayerList = new ArrayList<>();
    ArrayList<SimpleExoPlayer> exoplayerList = new ArrayList<>();
    ViewsSliderAdapter mAdapter;
    public ArrayList<VideoList> shortsUrlList = new ArrayList<>();
    AppDataBase videoDataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.viral_web);
        videoDataBase = AppDataBase.getDBInstance(this, AppConstant.AppName);
        appConstant = new AppConstant(this);
        viewPager2 = findViewById(R.id.view_pager);
        SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shortsUrlList.clear();
                exoplayerList.clear();
                SimpleExoPlayerList.clear();
                pullToRefresh.setRefreshing(false);
                getVideoFromDataBase(); // your code
            }
        });
        getVideoFromDataBase();
    }
    public void loadVideo(View v){
        Button btn = (Button)v;
        shortsUrlList.clear();
        shortsUrlList.addAll(btn.getTag().toString().equals("0") ? videoDataBase.videosDao().getAllVideo() : videoDataBase.videosDao().getAllSelectedVideo(btn.getTag()+""));
        mAdapter.notifyDataSetChanged();
        setLinear(btn);
    }
    public void setLinear(Button view){
        LinearLayout linearLayout = findViewById(R.id.linz);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            Button btn = (Button) linearLayout.getChildAt(i);
            if(btn.getTag() == view.getTag()){
                btn.setBackgroundResource(R.drawable.edittext_bottom_line);
            }else{
                btn.setBackgroundResource(0);
            }
        }
    }
    private void getVideoFromDataBase() {
        shortsUrlList.addAll(videoDataBase.videosDao().getAllVideo());
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
            exoplayerList.get(oldPosition).setPlayWhenReady(false);
            exoplayerList.get(oldPosition).getPlaybackState();
            exoplayerList.get(position).setPlayWhenReady(true);
            exoplayerList.get(position).getPlaybackState();
            oldPosition = position;
        }
    };


    ArrayList<Comments> commentsArrayList = new ArrayList<>();
    CommentsAdapter commentsAdapter;

    private void reloadData(String s) {
        RequestQueue queue = Volley.newRequestQueue(ViralWeb.this);
        String url = AppConstant.AppUrl + "comment_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick31", response);
                        try {
                            commentsArrayList.clear();
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                JSONArray jsoNArray = json.getJSONArray("info");
                                for (int i = 0; i < jsoNArray.length(); i++) {
                                    JSONObject jObj = jsoNArray.getJSONObject(i);
                                    Comments comments = new Comments(jObj.getString("id"), jObj.getString("comments"), jObj.getString("user_id"), jObj.getString("name"));
                                    commentsArrayList.add(comments);
                                }
                            } else {
                                Toast.makeText(ViralWeb.this, "Something Went wrong", Toast.LENGTH_LONG).show();
                            }
                            commentsAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("comment_id", s);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void postComment(String s, String gameId) {
        RequestQueue queue = Volley.newRequestQueue(ViralWeb.this);
        String url = AppConstant.AppUrl + "post_comment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick31", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                Map<String, Object> details = new HashMap<>();
                                details.put("comment", FieldValue.increment(1));
                                FirebaseFirestore.getInstance().collection(AppConstant.yralWeb)
                                        .document(gameId).set(details, SetOptions.merge());
                            } else {
                                Toast.makeText(ViralWeb.this, "Something Went wrong", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("comment_id", gameId);
                params.put("comments", s);
                params.put("user_id", appConstant.getId());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private RecyclerView recyclerView;
    EditText comments;

    public void wayToHome(View view) {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
            exoPlayer.seekTo(0);
        }
        startActivity(new Intent(ViralWeb.this, MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
            try {
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                exoPlayer = ExoPlayerFactory.newSimpleInstance(ViralWeb.this, trackSelector);
                Uri videoURI = Uri.parse("https://cdn.discordapp.com/attachments/911308156855005195/" + shortsUrlList.get(position).videoId + "/1.mp4");
                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
                exoPlayer.prepare(mediaSource);
                buttonViewHolder.idExoPlayerVIew.setPlayer(exoPlayer);
                if (exoplayerList.size() == position) {
                    exoplayerList.add(exoPlayer);
                    SimpleExoPlayerList.add(buttonViewHolder.idExoPlayerVIew);
                }
                // buttonViewHolder.idExoPlayerVIew.setPlayer(exoPlayer);

                SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, 0);
                ;
                FirebaseFirestore.getInstance()
                        .collection("yral_web")
                        .document(shortsUrlList.get(position).getVideoId())
                        .get(Source.SERVER)
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Long likes = (Long) documentSnapshot.get("likes");
                                Log.e("likes", likes + "");
                                buttonViewHolder.likes.setText(likes == null ? "0" : likes + "");
                                buttonViewHolder.likes.setTag(likes == null ? 0 : likes);
                            }
                        });

                if (sharedPreferences.getString(shortsUrlList.get(position).getVideoId(), null) == null) {
                    FirebaseDatabase.getInstance().getReference().child(AppConstant.yralWeb).child(shortsUrlList.get(position).videoId).child(AppConstant.likes).child(appConstant.getId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
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
                    buttonViewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(0, sharedPreferences.getString(shortsUrlList.get(position).videoId, null).equals("0") ? R.drawable.cards_heart : R.drawable.cards_heart_outline, 0, 0);
                }
                buttonViewHolder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ViralWeb.this, "Coming Soon", Toast.LENGTH_LONG).show();
                    }
                });
                buttonViewHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = getLayoutInflater().inflate(R.layout.comments, null);
                        BottomSheetDialog dialog = new RoundedBottomSheetDialog(ViralWeb.this);
                       comments = view.findViewById(R.id.comments);
                        recyclerView = view.findViewById(R.id.rv);
                        commentsAdapter = new CommentsAdapter(ViralWeb.this, commentsArrayList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(commentsAdapter);
                        reloadData(shortsUrlList.get(position).videoId);
                        view.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                postComment(comments.getText().toString(), shortsUrlList.get(position).videoId);
                                comments.setText(null);
                                Comments comment = new Comments(System.currentTimeMillis()+"", comments.getText().toString(), appConstant.getId(), appConstant.getName());
                                commentsArrayList.add(0, comment);
                                commentsAdapter.notifyDataSetChanged();
                            }
                        });

                        dialog.setContentView(view);
                        dialog.show();
                    }
                });
                buttonViewHolder.likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPause();
                        Long likes = (Long) v.getTag();
                        Map<String, Object> details = new HashMap<>();
                        SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (sharedPreferences.getString(shortsUrlList.get(position).videoId, null) == null || sharedPreferences.getString(shortsUrlList.get(position).videoId, null).equals("1")) {
                            buttonViewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cards_heart, 0, 0);
                            FirebaseDatabase.getInstance().getReference().child(AppConstant.yralWeb).child(shortsUrlList.get(position).videoId).child(AppConstant.likes).child(appConstant.getId()).setValue(System.currentTimeMillis() / 1000);
                            editor.putString(shortsUrlList.get(position).videoId, "0").apply();
                            details.put("likes", FieldValue.increment(1));
                            FirebaseFirestore.getInstance().collection(AppConstant.yralWeb)
                                    .document(shortsUrlList.get(position).videoId).set(details, SetOptions.merge());
                            likes = likes + 1;
                        } else {
                            buttonViewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cards_heart_outline, 0, 0);
                            editor.putString(shortsUrlList.get(position).videoId, "1").apply();
                            FirebaseDatabase.getInstance().getReference().child(AppConstant.yralWeb).child(shortsUrlList.get(position).videoId).child(AppConstant.likes).child(appConstant.getId()).removeValue();
                            details.put("likes", FieldValue.increment(-1));
                            FirebaseFirestore.getInstance().collection(AppConstant.yralWeb)
                                    .document(shortsUrlList.get(position).videoId)
                                    .set(details, SetOptions.merge());
                            likes = likes - 1;
                        }
                        buttonViewHolder.likes.setTag(likes);
                        buttonViewHolder.likes.setText(likes + "");
                    }
                });

            } catch (Exception e) {
                Log.e("MainAcvtivity", " exoplayer error " + e.toString());
            }
        }

        @Override
        public int getItemViewType(int position) {
            return R.layout.viral_videos;
        }

        @Override
        public int getItemCount() {
            return shortsUrlList.size();
        }

        public class SliderViewHolder extends RecyclerView.ViewHolder {
            SimpleExoPlayerView idExoPlayerVIew;
            TextView likes, comment, share, userName, caption;

            public SliderViewHolder(View view) {
                super(view);
                idExoPlayerVIew = view.findViewById(R.id.idExoPlayerVIew);
                likes = view.findViewById(R.id.likes);
                comment = view.findViewById(R.id.comment);
                share = view.findViewById(R.id.share);
                userName = view.findViewById(R.id.userName);
                caption = view.findViewById(R.id.caption);
            }
        }
    }
}

