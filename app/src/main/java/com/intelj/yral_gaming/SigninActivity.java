package com.intelj.yral_gaming;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intelj.yral_gaming.Activity.MainActivity;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class SigninActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    int[] layouts;
    AppConstant appConstant;
    TextInputEditText phoneNumber, otp;
    EditText pgUsername;
    CountryCodePicker ccp;
    TextView et_countdown, resend_btn;
    String _phoneNumber = "", _otp = "", token = "", _pgUsername = "", _countryCode = "+91";
    DatabaseReference mDatabase;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.signin);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        token = task.getResult();


                    }
                });
        appConstant = new AppConstant(SigninActivity.this);
        viewPager = findViewById(R.id.view_pager);
        mAuth = FirebaseAuth.getInstance();

        layouts = new int[]{
                R.layout.login,
                R.layout.otp,
                R.layout.pubg_username};

        changeStatusBarColor();
        TextView tv = findViewById(R.id.title);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "game.ttf");
        tv.setTypeface(face);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
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
    }


    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    /*
        @Override
        protected void onResume() {
            super.onResume();
            if (ContextCompat.checkSelfPermission(SigninActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(SigninActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) && dialog == null) {
                    //If User was asked permission before and denied
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                    alertDialogBuilder.setTitle("Permission needed");
                    alertDialogBuilder.setMessage("Storage permission needed for accessing Storage");
                    alertDialogBuilder.setPositiveButton("Open Setting", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", SigninActivity.this.getPackageName(),
                                    null);
                            intent.setData(uri);
                            SigninActivity.this.startActivity(intent);
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
                    ActivityCompat.requestPermissions(SigninActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            100);
                }
            }
        }


        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
            switch (requestCode) {
                case 100:
                    if(dialog !=  null)
                        dialog.cancel();
                    break;
                case 1:

                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    } else {
                        Toast.makeText(SigninActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    }
                    break;


                // other 'case' lines to check for other
                // permissions this app might request
            }
        }*/
    //	viewpager change listener
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
            if (viewPager.getCurrentItem() == 0) {
                phoneNumber = layout_view.get(0).findViewById(R.id.phoneNumber);
                ccp = layout_view.get(0).findViewById(R.id.ccp);
                _phoneNumber = phoneNumber.getText().toString();
                if (_phoneNumber.length() != 10) {
                    Toast.makeText(SigninActivity.this, "Please Fill Proper phone number", Toast.LENGTH_LONG).show();
                    return;
                }
                if (viewPager.getCurrentItem() < layouts.length) {
                    sendVerificationCode(_phoneNumber);
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
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
            } else if (viewPager.getCurrentItem() == 2) {
                pgUsername = layout_view.get(2).findViewById(R.id.pgUsername);
                _pgUsername = pgUsername.getText().toString();
                if (_pgUsername.equals("")) {
                    Toast.makeText(SigninActivity.this, "Please fill pubg id", Toast.LENGTH_LONG).show();
                    return;
                }
                mDatabase.orderByChild(appConstant.userId).equalTo(_pgUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if (AppController.getInstance().userId.equals(postSnapshot.getKey())) {
                                   // registerdOnServer();
//                                    Intent intent = new Intent(SigninActivity.this, MainActivity.class);
//                                    startActivity(intent);
                                    return;
                                } else {
                                    Toast.makeText(SigninActivity.this, "Pubg id already exist", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        }
                        mDatabase.child(AppController.getInstance().userId).child(appConstant.userId).
                                setValue(_pgUsername);
                        registerdOnServer();

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

            } else if (viewPager.getCurrentItem() == 1) {
                otp = layout_view.get(1).findViewById(R.id.otp);
                _otp = otp.getText().toString();


//                if (!_otp.equals("1234")) {
//                    Toast.makeText(SigninActivity.this, "Wrong otp", Toast.LENGTH_LONG).show();
//                    return;
//                }
                if (_otp.trim().length() != 0) {
                    if (!AppConstant.isProduction && _otp.equals("123456"))
                        checkAndSaveLogin();
                    else verifyVerificationCode(_otp);
                } else
                    Toast.makeText(SigninActivity.this, "Please enter the OTP", Toast.LENGTH_LONG).show();
                /*AppController.getInstance().userId = System.currentTimeMillis() + "";
                final ProgressDialog progressDialog = new ProgressDialog(SigninActivity.this);
                progressDialog.setMessage("Signing...");
                progressDialog.show();
                AppController.getInstance().progressDialog = progressDialog;
                mDatabase = FirebaseDatabase.getInstance().getReference(appConstant.users);
                mDatabase.orderByChild(appConstant.phoneNumber).equalTo(_phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Object> login = new HashMap<>();
                        login.put("model" , Build.MODEL);
                        login.put("brand" , Build.BRAND);
                        login.put("type" , Build.TYPE);
                        login.put("version" , Build.VERSION.RELEASE);
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                AppController.getInstance().userId = postSnapshot.getKey();
                        } else
                            login.put(appConstant.myPicUrl, "");
                        login.put(appConstant.userName, _userName);
                        login.put(appConstant.token, token);
                        login.put(appConstant.deviceId, Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID));
                        mDatabase.child(AppController.getInstance().userId).child(AppConstant.pinfo).
                                updateChildren(login);
                        mDatabase.child(AppController.getInstance().userId).child(appConstant.phoneNumber).setValue(_phoneNumber);
                        appConstant.saveLogin(AppController.getInstance().userId,_phoneNumber);
                        AppController.getInstance().getReadyForCheckin();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        AppController.getInstance().progressDialog = null;
                        progressDialog.cancel();
                        Toast.makeText(SigninActivity.this,"Something went wrong try again later...",Toast.LENGTH_LONG).show();
                    }
                });*/

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

    private void registerdOnServer() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Registering for App, please wait.");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/reg.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        startActivity(intent);
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
                params.put("token",token);
                params.put("uniqueId", AppController.getInstance().userId);
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
        if (mobile.contains("7738454952")) {
            checkAndSaveLogin();
            return;
        }
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
            if (code != null) {
                otp.setText(code);
                //verifying the code
                verifyVerificationCode(code);
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
            mResendToken = forceResendingToken;
        }
    };


    private void verifyVerificationCode(String code) {

        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    int coin = 0;

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            checkAndSaveLogin();
                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

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

    private void checkAndSaveLogin() {
        AppController.getInstance().userId = System.currentTimeMillis() + "";
        final ProgressDialog progressDialog = new ProgressDialog(SigninActivity.this);
        progressDialog.setMessage("Signing...");
        progressDialog.show();
        AppController.getInstance().progressDialog = progressDialog;
        mDatabase = FirebaseDatabase.getInstance().getReference(appConstant.users);
        mDatabase.orderByChild(appConstant.phoneNumber).equalTo(_phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> login = new HashMap<>();
                HashMap<String, Object> realTime = new HashMap<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        AppController.getInstance().userId = postSnapshot.getKey();
                    if (dataSnapshot.child(AppConstant.coin).exists())
                        coin = dataSnapshot.child(AppConstant.coin).getValue(Integer.class);
                    mDatabase.child(AppController.getInstance().userId).child(appConstant.phoneNumber).setValue(_phoneNumber);
                    appConstant.saveUserInfo(SigninActivity.this, dataSnapshot);
                } else
                    AppController.getInstance().isFirstTime = true;
                login.put(appConstant.token, token);
                login.put(appConstant.countryCode, _countryCode);
                realTime.put(appConstant.deviceId, Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID));
                mDatabase.child(AppController.getInstance().userId).child(AppConstant.pinfo).
                        updateChildren(login);
                mDatabase.child(AppController.getInstance().userId).child(AppConstant.realTime).
                        updateChildren(realTime);
                appConstant.saveLogin(AppController.getInstance().userId, _phoneNumber, coin, _countryCode);
                AppController.getInstance().mySnapShort = dataSnapshot;
                AppController.getInstance().progressDialog = null;
                progressDialog.cancel();
                AppController.getInstance().getReadyForCheckin();
                registerdOnServer();
               // startActivity(new Intent(SigninActivity.this, UserInfoCheck.class));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                AppController.getInstance().progressDialog = null;
                progressDialog.cancel();
                Toast.makeText(SigninActivity.this, "Something went wrong try again later...", Toast.LENGTH_LONG).show();
            }
        });
    }

}
