package com.intelj.y_ral_gaming;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DemoDelete extends AppCompatActivity {
    private static final int REQUEST_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.xx);

        ScreenshotManager.INSTANCE.requestScreenshotPermission(DemoDelete.this, REQUEST_ID);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DemoDelete.this,"hey",Toast.LENGTH_LONG).show();
                ScreenshotManager.INSTANCE.takeScreenshot(DemoDelete.this);
            }
        }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ID)
            ScreenshotManager.INSTANCE.onActivityResult(resultCode, data);
    }
}