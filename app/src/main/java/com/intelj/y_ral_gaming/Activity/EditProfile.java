package com.intelj.y_ral_gaming.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.R;
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
    TextInputEditText playerName, TI_userName,bio;
    AppCompatButton done;
    String userName = "";
    DatabaseReference mDatabase;
    AutoCompleteTextView tv_title;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appConstant = new AppConstant(this);
        setContentView(R.layout.edit_profile);
        imgProfile = findViewById(R.id.imgs);
        playerName = findViewById(R.id.name);
        TI_userName = findViewById(R.id.userName);
        bio = findViewById(R.id.bio);
        done = findViewById(R.id.done);
        TI_userName.setEnabled(true);
        playerName.setEnabled(true);
        done.setVisibility(View.VISIBLE);
        findViewById(R.id.save).setVisibility(View.GONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this,android.R.layout.simple_list_item_1,AppConstant.player_title);
        tv_title= findViewById(R.id.autoCompleteTextView1);
        tv_title.setThreshold(0);//will start working from first character
        tv_title.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        SharedPreferences sharedPreferences = getSharedPreferences(new AppConstant(EditProfile.this).getId(), 0);
        tv_title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                tv_title.showDropDown();
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (tv_title.getRight() - tv_title.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        tv_title.setText("");
                        return true;
                    }
                }
                return false;
            }
        });
        TI_userName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (tv_title.getRight() - tv_title.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        checkUserName();
                        return true;
                    }
                }
                return false;
            }
        });
        tv_title.setKeyListener(null);
        mDatabase = FirebaseDatabase.getInstance().getReference(appConstant.users);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerName.getText().toString().trim().equals("")) {
                    playerName.requestFocus();
                    playerName.setError("Player name cannot be empty");
                    return;
                }
                if (userName.equals("")) {
                    TI_userName.requestFocus();
                    TI_userName.setError("Click to check username availability", null);
                    return;
                }
                if (picturePath == null)
                    updateName();
                else
                    uploadProfile();
            }
        });
        TI_userName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(TI_userName.getText().toString().equals(sharedPreferences.getString(AppConstant.userId, ""))){
                    userName = TI_userName.getText().toString();
                    TI_userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                    TI_userName.setError(null);
                    return;
                }
                TI_userName.setError("Click to check username availability", null);
                userName = "";
                TI_userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.info, 0);
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
        playerName.setText(sharedPreferences.getString(AppConstant.name, "Player"));
        bio.setText(sharedPreferences.getString(AppConstant.bio, ""));
        tv_title.setText(sharedPreferences.getString(AppConstant.title, ""));
        TI_userName.setText(sharedPreferences.getString(AppConstant.userId, ""));
        TI_userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
        userName = sharedPreferences.getString(AppConstant.userId, "");
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(EditProfile.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        });
        Glide.with(this).load(sharedPreferences.getString(AppConstant.myPicUrl, "")).placeholder(R.drawable.game_avatar).apply(new RequestOptions().circleCrop()).into(imgProfile);

    }

    private void checkUserName() {
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child("users").orderByChild(AppConstant.userName).equalTo(TI_userName.getText().toString());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.e("number//---", postSnapshot.getKey());
                        if(postSnapshot.getKey().equals(appConstant.getId())){
                            Toast.makeText(EditProfile.this, "User name available ", Toast.LENGTH_SHORT).show();
                            TI_userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                        }else{
                            Toast.makeText(EditProfile.this, "User name unavailable ", Toast.LENGTH_SHORT).show();
                            TI_userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close, 0);
                        }
                    }
                }else{
                    userName = TI_userName.getText().toString();
                    Toast.makeText(EditProfile.this, "User name available ", Toast.LENGTH_SHORT).show();
                    TI_userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateName() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/profile_update.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);
                        progressDialog.cancel();
                        saveToProfile(null);
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
                params.put("userName", TI_userName.getText().toString() + "");
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
                "http://y-ral-gaming.com/admin/api/profile_pic.php?" +
                        "userid=" + appConstant.getId() + "&&name=" + playerName.getText().toString() + "&&userName=" + TI_userName.getText().toString()
                ,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        saveToProfile(new String(response.data));
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

            //            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("tags", tags);
//                return params;
//            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("uploaded_file", new DataPart(appConstant.getId() + ".png", getFileDataFromDrawable()));
                // params.put("userId", new DataPart("1605435786512"));
                return params;
            }
        };
        //adding the request to volley
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

    private void saveToProfile(String imageUrl) {
        SharedPreferences sharedPreferences = getSharedPreferences(new AppConstant(this).getId(), 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstant.name, playerName.getText().toString());
        editor.putString(AppConstant.userId, TI_userName.getText().toString());
        editor.putString(AppConstant.bio, bio.getText().toString());
        editor.putString(AppConstant.title, tv_title.getText().toString());
        if (imageUrl != null) {
//            FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.myPicUrl).setValue(imageUrl);
            editor.putString(AppConstant.myPicUrl, imageUrl);
            picturePath = null;
        }
        editor.apply();
        mDatabase.child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.bio).
                setValue(bio.getText().toString());
        mDatabase.child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.title).
                setValue(tv_title.getText().toString());
       mDatabase.child(AppController.getInstance().userId).child(AppConstant.userName).
                setValue(TI_userName.getText().toString());
        Toast.makeText(EditProfile.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 1:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
