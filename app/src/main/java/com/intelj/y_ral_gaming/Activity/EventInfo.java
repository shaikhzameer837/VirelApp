package com.intelj.y_ral_gaming.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.Fragment.OneFragment;
import com.intelj.y_ral_gaming.Fragment.PrizePoolFragment;
import com.intelj.y_ral_gaming.Fragment.RuleFragment;
import com.intelj.y_ral_gaming.Fragment.TeamFragment;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.TournamentAdapter;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.model.TournamentModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventInfo extends AppCompatActivity {
    ImageView iv_cover_pic;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView tv_title, tv_date;
    TextView join;
    EditText teamName;
    ArrayList<EditText> editTextList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_info);
        iv_cover_pic = findViewById(R.id.cover_pic);
        Fade fade = new Fade();
//        appConstant = new AppConstant(this);
//        userid = getIntent().getStringExtra("userid");
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tv_title = findViewById(R.id.title);
        tv_date = findViewById(R.id.date);
        tv_title.setText(AppController.getInstance().tournamentModel.getGame_name());
        tv_date.setText(AppController.getInstance().tournamentModel.getDate());
        tabLayout.addTab(tabLayout.newTab().setText("Rules"));
        tabLayout.addTab(tabLayout.newTab().setText("Team"));
        tabLayout.addTab(tabLayout.newTab().setText("Prize Pool"));
        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("joined_event"));
        join = findViewById(R.id.join);
        MyEventAdapter adapter = new MyEventAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Glide.with(this).load(AppController.getInstance().tournamentModel.getImage_url()).placeholder(R.drawable.placeholder).into(iv_cover_pic);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void addTeamList() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EventInfo.this);
        bottomSheetDialog.setContentView(R.layout.add_team_info);
        LinearLayout lin = bottomSheetDialog.findViewById(R.id.lin);
        teamName = bottomSheetDialog.findViewById(R.id.teamName);
        bottomSheetDialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinEvent();
                bottomSheetDialog.cancel();
            }
        });
        for (int x = 0; x < 4 ; x++) {
            EditText editText = new EditText(EventInfo.this);
            editText.setTextSize(12);
            editText.setSingleLine(true);
            editText.setTag(x == 0 ? new AppConstant(EventInfo.this).getId() : AppConstant.randomString(5));
            editTextList.add(editText);
            LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            TextInputLayout textInputLayout = new TextInputLayout(EventInfo.this, null, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
            textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            textInputLayout.setBoxCornerRadii(5, 5, 5, 5);
            LinearLayout.LayoutParams textInputLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            textInputLayout.setLayoutParams(textInputLayoutParams);
            textInputLayout.addView(editText, editTextParams);
            textInputLayout.setHint(x == 0 ? "Enter your ingame name" : "Enter Player " + (x+1) + " ingame name");
            lin.addView(textInputLayout);
        }
        bottomSheetDialog.show();

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            join.setVisibility(View.VISIBLE);
            boolean result = intent.getBooleanExtra("message",false);
            setButton(!result? " join " : " Already joined ",!result?R.drawable.curved_red:R.drawable.curved_white,!result?Color.WHITE:Color.RED);
            if(!result) {
                join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addTeamList();
                    }
                });
            }
        }
        //TabLayout.Tab tab = tabLayout.getTabAt(1).setText("Teams (10)");
    };
    private void setButton(String btnName,int drawables,int color) {
        join.setText(btnName);
        join.setBackgroundResource(drawables);
        join.setTextColor(color);
    }
    private void joinEvent() {
        String key = "@"+(System.currentTimeMillis()/1000);
        JSONObject jsonRootObject = new JSONObject();
        JSONObject jsonRootObject2 = new JSONObject();
        JSONObject jsonRootObject3 = new JSONObject();
        try {
            for (int x = 0; x < editTextList.size() ; x++) {
                JSONObject jsonRootObject4 = new JSONObject();
                jsonRootObject4.put("ingName", editTextList.get(x).getText().toString());
                jsonRootObject3.put(editTextList.get(x).getTag().toString(), jsonRootObject4);
            }
            jsonRootObject2.put("teams", jsonRootObject3);
            jsonRootObject2.put("teamName", teamName.getText().toString());
            jsonRootObject.put(key, jsonRootObject2);
            Log.e("jsonRootObject",jsonRootObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/join_event.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("lcat_response", response);
                        if(response.equals("success")){
                            if(viewPager.getCurrentItem() != 3) {
                                Intent intent = new Intent("register_event");
                                intent.putExtra("key", key);
                                intent.putExtra("teamName", teamName.getText().toString());
                                intent.putExtra("teams", jsonRootObject3.toString());
                                LocalBroadcastManager.getInstance(EventInfo.this).sendBroadcast(intent);
                            }
                            setButton("Already joined",R.drawable.curved_white,Color.RED);
                            Toast.makeText(EventInfo.this,"Registration done successfully",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
/*                shimmer_container.hideShimmer();
                shimmer_container.setVisibility(View.GONE);*/
                error.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userJson", jsonRootObject.toString());
                params.put("tid", AppController.getInstance().tournamentModel.getId());
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

    public class MyEventAdapter extends FragmentPagerAdapter {

        private Context myContext;
        int totalTabs;

        public MyEventAdapter(Context context, FragmentManager fm, int totalTabs) {
            super(fm);
            myContext = context;
            this.totalTabs = totalTabs;
        }

        // this is for fragment tabs
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RuleFragment();
                case 2:
                    return new PrizePoolFragment(AppController.getInstance().tournamentModel.getPrize_pool());
                case 3:
                    return new PrizePoolFragment(AppController.getInstance().tournamentModel.getInfo());
                default:
                    return new TeamFragment();
            }
        }

        // this counts total number of tabs
        @Override
        public int getCount() {
            return totalTabs;
        }
    }
}
