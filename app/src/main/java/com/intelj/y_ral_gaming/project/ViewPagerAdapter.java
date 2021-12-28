package com.intelj.y_ral_gaming.project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.Adapter.DemoRecyclerviewAdapter;
import com.intelj.y_ral_gaming.Adapter.MemberListAdapter;
import com.intelj.y_ral_gaming.Adapter.TimeSlotAdapter;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;
import com.intelj.y_ral_gaming.model.DemoRecyclerviewModel;
import com.intelj.y_ral_gaming.model.UserListModel;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

class ViewPagerAdapter extends PagerAdapter {
    private RecyclerView recyclerView;
    private ViewPager mViewpager;
    //Context object
    Context context;
    private int selected_position;
    ArrayList<UserListModel> teamModel = new ArrayList<>();
    int count = 0;
    //Array of images
    int[] layout = {R.layout.bottom_sheet_dialog, R.layout.activity_demo, R.layout.layout_registration_successful, R.layout.recycler};

    //Layout Inflater
    LayoutInflater mLayoutInflater;
    private List<Movie> movieList = new ArrayList<>();
    private SharedPreferences sharedPreferencesAppname;
    //Viewpager Constructor
    public ViewPagerAdapter(Context context, ViewPager viewPager) {
        this.context = context;
        this.mViewpager = viewPager;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sharedPreferencesAppname =
                context.getSharedPreferences
                        (AppConstant.AppName, 0);
    }

    /*public ViewPagerAdapter(Context context, Set<String> teammember) {
        this.context = context;
        this.teammember = teammember;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }*/


    @Override
    public int getCount() {
        //return the number of images
        return layout.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return view == ((RelativeLayout) object);
//        if (position ==0)
        return view == ((LinearLayout) object);
//        else
//            return view == ((RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        //inflating the item.xml
        View itemView = mLayoutInflater.inflate(layout[position], container, false);
        Log.i("load_seleted_pos",selected_position+"");
        if (position == 0) {
//            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
            itemView.findViewById(R.id.newTeam).setVisibility(View.GONE);
            itemView.findViewById(R.id.bott_button).setVisibility(View.GONE);
            itemView.findViewById(R.id.create_team).setVisibility(View.GONE);
            TextView title = itemView.findViewById(R.id.title);
            title.setText("Select your team");
            TextView txt = itemView.findViewById(R.id.subtitle);
            txt.setText("only one team can be seelcted");
            RecyclerView recyclerview = itemView.findViewById(R.id.recyclerview);
            for (DataSnapshot snapshot : AppController.getInstance().mySnapShort.child(AppConstant.team).getChildren()) {
                SharedPreferences prefs = context.getSharedPreferences(snapshot.getKey(), Context.MODE_PRIVATE);
                teamModel.add(new UserListModel(prefs.getString(AppConstant.teamName, ""),
                        prefs.getString(AppConstant.myPicUrl, ""),
                        snapshot.getKey(),
                        prefs.getStringSet(AppConstant.teamMember, null)));
            }
            MemberListAdapter userAdapter = new MemberListAdapter(context, teamModel, AppConstant.team);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            recyclerview.setLayoutManager(mLayoutManager);
            recyclerview.setAdapter(userAdapter);
//            bottomSheetDialog.show();
            recyclerview.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerview, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    selected_position = position;
                    Log.i("after_click_pos",selected_position+"");
                    mViewpager.setCurrentItem(1);
                    notifyDataSetChanged();
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
        } else if (position == 1) {
            Log.i("selected_postion",selected_position+"");
            RecyclerView recyclerview = itemView.findViewById(R.id.recyclerview);
            ShimmerFrameLayout shimmer_view_container = itemView.findViewById(R.id.shimmer_view_container);
            Button nextButton = itemView.findViewById(R.id.nextButton);
            shimmer_view_container.startShimmer();

            List<DemoRecyclerviewModel> list = new ArrayList<>();
            if (teamModel.get(selected_position).getTeamMember() != null) {
                Set<String> myList = teamModel.get(selected_position).getTeamMember();
                for (String s : myList) {
                    Log.i("teammember", s);

            /*DemoRecyclerviewAdapter adapter = new DemoRecyclerviewAdapter(myListData);
            recyclerview.setHasFixedSize(true);
            recyclerview.setLayoutManager(new LinearLayoutManager(this));
            recyclerview.setAdapter(adapter);  */
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users).child(s).child(AppConstant.pinfo);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            count++;
                            DemoRecyclerviewModel demoRecyclerviewModel = new DemoRecyclerviewModel();
                            demoRecyclerviewModel.setTitle(s);
                            demoRecyclerviewModel.setDiscordId(snapshot.child("discordId").getValue() != null ? snapshot.child("discordId").getValue().toString() : null);
                            demoRecyclerviewModel.setGameId(snapshot.child("pubg").getValue() != null ? snapshot.child("pubg").getValue().toString() : null);
                            demoRecyclerviewModel.setGameName(snapshot.child("pubg_userName").getValue() != null ? snapshot.child("pubg_userName").getValue().toString() : null);
                            demoRecyclerviewModel.setImage(snapshot.child("myPicUrl").getValue() != null ? snapshot.child("myPicUrl").getValue().toString() : null);
                            list.add(demoRecyclerviewModel);
                            if (count == myList.size()) {
                                DemoRecyclerviewAdapter adapter = new DemoRecyclerviewAdapter(list, context);
                                recyclerview.setLayoutManager(new LinearLayoutManager(context));
                                recyclerview.setAdapter(adapter);
                                shimmer_view_container.hideShimmer();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendRequest(sharedPreferencesAppname.getString("gameWithTime",""));
                }
            });

        }
        if (position == 4) {
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);

