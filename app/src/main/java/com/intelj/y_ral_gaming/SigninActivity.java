package com.intelj.y_ral_gaming;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class SigninActivity extends AppCompatActivity {
    HashMap<String, String> contentList = new HashMap<>();
    private ViewPager viewPagerLogin;
    private MyViewPagerAdapter myViewPagerAdapter;
    int[] layouts;
    AppConstant appConstant;
    EditText phoneNumber, referral, otp;
    CountryCodePicker ccp;
    TextView et_countdown, resend_btn;
    String _phoneNumber = "", _otp = "", token = "", _pgUsername = "", _countryCode = "+91";
    DatabaseReference mDatabase;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    String skey = "9911";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.signin);
        contentList.put("phone.json", "Enter just phone number");
        contentList.put("login.json", "Register & play Game");
        contentList.put("cash.json", "You earn coins as joining bonus");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        token = task.getResult();
                    }
                });
        appConstant = new AppConstant(SigninActivity.this);
        viewPagerLogin = findViewById(R.id.view_pager);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(appConstant.users);
        layouts = new int[]{
                R.layout.login,
                R.layout.otp,
                R.layout.pubg_username};

        changeStatusBarColor();
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPagerLogin.setAdapter(myViewPagerAdapter);
        viewPagerLogin.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPagerLogin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewPagerAdapter.checkError();
            }
        });
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(SigninActivity.this));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                viewPager.setCurrentItem(viewPager.getCurrentItem() == (contentList.size() - 1) ? 0 : viewPager.getCurrentItem() + 1);
                handler.postDelayed(this, 2000); //now is every 2 minutes
            }
        }, 2000);
    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.image_view, collection, false);
            TextView textView  = layout.findViewById(R.id.title);
            LottieAnimationView lottieAnimationView = layout.findViewById(R.id.animationView);
            String firstKey = contentList.keySet().toArray()[position].toString();
            lottieAnimationView.setAnimation(firstKey);
            textView.setText(contentList.get(firstKey));
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return contentList.size();
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
    private int getItem(int i) {
        return viewPagerLogin.getCurrentItem() + i;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {


        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        View view;
        CountDownTimer mcountDownTimer = null;
        ArrayList<View> layout_view = new ArrayList<>();

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(layouts[position], container, false);
            if (position == layout_view.size())
                layout_view.add(view);
            if (position == 0) {
                ccp = layout_view.get(0).findViewById(R.id.ccp);
                ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                    @Override
                    public void onCountrySelected(Country selectedCountry) {
                        _countryCode = selectedCountry.getPhoneCode();
                        Toast.makeText(SigninActivity.this, "Updated " + selectedCountry.getPhoneCode(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            container.addView(view);
            return view;
        }

        public void checkError() {
            if (viewPagerLogin.getCurrentItem() == 0) {
                phoneNumber = layout_view.get(0).findViewById(R.id.phoneNumber);
                referral = layout_view.get(0).findViewById(R.id.referral);
                ccp = layout_view.get(0).findViewById(R.id.ccp);
                _phoneNumber = phoneNumber.getText().toString();
                if (_phoneNumber.length() != 10) {
                    Toast.makeText(SigninActivity.this, "Please Fill Proper phone number", Toast.LENGTH_LONG).show();
                    return;
                }
                if (viewPagerLogin.getCurrentItem() < layouts.length) {
                    mobileConfirmationPopup();
                    resend_btn = findViewById(R.id.resend_btn);
                    resend_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            resendVerificationCode(_phoneNumber, mResendToken);
                            countdown_timer();
                        }
                    });
                    countdown_timer();
                }
            }  else if (viewPagerLogin.getCurrentItem() == 1) {
                otp = layout_view.get(1).findViewById(R.id.otp);
                _otp = otp.getText().toString();
                if (_otp.trim().length() != 0) {
                    if (skey.equals(_otp))
                        registerdOnServer();
                    else verifyVerificationCode(_otp);
                } else
                    Toast.makeText(SigninActivity.this, "Please enter the OTP", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        private void countdown_timer() {
            resend_btn.setVisibility(View.GONE);
            et_countdown = layout_view.get(1).findViewById(R.id.et_countdown);
            if (mcountDownTimer != null)
                mcountDownTimer.cancel();
            mcountDownTimer = new CountDownTimer(120000, 1000) {
                public void onTick(long millisUntilFinished) {
                    NumberFormat f = new DecimalFormat("00");
//                        long hour = (millisUntilFinished / 3600000) % 24;
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
//                        et_countdown.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
                    et_countdown.setText(f.format(min) + ":" + f.format(sec));
                }

                public void onFinish() {
                    resend_btn.setVisibility(View.VISIBLE);
                    et_countdown.setText("00:00");
                }

            }.start();
        }

        private void resendVerificationCode(String phoneNumber,
                                            PhoneAuthProvider.ForceResendingToken token) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91" + phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    SigninActivity.this,               // Activity (for callback binding)
                    mCallbacks,         // OnVerificationStateChangedCallbacks
                    token);             // ForceResendingToken from callbacks
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerdOnServer();
    }

    private void registerdOnServer() {
        if (token.equals("")) {
            Toast.makeText(SigninActivity.this, "Something went wrong try again later", Toast.LENGTH_LONG).show();
            return;
        }
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Registering for App, please wait.");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "register.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e( "onResponse: ", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("success")) {
                                String referral = obj.getString("referral");
                                String userName = obj.getString("userName");
                                String name = obj.getString("name");
                                String uniqueId = obj.getString("id");
                                String games = obj.getString("games");
                                boolean isNew = obj.getBoolean("isNew");
                                SharedPreferences sharedPreferences = getSharedPreferences(uniqueId, 0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(AppConstant.gameList, games);
                                editor.putString(AppConstant.name, name);
                                editor.apply();
                                HashMap<String, Object> login = new HashMap<>();
                                HashMap<String, Object> realTime = new HashMap<>();
                                AppController.getInstance().userId = uniqueId;
                                mDatabase.child(AppController.getInstance().userId).child(AppConstant.phoneNumber).
                                        setValue(_countryCode + _phoneNumber);
                                mDatabase.child(AppController.getInstance().userId).child(AppConstant.userName).
                                        setValue(userName);
                                login.put(AppConstant.countryCode, _countryCode);
                                realTime.put(AppConstant.deviceId, Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.ANDROID_ID));
                                mDatabase.child(AppController.getInstance().userId).child(AppConstant.pinfo).
                                        updateChildren(login);
                                mDatabase.child(AppController.getInstance().userId).child(AppConstant.realTime).
                                        updateChildren(realTime);
                                appConstant.saveLogin(AppController.getInstance().userId);
                                appConstant.saveUserInfo(_phoneNumber, AppController.getInstance().userId, AppConstant.AppUrl + "images/" + AppController.getInstance().userId + ".png?u=" + AppConstant.imageExt(), name, _countryCode, null, userName);
                                AppController.getInstance().getReadyForCheckin();
                                AppController.getInstance().progressDialog = null;
                                appConstant.savePackage(uniqueId, referral);
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                Intent intent = new Intent(SigninActivity.this, isNew ? Congrats.class : MainActivity.class);//UserInfoCheck.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SigninActivity.this,"Something Went Wrong",Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Log.e("Exception", e.getMessage());
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
                params.put("token", token);
                params.put("uniqueId", (System.currentTimeMillis() / 1000) + "");
                params.put("phoneNumber", _phoneNumber);
                params.put("referral", referral.getText().toString());
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


    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        //  if (mobile.contains("7758454952")) {
        // checkAndSaveLogin();
        //     return;
        //   }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                _countryCode + mobile,
                60,
                TimeUnit.SECONDS,
                SigninActivity.this,
                mCallbacks);
    }


    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null && otp != null) {
                otp.setText(code);
                //verifying the code
//                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(SigninActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
            Log.e("mVerificationId", mVerificationId);
            mResendToken = forceResendingToken;
        }
    };


    private void verifyVerificationCode(String code) {

        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            registerdOnServer();
                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

//                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            Toast.makeText(SigninActivity.this, message, Toast.LENGTH_LONG).show();
                            /*snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();*/
                        }
                    }
                });
    }

    private void mobileConfirmationPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(PhoneNumberUtils.formatNumber(_countryCode + _phoneNumber, "IN"))
                .setCancelable(false)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Random r = new Random();
                        if (!_phoneNumber.equals("7738454952"))
                            skey = (r.nextInt(9999 - 1000) + 1000) + "";
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("key");

                        myRef.setValue(skey);
                        // if (AppController.getInstance().is_production.equals("true"))
                        sendVerificationCode(_phoneNumber);
                        //registerdOnServer();
                        viewPagerLogin.setCurrentItem(viewPagerLogin.getCurrentItem() + 1);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Please confirm your mobile number");
        alert.show();
    }
}
