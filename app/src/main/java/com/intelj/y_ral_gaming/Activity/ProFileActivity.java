package com.intelj.y_ral_gaming.Activity;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.R;

import java.util.ArrayList;
import java.util.List;

public class ProFileActivity extends AppCompatActivity {
    private List<ytModel> ytModelList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ytModelsAdapter mAdapter;
    TextView txt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        FillCustomGradient(findViewById(R.id.imgs));
        txt = findViewById(R.id.info);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ytModelsAdapter(this,ytModelList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        //for (DataSnapshot snapshot : AppController.getInstance().ytdataSnapshot.getChildren()) {
            txt.setText(AppController.getInstance().ytdataSnapshot.child("youtube_channel_name").getValue()+"\n");
            txt.append(AppController.getInstance().ytdataSnapshot.child("description").getValue()+"\n");
       // }
        Glide.with(this).load(AppController.getInstance().ytdataSnapshot.child("profile_photo").getValue() + "").into((ImageView)findViewById(R.id.profPic));
        prepareytModelData();
    }

    private void prepareytModelData() {
        ytModel ytModel = new ytModel("Mad Max: Fury Road", "https://s3.india.com/wp-content/uploads/2020/08/PUBG-cover-image_0_0.jpg", "2015");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Inside Out", "https://cdn.i-scmp.com/sites/default/files/styles/1200x800/public/d8/images/methode/2020/07/30/1a3889b0-d223-11ea-88dd-6bec610be4a6_image_hires_163718.jpg?itok=cQRz9TUK&v=1596098244", "2015");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Shaun the Sheep", "Animation", "2015");
        ytModelList.add(ytModel);

        ytModel = new ytModel("The Martian", "Science Fiction & Fantasy", "2015");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Mission: Impossible Rogue Nation", "Action", "2015");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Up", "Animation", "2009");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Star Trek", "Science Fiction", "2009");
        ytModelList.add(ytModel);

        ytModel = new ytModel("The LEGO ytModel", "Animation", "2014");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Iron Man", "Action & Adventure", "2008");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Aliens", "Science Fiction", "1986");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Chicken Run", "Animation", "2000");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Back to the Future", "Science Fiction", "1985");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Raiders of the Lost Ark", "Action & Adventure", "1981");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Goldfinger", "Action & Adventure", "1965");
        ytModelList.add(ytModel);

        ytModel = new ytModel("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        ytModelList.add(ytModel);

        mAdapter.notifyDataSetChanged();
    }
    private void FillCustomGradient(View v) {
        final View view = v;
        Drawable[] layers = new Drawable[1];

        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                LinearGradient lg = new LinearGradient(
                        0,
                        0,
                        0,
                        view.getHeight(),
                        new int[] {
                                getResources().getColor(android.R.color.transparent), // please input your color from resource for color-4
                                getResources().getColor(android.R.color.transparent),
                                getResources().getColor(android.R.color.transparent),
                                getResources().getColor(R.color.colorAccent),
                                getResources().getColor(R.color.colorPrimaryDark)},
                        new float[] { 0, 0,0,0.90f, 1 },
                        Shader.TileMode.CLAMP);
                return lg;
            }
        };
        PaintDrawable p = new PaintDrawable();
        p.setShape(new RectShape());
        p.setShaderFactory(sf);
        p.setCornerRadii(new float[] { 5, 5, 5, 5, 0, 0, 0, 0 });
        layers[0] = (Drawable) p;

        LayerDrawable composite = new LayerDrawable(layers);
        view.setBackgroundDrawable(composite);
    }
}
