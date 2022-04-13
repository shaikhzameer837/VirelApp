package com.intelj.y_ral_gaming.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SigninActivity;
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
    TextInputEditText playerName, discordId,bio;
    AppCompatButton done;
    DatabaseReference mDatabase;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appConstant = new AppConstant(this);
        setContentView(R.layout.edit_profile);
        imgProfile = findViewById(R.id.imgs);
        playerName = findViewById(R.id.name);
        discordId = findViewById(R.id.discordId);
        bio = findViewById(R.id.bio);
        done = findViewById(R.id.done);
        discordId.setEnabled(true);
        playerName.setEnabled(true);
        done.setVisibility(View.VISIBLE);
        findViewById(R.id.save).setVisibility(View.GONE);
        mDatabase = FirebaseDatabase.getInstance().getReference(appConstant.users);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerName.getText().toString().trim().equals("")) {
                    playerName.requestFocus();
                    playerName.setError("Player name cannot be empty");
                    return;
                }
                if (picturePath == null)
                    updateName();
                else
                    uploadProfile();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences(new AppConstant(EditProfile.this).getId(), 0);
        discordId.setText(sharedPreferences.getString(AppConstant.discordId, ""));
        playerName.setText(sharedPreferences.getString(AppConstant.userName, "Player"));
        bio.setText(sharedPreferences.getString(AppConstant.bio, "Player"));
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
                params.put("discord", discordId.getText().toString() + "");
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
                        "userid=" + appConstant.getId() + "&&name=" + playerName.getText().toString() + "&&discordId=" + discordId.getText().toString()
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
        editor.putString(AppConstant.userName, playerName.getText().toString());
        editor.putString(AppConstant.discordId, discordId.getText().toString());
        editor.putString(AppConstant.bio, bio.getText().toString());
        if (imageUrl != null) {
//            FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.myPicUrl).setValue(imageUrl);
            editor.putString(AppConstant.myPicUrl, imageUrl);
            picturePath = null;
        }
        editor.apply();
        mDatabase.child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.bio).
                setValue(bio.getText().toString());
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
