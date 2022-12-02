package com.intelj.y_ral_gaming.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.intelj.y_ral_gaming.Activity.GameInfo;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.Fragment.OneFragment;
import com.intelj.y_ral_gaming.GameItem;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.RoundedBottomSheetDialog;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soup.neumorphism.NeumorphCardView;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {
    Context mContext;
    AppConstant appConstant;
    String title = "";
    int cost = 0;
    public List<GameItem> gameItem;
    BottomSheetDialog bottomSheetDialog;
    SharedPreferences sharedPreferences;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, info, type, count, prizepool,pp;
        ImageView imgs;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            count = view.findViewById(R.id.count);
            prizepool = view.findViewById(R.id.prizepool);
            pp = view.findViewById(R.id.pp);
            imgs = view.findViewById(R.id.imgs);
            info = view.findViewById(R.id.info);
            type = view.findViewById(R.id.type);
        }
    }


    public TimeSlotAdapter(Context mContext, ArrayList<GameItem> gameItem, String title) {
        this.mContext = mContext;
        this.gameItem = gameItem;
        this.title = title;
        sharedPreferences =
                mContext.getSharedPreferences
                        (AppConstant.AppName, Context.MODE_PRIVATE);
        appConstant = new AppConstant(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(gameItem.get(position).getTime());
        holder.prizepool.setText("Win \u20B9" + gameItem.get(position).getPerKill() + " Per kill");
        holder.type.setText("Pp \uD83D\uDCB0 \u20B9"+(Integer.parseInt(gameItem.get(position).getPerKill()) * gameItem.get(position).getMax()));
        Glide.with(mContext).load(gameItem.get(position).getYt_url().equals("") ? R.drawable.placeholder : "https://i.ytimg.com/vi/" + gameItem.get(position).getYt_url() + "/hqdefault_live.jpg").placeholder(R.mipmap.app_logo).into(holder.imgs);
        holder.imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameItem.get(position).getYt_url().equals("")) {
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/c/YRALGaming"));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/c/YRALGaming"));
                    try {
                        mContext.startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        mContext.startActivity(webIntent);
                    }
                } else {
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + gameItem.get(position).getYt_url()));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + gameItem.get(position).getYt_url()));
                    try {
                        mContext.startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        mContext.startActivity(webIntent);
                    }
                }
            }
        });
        switch (Integer.parseInt(gameItem.get(position).getType())) {
            case 1:
                holder.pp.setText(" [Solo]");
                break;
            case 2:
                holder.pp.setText(" [Duo]");
                break;
            case 4:
                holder.pp.setText(" [Squad]");
                break;
        }
        holder.count.setText(gameItem.get(position).getCount() + "/" + gameItem.get(position).getMax());
        holder.info.setText(gameItem.get(position).getIsexist().equals("0") ? " Join now \uD83D\uDC49" : " Already Joined \uD83E\uDD1E");
        holder.info.setTextColor(gameItem.get(position).getIsexist().equals("0") ? Color.parseColor("#7e241c") : Color.parseColor("#097969"));
        holder.info.setTag(position);
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameItem.get((int) v.getTag()).getIsexist().equals("0"))
                    registerTeam(position);
                else {
                    Toast.makeText(v.getContext(), "You have already registered", Toast.LENGTH_LONG).show();
                    GameInfo BottomSheetFragment = new GameInfo();
                    BottomSheetFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "");
                }
            }
        });
    }

    private void registerTeam(int position) {
        if (!new AppConstant(mContext).checkLogin()) {
            Intent intent = new Intent("custom-event-name");
            intent.putExtra(AppConstant.AppName, true);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            return;
        }
        showRoundedBottomSheetDialog(position);
    }

    private void showRoundedBottomSheetDialog(int position) {
        bottomSheetDialog = new RoundedBottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.register_match);
        TextView textView = bottomSheetDialog.findViewById(R.id.integer_number);
        TextView infos = bottomSheetDialog.findViewById(R.id.infos);
        NeumorphCardView btn_next = bottomSheetDialog.findViewById(R.id.btn_next);
        LinearLayout lin = bottomSheetDialog.findViewById(R.id.lin);
        EditText gameName = bottomSheetDialog.findViewById(R.id.ingameName);
        SharedPreferences prefs = mContext.getSharedPreferences(AppConstant.AppName, 0);
        gameName.setHint("Enter your " + title + " player 1 in game name");
        gameName.setText(prefs.getString("GameName", ""));
        setViews(infos, position, textView);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameName.getText().toString().trim().equals("")) {
                    gameName.requestFocus();
                    gameName.setError("This cannot be empty");
                    return;
                }
                 registerForMatch(position, gameName.getText().toString());

            }
        });
        bottomSheetDialog.findViewById(R.id.decrease).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(textView.getText().toString()) == 1) {
                    return;
                }
                View namebar = bottomSheetDialog.findViewById(Integer.parseInt(textView.getText().toString()));
                ((ViewGroup) namebar.getParent()).removeView(namebar);
                textView.setText((Integer.parseInt(textView.getText().toString()) - 1) + "");
                setViews(infos, position, textView);
            }
        });
        bottomSheetDialog.findViewById(R.id.increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(textView.getText().toString()) == Integer.parseInt(gameItem.get(position).getType())) {
                    return;
                }
                textView.setText((Integer.parseInt(textView.getText().toString()) + 1) + "");
                EditText et = new EditText(mContext);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                et.setLayoutParams(p);
                et.setSingleLine(true);
                et.setTextSize(14);
                et.setHint("Enter your " + title + " player " + textView.getText().toString() + " in game name");
                et.setId(Integer.parseInt(textView.getText().toString()));
                lin.addView(et);
                setViews(infos, position, textView);
            }
        });
