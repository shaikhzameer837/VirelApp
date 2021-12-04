//MainActivity.java file

package com.intelj.yral_gaming.project;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.intelj.yral_gaming.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom);
    }
    public void showBottom(View v){
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(((AppCompatActivity)this).getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
}