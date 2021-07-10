package com.intelj.yral_gaming.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.intelj.yral_gaming.Adapter.UserListAdapter;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.model.UserListModel;

import java.util.ArrayList;
import java.util.Set;

public class GroupProfile extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 23;
    ImageView imgs;
    String picturePath = null;
    private Uri filePath = null;
    private RecyclerView recyclerview;
    private ArrayList<String> contactList;
    private ArrayList<UserListModel> userListModel;
    EditText nameEdt;
    private UserListAdapter userAdapter;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_profile);
        imgs = findViewById(R.id.group_img);
        nameEdt = findViewById(R.id.nameEdt);
        Glide.with(this).load(R.drawable.account_group).placeholder(R.drawable.account_group)
                .apply(new RequestOptions().circleCrop()).into(imgs);
        recyclerview = findViewById(R.id.recyclerview);
        userListModel = new ArrayList<>();
        userAdapter = new UserListAdapter(this, userListModel);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(userAdapter);
        prefs = getSharedPreferences(AppConstant.AppName, Context.MODE_PRIVATE);
        displayFriends();
    }
    private void displayFriends() {
        userListModel.clear();
        Set<String> set = prefs.getStringSet(AppConstant.users, null);
        for (String s : set) {
            SharedPreferences sharedpreferences = getSharedPreferences(s, Context.MODE_PRIVATE);
            userListModel.add(new UserListModel(sharedpreferences.getString(AppConstant.myPicUrl, ""),
                    sharedpreferences.getString(AppConstant.userName, ""),
                    sharedpreferences.getString(AppConstant.phoneNumber, ""),s));
        }
        userAdapter.notifyDataSetChanged();
    }
    public void openCamera(View view) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
    }
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else {
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == REQUEST_ID)
            ScreenshotManager.INSTANCE.onActivityResult(resultCode, data);*/
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            filePath = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
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
            Glide.with(this).load(picturePath).into(imgs);
        }
    }

    public void changeName(View view) {
        ImageView imgsV = (ImageView)view;
        if(!nameEdt.isEnabled()) {
            nameEdt.setEnabled(true);
            nameEdt.setSelection(nameEdt.getText().length());
            nameEdt.requestFocus();
            imgsV.setImageResource(R.drawable.ic_check);
            InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }else{
            nameEdt.setEnabled(false);
            nameEdt.clearFocus();
            imgsV.setImageResource(R.drawable.ic_edit);
        }
    }

    public void DeleteGroup(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to delete this group");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
