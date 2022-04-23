package com.intelj.y_ral_gaming.Activity;

import android.content.Context;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.Fragment.OneFragment;
import com.intelj.y_ral_gaming.Fragment.TeamFragment;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;

public class EventInfo extends AppCompatActivity {
    ImageView iv_cover_pic;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView tv_title,tv_date;
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
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final MyEventAdapter adapter = new MyEventAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        Glide.with(this).load(AppController.getInstance().tournamentModel.getImage_url()).placeholder(R.drawable.placeholder).into(iv_cover_pic);

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
                    TeamFragment homeFragment = new TeamFragment();
                    return homeFragment;
                case 1:
                    TeamFragment sportFragment = new TeamFragment();
                    return sportFragment;
                case 2:
                    TeamFragment movieFragment = new TeamFragment();
                    return movieFragment;
                default:
                    return null;
            }
        }

        // this counts total number of tabs
        @Override
        public int getCount() {
            return totalTabs;
        }
    }
}
