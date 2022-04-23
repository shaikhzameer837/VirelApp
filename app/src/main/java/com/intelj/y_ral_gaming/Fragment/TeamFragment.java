package com.intelj.y_ral_gaming.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intelj.y_ral_gaming.Adapter.PopularAdapter;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.PopularModel;
import com.intelj.y_ral_gaming.R;

import java.util.ArrayList;
import java.util.List;

public class TeamFragment extends Fragment {
    View rootView;
    RecyclerView rv_teamlist;
    PopularAdapter popularAdapter;
    List<PopularModel> popularModels = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.team_list, container, false);
        rv_teamlist = rootView.findViewById(R.id.rv_teamlist);
        popularModels.add(new PopularModel("http://y-ral-gaming.com/admin/api/images/95.png?u=" + (System.currentTimeMillis() / 1000),"","",""));
        popularAdapter = new PopularAdapter(getActivity(), popularModels);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        rv_teamlist.setLayoutManager(mLayoutManager);
        rv_teamlist.setAdapter(popularAdapter);
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }
    }

    public void onResumeView() {

    }
    public TeamFragment() {

    }


    public TeamFragment(String title, long show_listview) {

    }

}
