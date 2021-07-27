package com.intelj.yral_gaming.Activity;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intelj.yral_gaming.Adapter.UserListAdapter;
import com.intelj.yral_gaming.AppController;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.Utils.RecyclerTouchListener;
import com.intelj.yral_gaming.model.UserListModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class GroupProfile extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 23;
    ImageView imgs;
    String picturePath = null;
    private Uri filePath = null;
    private RecyclerView recyclerview;
    private ArrayList<UserListModel> userListModel;
    EditText nameEdt;
    TextView member_count;
    private UserListAdapter userAdapter;
    SharedPreferences prefs;
    String groupId;
    SharedPreferences Groupprefs;
    Set<String> set;
    private Button addmemberingoup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_profile);
        imgs = findViewById(R.id.group_img);
        groupId = getIntent().getStringExtra(AppConstant.team);
        Groupprefs = getSharedPreferences(groupId, Context.MODE_PRIVATE);
        nameEdt = findViewById(R.id.nameEdt);
        member_count = findViewById(R.id.member_count);
        addmemberingoup = findViewById(R.id.addmemberingoup);
        nameEdt.setText(Groupprefs.getString(AppConstant.teamName, ""));
        set = Groupprefs.getStringSet(AppConstant.teamMember, null);

        member_count.setText(set == null ? "0" : set.size() + " Member");
        Glide.with(this).load(Groupprefs.getString(AppConstant.myPicUrl, "")).placeholder(R.drawable.account_group)
                .into(imgs);
        recyclerview = findViewById(R.id.recyclerview);
        userListModel = new ArrayList<>();
        userAdapter = new UserListAdapter(this, userListModel);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(userAdapter);
        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (AppController.getInstance().mySnapShort.child(AppConstant.team).child(groupId).getValue().equals("1")) {
                    removeMember(position);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prefs = getSharedPreferences(AppConstant.AppName, Context.MODE_PRIVATE);

        addmemberingoup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GroupProfile.this, SearchFriendActivity.class);
                i.putExtra("group_id", groupId);
                ArrayList<String> member_list = new ArrayList<>();
                for (UserListModel listModel : userListModel) {
                    if (!listModel.getUserId().equals(AppController.getInstance().userId))
                        member_list.add(listModel.getUserId());
                }
                i.putExtra("member_list", member_list);
                i.putExtra("team_name", nameEdt.getText().toString());
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        displayFriends();
    }

    private void removeMember(int position) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure you want to remove " + userListModel.get(position).getGenre() + " from group");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        set.remove(userListModel.get(position).getUserId());
//                        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userListModel.get(position).getUserId())
//                                .child(AppConstant.pinfo).child(AppConstant.team).child(groupId).removeValue();
//                            FirebaseFirestore.getInstance()
//                                    .collection(AppConstant.team)
//                                    .document(groupId).update(set);
                        Toast.makeText(GroupProfile.this, userListModel.get(position).getGenre() + " Removed", Toast.LENGTH_LONG).show();
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

    private void displayFriends() {
        userListModel.clear();
        for (String s : set) {
            SharedPreferences sharedpreferences = getSharedPreferences(s, Context.MODE_PRIVATE);
            userListModel.add(new UserListModel(sharedpreferences.getString(AppConstant.myPicUrl, ""),
                    sharedpreferences.getString(AppConstant.userName, ""),
                    sharedpreferences.getString(AppConstant.phoneNumber, ""), s));
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
            uploadFiles();
        }
    }

    private void uploadFiles() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            HashMap<String, Object> updateImg = new HashMap<>();
                                            updateImg.put(AppConstant.myPicUrl, imageUrl);
                                            FirebaseFirestore.getInstance()
                                                    .collection(AppConstant.team)
                                                    .document(groupId)
                                                    .update(updateImg);
                                            Groupprefs.edit().putString(AppConstant.myPicUrl, imageUrl).apply();
                                            progressDialog.dismiss();
                                            picturePath = null;
                                            Toast.makeText(GroupProfile.this, "Group pic Updated", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(GroupProfile.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }

    }

    public void changeName(View view) {
        ImageView imgsV = (ImageView) view;
        if (!nameEdt.isEnabled()) {
            nameEdt.setEnabled(true);
            nameEdt.setSelection(nameEdt.getText().length());
            nameEdt.requestFocus();
            imgsV.setImageResource(R.drawable.ic_check);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } else {
            nameEdt.setEnabled(false);
            nameEdt.clearFocus();
            imgsV.setImageResource(R.drawable.ic_edit);
            HashMap<String, Object> updateImg = new HashMap<>();
            updateImg.put(AppConstant.teamName, nameEdt.getText().toString());
            FirebaseFirestore.getInstance()
                    .collection(AppConstant.team)
                    .document(groupId)
                    .update(updateImg);
            Groupprefs.edit().putString(AppConstant.teamName, nameEdt.getText().toString()).apply();
            Toast.makeText(this, "info Updated", Toast.LENGTH_LONG).show();
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
                        if (AppController.getInstance().mySnapShort.child(AppConstant.team).child(groupId).getValue().equals("1")) {
                            for (String s : set) {
                                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(s)
                                        .child(AppConstant.pinfo).child(AppConstant.team).child(groupId).removeValue();
                            }
                            FirebaseFirestore.getInstance()
                                    .collection(AppConstant.team)
                                    .document(groupId).delete();
                            Toast.makeText(GroupProfile.this, "Group Deleted", Toast.LENGTH_LONG).show();
                            finish();
                        }
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
