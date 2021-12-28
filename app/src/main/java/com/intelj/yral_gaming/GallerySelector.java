package com.intelj.yral_gaming;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intelj.yral_gaming.Utils.RecyclerTouchListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class GallerySelector extends AppCompatActivity {
    private List<String> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GalleryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_selection);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new GalleryAdapter(GallerySelector.this, movieList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",movieList.get(position));
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                Toast.makeText(getApplicationContext(), movieList.get(position) + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        getFilePaths();
    }
        public ArrayList<String> getFilePaths()
        {


            Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.ImageColumns.DATA};
            Cursor c = null;
            SortedSet<String> dirList = new TreeSet<String>();
            ArrayList<String> resultIAV = new ArrayList<String>();

            String[] directories = null;
            if (u != null)
            {
                c = managedQuery(u, projection, null, null, null);
            }

            if ((c != null) && (c.moveToFirst()))
            {
                do
                {
                    String tempDir = c.getString(0);
                    tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                    try{
                        dirList.add(tempDir);
                    }
                    catch(Exception e)
                    {

                    }
                }
                while (c.moveToNext());
                directories = new String[dirList.size()];
                dirList.toArray(directories);

            }

            for(int i=0;i<dirList.size();i++)
            {
                File imageDir = new File(directories[i]);
                File[] imageList = imageDir.listFiles();
                if(imageList == null)
                    continue;
                for (File imagePath : imageList) {
                    try {

                        if(imagePath.isDirectory())
                        {
                            imageList = imagePath.listFiles();

                        }
                        if ( imagePath.getName().contains(".jpg")|| imagePath.getName().contains(".JPG")
                                || imagePath.getName().contains(".jpeg")|| imagePath.getName().contains(".JPEG")
                                || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                                || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                                || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")
                        )
                        {



                            String path= imagePath.getAbsolutePath();
                            resultIAV.add(path);
                            movieList.add(imagePath.getAbsolutePath());
                        }
                    }
                    //  }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
return resultIAV;

        }
     }

    /**
     * Prepares sample data to provide data set to adapter
     */


