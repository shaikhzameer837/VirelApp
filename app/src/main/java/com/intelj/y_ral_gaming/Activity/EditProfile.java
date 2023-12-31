package com.intelj.y_ral_gaming.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.RoundedBottomSheetDialog;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.VolleyMultipartRequest;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    ImageView imgProfile;
    int PROFILE_IMAGE = 11;
    String picturePath = null;
    Bitmap selectedImage;
    AppConstant appConstant;
    ProgressDialog progressDialog;
    TextInputEditText playerName, bio;
    String userName = "";
    DatabaseReference mDatabase;
    AutoCompleteTextView tv_title;
    LinearLayout gameList;
    SharedPreferences prefs;
    TextView  TI_userName;
    EditText ed_userName;
    BottomSheetDialog dialogBottom;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appConstant = new AppConstant(this);
        setContentView(R.layout.edit_profile);
        imgProfile = findViewById(R.id.imgs);
        playerName = findViewById(R.id.name);
        TI_userName = findViewById(R.id.userName);
        bio = findViewById(R.id.bio);
        playerName.setEnabled(true);
        gameList = findViewById(R.id.gameList);
        findViewById(R.id.done).setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, AppConstant.player_title);
        tv_title = findViewById(R.id.autoCompleteTextView1);
        tv_title.setThreshold(0);//will start working from first character
        tv_title.setAdapter(adapter);
        SharedPreferences sharedPreferences = getSharedPreferences(new AppConstant(EditProfile.this).getId(), 0);
        prefs = getSharedPreferences(AppConstant.AppName, MODE_PRIVATE);
        tv_title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                tv_title.showDropDown();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (tv_title.getRight() - tv_title.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        tv_title.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        tv_title.setKeyListener(null);
        mDatabase = FirebaseDatabase.getInstance().getReference(appConstant.users);
        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerName.getText().toString().trim().equals("")) {
                    playerName.requestFocus();
                    playerName.setError("Player name cannot be empty");
                    return;
                }
                if (userName.equals("")) {
                    Toast.makeText(EditProfile.this, "Please check user name first", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!userName.matches("[a-zA-Z0-9]*")) {
                    Toast.makeText(EditProfile.this, "only text and numbers are allowed in name", Toast.LENGTH_LONG).show();
                    return;
                }
                boolean isSlected = false;
                for (int i = 0; i < gameList.getChildCount(); i++) {
                    TextView textView = (TextView) gameList.getChildAt(i);
                    if (textView.getTag().toString().equals("1")) {
                        isSlected = true;
                    }
                }
                if (!isSlected) {
                    Toast.makeText(EditProfile.this, "Please select the game you play", Toast.LENGTH_LONG).show();
                    return;
                }
                if (picturePath == null)
                    updateName();
                else
                    uploadProfile();
            }
        });
        TI_userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View views = getLayoutInflater().inflate(R.layout.username_sheet, null);
                dialogBottom = new RoundedBottomSheetDialog(EditProfile.this);
                ed_userName = views.findViewById(R.id.userName);
                TextView done = views.findViewById(R.id.done);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!ed_userName.getText().toString().trim().equals(""))
                            checkUserName();
                    }
                });
                ed_userName.setText(userName);
                ed_userName.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (ed_userName.getText().toString().equals(sharedPreferences.getString(AppConstant.userName, ""))) {
                            userName = ed_userName.getText().toString();
                            ed_userName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.card_account_details_outline, 0, R.drawable.ic_check, 0);
                            ed_userName.setError(null);
                            return;
                        }
                        ed_userName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.card_account_details_outline, 0, R.drawable.info, 0);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                    }
                });
                dialogBottom.setContentView(views);
                dialogBottom.show();
            }
        });
        playerName.setText(sharedPreferences.getString(AppConstant.name, "Player"));
        bio.setText(sharedPreferences.getString(AppConstant.bio, ""));
        tv_title.setText(sharedPreferences.getString(AppConstant.title, ""));
        userName = sharedPreferences.getString(AppConstant.userName, "");
        TI_userName.setText(userName);
        TI_userName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.card_account_details_outline, 0, R.drawable.ic_check, 0);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(EditProfile.this, Manifest.permission.ACCESS_MEDIA_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION},
                                1);
                    } else {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, PROFILE_IMAGE);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(EditProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditProfile.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                    } else {
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, PROFILE_IMAGE);
                    }
                }

            }
        });
        Glide.with(this).load(AppConstant.AppUrl + "images/" + appConstant.getId() + ".png?u=" + AppConstant.imageExt())
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.game_avatar).into(imgProfile);
        for (int i = 0; i < gameList.getChildCount(); i++) {
            MaterialTextView textView = (MaterialTextView) gameList.getChildAt(i);
            if (prefs.getString(textView.getText().toString(), "0").equals("0")) {
                textView.setBackgroundResource(R.drawable.curved_drawable);
                textView.setTextColor(Color.parseColor("#333333"));
            } else {
                textView.setBackgroundResource(R.drawable.curved_red);
                textView.setTextColor(Color.parseColor("#ffffff"));
            }
            textView.setTag(prefs.getString(textView.getText().toString(), "0"));
            textView.setPadding(13, 13, 13, 13);
        }
    }

    public void subscribe(View view) {
        TextView tv_view = (TextView) view;
        if (tv_view.getTag().toString().equals("0")) {
            tv_view.setTag("1");
            tv_view.setBackgroundResource(R.drawable.curved_red);
            tv_view.setTextColor(Color.parseColor("#ffffff"));
        } else {
            tv_view.setTag("0");
            tv_view.setBackgroundResource(R.drawable.curved_drawable);
            tv_view.setTextColor(Color.parseColor("#333333"));
        }
        tv_view.setPadding(13, 13, 13, 13);
    }

    public void SubscribeChannel(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SUBSCRIBED", "SUCCESS Free Fire");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("SUBSCRIBED--1", e.getMessage());
            }
        });
    }

    public void unSubscribeChannel(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("UNSUBSCRIBED", "SUCCESS Free Fire");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("UNSUBSCRIBED--1", e.getMessage());
            }
        });
    }

    private void checkUserName() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("checking username...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "username.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseValue) {
                        ed_userName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.card_account_details_outline,
                                0,
                                responseValue.equalsIgnoreCase("true") ? R.drawable.ic_check : R.drawable.close,
                                0);
                        Toast.makeText(EditProfile.this, responseValue.equalsIgnoreCase("true") ? "User name available " : "User name not available ", Toast.LENGTH_SHORT).show();
                        userName = responseValue.equalsIgnoreCase("true") ? ed_userName.getText().toString() : "";
                        if (responseValue.equalsIgnoreCase("true")) {
                            TI_userName.setText(userName);
                            dialogBottom.dismiss();
                        }
                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(EditProfile.this, "Something went wrong try again later ", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userName", ed_userName.getText().toString().toLowerCase());
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

    private void updateName() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "profile_update.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        progressDialog.cancel();

                        saveToProfile();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
                Toast.makeText(EditProfile.this, "Something went wrong try again later ", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", new AppConstant(EditProfile.this).getId());
                params.put("name", playerName.getText().toString() + "");
                params.put("userName", TI_userName.getText().toString().toLowerCase() + "");
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

    private void uploadProfile() {
        ProgressDialog dialog = new ProgressDialog(EditProfile.this);
        dialog.setMessage("Uploading file, please wait.");
        dialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                AppConstant.AppUrl + "profile_pic.php?" +
                        "userid=" + appConstant.getId() + "&&name=" + playerName.getText().toString() + "&&userName=" + TI_userName.getText().toString()
                ,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        saveToProfile();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                        // Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError", "" + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("uploaded_file", new DataPart(appConstant.getId() + ".png", getFileDataFromDrawable()));
                // params.put("userId", new DataPart("1605435786512"));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable() {
        selectedImage = getResizedBitmap(selectedImage, 700);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void saveToProfile() {
        mDatabase = FirebaseDatabase.getInstance().getReference(appConstant.users);
        SharedPreferences sharedPreferences = getSharedPreferences(new AppConstant(this).getId(), 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstant.name, playerName.getText().toString());
        editor.putString(AppConstant.userName, TI_userName.getText().toString());
        editor.putString(AppConstant.bio, bio.getText().toString());
        editor.putString(AppConstant.title, tv_title.getText().toString());
        editor.putString(AppConstant.profile, (System.currentTimeMillis() / 1000) + "");
        picturePath = null;
        editor.apply();
        mDatabase.child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.bio).
                setValue(bio.getText().toString());
        mDatabase.child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.title).
                setValue(tv_title.getText().toString());
        mDatabase.child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.name).
                setValue(playerName.getText().toString());
        mDatabase.child(AppController.getInstance().userId).child(AppConstant.userName).
                setValue(TI_userName.getText().toString());
        for (int i = 0; i < gameList.getChildCount(); i++) {
            TextView textView = (TextView) gameList.getChildAt(i);
            SharedPreferences.Editor editors = getSharedPreferences(AppConstant.AppName, MODE_PRIVATE).edit();
            editors.putString(textView.getText().toString(), textView.getTag().toString());
            editors.apply();
            if (textView.getTag().toString().equals("1")) {
                SubscribeChannel(textView.getText().toString());
            } else {
                unSubscribeChannel(textView.getText().toString());
            }
        }
        Toast.makeText(EditProfile.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //                   CropImage.activity().start(EditProfile.this);
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, PROFILE_IMAGE);
                } else {
                    Toast.makeText(EditProfile.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImg = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImg,
                    filePathColumn, null, null, null);

            if (cursor == null || cursor.getCount() < 1) {
                return; // no cursor or no record. DO YOUR ERROR HANDLING
            }

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            if (columnIndex < 0) // no column index
                return; // DO YOUR ERROR HANDLING

            picturePath = cursor.getString(columnIndex);
            cursor.close();
            try {
                selectedImage = getBitmapFromUri(data.getData());
                Glide.with(this).load(picturePath).apply(new RequestOptions().circleCrop()).into(imgProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = resize(BitmapFactory.decodeFileDescriptor(fileDescriptor), 120, 120);
        parcelFileDescriptor.close();
        return image;
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }


}
