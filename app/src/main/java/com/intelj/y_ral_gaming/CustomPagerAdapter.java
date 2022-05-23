package com.intelj.y_ral_gaming;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.firebase.database.DataSnapshot;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import java.util.ArrayList;

public class CustomPagerAdapter extends PagerAdapter {
    private Context mContext;
    LinearLayout linearLayout;
    TextView question;
    ArrayList<DataSnapshot> dataSnapshots;
    ArrayList<Button> answerBtn = new ArrayList<>();
    ArrayList<Boolean> isPress = new ArrayList<>();
    boolean isWinner = false;
    public CustomPagerAdapter(Context context, ArrayList<DataSnapshot> dataSnapshots) {
        mContext = context;
        this.dataSnapshots = dataSnapshots;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.img_swipe, collection, false);
        int x = 0;
        linearLayout = layout.findViewById(R.id.linearLayout);
        question = layout.findViewById(R.id.question);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        params.setMargins(7, 7, 7, 7);
        for (DataSnapshot snapshot : dataSnapshots.get(position).getChildren()) {
            if (snapshot.getValue() instanceof Boolean) {
                x++;
                Button button = new Button(mContext);
                button.setId(Integer.parseInt(position + "" + x));
                button.setTag(snapshot.getValue(Boolean.class));
                button.setText(snapshot.getKey());
                button.setLayoutParams(params);
                button.setBackgroundResource(R.drawable.curved_transparent);
                button.setTextColor(Color.parseColor("#ffffff"));
                if (snapshot.getValue(Boolean.class))
                    answerBtn.add(button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isPress.size() == position) {
                            v.setBackgroundResource(R.drawable.curved_blue);
                            ((Button) v).setTextColor(Color.parseColor("#ffffff"));
                            isPress.add(true);
                            isWinner = (Boolean) v.getTag();

                        }
                        //  answer = (Boolean) button.getTag();
                    }
                });
                linearLayout.addView(button);
            } else
                question.setText(snapshot.getValue(String.class));
        }
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return dataSnapshots.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return "";
    }
    public boolean isCorrectAnswer(){

        return isWinner;
    }
    public void setCorrectAnswer(int position) {
        answerBtn.get(position).setBackgroundResource(R.drawable.curved_purple);
        notifyDataSetChanged();
    }
}
