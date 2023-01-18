package com.intelj.y_ral_gaming.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.intelj.y_ral_gaming.Adapter.AnnouncementListAdapter;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.model.AnnouncementModel;

import java.util.ArrayList;

public class AnnouncementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<AnnouncementModel> announcementModelArrayList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_member);

        recyclerView = findViewById(R.id.recyclerView);
        announcementModelArrayList = new ArrayList<>();
        announcementModelArrayList.add(new AnnouncementModel("Email", android.R.drawable.ic_menu_help));

        AnnouncementListAdapter adapter = new AnnouncementListAdapter(announcementModelArrayList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}