package com.intelj.y_ral_gaming;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.transition.Fade;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.Adapter.ChatAdapter;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.db.AppDataBase;
import com.intelj.y_ral_gaming.db.Chat;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    TextInputEditText message;
    ImageView fileSelect, profile;
    int App_Image = 11;
    String picturePath = "";
    Bitmap selectedImage;
    AppDataBase appDataBase;
    String userId;
    TextView name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        View decor = getWindow().getDecorView();
        Fade fade = new Fade();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        name = findViewById(R.id.name);
        profile = findViewById(R.id.profile);
        recyclerView = findViewById(R.id.recycler_view);
        fileSelect = findViewById(R.id.fileSelect);
        userId = getIntent().getStringExtra(AppConstant.id);
        appDataBase = AppDataBase.getDBInstance(ChatActivity.this, userId + "_chats");
        message = findViewById(R.id.message);
        mAdapter = new ChatAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        fileSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(ChatActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("chat"));
        mAdapter.setAllChat(appDataBase);
        scrollToBottom();
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.getText().toString().trim().length() == 0) {
                    Toast.makeText(ChatActivity.this, "Message Cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                Chat chat = new Chat();
                chat.messages = message.getText().toString();
                chat.msgStatus = 0;
                chat.owner = new AppConstant(ChatActivity.this).getId();
                chat.subject = 0;
                chat.times = System.currentTimeMillis() + "";
                appDataBase.chatDao().insertUser(chat);
                mAdapter.setAllChat(appDataBase);
                message.setText(null);
                scrollToBottom();
                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userId)
                        .child(AppConstant.realTime).child(AppConstant.msg).child("" + (System.currentTimeMillis() / 1000)).setValue(chat);
            }
        });
        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userId).child(AppConstant.pinfo)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name.setText(dataSnapshot.child(AppConstant.name).getValue(String.class));
                        Glide.with(ChatActivity.this).load("http://y-ral-gaming.com/admin/api/images/" + userId + ".png?u=" + (System.currentTimeMillis() / 1000)).apply(new RequestOptions().circleCrop()).placeholder(R.drawable.game_avatar).into(profile);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.setAllChat(appDataBase);
            scrollToBottom();
        }
    };

    private void scrollToBottom() {
        if (appDataBase.chatDao().getAllChat().size() > 0) {
            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(appDataBase.chatDao().getAllChat().size() - 1);
                        }
                    }, 500);
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
                    startActivityForResult(i, App_Image);
                } else {
                    Toast.makeText(ChatActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == App_Image && resultCode == RESULT_OK
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
                Bitmap blurBitmap = blur(Bitmap.createScaledBitmap(selectedImage, 250, 250, false));
                Chat chat = new Chat();
                chat.messages = picturePath;
                chat.msgStatus = 0;
                chat.owner = new AppConstant(this).getId();
                chat.subject = 1;
                chat.blurImg = getBase64String(blurBitmap);
                chat.times = System.currentTimeMillis() + "";
                appDataBase.chatDao().insertUser(chat);
                mAdapter.setAllChat(appDataBase);
                scrollToBottom();
                FirebaseDatabase.getInstance().getReference(AppConstant.users).child(userId)
                        .child(AppConstant.realTime).child(AppConstant.msg).child("" + (System.currentTimeMillis() / 1000)).setValue(chat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] imageBytes = baos.toByteArray();

        String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        return base64String;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public Bitmap blur(Bitmap image) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(this);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(25f);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }
}