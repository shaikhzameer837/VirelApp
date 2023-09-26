package com.intelj.y_ral_gaming.Adapter;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.intelj.y_ral_gaming.Activity.GameInfo;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.Fragment.OneFragment;
import com.intelj.y_ral_gaming.GameItem;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.RoundedBottomSheetDialog;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.bottomSheet.BrowserBottomSheet;

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
        public TextView title, info, type, count, prizepool,pp,gid,gpassword;
        ImageView imgs,poll;
        LinearLayout passLin;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            count = view.findViewById(R.id.count);
            prizepool = view.findViewById(R.id.prizepool);
            pp = view.findViewById(R.id.pp);
            imgs = view.findViewById(R.id.imgs);
            info = view.findViewById(R.id.info);
            poll = view.findViewById(R.id.poll);
            type = view.findViewById(R.id.type);
            gid = view.findViewById(R.id.gid);
            passLin = view.findViewById(R.id.passLin);
            gpassword = view.findViewById(R.id.password);
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
        Glide.with(mContext).load(gameItem.get(position).getYt_url().equals("") ? R.drawable.placeholder : "https://i.ytimg.com/vi/" + gameItem.get(position).getYt_url() + "/hqdefault_live.jpg").placeholder(R.mipmap.ic_launcher).into(holder.imgs);
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
        holder.poll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrowserBottomSheet myBottomSheetDialogFragment = new BrowserBottomSheet(gameItem.get(position).getGameId());
                myBottomSheetDialogFragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "");
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
        holder.gid.setText("id: " + gameItem.get(position).getInGameid());
        holder.gpassword.setText("pass: "+gameItem.get(position).getGamePassword());
        holder.info.setTextColor(gameItem.get(position).getIsexist().equals("0") ? Color.parseColor("#7e241c") : Color.parseColor("#097969"));
        holder.info.setTag(position);
        holder.gpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Password Copied",Toast.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label",gameItem.get(position).getGamePassword());
                clipboard.setPrimaryClip(clip);
            }
        });
        holder.gid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Id Copied",Toast.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label",gameItem.get(position).getGameId());
                clipboard.setPrimaryClip(clip);
            }
        });

        if(!gameItem.get(position).getInGameid().equals("")){
           holder.passLin.setVisibility(View.VISIBLE);
        }else{
            holder.passLin.setVisibility(View.GONE);
        }
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
                                FirebaseMessaging.getInstance().subscribeToTopic("give_away_"+ gameItem.get(position).getGameId())
                                        .addOnCompleteListener(task -> {
                                            String msg = "Subscribed";
                                            if (!task.isSuccessful()) {
                                                msg = "Subscribe failed";
                                            }
                                            Log.d("FCM", msg);
                                            // You can also show a toast or UI element to indicate success or failure
                                        });
                                appConstant.savePackage(appConstant.getId(),"");
                                int player_count = json.getInt("player_count");
                                AppController.getInstance().amount = AppController.getInstance().amount - json.getInt("entryFees");
                                Intent intent = new Intent("custom-event-name");
                                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                                gameItem.get(position).setIsexist(gameItem.get(position).getIsexist().equals("1") ? "0" : "1");
                                gameItem.get(position).setCount(player_count + "");
                                notifyDataSetChanged();
                                bottomSheetDialog.dismiss();
                                GameInfo BottomSheetFragment = new GameInfo(gameItem.get(position).getGameId());
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



    public int getItemCount() {
        return gameItem.size();
    }

}