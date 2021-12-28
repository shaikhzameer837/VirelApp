package com.intelj.y_ral_gaming;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.intelj.y_ral_gaming.Utils.AppConstant;

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

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerView =  findViewById(R.id.recycler_view);
        mAdapter = new SubscriptionAdapter(SubscriptionModelList,this);
        setupViewPager();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-names"));
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                showBottomSheetDialog();
        }
    };
    AlertDialog dialog;
    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_login_bottom_sheet_fragment, null);
        final BottomSheetDialog dialogBottom = new BottomSheetDialog(ComingSoon.this);
        dialogBottom.setContentView(view);
        dialogBottom.show();
        Button btn_ok = view.findViewById(R.id.ok);
//        Button btn_cancel = view.findViewById(R.id.cancel);
        ImageView imageView = view.findViewById(R.id.imageview);
        Glide.with(ComingSoon.this).load(R.drawable.login).into(imageView);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ComingSoon.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ComingSoon.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) && dialog == null) {
                        //If User was asked permission before and denied
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ComingSoon.this);

                        alertDialogBuilder.setTitle("Permission needed");
                        alertDialogBuilder.setMessage("Storage permission needed for accessing Storage");
                        alertDialogBuilder.setPositiveButton("Open Setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", ComingSoon.this.getPackageName(),
                                        null);
                                intent.setData(uri);
                                ComingSoon.this.startActivity(intent);
                            }
                        });
                        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

                        dialog = alertDialogBuilder.create();
                        dialog.show();
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(ComingSoon.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                100);
                    }
                } else
                    startActivity(new Intent(ComingSoon.this, SigninActivity.class));
            }
        });

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