            MoviesAdapter mAdapter = new MoviesAdapter(movieList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            prepareMovieData();
            mAdapter.notifyDataSetChanged();
        }
        Objects.requireNonNull(container).addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        //container.removeView((LinearLayout) object);
    }

    private void prepareMovieData() {
        Movie movie = new Movie("Mad Max: Fury Road", "Action & Adventure", "2015");
        movieList.add(movie);

        movie = new Movie("Inside Out", "Animation, Kids & Family", "2015");
        movieList.add(movie);

        movie = new Movie("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        movieList.add(movie);

        movie = new Movie("Shaun the Sheep", "Animation", "2015");
        movieList.add(movie);

        movie = new Movie("The Martian", "Science Fiction & Fantasy", "2015");
        movieList.add(movie);

        movie = new Movie("Mission: Impossible Rogue Nation", "Action", "2015");
        movieList.add(movie);

        movie = new Movie("Up", "Animation", "2009");
        movieList.add(movie);

        movie = new Movie("Star Trek", "Science Fiction", "2009");
        movieList.add(movie);

        movie = new Movie("The LEGO Movie", "Animation", "2014");
        movieList.add(movie);

        movie = new Movie("Iron Man", "Action & Adventure", "2008");
        movieList.add(movie);

        movie = new Movie("Aliens", "Science Fiction", "1986");
        movieList.add(movie);

        movie = new Movie("Chicken Run", "Animation", "2000");
        movieList.add(movie);

        movie = new Movie("Back to the Future", "Science Fiction", "1985");
        movieList.add(movie);

        movie = new Movie("Raiders of the Lost Ark", "Action & Adventure", "1981");
        movieList.add(movie);

        movie = new Movie("Goldfinger", "Action & Adventure", "1965");
        movieList.add(movie);

        movie = new Movie("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        movieList.add(movie);

    }


    private void sendRequest(String strDate) {
        Set<String> teamMember = teamModel.get(selected_position).getTeamMember();
       SharedPreferences sharedPreferencesAppname =
                context.getSharedPreferences
                        (AppConstant.AppName, 0);
       String title = sharedPreferencesAppname.getString("gameTitle","");
        ArrayList<String> discordId = new ArrayList<>();
        ArrayList<String> igid = new ArrayList<>();
        ArrayList<String> ign = new ArrayList<>();
        String error = "";
        for (String s : teamMember) {
            Log.i("valuesofS",s);
            SharedPreferences sharedPreferences = context.getSharedPreferences(s, 0);
            if(sharedPreferences.getString(AppConstant.discordId, "").equals("")){
                error = error + " Discord id not found for "+sharedPreferences.getString(AppConstant.userName, "") +"\n";
            }
            if(sharedPreferences.getString(title, "").equals("")){
                error = error + title + " Game id not found for "+sharedPreferences.getString(AppConstant.userName, "")+"\n";
            }
            if(sharedPreferences.getString(title+ "_" + AppConstant.userName, "").equals("")){
                error = error + title + " Game name not found for "+sharedPreferences.getString(AppConstant.userName, "")+"\n";
            }
            discordId.add(sharedPreferences.getString(AppConstant.discordId, ""));
            ign.add(sharedPreferences.getString(title, ""));
            igid.add(sharedPreferences.getString(title + "_" + AppConstant.userName, ""));
        }
        if(!error.equals("")){
            showDialog(error);
            return;
        }
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Registering for App, please wait.");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://y-ral-gaming.com/admin/api/register_matches.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean(AppConstant.success)) {
                                Toast.makeText(context,obj.getString(AppConstant.message),Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editShared = sharedPreferencesAppname.edit();
                                editShared.putBoolean(strDate,true);
                                editShared.apply();
                                mViewpager.setCurrentItem(2);
//                                notifyDataSetChanged();
//                                bottomSheetDialog.cancel();
                            }else{
                                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setMessage(obj.getString(AppConstant.message));
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }catch (Exception e){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", teamMember.toString().replace("[", "").replace("]", ""));
                params.put("strDate", strDate);
                params.put("teamName", teamModel.get(selected_position).getTeamName());
                params.put("teamUrl", teamModel.get(selected_position).getTeamUrl());
                params.put("discordId", discordId.toString().replace("[", "").replace("]", ""));
                params.put("teamId", teamModel.get(selected_position).getTeamId());
                params.put("game_name", title);
                params.put("ign", ign.toString().replace("[", "").replace("]", ""));
                params.put("igid", igid.toString().replace("[", "").replace("]", ""));
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

    private void showDialog(String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
