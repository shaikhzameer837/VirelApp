package com.intelj.yral_gaming;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intelj.yral_gaming.Utils.AppConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComingSoon extends AppCompatActivity {
    private Toolbar toolbar;
    private List<SubscriptionModel> SubscriptionModelList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SubscriptionAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coming_soon);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setTitle("Subscription");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView =  findViewById(R.id.recycler_view);
        mAdapter = new SubscriptionAdapter(SubscriptionModelList,this);
        setupViewPager();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }



    private void setupViewPager() {
        try {
            JSONObject jsnobject = new JSONObject(AppController.getInstance().getSubscription_package());
            JSONArray jsonArray = jsnobject.getJSONArray("package");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject explrObject = jsonArray.getJSONObject(i);
                SubscriptionModel SUbscriptionModel = new SubscriptionModel(explrObject.getString(AppConstant.package_name),
                        explrObject.getString("description"),
                        explrObject.getString("tenure"),
                        explrObject.getString("price"),explrObject.getString(AppConstant.package_id));
                SubscriptionModelList.add(SUbscriptionModel);
            }
            mAdapter.notifyDataSetChanged();

            Log.d("My App", jsnobject.toString());

        } catch (Exception e) {
            Log.e("My App", "Could not parse malformed JSON:" + e.getMessage());
        }

    }
}
