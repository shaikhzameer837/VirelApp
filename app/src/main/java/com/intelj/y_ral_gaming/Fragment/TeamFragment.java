package com.intelj.y_ral_gaming.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.intelj.y_ral_gaming.Adapter.EventTeamAdapter;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.PopularModel;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.model.EventTeamModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TeamFragment extends Fragment {
    View rootView;
    RecyclerView rv_teamlist;
    EventTeamAdapter eventTeamAdapter;
    List<EventTeamModel> eventTeamList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        eventTeamList.clear();
        rootView = inflater.inflate(R.layout.team_list, container, false);
        rv_teamlist = rootView.findViewById(R.id.rv_teamlist);
        try {
            JSONObject jsonObject = new JSONObject(AppController.getInstance().tournamentModel.getTeam_list());
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (jsonObject.get(key) instanceof JSONObject) {
                    JSONObject teamJson = ((JSONObject) jsonObject.get(key));
                    String teamName = teamJson.getString("teamName");
                    JSONObject teamList = (JSONObject)teamJson.get("teams");
                    Log.e("lcat teamList", teamJson.getString("teams"));
                    eventTeamList.add(new EventTeamModel("http://y-ral-gaming.com/admin/api/images/"+key+".png?u=" + (System.currentTimeMillis() / 1000), teamName, teamJson.getString("teams"), key));
                }
            }

        } catch (Exception e) {

        }
        eventTeamAdapter = new EventTeamAdapter(getActivity(), eventTeamList);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        rv_teamlist.setLayoutManager(mLayoutManager);
        rv_teamlist.setAdapter(eventTeamAdapter);
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