//        bottomSheetDialog.findViewById(R.id.add_money).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AppConstant(mContext).addMoney(mContext);
//            }
//        });
        bottomSheetDialog.show();
    }

    private void setViews(TextView infos, int position, TextView textView) {
        cost = Integer.parseInt(textView.getText().toString()) * Integer.parseInt(gameItem.get(position).getEntryFees());
        if (Integer.parseInt(gameItem.get(position).getType()) != 1) {
            switch (Integer.parseInt(textView.getText().toString())) {
                case 2:
                    infos.setText("Play as Duo");
                    break;
                case 3:
                    infos.setText("Play as Trio");
                    break;
                case 4:
                    infos.setText("Play as Squad");
                    break;
            }
        } else
            bottomSheetDialog.findViewById(R.id.rel_increment).setVisibility(View.GONE);
    }

    private void registerForMatch(int position, String inGameName) {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("loading...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = AppConstant.AppUrl + "join_game.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick3", response);
                        progressDialog.cancel();

                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                appConstant.savePackage(appConstant.getId(),"");
                                int player_count = json.getInt("player_count");
                                AppController.getInstance().amount = AppController.getInstance().amount - json.getInt("entryFees");
                                Intent intent = new Intent("custom-event-name");
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                                gameItem.get(position).setIsexist(gameItem.get(position).getIsexist().equals("1") ? "0" : "1");
                                gameItem.get(position).setCount(player_count + "");
                                notifyDataSetChanged();
                                bottomSheetDialog.dismiss();
                                GameInfo BottomSheetFragment = new GameInfo();
                                BottomSheetFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "");
                            } else
                                Toast.makeText(mContext, json.getString("msg"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e("logMess", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.cancel();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("inGameName", inGameName);
                params.put("gameId", gameItem.get(position).getGameId());
                params.put("userId", appConstant.getId());
                if(!appConstant.getReferal().equals(""))
                    params.put("referral", appConstant.getReferal());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        queue.add(stringRequest);
    }
//    private void saveUserInfo(int position, View v, String igname, String strDate) {
//        ProgressDialog progressDialog = new ProgressDialog(mContext);
//        progressDialog.setTitle("Uploading...");
//        progressDialog.show();
//        RequestQueue queue = Volley.newRequestQueue(mContext);
//        String url = AppConstant.AppUrl + "register_give_away.php";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.e("onClick3", response);
//                        progressDialog.cancel();
//                        try {
//                            JSONObject json = new JSONObject(response);
//                            if (json.getBoolean("success")) {
//                                SharedPreferences.Editor editShared = sharedPreferences.edit();
//                                editShared.putString("gameWithTime", v.getTag() + "");
//                                editShared.putString("gameTitle", title);
//                                editShared.putBoolean(v.getTag() + "", json.getString("status").equals("1"));
//                                editShared.apply();
//                                notifyItemChanged(position);
//                                if (json.getString("status").equals("1")) {
//                                    Toast.makeText(mContext, "Successfully registered for the match", Toast.LENGTH_LONG).show();
//                                //    GameInfo bottomSheetFragment = new GameInfo(allData.get(position).getTime());
//                                   // bottomSheetFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), bottomSheetFragment.getTag());
//                                }
//                            } else {
//                                Toast.makeText(mContext, "Something went wrong try again later", Toast.LENGTH_LONG).show();
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.cancel();
///*                shimmer_container.hideShimmer();
//                shimmer_container.setVisibility(View.GONE);*/
//                error.printStackTrace();
//                FirebaseCrashlytics.getInstance().recordException(error);
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("user_id", appConstant.getUserId());
//                params.put("ign", igname);
//                params.put("match_id", strDate);
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//
//        queue.add(stringRequest);
//    }

//    private void showTeamList(String strDate) {
//        bottomSheetDialog = new RoundedBottomSheetDialog(mContext);
//        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
//        bottomSheetDialog.findViewById(R.id.newTeam).setVisibility(View.GONE);
//        bottomSheetDialog.findViewById(R.id.bott_button).setVisibility(View.GONE);
//        bottomSheetDialog.findViewById(R.id.create_team).setVisibility(View.GONE);
//        TextView title = bottomSheetDialog.findViewById(R.id.title);
//        title.setText("Select your team");
//        TextView txt = bottomSheetDialog.findViewById(R.id.subtitle);
//        txt.setText("only one team can be seelcted");
//        RecyclerView recyclerview = bottomSheetDialog.findViewById(R.id.recyclerview);
//        teamModel = new ArrayList<>();
//        for (DataSnapshot snapshot : mySnapShort.child(AppConstant.team).getChildren()) {
//            SharedPreferences prefs = mContext.getSharedPreferences(snapshot.getKey(), Context.MODE_PRIVATE);
//            teamModel.add(new UserListModel(prefs.getString(AppConstant.teamName, ""),
//                    prefs.getString(AppConstant.myPicUrl, ""),
//                    snapshot.getKey(),
//                    prefs.getStringSet(AppConstant.teamMember, null)));
//        }
//        MemberListAdapter userAdapter = new MemberListAdapter(mContext, teamModel, AppConstant.team);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
//        recyclerview.setLayoutManager(mLayoutManager);
//        recyclerview.setAdapter(userAdapter);
//        bottomSheetDialog.show();
//        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(mContext, recyclerview, new RecyclerTouchListener.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
//                bottomSheetFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), bottomSheetFragment.getTag());
//                /*BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
//                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());*/
//                /*Intent intent= new Intent(mContext, DemoActivity.class);
//                intent.putExtra("teammeber", (Serializable) teamModel.get(position).getTeamMember());
//                mContext.startActivity(intent);*/
////                sendRequest(position, strDate);
//                /*Intent intent = new Intent("custom-event-name");
//                intent.putExtra(AppConstant.teamMember, true);
//                intent.putExtra("teammeber", (Serializable) teamModel.get(position).getTeamMember());
//                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);*/
//
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));
//    }

    private void viewpagerbottomsheet() {
        bottomSheetDialog = new RoundedBottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.main_bottom_sheet_dialoglayout);
        ViewPager viewPager = bottomSheetDialog.findViewById(R.id.vpPager);
        Button next_button = bottomSheetDialog.findViewById(R.id.nextpage);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + (+1), true);
            }
        });


        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new OneFragment());
        // fragmentList.add(new BottomSheetFragment2());
        FragmentManager manager = ((AppCompatActivity) mContext).getSupportFragmentManager();
        MyPageAdapter pageAdapter = new MyPageAdapter(manager, fragmentList);
        viewPager.setAdapter(pageAdapter);
        bottomSheetDialog.show();

    }


