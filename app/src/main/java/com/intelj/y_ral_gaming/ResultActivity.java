package com.intelj.y_ral_gaming;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    ImageView result,yt;
    TextView title, pk, entry, type, map, time, count, id, password;
    AppCompatButton join;
    LinearLayout idp;
    ProgressBar simpleProgressBar;
    Context mContext;
    BottomSheetDialog bottomSheetDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        result = findViewById(R.id.result);
        mContext = this;
        if (AppController.getInstance().gameItem.getResult_url() != null) {
            Glide.with(this)
                    .load(AppController.getInstance().gameItem.getResult_url())
                    .placeholder(0)
                    .into(result);
        }
        title = findViewById(R.id.title);
        pk = findViewById(R.id.pk);
        yt = findViewById(R.id.yt);
        entry = findViewById(R.id.entry);
        type = findViewById(R.id.type);
        map = findViewById(R.id.map);
        time = findViewById(R.id.time);
        join = findViewById(R.id.join);
        count = findViewById(R.id.count);
        id = findViewById(R.id.id);
        password = findViewById(R.id.password);
        idp = findViewById(R.id.idp);
        simpleProgressBar = findViewById(R.id.simpleProgressBar);
        join.setText(AppController.getInstance().gameItem.getIsexist().equals("0") ? "Join now" : "Aready Joined");
        entry.setText("Entry Fee \n \u20B9" + AppController.getInstance().gameItem.getEntryFees());
        pk.setText("Per Kill \n \u20B9" + AppController.getInstance().gameItem.getPerKill());
        switch (Integer.parseInt(AppController.getInstance().gameItem.getType())) {
            case 1:
                type.setText("Type \n Solo");
                break;
            case 2:
                type.setText("Type \n Duo");
                break;
            case 4:
                type.setText("Type \n Squad");
                break;
        }
        Glide.with(mContext).load(AppController.getInstance().gameItem.getYt_url().equals("") ? AppConstant.defaultImg  : "https://i.ytimg.com/vi/"+AppController.getInstance().gameItem.getYt_url()+"/hqdefault_live.jpg").placeholder(R.mipmap.app_logo).into(yt);
        yt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppController.getInstance().gameItem.getYt_url().equals("")){
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/c/YRALGaming"));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/c/YRALGaming"));
                    try {
                        mContext.startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        mContext.startActivity(webIntent);
                    }
                }else{
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + AppController.getInstance().gameItem.getYt_url()));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + AppController.getInstance().gameItem.getYt_url()));
                    try {
                        mContext.startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        mContext.startActivity(webIntent);
                    }
                }
            }
        });
        map.setText("Map \n " + AppController.getInstance().gameItem.getMap());
        time.setText("Time \n " + AppController.getInstance().gameItem.getTime());
        count.setText(AppController.getInstance().gameItem.getCount() + "/100 players");
        title.setText(getIntent().getStringExtra("title"));
        id.setText("Bgmi id \n " + AppController.getInstance().gameItem.getId());
        password.setText("Bgmi pass \n Password on Youtube Chat");
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.getInstance().gameItem.getIsexist().equals("0"))
                    showBottomSheetDialog();
            }
        });
        Log.e("posT", AppController.getInstance().gameItem.getCount());
        //idp.setVisibility(AppController.getInstance().gameItem.getIsexist().equals("1") ? VISIBLE : GONE);
        simpleProgressBar.setProgress(Integer.parseInt(AppController.getInstance().gameItem.getCount()));
        findViewById(R.id.yt_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppController.getInstance().gameItem.getYt_url().equals(""))
                    return;
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + AppController.getInstance().gameItem.getYt_url()));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });
    }

    private void showBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.register_match);
        TextView textView = bottomSheetDialog.findViewById(R.id.integer_number);
        TextView infos = bottomSheetDialog.findViewById(R.id.infos);
        AppCompatButton btn_next = bottomSheetDialog.findViewById(R.id.btn_next);
        LinearLayout lin = bottomSheetDialog.findViewById(R.id.lin);
        EditText gamename = bottomSheetDialog.findViewById(R.id.ingameName);
        gamename.setHint("Enter your " + title.getText().toString() + " player 1 in game name");
        AppCompatButton add_money = bottomSheetDialog.findViewById(R.id.add_money);
        setViews(infos, textView, add_money);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj1 = new JSONObject();
                    final int childCount = lin.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        EditText editText = (EditText) lin.getChildAt(i);
                        if (editText.getText().toString().trim().equals("")) {
                            editText.requestFocus();
                            editText.setError("This cannot be empty");
                            return;
                        } else {
                            JSONObject obj = new JSONObject();
                            obj.put("ingName", editText.getText().toString());
                            obj.put("count", i == 0 ? textView.getText().toString() : 0);
                            obj.put("kill",  0);
                            obj1.put(i == 0 ? new AppConstant(mContext).getId() : new AppConstant(mContext).randomString(5) + "", obj);
                        }
                    }
                    Log.e("jsoobject", obj1.toString());
                    Log.e("jsoobject", new AppConstant(mContext).getId());
                    saveUserInfo(obj1.toString(), textView.getText().toString(), childCount);
                } catch (Exception e) {

                }
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
                setViews(infos, textView, add_money);
            }
        });
        bottomSheetDialog.findViewById(R.id.increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(textView.getText().toString()) == Integer.parseInt(AppController.getInstance().gameItem.getType())) {
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
                setViews(infos, textView, add_money);
            }
        });
        bottomSheetDialog.findViewById(R.id.add_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppConstant(mContext).addMoney(mContext);
            }
        });
        bottomSheetDialog.show();
    }

    int cost = 0;

    private void setViews(TextView infos, TextView textView, Button add_money) {
        cost = Integer.parseInt(textView.getText().toString()) * Integer.parseInt(AppController.getInstance().gameItem.getEntryFees());
//        if (cost > amount) {
//            btn_next.setVisibility(View.GONE);
//          //  add_money.setText("ADD Money (" + (cost - amount) + ")");
//        } else {
//            btn_next.setVisibility(View.VISIBLE);
        //  btn_next.setText("NEXT");
        //  add_money.setText("ADD Money");
        // }
        if (Integer.parseInt(AppController.getInstance().gameItem.getType()) != 1) {
            bottomSheetDialog.findViewById(R.id.rel_increment).setVisibility(View.VISIBLE);
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

    private void saveUserInfo(String userJson, String totalPlayer, int childCount) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("loading...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://y-ral-gaming.com/admin/api/join_game.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("onClick3", response);
                        progressDialog.cancel();
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getBoolean("success")) {
                                int player_count = json.getInt("player_count");
                                AppController.getInstance().amount = AppController.getInstance().amount - json.getInt("entryFees");
                                Intent intent = new Intent("custom-event-name");
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                                AppController.getInstance().gameItem.setIsexist(AppController.getInstance().gameItem.getIsexist().equals("1") ? "0" : "1");
                                AppController.getInstance().gameItem.setCount(player_count + "");
                                join.setText(AppController.getInstance().gameItem.getIsexist().equals("0") ? "Join now" : "Aready Joined");
                                bottomSheetDialog.dismiss();
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
                params.put("entry", AppController.getInstance().gameItem.getEntryFees());
                params.put("userId", new AppConstant(mContext).getId());
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = df.format(c);
                params.put("date", formattedDate);
                Log.e("formattedDate", formattedDate);
                params.put("time", AppController.getInstance().gameItem.getTime());
                params.put("userJson", userJson);
                params.put("count", totalPlayer);
                params.put("entryFees", "entryFees = 0");
                params.put("game_type", title.getText().toString());
                params.put("player_count", childCount + "");
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
}
