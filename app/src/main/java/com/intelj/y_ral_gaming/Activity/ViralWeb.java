package com.intelj.y_ral_gaming.Activity;

import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.intelj.y_ral_gaming.Adapter.CommentsAdapter;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.BuildConfig;
import com.intelj.y_ral_gaming.Comments;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViralWeb extends AppCompatActivity {
    private ViewsSliderAdapter mAdapter;
    ViewPager2 viewPager2;
    SimpleExoPlayer exoPlayer;
    ArrayList<SimpleExoPlayerView> SimpleExoPlayerList = new ArrayList<>();
    ArrayList<SimpleExoPlayer> exoplayerList = new ArrayList<>();
    ArrayList<String> keys;
    AppConstant appConstant;
    private RecyclerView recyclerView;
    TextView userName;
    int UniversalPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.viral_web);
        appConstant = new AppConstant(this);
        viewPager2 = findViewById(R.id.view_pager);
        userName = findViewById(R.id.userName);
        getVideoList();
    }

    public Map<String, Object> shortsUrlList = new HashMap<>();

    public void getVideoList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("yral_web").document("video");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        shortsUrlList = document.getData();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                    keys = new ArrayList(shortsUrlList.keySet());
                    Collections.shuffle(keys);
                    //keys = shortsUrlList.keySet().toArray();
                    init();
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
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
            UniversalPosition = position;
            // exoPlayer = exoplayerList.get(UniversalPosition);
            exoplayerList.get(UniversalPosition).setPlayWhenReady(true);
        }
    };


    public void stopVideo() {
        if (exoPlayer != null) {
            exoplayerList.get(UniversalPosition).setPlayWhenReady(false);
            // exoPlayer.release();
            //   exoPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopVideo();
    }

    public void getYoutubeVid(int position) {
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

            if (exoplayerList.size() == position)
                exoplayerList.add(ExoPlayerFactory.newSimpleInstance(ViralWeb.this, trackSelector));
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            SimpleExoPlayerList.get(position).setPlayer(exoplayerList.get(position));
            Uri videouri = Uri.parse("https://cdn.discordapp.com/attachments/911308156855005195/" + keys.get(position) + "/1.mp4");
            Log.e("getYoutubeVid: ", "https://cdn.discordapp.com/attachments/911308156855005195/" + keys.get(position) + "/1.mp4");
            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);
            exoplayerList.get(position).prepare(mediaSource);
            exoplayerList.get(position).addListener(new ExoPlayer.EventListener() {
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
    protected void onPause() {
        super.onPause();
        exoplayerList.get(UniversalPosition).setPlayWhenReady(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (exoplayerList.size() > UniversalPosition)
            exoplayerList.get(UniversalPosition).setPlayWhenReady(true);
    }


    ArrayList<Comments> commentsArrayList = new ArrayList<>();
    CommentsAdapter commentsAdapter;

    private void reloadData(String s) {
        RequestQueue queue = Volley.newRequestQueue(ViralWeb.this);
        String url = "http://y-ral-gaming.com/admin/api/comment_list.php";
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

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void postComment(String s, String gameId) {
        RequestQueue queue = Volley.newRequestQueue(ViralWeb.this);
        String url = "http://y-ral-gaming.com/admin/api/post_comment.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick31", response);
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                Comments comments = new Comments(json.getString("id"), s, appConstant.getId(), appConstant.getName());
                                commentsArrayList.add(0, comments);
                                Map<String, Object> city = new HashMap<>();
                                Map<String, Object> details = new HashMap<>();
                                details.put("comment", FieldValue.increment(1));
                                city.put(gameId, details);
                                FirebaseFirestore.getInstance().collection(AppConstant.yralWeb)
                                        .document("video").set(city, SetOptions.merge());
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

    EditText comments;

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
            if (SimpleExoPlayerList.size() == position)
                SimpleExoPlayerList.add(buttonViewHolder.idExoPlayerVIew);
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, 0);
            FirebaseDatabase.getInstance().getReference().child(AppConstant.users)
                    .child(((Map<String, Object>) shortsUrlList.get(keys.get(position))).get("userid").toString())
                    .child(AppConstant.pinfo)
                    .child(AppConstant.name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            buttonViewHolder.userName.setText(snapshot.getValue() + "");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            buttonViewHolder.likes.setTag(((Map<String, Object>) shortsUrlList.get(keys.get(position))).get("likes"));
            buttonViewHolder.likes.setText(((Map<String, Object>) shortsUrlList.get(keys.get(position))).get("likes").toString());
            buttonViewHolder.comment.setText(((Map<String, Object>) shortsUrlList.get(keys.get(position))).get("comment").toString());
            buttonViewHolder.caption.setText(((Map<String, Object>) shortsUrlList.get(keys.get(position))).get("caption").toString());
            if (sharedPreferences.getString(keys.get(position), null) == null) {
                FirebaseDatabase.getInstance().getReference().child(AppConstant.yralWeb).child(keys.get(position)).child(AppConstant.likes).child(appConstant.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                buttonViewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(0, sharedPreferences.getString(keys.get(position).toString(), null).equals("0") ? R.drawable.cards_heart : R.drawable.cards_heart_outline, 0, 0);
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
                    BottomSheetDialog dialog = new BottomSheetDialog(ViralWeb.this);
                    comments = view.findViewById(R.id.comments);
                    recyclerView = view.findViewById(R.id.rv);
                    commentsAdapter = new CommentsAdapter(ViralWeb.this, commentsArrayList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(commentsAdapter);
                    reloadData(keys.get(position));
                    view.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            postComment(comments.getText().toString(), keys.get(position));
                        }
                    });

                    dialog.setContentView(view);
                    dialog.show();
                }
            });
            getYoutubeVid(position);
            buttonViewHolder.likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPause();
                    Long likes = (Long) v.getTag();
                    Map<String, Object> city = new HashMap<>();
                    Map<String, Object> details = new HashMap<>();
                    SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (sharedPreferences.getString(keys.get(position).toString(), null) == null || sharedPreferences.getString(keys.get(position).toString(), null).equals("1")) {
                        buttonViewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cards_heart, 0, 0);
                        FirebaseDatabase.getInstance().getReference().child(AppConstant.yralWeb).child(keys.get(position).toString()).child(AppConstant.likes).child(appConstant.getId()).setValue(System.currentTimeMillis() / 1000);
                        editor.putString(keys.get(position).toString(), "0").apply();
                        details.put("likes", FieldValue.increment(1));
                        city.put(keys.get(position).toString(), details);
                        FirebaseFirestore.getInstance().collection(AppConstant.yralWeb)
                                .document("video").set(city, SetOptions.merge());
                        likes = likes + 1;
                    } else {
                        buttonViewHolder.likes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.cards_heart_outline, 0, 0);
                        editor.putString(keys.get(position).toString(), "1").apply();
                        FirebaseDatabase.getInstance().getReference().child(AppConstant.yralWeb).child(keys.get(position).toString()).child(AppConstant.likes).child(appConstant.getId()).removeValue();
                        details.put("likes", FieldValue.increment(-1));
                        city.put(keys.get(position).toString(), details);
                        FirebaseFirestore.getInstance().collection(AppConstant.yralWeb)
                                .document("video").set(city, SetOptions.merge());
                        likes = likes - 1;
                    }
                    buttonViewHolder.likes.setTag(likes);
                    buttonViewHolder.likes.setText(likes + "");
                }
            });
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

