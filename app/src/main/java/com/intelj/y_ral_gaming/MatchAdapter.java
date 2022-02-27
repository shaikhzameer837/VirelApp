package com.intelj.y_ral_gaming;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.intelj.y_ral_gaming.Utils.AppConstant;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MyViewHolder> {
    private List<GameItem> movieList;
    Context mContext;
    BottomSheetDialog bottomSheetDialog;
    int cost = 0;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView  pk, type, time, count, id, password;
        AppCompatButton join;
        ImageView info;
        LinearLayout idp;
        ProgressBar simpleProgressBar;
        public MyViewHolder(View view) {
            super(view);
            pk = view.findViewById(R.id.pk);
            type = view.findViewById(R.id.type);
            time = view.findViewById(R.id.time);
            join = view.findViewById(R.id.join);
            count = view.findViewById(R.id.count);
            id = view.findViewById(R.id.id);
            info = view.findViewById(R.id.info);
            password = view.findViewById(R.id.password);
            idp = view.findViewById(R.id.idp);
            simpleProgressBar = view.findViewById(R.id.simpleProgressBar);
//            imgs =  view.findViewById(R.id.imgs);
        }
    }


    public MatchAdapter(Context mContext, List<GameItem> moviesList) {
        this.movieList = moviesList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.pk.setText("Per Kill \u20B9" + movieList.get(position).getPerKill());
        switch (Integer.parseInt(movieList.get(position).getType())) {
            case 1:
                holder.type.setText("Solo");
                break;
            case 2:
                holder.type.setText("Duo");
                break;
            case 4:
                holder.type.setText("Squad");
                break;
        }
       // holder.time.setText("Time \n " + movieList.get(position).getTime());
        holder.time.setText(movieList.get(position).getTime());
        holder.count.setText(movieList.get(position).getCount() + "/100 players");
        //holder.title.setText(movieList.get(position).getTitle());
       // holder.id.setText("Bgmi id \n " + movieList.get(position).getId());
        holder.id.setText(movieList.get(position).getId());
        holder.password.setText( movieList.get(position).getPassword());
       // holder.password.setText("Bgmi pass \n " + movieList.get(position).getPassword());
        Log.e("posT", movieList.get(position).getCount());
        holder.join.setVisibility(movieList.get(position).getPrizePool().equals("1") ? View.VISIBLE : View.GONE);
        holder.idp.setVisibility(movieList.get(position).getIsexist().equals("1") ? View.VISIBLE : View.GONE);
        holder.simpleProgressBar.setProgress(Integer.parseInt(movieList.get(position).getCount()));
        holder.join.setText(movieList.get(position).getIsexist().equals("0") ? "Join now" : "Aready Joined");
        holder.join.setBackgroundColor(movieList.get(position).getIsexist().equals("0") ? Color.parseColor("#000000") : Color.parseColor("#dddddd"));
        holder.join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new AppConstant(mContext).checkLogin()) {
                    Intent intent = new Intent("custom-event-name");
                    intent.putExtra(AppConstant.AppName, true);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return;
                }
                    showBottomSheetDialog(position);
            }
        });
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().gameItem = movieList.get(position);
                Intent intent = new Intent(mContext, ResultActivity.class);
                mContext.startActivity(intent);
            }
        });
//        Glide.with(mContext).load(movieList.get(position))
//                .apply(new RequestOptions()).into(holder.imgs);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    private void showBottomSheetDialog(int position) {
        bottomSheetDialog = new BottomSheetDialog(mContext);
        bottomSheetDialog.setContentView(R.layout.register_match);
        TextView textView = bottomSheetDialog.findViewById(R.id.integer_number);
        TextView infos = bottomSheetDialog.findViewById(R.id.infos);
        Button btn_next = bottomSheetDialog.findViewById(R.id.btn_next);
        LinearLayout lin = bottomSheetDialog.findViewById(R.id.lin);
        Button add_money = bottomSheetDialog.findViewById(R.id.add_money);
        setViews(infos, btn_next, position, textView, add_money);
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
                            obj1.put(i == 0 ? new AppConstant(mContext).getId() : new AppConstant(mContext).randomString(5) + "", obj);
                        }
                    }
                    Log.e("jsoobject",obj1.toString());
                    Log.e("jsoobject",new AppConstant(mContext).getId());
                     saveUserInfo(position, obj1.toString(), textView.getText().toString());
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
                setViews(infos, btn_next, position, textView, add_money);
            }
        });
        bottomSheetDialog.findViewById(R.id.increase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(textView.getText().toString()) == Integer.parseInt(movieList.get(position).getType())) {
                    return;
                }
                textView.setText((Integer.parseInt(textView.getText().toString()) + 1) + "");
                EditText et = new EditText(mContext);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                et.setLayoutParams(p);
                et.setSingleLine(true);
                et.setTextSize(14);
                et.setHint("Enter your player " + textView.getText().toString() + " BGMI ingame name");
                et.setId(Integer.parseInt(textView.getText().toString()));
                lin.addView(et);
                setViews(infos, btn_next, position, textView, add_money);
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

    private void setViews(TextView infos, Button btn_next, int position, TextView textView, Button add_money) {
        cost = Integer.parseInt(textView.getText().toString()) * Integer.parseInt(movieList.get(position).getEntryFees());
        if (cost > AppController.getInstance().amount) {
            btn_next.setVisibility(View.GONE);
            add_money.setText("ADD Money (" + (cost - AppController.getInstance().amount) + ")");
        } else {
            btn_next.setVisibility(View.VISIBLE);
            btn_next.setText("NEXT (" + (cost) + ")");
            add_money.setText("ADD Money");
        }
        switch (Integer.parseInt(textView.getText().toString())) {
            case 1:
                infos.setText("Play as Solo");
                break;
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
    }

    private void saveUserInfo(int position, String userJson, String totalPlayer) {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("loading...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(mContext);
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
                                AppController.getInstance().amount = AppController.getInstance().amount - json.getInt("entryFees");
                                Intent intent = new Intent("custom-event-name");
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                                movieList.get(position).setIsexist(movieList.get(position).getIsexist().equals("1") ? "0" : "1");
                                movieList.get(position).setCount(Integer.parseInt(movieList.get(position).getCount()) + totalPlayer);
                                notifyDataSetChanged();
                                bottomSheetDialog.dismiss();
                            }
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
                params.put("entry", movieList.get(position).getEntryFees());
                params.put("userId", new AppConstant(mContext).getId());
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = df.format(c);
                params.put("date", formattedDate);
                Log.e("formattedDate", formattedDate);
                params.put("time", movieList.get(position).getTime());
                params.put("userJson", userJson);
                params.put("count", totalPlayer);
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
