package com.intelj.y_ral_gaming.Activity;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SplashScreenStory;
import com.intelj.y_ral_gaming.UserInfoCheck;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;
import static com.google.android.play.core.install.model.UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS;
import static com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE;

public class SplashScreen extends AppCompatActivity {
    private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 222222;
    private AppUpdateManager appUpdateManager;
    private ReviewManager reviewManager;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d("Tokens",token);
                         FirebaseMessaging.getInstance().subscribeToTopic("push_yt").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("SUBSCRIBED","SUCCESS");
                            }
                        });
                    }
                });
        Intent intent = null;

         if (!AppController.getInstance().userId.isEmpty() && !new AppConstant(this).getFriendCheck())
            intent = new Intent(this, MainActivity.class); //UserInfoCheck.class);
        else if (!AppController.getInstance().remoteConfig.getString("subscription_package").equals(""))
            intent = new Intent(this, MainActivity.class);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return;
        }
        progress = findViewById(R.id.progress);
        serviceForData();
//        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
//        reviewManager = ReviewManagerFactory.create(this);
    //    showRateApp();
    }

    private void serviceForData() {
        progress.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/background_data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.setVisibility(View.GONE);
                        Log.e("ProgressResponse", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("success")) {
                                new AppConstant(SplashScreen.this).setDataFromShared(AppConstant.package_info, obj.getString(AppConstant.package_info));
                                new AppConstant(SplashScreen.this).setDataFromShared(AppConstant.game_slot, obj.getString(AppConstant.game_slot));
                                new AppConstant(SplashScreen.this).setDataFromShared(AppConstant.gameStreaming, obj.getString(AppConstant.gameStreaming));
                                AppController.getInstance().getGameName();
                                AppController.getInstance().getTournamentTime();
                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                if (!AppController.getInstance().userId.isEmpty() && !new AppConstant(SplashScreen.this).getFriendCheck())
                                    intent = new Intent(SplashScreen.this, MainActivity.class);//UserInfoCheck.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            } else {
                                Log.e("error Rec", "success false");
                            }
                        } catch (Exception e) {
                            Log.e("error Rec", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
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

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void checkUpdate() {
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdateFlow(appUpdateInfo);
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, this, IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void showRateApp() {
        com.google.android.play.core.tasks.Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                com.google.android.play.core.tasks.Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
                showRateAppFallbackDialog();
            }
        });
    }

    private void showRateAppFallbackDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.rate_app_title)
                .setMessage(R.string.rate_app_message)
                .setPositiveButton(R.string.rate_btn_pos, (dialog, which) -> redirectToPlayStore())
                .setNegativeButton(R.string.rate_btn_neg,
                        (dialog, which) -> {
                            // take action when pressed not now
                        })
                .setNeutralButton(R.string.rate_btn_nut,
                        (dialog, which) -> {
                            // take action when pressed remind me later
                        })
                .setOnDismissListener(dialog -> {
                })
                .show();
    }

    public void redirectToPlayStore() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            FirebaseCrashlytics.getInstance().recordException(exception);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Update canceled by user! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Update success! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Update Failed! Result Code: " + resultCode, Toast.LENGTH_LONG).show();
                checkUpdate();
            }
        }
    }
}