//    private void sendRequest(int position, String strDate) {
//        Set<String> teamMember = teamModel.get(position).getTeamMember();
//        ArrayList<String> discordId = new ArrayList<>();
//        ArrayList<String> igid = new ArrayList<>();
//        ArrayList<String> ign = new ArrayList<>();
//        String error = "";
//        for (String s : teamMember) {
//            SharedPreferences sharedPreferences = mContext.getSharedPreferences(s, 0);
//            if (sharedPreferences.getString(AppConstant.discordId, "").equals("")) {
//                error = error + " Discord id not found for " + sharedPreferences.getString(AppConstant.userName, "") + "\n";
//            }
//            if (sharedPreferences.getString(title, "").equals("")) {
//                error = error + title + " Game id not found for " + sharedPreferences.getString(AppConstant.userName, "") + "\n";
//            }
//            if (sharedPreferences.getString(title + "_" + AppConstant.userName, "").equals("")) {
//                error = error + title + " Game name not found for " + sharedPreferences.getString(AppConstant.userName, "") + "\n";
//            }
//            discordId.add(sharedPreferences.getString(AppConstant.discordId, ""));
//            ign.add(sharedPreferences.getString(title, ""));
//            igid.add(sharedPreferences.getString(title + "_" + AppConstant.userName, ""));
//        }
//        if (!error.equals("")) {
//            showDialog(error);
//            return;
//        }
//        ProgressDialog dialog = new ProgressDialog(mContext);
//        dialog.setMessage("Registering for App, please wait.");
//        dialog.show();
//        RequestQueue queue = Volley.newRequestQueue(mContext);
//        String url = AppConstant.AppUrl + "register_matches.php";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        if (dialog.isShowing()) {
//                            dialog.dismiss();
//                        }
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            if (obj.getBoolean(AppConstant.success)) {
//                                Toast.makeText(mContext, obj.getString(AppConstant.message), Toast.LENGTH_LONG).show();
//                                SharedPreferences.Editor editShared = sharedPreferences.edit();
//                                editShared.putBoolean(strDate, true);
//                                editShared.apply();
//                                notifyDataSetChanged();
//                                bottomSheetDialog.cancel();
//                            } else {
//                                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
//                                alertDialog.setMessage(obj.getString(AppConstant.message));
//                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                                        new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.dismiss();
//                                            }
//                                        });
//                                alertDialog.show();
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("user", teamMember.toString().replace("[", "").replace("]", ""));
//                params.put("strDate", strDate);
//                params.put("teamName", teamModel.get(position).getTeamName());
//                params.put("teamUrl", teamModel.get(position).getTeamUrl());
//                params.put("discordId", discordId.toString().replace("[", "").replace("]", ""));
//                params.put("teamId", teamModel.get(position).getTeamId());
//                params.put("game_name", title);
//                params.put("ign", ign.toString().replace("[", "").replace("]", ""));
//                params.put("igid", igid.toString().replace("[", "").replace("]", ""));
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//
//        queue.add(stringRequest);
//    }

    private void showDialog(String message) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showBottomSheet(final int position) {
        final SharedPreferences shd = mContext.getSharedPreferences(AppConstant.saveYTid, 0);
        View myView = LayoutInflater.from(mContext).inflate(R.layout.my_view, null);
        final BottomSheetDialog dialog = new RoundedBottomSheetDialog(mContext);
        final EditText gameId = myView.findViewById(R.id.gameId);
        TextView textId = myView.findViewById(R.id.textId);
        textId.setText("Enter your " + title + " id");
//        gameId.setText(mySnapShort == null ? "" : mySnapShort.child(
//                AppConstant.pinfo).child(title).getValue() == null ? "" :
//                mySnapShort.child(AppConstant.pinfo).child(title).getValue() + "");
        myView.findViewById(R.id.books).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                appConstant.savebooking(endslot);
//                if (!gameId.getText().toString().equals("")) {
//                    FirebaseDatabase.getInstance().getReference(AppConstant.users)
//                            .child(userId).child(AppConstant.pinfo).child(title).setValue(gameId.getText().toString());
//                    if (allData.get(position).getRegisterd()) {
//                        mDatabase.child(channelId).child(date).child(title).child(allData.get(position).getTime()).child(AppConstant.member).child(userId).
//                                removeValue();
//                        allData.get(position).setRegisterd(false);
//                        allData.get(position).setTotalCount(allData.get(position).getTotalCount() - 1);
//                        shd.edit().putString(AppConstant.saveYTid, "").apply();
//                    } else {
//                        allData.get(position).setTotalCount(allData.get(position).getTotalCount() + 1);
//                        mDatabase.child(channelId).child(date).child(title).child(allData.get(position).getTime()).child(AppConstant.member).child(userId).
//                                setValue(miliSec);
//                        allData.get(position).setRegisterd(true);
//                        shd.edit().putString(AppConstant.saveYTid, allData.get(position).getStrDate()).apply();
//                    }
//                    notifyDataSetChanged();
//                    dialog.dismiss();
//                }
            }
        });
        dialog.setContentView(myView);
        dialog.show();
        ((View) myView.getParent()).setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        return gameItem.size();
    }

    class MyPageAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;


       /* public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {

            super(fm);

            this.fragments = fragments;

        }*/

        public MyPageAdapter(FragmentManager fragmentManager, List<Fragment> fragmentList) {
            super(fragmentManager);
            this.fragments = fragmentList;
        }

        @Override

        public Fragment getItem(int position) {

            return this.fragments.get(position);

        }


        @Override

        public int getCount() {

            return this.fragments.size();

        }

    }
}