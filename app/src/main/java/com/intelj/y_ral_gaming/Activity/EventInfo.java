package com.intelj.y_ral_gaming.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.intelj.y_ral_gaming.Adapter.TeamDisplayList;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.Fragment.RuleFragment;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.RoundedBottomSheetDialog;
import com.intelj.y_ral_gaming.SigninActivity;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;
import com.intelj.y_ral_gaming.model.MyListData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EventInfo extends AppCompatActivity {
    ImageView iv_cover_pic;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView tv_title, tv_date;
    TextView join;
    ArrayList<EditText> editTextList = new ArrayList<>();
    ArrayList<TextInputLayout> textInputLayouts = new ArrayList<>();
    AppConstant appConstant;
    int teamCount = 0;
    private static final int REQUEST = 112;
    ImageView img;
    boolean isRegistered = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.event_info);

        iv_cover_pic = findViewById(R.id.cover_pic);
        img = findViewById(R.id.img);
        Glide.with(this).load("https://media.discordapp.net/attachments/1024724326957715567/1057137645110698004/bgmi.png?width=564&height=663")
                .apply(new RequestOptions().circleCrop())
                .placeholder(R.drawable.placeholder).into(img);
        appConstant = new AppConstant(this);
        Fade fade = new Fade();
//        appConstant = new AppConstant(this);
//        userid = getIntent().getStringExtra("userid");
        View decor = getWindow().getDecorView();
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tv_title = findViewById(R.id.title);
        tv_date = findViewById(R.id.date);
        tv_title.setText(getIntent().getStringExtra("teamName"));
        tv_date.setText(getIntent().getStringExtra("gameName"));
        isRegistered = getIntent().getStringExtra("isRegistered").equals("1");

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("joined_event"));

        join = findViewById(R.id.join);
        viewPager.setOffscreenPageLimit(0);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        Glide.with(this).load(getIntent().getStringExtra("eId")).placeholder(R.drawable.placeholder).into(iv_cover_pic);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        if (!appConstant.checkLogin()) {
            setButton(" Login ", R.drawable.curved_white, Color.RED);
        } else if (getIntent().getStringExtra("status").equals("0"))
            setButton(" Closed ", R.drawable.curved_white, Color.BLACK);
        else
            setButton(!isRegistered ? " Join " : " Already joined ", !isRegistered ? R.drawable.curved_red : R.drawable.curved_white, !isRegistered ? Color.WHITE : Color.RED);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!appConstant.checkLogin()) {
                    startActivity(new Intent(EventInfo.this, SigninActivity.class));
                    return;
                }
                if (join.getText().toString().trim().equals("Join") && !getIntent().getStringExtra("status").equals("0")) {
                    showTeamList();
                }
            }
        });
        getEventDetails();
    }

    SharedPreferences shd;
    LinearLayout lin;
    BottomSheetDialog bottomCreateTeam;

    ArrayList<MyListData> myListData = new ArrayList<>();
    TeamDisplayList teamAdapter;

    public void showTeamList() {
        //  startActivity(new Intent(ProFileActivity.this,CreateTeam.class));
        View inflated = getLayoutInflater().inflate(R.layout.team_list, null);
        bottomCreateTeam = new RoundedBottomSheetDialog(EventInfo.this);
        RecyclerView teamRecyclerView = inflated.findViewById(R.id.rv_teamlist);
        inflated.findViewById(R.id.createTeam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventInfo.this, CreateTeam.class));
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        teamRecyclerView.setLayoutManager(mLayoutManager);
        teamRecyclerView.setItemAnimator(new DefaultItemAnimator());
        teamAdapter = new TeamDisplayList(myListData);
        teamRecyclerView.setAdapter(teamAdapter);
        ShimmerFrameLayout shimmerFrameLayout = inflated.findViewById(R.id.shimmer_layout);
        shimmerFrameLayout.startShimmer();

        teamRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), teamRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                addTeamLists(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        bottomCreateTeam.setContentView(inflated);
        bottomCreateTeam.show();
    }

    private void getEventDetails() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "events/get_events_tab.php?u=" + new AppConstant(this).getId()+"&&id="+ getIntent().getStringExtra("eId");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("tokenResponse", response);
                try {
                    JSONObject json = new JSONObject(response);
                    JSONObject tab = json.getJSONObject("tab");
                    Iterator<String> keys = tab.keys();
                    AppController.getInstance().tab.clear();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        String value = tab.getString(key);
                        System.out.println(key + ": " + value);
                        AppController.getInstance().tab.put(key,value);
                        tabLayout.addTab(tabLayout.newTab().setText(key));
                    }
                    MyEventAdapter adapter = new MyEventAdapter(EventInfo.this, getSupportFragmentManager(), tabLayout.getTabCount());
                    viewPager.setAdapter(adapter);

                } catch (Exception e) {
                    Log.e("error Rec", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
/*                shimmer_container.hideShimmer();
                shimmer_container.setVisibility(View.GONE);*/
                error.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
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

    private void addTeamLists(int position) {
        editTextList.clear();
        View view = getLayoutInflater().inflate(R.layout.add_team_info, null);
        contactModel = new ArrayList<>();
        final BottomSheetDialog dialogBottom = new RoundedBottomSheetDialog(EventInfo.this);
        lin = view.findViewById(R.id.lin);
        shd = getSharedPreferences(AppConstant.id, MODE_PRIVATE);
        appConstant = new AppConstant(this);
        String[] userPlayer = myListData.get(position).getPlaying_status().split(",");
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(userPlayer));
        arrayList.remove(new AppConstant(this).getId());
        addEditText(new AppConstant(this).getId());
        for (int x = 0; x < arrayList.size(); x++) {
            addEditText(arrayList.get(x));
        }
        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextList.size() < 1) {
                    Toast.makeText(EventInfo.this, "Minimum 1 players are required", Toast.LENGTH_LONG).show();
                    return;
                }
                for (int x = 0; x < editTextList.size(); x++) {
                    if (editTextList.get(x).getText().toString().trim().equals("")) {
                        editTextList.get(x).setError("Player Name cannot be empty");
                        editTextList.get(x).requestFocus();
                        return;
                    }
                }
                joinEvent(position);
                bottomCreateTeam.cancel();
                dialogBottom.cancel();
            }
        });
        dialogBottom.setContentView(view);
        dialogBottom.show();
    }

    public void addEditText(String userId) {
        EditText editText = new EditText(EventInfo.this);
        editText.setTextSize(12);
        editText.setSingleLine(true);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        editText.setTag(userId);
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        TextInputLayout textInputLayout = new TextInputLayout(EventInfo.this, null, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        textInputLayout.setBoxCornerRadii(5, 5, 5, 5);
        LinearLayout.LayoutParams textInputLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textInputLayout.setLayoutParams(textInputLayoutParams);
        textInputLayout.addView(editText, editTextParams);
        textInputLayout.setHint(userId.equals(new AppConstant(this).getId()) ? "Enter your in-game name (IGL)" : "Enter player " + editTextList.size() + "'s in-game name");
        editTextList.add(editText);
        lin.addView(textInputLayout);
        textInputLayouts.add(textInputLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "get_team_list.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("responseT", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                myListData.clear();
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("teamList"));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
                                    myListData.add(new MyListData(jsonObject2.getString("teamName"), jsonObject2.getString("teamId"), jsonObject2.getString("teamMember")));
                                }
                                if (teamAdapter != null){
                                    teamAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(EventInfo.this, "No Team Found", Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            Log.e("error Rec", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                Log.e("teamIdList",AppController.getInstance().teamList);
                params.put("teamIdList", AppController.getInstance().teamList);
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean result = false;
            join.setVisibility(View.VISIBLE);
            if (!appConstant.checkLogin()) {
                setButton(" Login ", R.drawable.curved_white, Color.RED);
            } else {
                result = intent.getBooleanExtra("message", false);
                teamCount = intent.getIntExtra("count", 0);
                if (getIntent().getIntExtra("max", 0) == teamCount && !result)
                    setButton(" Closed ", R.drawable.curved_white, Color.BLACK);
                else
                    setButton(!result ? " Join " : " Already joined ", !result ? R.drawable.curved_red : R.drawable.curved_white, !result ? Color.WHITE : Color.RED);
            }
            if (!result) {
                join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!appConstant.checkLogin()) {
                            startActivity(new Intent(EventInfo.this, SigninActivity.class));
                            return;
                        }
                        if (getIntent().getIntExtra("max", 0) != teamCount) {
                            showTeamList();
                        }
                    }
                });
            }
        }
        //TabLayout.Tab tab = tabLayout.getTabAt(1).setText("Teams (10)");
    };

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new readContactTask().execute();
                } else {
                    Toast.makeText(EventInfo.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    ArrayList<ContactListModel> contactModel = new ArrayList<>();
    HashMap<String, String> contactArrayList = new HashMap<>();
    ContactListAdapter contactListAdapter;

    class readContactTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();
        ProgressDialog pd = new ProgressDialog(EventInfo.this);

        protected void onPreExecute() {
            super.onPreExecute();
            contactModel.clear();
            pd.show();
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(Void... arg0) {
            readContacts();

            return "You are at PostExecute";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.cancel();
            loadChats();
        }
    }

    private void loadChats() {
        Set<String> set = shd.getStringSet(AppConstant.contact, null);
        try {
            if (set != null) {
                for (String s : set) {
                    SharedPreferences userInfo = getSharedPreferences(s, Context.MODE_PRIVATE);
                    contactModel.add(new ContactListModel(s,
                            AppConstant.AppUrl + "images/" + userInfo.getString(AppConstant.id, "") + ".png?u=" + AppConstant.imageExt(),
                            appConstant.getContactName(s),
                            userInfo.getString(AppConstant.id, ""),
                            userInfo.getString(AppConstant.bio, "")));
                }
                Collections.sort(contactModel, new Comparator<ContactListModel>() {
                    @Override
                    public int compare(final ContactListModel object1, final ContactListModel object2) {
                        Log.e("Collections", object1.getName() + " " + object2.getName());
                        return object1.getName().compareTo(object2.getName());
                    }
                });
                contactListAdapter.notifyDataSetChanged();
            } else
                new readContactTask().execute();
        } catch (Exception e) {
            new readContactTask().execute();
        }
    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void readContacts() {
        contactArrayList.clear();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "");
                        if (phoneNo.length() > 8) {
                            String original = phoneNo;
                            if (phoneNo.startsWith("0"))
                                phoneNo = phoneNo.substring(1);
                            if (!phoneNo.startsWith("+"))
                                phoneNo = appConstant.getCountryCode() + phoneNo;
                            Log.i("Phone Number: ", appConstant.getCountryCode());
                            contactArrayList.put(phoneNo, original);
                        }

                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
    }

    private void setButton(String btnName, int drawables, int color) {
        join.setText(btnName);
        join.setBackgroundResource(drawables);
        join.setTextColor(color);
    }

    private void joinEvent(int position) {
        JSONObject jsonRootObject = new JSONObject();
        JSONObject jsonRootObject2 = new JSONObject();
        JSONObject jsonRootObject3 = new JSONObject();
        try {
            for (int x = 0; x < editTextList.size(); x++) {
                JSONObject jsonRootObject4 = new JSONObject();
                jsonRootObject4.put("ingName", editTextList.get(x).getText().toString());
                jsonRootObject3.put(editTextList.get(x).getTag().toString(), jsonRootObject4);
            }
            byte[] data = android.util.Base64.encode(myListData.get(position).getName().getBytes(), android.util.Base64.DEFAULT);
            String text = new String(data, StandardCharsets.UTF_8);
            System.out.println("Encoded String: " + text);
            jsonRootObject2.put("teams", jsonRootObject3);
            jsonRootObject2.put("name", myListData.get(position).getName());
            jsonRootObject2.put("teamId", myListData.get(position).getUserId());
            jsonRootObject.put(myListData.get(position).getUserId(), jsonRootObject2);
            Log.e("jsonRootObject", jsonRootObject.toString());
            Log.e("jsonRootObject", jsonRootObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = AppConstant.AppUrl + "join_event.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("lcat_response", url + "\n" + response);
                        if (response.equals("success")) {
                            SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putBoolean(AppConstant.pinfo + getIntent().getStringExtra("eId"), true);
                            myEdit.apply();
                            Intent intent = new Intent("refreshWeb");
                            LocalBroadcastManager.getInstance(EventInfo.this).sendBroadcast(intent);
                            isRegistered = true;
                            setButton("Already joined", R.drawable.curved_white, Color.RED);
                            Toast.makeText(EventInfo.this, "Registration done successfully", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(EventInfo.this, response, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
/*                shimmer_container.hideShimmer();
                shimmer_container.setVisibility(View.GONE);*/
                error.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userJson", jsonRootObject.toString());
                params.put("tid", getIntent().getStringExtra("eId"));
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

    public class MyEventAdapter extends FragmentPagerAdapter {

        private Context myContext;
        int totalTabs;

        public MyEventAdapter(Context context, FragmentManager fm, int totalTabs) {
            super(fm);
            myContext = context;
            this.totalTabs = totalTabs;
        }

        // this is for fragment tabs
        @Override
        public Fragment getItem(int position) {
            String url = AppController.getInstance().tab.get(tabLayout.getTabAt(position).getText().toString());
            Log.e("getItemUrl: ", url);
            return new RuleFragment(url);
        }

        // this counts total number of tabs
        @Override
        public int getCount() {
            return totalTabs;
        }
    }
}
