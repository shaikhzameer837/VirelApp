package com.intelj.yral_gaming;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intelj.yral_gaming.Activity.MainActivity;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.sdsmdg.harjot.rotatingtext.RotatingTextWrapper;
import com.sdsmdg.harjot.rotatingtext.models.Rotatable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.intelj.yral_gaming.Activity.MainActivity.PERMISSIONS_REQUEST_READ_CONTACTS;

public class UserInfoCheck extends AppCompatActivity {
    EditText playerName, discordId;
    ImageView profile;
    TextView status;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath = null;
    String picturePath = null;
    int RESULT_LOAD_IMAGE = 9;
    int x = 0;
    private ArrayList<String> contactList = new ArrayList<>();
    private DatabaseReference mFirebaseDatabaseReference;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_check);
        playerName = findViewById(R.id.name);
        status = findViewById(R.id.status);
        profile = findViewById(R.id.profile);
        discordId = findViewById(R.id.discordId);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(AppConstant.users);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(UserInfoCheck.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        });
        Glide.with(this).load(AppController.getInstance().mySnapShort.child(AppConstant.myPicUrl).getValue() == null ? "" : AppController.getInstance().mySnapShort.child(AppConstant.myPicUrl).getValue() + "").placeholder(R.drawable.profile_icon)
                .apply(new RequestOptions().circleCrop()).into(profile);
        playerName.setText(AppController.getInstance().mySnapShort.child(AppConstant.userName).getValue() == null ? "" : AppController.getInstance().mySnapShort.child(AppConstant.userName).getValue() + "");
        discordId.setText(AppController.getInstance().mySnapShort.child(AppConstant.discordId).exists() ? AppController.getInstance().mySnapShort.child(AppConstant.discordId).getValue() + "" : "");
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerName.getText().toString().isEmpty() || discordId.getText().toString().isEmpty())
                    Toast.makeText(UserInfoCheck.this, "Name and Discord Id cannot be empty", Toast.LENGTH_LONG).show();
                else
                    saveUserInfoData();
            }
        });
        RotatingTextWrapper rotatingTextWrapper =  findViewById(R.id.custom_switcher);
        rotatingTextWrapper.setSize(15);
        Rotatable rotatable = new Rotatable(Color.parseColor("#7e241c"), 1000, "Finding friends for you ", "We are almost done", "Thanks for keeping patience");
        rotatable.setSize(15);
        rotatable.setCenter(true);
        rotatable.setAnimationDuration(500);
        rotatable.setInterpolator(new BounceInterpolator());
        rotatingTextWrapper.setContent("", rotatable);
        PackageManager pm = getPackageManager();
        int hasPerm = pm.checkPermission(
                android.Manifest.permission.READ_CONTACTS,
                getPackageName());
        if (hasPerm != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserInfoCheck.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            new LoadContact().execute();
        }
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Map.Entry<String, Boolean> entry : AppController.getInstance().gameNameHashmap.entrySet()) {
            editor.putString(entry.getKey()+ "_" + AppConstant.userName,AppController.getInstance().mySnapShort.child(entry.getKey()+ "_" + AppConstant.userName).getValue() == null ? "" : AppController.getInstance().mySnapShort.child(entry.getKey()+ "_" + AppConstant.userName).getValue()+"");
            editor.putString(entry.getKey()+"",AppController.getInstance().mySnapShort.child(entry.getKey()).getValue() == null ? "" : AppController.getInstance().mySnapShort.child(entry.getKey()).getValue()+"");
            Log.e(entry.getKey()+"",AppController.getInstance().mySnapShort.child(entry.getKey()).getValue()+"");
        }
        editor.apply();

        startActivity(new Intent(UserInfoCheck.this, MainActivity.class));
    }

    private void saveUserInfoData() {
        if (picturePath == null)
            saveAndExit();
        else
            uploadFiles();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void saveAndExit() {
        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.userName).setValue(playerName.getText().toString());
        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.discordId).setValue(discordId.getText().toString());
        SharedPreferences sharedPreferences = getSharedPreferences(AppController.getInstance().userId, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstant.userName, playerName.getText().toString());
        editor.putBoolean(AppConstant.friends, true);
        editor.putString(AppConstant.discordId, discordId.getText().toString());
        editor.apply();
        Toast.makeText(this, "Profile  updated", Toast.LENGTH_LONG).show();
        startActivity(new Intent(UserInfoCheck.this, MainActivity.class));
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
            Glide.with(this).load(picturePath).apply(new RequestOptions().circleCrop()).into(profile);
        }
    }

    @Override
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
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new LoadContact().execute();
                } else {
                    showDialogPermission();
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void showDialogPermission() {
        new AlertDialog.Builder(this)
                .setTitle("Grant Permission")
                .setMessage("Please grant permission")
                .setCancelable(false)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(UserInfoCheck.this,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    class LoadContact extends AsyncTask<Void, Integer, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            getContactList();

        }

        protected String doInBackground(Void... arg0) {
            startingList();
            return null;
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void getContactList() {
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_URI};
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        int totalCount = phones.getCount();
        if (phones.getCount() > 0) {
            while (phones.moveToNext()) {
                status.setText(totalCount + "/" + contactList.size() + " Contacts found");
                String phoneNo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNo = phoneNo.replace(" ", "");
                phoneNo = phoneNo.replace("+91", "");
                if (phoneNo.startsWith("0"))
                    phoneNo = phoneNo.substring(1);
                if (phoneNo.length() > 7 && !phoneNo.equals(new AppConstant(this).getPhoneNumber().replace("+91", "")) && !contactList.contains(phoneNo))
                    contactList.add(phoneNo);
            }
        }
        phones.close();
    }

    private void startingList() {
        HashSet hs = new HashSet();
        hs.addAll(contactList);
        contactList.clear();
        contactList.addAll(hs);
        SharedPreferences prefs = getSharedPreferences(AppConstant.AppName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = new HashSet<>();
        x = 0;
        for (String s : contactList) {
            Query query = mFirebaseDatabaseReference.orderByChild(AppConstant.phoneNumber).equalTo(s);
            query.keepSynced(false);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            status.setText(x + "/" + contactList.size() + " Contacts");
                        }
                    });
                    x++;
                    for (DataSnapshot childDataSnap : snapshot.getChildren()) {
                        set.add(childDataSnap.getKey());
                        SharedPreferences sharedpreferences = getSharedPreferences(childDataSnap.getKey(), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editors = sharedpreferences.edit();
                        editors.putString(AppConstant.userName, childDataSnap.child(AppConstant.pinfo).child(AppConstant.userName).getValue() + "");
                        editors.putString(AppConstant.phoneNumber, childDataSnap.child(AppConstant.phoneNumber).getValue() + "");
                        editors.putString(AppConstant.myPicUrl, childDataSnap.child(AppConstant.pinfo).child(AppConstant.myPicUrl).getValue() + "");
                        editors.putString(AppConstant.discordId, childDataSnap.child(AppConstant.pinfo).child(AppConstant.discordId).getValue() + "");
                        for (Map.Entry<String, Boolean> entry : AppController.getInstance().gameNameHashmap.entrySet()) {
                            editors.putString(entry.getKey(), childDataSnap.child(AppConstant.pinfo).child(entry.getKey()).getValue() + "");
                        }
                        editors.apply();
                    }
                    if (contactList.size() == x) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                status.setText(" Edit Profile");
                                editor.putStringSet(AppConstant.users, set);
                                editor.apply();
                                findViewById(R.id.btn_next).setVisibility(View.VISIBLE);
                                findViewById(R.id.profile).setVisibility(View.VISIBLE);
                                findViewById(R.id.lin).setVisibility(View.VISIBLE);
                                findViewById(R.id.pBar3).setVisibility(View.INVISIBLE);
                                findViewById(R.id.message).setVisibility(View.INVISIBLE);
                                findViewById(R.id.custom_switcher).setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void uploadFiles() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
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
                                            FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.myPicUrl).setValue(imageUrl);
                                            progressDialog.dismiss();
                                            saveAndExit();
                                            picturePath = null;
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
                            Toast.makeText(UserInfoCheck.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}
