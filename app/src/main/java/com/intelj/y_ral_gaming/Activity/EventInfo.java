package com.intelj.y_ral_gaming.Activity;

import android.app.Activity;
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
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.ChatActivity;
import com.intelj.y_ral_gaming.ContactListModel;
import com.intelj.y_ral_gaming.Fragment.OneFragment;
import com.intelj.y_ral_gaming.Fragment.PrizePoolFragment;
import com.intelj.y_ral_gaming.Fragment.RuleFragment;
import com.intelj.y_ral_gaming.Fragment.TeamFragment;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.SigninActivity;
import com.intelj.y_ral_gaming.TournamentAdapter;
import com.intelj.y_ral_gaming.Utils.AppConstant;
import com.intelj.y_ral_gaming.Utils.RecyclerTouchListener;
import com.intelj.y_ral_gaming.model.TournamentModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventInfo extends AppCompatActivity {
    ImageView iv_cover_pic;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView tv_title, tv_date;
    TextView join;
    EditText teamName;
    ArrayList<EditText> editTextList = new ArrayList<>();
    ArrayList<TextInputLayout> textInputLayouts = new ArrayList<>();
    AppConstant appConstant;
    int teamCount = 0;
    private static final int REQUEST = 112;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_info);
        iv_cover_pic = findViewById(R.id.cover_pic);
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
        tv_title.setText(AppController.getInstance().tournamentModel.getGame_name());
        tv_date.setText(AppController.getInstance().tournamentModel.getDate());
        tabLayout.addTab(tabLayout.newTab().setText("Rules"));
        tabLayout.addTab(tabLayout.newTab().setText("Team"));
        tabLayout.addTab(tabLayout.newTab().setText("Prize Pool"));
        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("joined_event"));
        join = findViewById(R.id.join);
        MyEventAdapter adapter = new MyEventAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
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

        Glide.with(this).load(AppController.getInstance().tournamentModel.getImage_url()).placeholder(R.drawable.placeholder).into(iv_cover_pic);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    RecyclerView rv_contact;
    SharedPreferences shd;
    LinearLayout lin;

    private void addTeamList() {
        editTextList.clear();
        contactModel = new ArrayList<>();
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(EventInfo.this);
        bottomSheetDialog.setContentView(R.layout.add_team_info);
        lin = bottomSheetDialog.findViewById(R.id.lin);
        bottomSheetDialog.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new readContactTask().execute();
            }
        });
        teamName = bottomSheetDialog.findViewById(R.id.teamName);
        shd = getSharedPreferences(AppConstant.id, MODE_PRIVATE);
        rv_contact = bottomSheetDialog.findViewById(R.id.rv_contact);
        appConstant = new AppConstant(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(EventInfo.this);
        rv_contact.setLayoutManager(mLayoutManager);
        contactModel = new ArrayList<>();
        contactListAdapter = new ContactListAdapter(EventInfo.this, contactModel);
        rv_contact.setAdapter(contactListAdapter);
        contactListAdapter.checkVisible();
        rv_contact.addOnItemTouchListener(new RecyclerTouchListener(EventInfo.this, rv_contact, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                int slectedContact = contactListAdapter.setChecBox(editTextList.size() == 6 ? -1 : position);
                if (slectedContact != -1) {
                    addEditText(contactModel.get(position).getName(), contactModel.get(position).getUserid());
                } else
                    Toast.makeText(EventInfo.this, "Max 6 players can register", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        bottomSheetDialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (teamName.getText().toString().trim().equals("")) {
                    teamName.setError("Enter your team name");
                    teamName.requestFocus();
                    return;
                }
                if (editTextList.size() < 4) {
                    Toast.makeText(EventInfo.this, "Minimum 4 players are required", Toast.LENGTH_LONG).show();
                    return;
                }
                for (int x = 0; x < editTextList.size(); x++) {
                    if (editTextList.get(x).getText().toString().trim().equals("")) {
                        editTextList.get(x).setError("Player Name cannot be empty");
                        editTextList.get(x).requestFocus();
                        return;
                    }
                }
                joinEvent();
                bottomSheetDialog.cancel();
            }
        });
        addEditText("", new AppConstant(EventInfo.this).getId());
        bottomSheetDialog.show();

    }

    public void addEditText(String userName, String userId) {
        for (int i = 0; i < editTextList.size(); i++) {
            if (editTextList.get(i).getTag().equals(userId)) {
                lin.removeView(textInputLayouts.get(i));
                editTextList.remove(editTextList.get(i));
                return;
            }
        }
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
        textInputLayout.setHint(editTextList.size() == 0 ? "Enter your ingame name" : "Enter " + userName + "'s ingame name");
        editTextList.add(editText);
        lin.addView(textInputLayout);
        textInputLayouts.add(textInputLayout);
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
                if (AppController.getInstance().tournamentModel.getMax() == teamCount && !result)
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
                        if (AppController.getInstance().tournamentModel.getMax() != teamCount) {
                            addTeamList();
                            if (Build.VERSION.SDK_INT >= 23) {
                                String[] PERMISSIONS = {android.Manifest.permission.READ_CONTACTS};
                                if (!hasPermissions(EventInfo.this, PERMISSIONS)) {
                                    ActivityCompat.requestPermissions(EventInfo.this, PERMISSIONS, REQUEST);
                                } else {
                                    new readContactTask().execute();
                                }
                            } else {
                                new readContactTask().execute();
                            }
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
    ArrayList<ContactListModel> contactModel;
    HashMap<String, String> contactArrayList = new HashMap<>();
    HashSet<String> originalContact = new HashSet<>();
    ContactListAdapter contactListAdapter;
    int returnPhone = 0;

    class readContactTask extends AsyncTask<Void, Integer, String> {
        String TAG = getClass().getSimpleName();
        ProgressDialog pd = new ProgressDialog(EventInfo.this);

        protected void onPreExecute() {
            super.onPreExecute();
            contactModel.clear();
            pd.setMessage("loading");
            pd.show();
            Log.d(TAG + " PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(Void... arg0) {
            Set<String> set = shd.getStringSet(AppConstant.contact, null);
            if (set == null) {
                readContacts();
                returnPhone = 0;
                originalContact.clear();
                Log.e("contactArrayList", contactArrayList.size() + "");
                for (String phoneNo : contactArrayList.keySet()) {
                    serverContact(phoneNo, contactArrayList.get(phoneNo));
                }
            }
            set = shd.getStringSet(AppConstant.contact, null);
            for (String s : set) {
                SharedPreferences userInfo = getSharedPreferences(s, Context.MODE_PRIVATE);
                contactModel.add(new ContactListModel(s,userInfo.getString(AppConstant.myPicUrl, ""), appConstant.getContactName(userInfo.getString(AppConstant.phoneNumber, "")), userInfo.getString(AppConstant.id, ""), userInfo.getString(AppConstant.bio, "")));
            }
            Log.e("contactArrayList", "taskComplete");
            return "You are at PostExecute";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.cancel();
            contactListAdapter.notifyDataSetChanged();
        }
    }

    public void serverContact(String number, String original) {
        Log.e("userNum", number);
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child("users").orderByChild("phoneNumber").equalTo(number);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                returnPhone++;
                if (dataSnapshot != null) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.e("contactArrayList//", original);
                        Log.e("contactArrayList//---", postSnapshot.getKey());
                        originalContact.add(postSnapshot.getKey());
                        appConstant.saveUserInfo(original, postSnapshot.getKey(), AppConstant.AppUrl + "images/" + postSnapshot.getKey() + ".png?u=" + AppConstant.imageExt(), null, "", postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue() != null ? postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue().toString() : null, postSnapshot.child(AppConstant.userName).getValue() != null ? postSnapshot.child(AppConstant.userName).getValue().toString() : System.currentTimeMillis() + "");
                        contactModel.add(new ContactListModel(original,AppConstant.AppUrl + "images/" + postSnapshot.getKey() + ".png?u=" + AppConstant.imageExt(), appConstant.getContactName(postSnapshot.child(AppConstant.phoneNumber).getValue(String.class)), postSnapshot.getKey(), postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue() != null ? postSnapshot.child(AppConstant.pinfo).child(AppConstant.bio).getValue().toString() : ""));
                    }
                }
                if (contactArrayList.size() == returnPhone) {
                    SharedPreferences.Editor setEditor = shd.edit();
                    setEditor.putStringSet(AppConstant.contact, originalContact);
                    setEditor.apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    private void joinEvent() {
        String key = "@" + (System.currentTimeMillis() / 1000);
        JSONObject jsonRootObject = new JSONObject();
        JSONObject jsonRootObject2 = new JSONObject();
        JSONObject jsonRootObject3 = new JSONObject();
        try {
            for (int x = 0; x < editTextList.size(); x++) {
                JSONObject jsonRootObject4 = new JSONObject();
                jsonRootObject4.put("ingName", editTextList.get(x).getText().toString());
                jsonRootObject3.put(editTextList.get(x).getTag().toString(), jsonRootObject4);
            }
            jsonRootObject2.put("teams", jsonRootObject3);
            jsonRootObject2.put("teamName", teamName.getText().toString());
            jsonRootObject.put(key, jsonRootObject2);
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
                        Log.e("lcat_response", response);
                        if (response.equals("success")) {
                            if (viewPager.getCurrentItem() != 3) {
                                Intent intent = new Intent("register_event");
                                intent.putExtra("key", key);
                                intent.putExtra("teamName", teamName.getText().toString());
                                intent.putExtra("teams", jsonRootObject3.toString());
                                LocalBroadcastManager.getInstance(EventInfo.this).sendBroadcast(intent);
                            }
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
                params.put("tid", AppController.getInstance().tournamentModel.getId());
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
            switch (position) {
                case 0:
                    return new RuleFragment();
                case 2:
                    return new PrizePoolFragment(AppController.getInstance().tournamentModel.getPrize_pool());
                case 3:
                    return new PrizePoolFragment(AppController.getInstance().tournamentModel.getInfo());
                default:
                    return new TeamFragment();
            }
        }

        // this counts total number of tabs
        @Override
        public int getCount() {
            return totalTabs;
        }
    }
}
