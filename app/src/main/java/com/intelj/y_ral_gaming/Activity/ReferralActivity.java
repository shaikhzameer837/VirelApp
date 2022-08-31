package com.intelj.y_ral_gaming.Activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.intelj.y_ral_gaming.Adapter.MyListAdapter;
import com.intelj.y_ral_gaming.CustomPagerAdapter;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.model.MyListData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ReferralActivity extends AppCompatActivity {
    ArrayList<String> imageUrls = new ArrayList<>();
    TextView refer;
    RecyclerView recyclerView;
    ArrayList<MyListData> myListData =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.referral_activity);
        recyclerView = findViewById(R.id.recyclerView);
        ViewPager viewPager = findViewById(R.id.viewpager);
        imageUrls.add("referral.json");
        imageUrls.add("login.json");
        imageUrls.add("cash.json");
        refer = findViewById(R.id.refer);
        refer.setText("YRAL" + new AppConstant(this).getId());
        viewPager.setAdapter(new CustomPagerAdapter(ReferralActivity.this));
        getReferalList();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                viewPager.setCurrentItem(viewPager.getCurrentItem() == (imageUrls.size() - 1) ? 0 : viewPager.getCurrentItem() + 1);
                handler.postDelayed(this, 2000); //now is every 2 minutes
            }
        }, 2000);
    }

    public void shareApp(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out Y-ral Gaming app play BGMI , Free Fire game with Free entry and earn per kill : https://play.google.com/store/apps/details?id=com.intelj.y_ral_gaming");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void copyToClipboard(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", "YRAL" + new AppConstant(this).getId());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Referral code copied", Toast.LENGTH_LONG).show();
    }

    private void getReferalList() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "get_referral_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("responses", response);
                        try {
                            JSONArray json = new JSONArray(response);
                            findViewById(R.id.animationView).setVisibility(json.length() > 0 ? View.GONE : View.VISIBLE);

                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonObject = (JSONObject)json.get(i);
                                myListData.add(new MyListData(jsonObject.getString("name"),jsonObject.getString("name"),jsonObject.getString("name")));
                                Log.e("responses",jsonObject.getString("name"));
                            }
                            Log.e("responses",myListData.size()+"");
                            MyListAdapter adapter = new MyListAdapter(myListData);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ReferralActivity.this));
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        findViewById(R.id.progress).setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                findViewById(R.id.progress).setVisibility(View.GONE);
                Toast.makeText(ReferralActivity.this, "Something went wrong try again later ", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("referral_id", "YRAL" + new AppConstant(ReferralActivity.this).getId());
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

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            String imageUrl = imageUrls.get(position);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.image_view, collection, false);
            LottieAnimationView lottieAnimationView = layout.findViewById(R.id.animationView);
            lottieAnimationView.setAnimation(imageUrl);
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }
}
