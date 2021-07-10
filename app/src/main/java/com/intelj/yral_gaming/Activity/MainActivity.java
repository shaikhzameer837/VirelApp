package com.intelj.yral_gaming.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intelj.yral_gaming.Adapter.MemberListAdapter;
import com.intelj.yral_gaming.Adapter.PayMentAdapter;
import com.intelj.yral_gaming.Adapter.RankAdapter;
import com.intelj.yral_gaming.AppController;
import com.intelj.yral_gaming.ComingSoon;
import com.intelj.yral_gaming.DatabaseHelper;
import com.intelj.yral_gaming.HelloService;
import com.intelj.yral_gaming.NotesAdapter;
import com.intelj.yral_gaming.R;
import com.intelj.yral_gaming.SigninActivity;
import com.intelj.yral_gaming.Utils.AppConstant;
import com.intelj.yral_gaming.Utils.RecyclerTouchListener;
import com.intelj.yral_gaming.model.Note;
import com.intelj.yral_gaming.model.UserListModel;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    AppConstant appConstant;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private ArrayList<String> diaplayUrl = new ArrayList<>();
    BottomNavigationView bottomNavigation;
    private TabLayout tabLayout;
    ViewPager gameViewpager;
    TextView timeLeft;
    TextView coin;
    EditText game_id;
    ImageView edit;
    private RecyclerView recyclerView;
    View inflated;
    int RESULT_LOAD_IMAGE = 9;
    ImageView imgProfile,saveProf;
    TextView textView;
    String picturePath = null;
    TextInputEditText playerName, discordId;
    private Uri filePath = null;
    private DatabaseHelper db;
    private DatabaseHelper backgroundDB;
    AlertDialog dialog;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 15;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        AppController.getInstance().getGameName();
        AppController.getInstance().getTournamentTime();
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view_drawer);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
        setUpNavigationView();
        if (savedInstanceState == null) {
            invalidateOptionsMenu();
        }
        //JDABuilder
//        ScreenshotManager.INSTANCE.requestScreenshotPermission(MainActivity.this, REQUEST_ID);
//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(MainActivity.this,"hey",Toast.LENGTH_LONG).show();
//                ScreenshotManager.INSTANCE.takeScreenshot(MainActivity.this);
//            }
//        }, 3000);
        diaplayUrl.add("https://images.indianexpress.com/2020/09/PUBG-mobile.jpg");
        diaplayUrl.add("https://lh3.googleusercontent.com/proxy/TF9lDUkb1D8BL8kxXlbPFf4FZdoQcR8_eYpnxPdfGkUHHANMXHEXNc7c9ojPLo8_7x83nlHg_amEIdne2aulZve-p_VGIsZen6CpkDm4A45xIhf9_LfQst8");
        diaplayUrl.add("https://i.ytimg.com/vi/gs4mXgMxh-4/maxresdefault.jpg");
        diaplayUrl.add("https://i.ytimg.com/vi/WfExDgYKHGQ/maxresdefault.jpg");
        viewPager = findViewById(R.id.view_pager);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        final ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.game_slot);
        appConstant = new AppConstant(this);
        db = new DatabaseHelper(this, "notifications");
        backgroundDB = new DatabaseHelper(this, "background_db");
        inflated = stub.inflate();
        SwipeSelector swipeSelector = findViewById(R.id.swipeSelector);
        swipeSelector.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Silver", "Earn more on Every Chicken Dinner"),
                new SwipeItem(1, "Gold", "it is very easy to apply for subscription"),
                new SwipeItem(2, "Platinum", "Free coins")
        );
        swipeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSubscribe();
             }
        });
        final Handler handler = new Handler();
        final int delay = 5000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                SwipeItem selectedItem = swipeSelector.getSelectedItem();
                int current = (Integer) selectedItem.value;
                if (current == 2)
                    current = 0;
                else
                    ++current;
                swipeSelector.selectItemAt(current); // Do your work here
                handler.postDelayed(this, delay);
            }
        }, delay);
//        getGameName();
//        openTopSheetDialog(roomPassword);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
                    getFragmentManager().findFragmentById(R.id.youtubeFragment);
            youtubeFragment.initialize(AppConstant.youtubeApiKey,
                    new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            YouTubePlayer youTubePlayer, boolean b) {
                            if (!b) {
                                youTubePlayer.loadVideo("zBD-WH3X4nc");
                            }
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult youTubeInitializationResult) {
                            Log.d("onInitianFailure: ", youTubeInitializationResult.toString());
                        }
                    });
        }
        setFirstView();
        SharedPreferences shd = getSharedPreferences(AppConstant.saveYTid, 0);
        String saveYTid = shd.getString(AppConstant.saveYTid, "");
        if (!saveYTid.equals("")) {
            setRoomVideo(saveYTid);
        }

        // startService(new Intent(MainActivity.this,MyService.class));

        timeLeft = findViewById(R.id.timeLeft);

        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.game_slot:
                                inflateView(R.layout.game_slot);
                                setFirstView();
                                // showTeamBottomSheet();
                                return true;
                            case R.id.showNotification:
                                if (!new AppConstant(MainActivity.this).checkLogin()) {
                                    showBottomSheetDialog();
                                    return true;
                                }
                                inflateView(R.layout.rank);
                                showNotification();
                                return true;
                            case R.id.rank:
                                if (!new AppConstant(MainActivity.this).checkLogin()) {
                                    showBottomSheetDialog();
                                    return true;
                                }
                                inflateView(R.layout.rank);
                                showRank();
                                return true;
                            case R.id.team:
                                if (!new AppConstant(MainActivity.this).checkLogin()) {
                                    showBottomSheetDialog();
                                    return true;
                                }
                                inflateView(R.layout.bottom_sheet_dialog);
                                showTeam();
                                return true;
                            case R.id.history:
                                if (!new AppConstant(MainActivity.this).checkLogin()) {
                                    showBottomSheetDialog();
                                    return true;
                                }
                                inflateView(R.layout.history);
                                showHistory(inflated);
                                return true;
                            case R.id.profile:
                                if (!new AppConstant(MainActivity.this).checkLogin()) {
                                    showBottomSheetDialog();
                                    return true;
                                }
                                inflateView(R.layout.edit_profile);
                                imgProfile = inflated.findViewById(R.id.imgs);
                                imgProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                1);
                                    }
                                });

                                Glide.with(MainActivity.this).load(AppController.getInstance().mySnapShort.child(AppConstant.myPicUrl).getValue() + "").placeholder(R.drawable.profile_icon).apply(new RequestOptions().circleCrop()).into(imgProfile);
                                playerName = inflated.findViewById(R.id.name);
                                discordId = inflated.findViewById(R.id.discordId);
                                TextInputEditText phoneNumber = inflated.findViewById(R.id.phoneNumber);
                                saveProf = inflated.findViewById(R.id.save);
                                playerName.setText(AppController.getInstance().mySnapShort.child(AppConstant.userName).getValue() + "");
                                phoneNumber.setText(new AppConstant(MainActivity.this).getPhoneNumber());
                                discordId.setText(AppController.getInstance().mySnapShort.child(AppConstant.discordId).exists() ? AppController.getInstance().mySnapShort.child(AppConstant.discordId).getValue() + "" : "");
                                saveProf.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(!discordId.isEnabled()){
                                            discordId.setEnabled(true);
                                            playerName.setEnabled(true);
                                            saveProf.setImageResource(R.drawable.check);
                                            return;
                                        }
                                        if (picturePath == null)
                                            saveToProfile(null);
                                         else
                                            uploadFiles();
                                    }
                                });
                                return true;
                        }
                        return false;
                    }
                };
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private void saveToProfile(String imageUrl) {
        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.userName).setValue(playerName.getText().toString());
        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.discordId).setValue(discordId.getText().toString());
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AppConstant.userName, playerName.getText().toString());
        editor.putString(AppConstant.discordId, discordId.getText().toString());
        if(imageUrl != null){
            FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(AppConstant.myPicUrl).setValue(imageUrl);
            editor.putString(AppConstant.myPicUrl, imageUrl);
            progressDialog.dismiss();
            picturePath = null;
        }
        editor.apply();
        discordId.setEnabled(false);
        playerName.setEnabled(false);
        saveProf.setImageResource(R.drawable.ic_edit);
        Toast.makeText(MainActivity.this,"Profile Updated Susccessfully",Toast.LENGTH_LONG).show();
    }


    private void showTeam() {
        inflated.findViewById(R.id.newTeam).setVisibility(View.GONE);
        inflated.findViewById(R.id.bott_button).setVisibility(View.GONE);
        inflated.findViewById(R.id.create_team).setVisibility(View.VISIBLE);
        RecyclerView recyclerview = inflated.findViewById(R.id.recyclerview);
        ArrayList teamModel = new ArrayList<>();
        for (DataSnapshot snapshot : AppController.getInstance().mySnapShort.child(AppConstant.team).getChildren()) {
            SharedPreferences prefs = getSharedPreferences(snapshot.getKey(), Context.MODE_PRIVATE);
            teamModel.add(new UserListModel(prefs.getString(AppConstant.teamName, null),
                    prefs.getString(AppConstant.myPicUrl, null),
                    snapshot.getKey(),
                    prefs.getStringSet(AppConstant.teamMember, null)));
        }
        MemberListAdapter userAdapter = new MemberListAdapter(this, teamModel, AppConstant.applyMatches);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setAdapter(userAdapter);
        recyclerview.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this,GroupProfile.class);
               // intent.
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        inflated.findViewById(R.id.create_team).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestContactPermission();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        boolean granted;
//        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
//        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
//                android.os.Process.myUid(), getPackageName());
//        if (mode == AppOpsManager.MODE_DEFAULT) {
//            granted = (checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
//        } else {
//            granted = (mode == AppOpsManager.MODE_ALLOWED);
//        }
//        if (!granted) {
//            MyBottomSheetDialog bottomSheetFragment = new MyBottomSheetDialog();
//            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
//        }
    }

    private void showNotification() {
        List<Note> notesList = new ArrayList<>();
        TextView subtitle = inflated.findViewById(R.id.subtitle);
        subtitle.setText("Notification game result in which u participated");
        TextView title = inflated.findViewById(R.id.title);
        title.setText("Match history");
        recyclerView = inflated.findViewById(R.id.recycler_view);
        notesList.addAll(db.getAllNotes());
        if (notesList.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            findViewById(R.id.not).setVisibility(View.VISIBLE);
            findViewById(R.id.pBar3).setVisibility(View.VISIBLE);
        }
        NotesAdapter mAdapter = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void createNote(String note, String youtubeId) {
        long id = db.insertNote(note, youtubeId);
        db.getNote(id);

    }

    private void showRank() {
        final RecyclerView recyclerView = inflated.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        FirebaseDatabase.getInstance().getReference(AppConstant.users).orderByChild("winner").limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                ArrayList<DataSnapshot> dataSnapshots = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    dataSnapshots.add(0, child);
                }
                RankAdapter pAdapter = new RankAdapter(MainActivity.this, dataSnapshots);
                recyclerView.setAdapter(pAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void inflateView(int layout) {
        ViewStub newViewStub = deflate(inflated);
        newViewStub.setLayoutResource(layout);
        inflated = newViewStub.inflate();
    }

    private void showHistory(View inflated) {
        final RecyclerView recyclerView = inflated.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        if (AppController.getInstance().userId != null) {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(AppConstant.users)
                    .child(AppController.getInstance().userId).child(AppConstant.paymentHistory);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    ArrayList<DataSnapshot> dataSnapshots = new ArrayList<>();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        dataSnapshots.add(child);
                    }
                    PayMentAdapter pAdapter = new PayMentAdapter(MainActivity.this, dataSnapshots);
                    recyclerView.setAdapter(pAdapter);
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

        }
    }


    private void uploadFiles() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (filePath != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            saveToProfile(imageUrl);
                                        }
                                    });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (dialog != null)
                    dialog.cancel();
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    startActivity(new Intent(MainActivity.this, SigninActivity.class));
                break;
            case 1:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MainActivity.this, SearchFriendActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == REQUEST_ID)
            ScreenshotManager.INSTANCE.onActivityResult(resultCode, data);*/
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            filePath = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);

            if (cursor == null || cursor.getCount() < 1) {
                return; // no cursor or no record. DO YOUR ERROR HANDLING
            }

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            if (columnIndex < 0) // no column index
                return; // DO YOUR ERROR HANDLING

            picturePath = cursor.getString(columnIndex);

            cursor.close();
            Glide.with(this).load(picturePath).apply(new RequestOptions().circleCrop()).into(imgProfile);


        }
    }

    public void setFirstView() {
        gameViewpager = inflated.findViewById(R.id.gameViewpager);
        tabLayout = inflated.findViewById(R.id.tabs);
        game_id = inflated.findViewById(R.id.game_id);
        edit = inflated.findViewById(R.id.edit);
        coin = inflated.findViewById(R.id.coin);
        coin.setText(new AppConstant(this).getCoins() + " Coin");
        tabLayout.setupWithViewPager(gameViewpager);
        textView = inflated.findViewById(R.id.subscription);
        textView.setSelected(true);
        AppController.getInstance().supportFragmentManager = getSupportFragmentManager();
        AppController.getInstance().gameViewpager = gameViewpager;
        AppController.getInstance().setupViewPager(gameViewpager);

    }

    /*public void getGameName() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);
        ArrayList<String> arrayList = new ArrayList<>();
        remoteConfig = FirebaseRemoteConfig.getInstance();
        String game_name = remoteConfig.getString("gameStreaming");
        Log.e("game_name", game_name);
        try {
            JSONObject jsonObject = new JSONObject(game_name);
            JSONArray keys = jsonObject.names();
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.getString(i); // Here's your key
                boolean value = jsonObject.getBoolean(key);
                if (value)
                    arrayList.add(key);
            }
            for (int i = 0; i < arrayList.size(); i++) {
                Log.e("arraylistvalue",arrayList.get(i));
            }


        } catch (JSONException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }

    }*/


    public ViewStub deflate(View view) {
        ViewParent viewParent = view.getParent();
        if (viewParent != null && viewParent instanceof ViewGroup) {
            int index = ((ViewGroup) viewParent).indexOfChild(view);
            int inflatedId = view.getId();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            ((ViewGroup) viewParent).removeView(view);
            Context context = ((ViewGroup) viewParent).getContext();
            ViewStub viewStub = new ViewStub(context);
            viewStub.setInflatedId(inflatedId);
            viewStub.setLayoutParams(layoutParams);
            ((ViewGroup) viewParent).addView(viewStub, index);
            return viewStub;
        } else {
            throw new IllegalStateException("Inflated View has not a parent");
        }
    }

//    private void showTeamBottomSheet() {
//        View myView = LayoutInflater.from(this).inflate(R.layout.my_team, null);
//        final BottomSheetDialog dialog = new BottomSheetDialog(this);
//        dialog.setContentView(myView);
//        dialog.show();
//        recyclerView = myView.findViewById(R.id.recycler_view);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new TeamAdapter(MainActivity.this, AppController.getInstance().userInfoList, AppConstant.friends);
//        recyclerView.setAdapter(mAdapter);
//        ((View) myView.getParent()).setBackgroundColor(Color.TRANSPARENT);
//    }

    public void openSubscribe() {
        Intent intent = new Intent(this, ComingSoon.class);
        startActivity(intent);
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_view, container, false);
            ImageView imageView = view.findViewById(R.id.img);
            Glide.with(MainActivity.this).load(diaplayUrl.get(position)).into(imageView);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return diaplayUrl.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    DatabaseReference mDatabase;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(AppConstant.AppName,false) == true){
                showBottomSheetDialog();
                return;
            }
            SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.AppName, 0);
            game_id.setHint(intent.getStringExtra(AppConstant.title) + " id");
            game_id.setText(sharedPreferences.getString(intent.getStringExtra(AppConstant.title), ""));
            inflated.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!new AppConstant(context).checkLogin()){
                        showBottomSheetDialog();
                        return;
                    }
                    if (game_id.isEnabled()) {
                        game_id.setEnabled(false);
                        game_id.clearFocus();
                        edit.setImageResource(R.drawable.ic_edit);
                    } else {
                        edit.setImageResource(R.drawable.close);
                        game_id.setEnabled(true);
                        InputMethodManager inputMethodManager =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInputFromWindow(game_id.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                        game_id.requestFocus();
                        game_id.setSelection(game_id.getText().length());
                    }
                }
            });
            game_id.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        edit.setImageResource(R.drawable.ic_edit);
                        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(intent.getStringExtra(AppConstant.title), game_id.getText().toString());
                        editor.apply();
                        Toast.makeText(MainActivity.this,"Id updated",Toast.LENGTH_LONG).show();
                        FirebaseDatabase.getInstance().getReference(AppConstant.users).child(AppController.getInstance().userId).child(AppConstant.pinfo).child(intent.getStringExtra(AppConstant.title)).setValue(game_id.getText().toString());
                        inflated.findViewById(R.id.edit).performClick();
                        return true;
                    }
                    return false;
                }
            });

            // game_id.setText(intent.getStringExtra(AppConstant.title));
//            // Get extra data included in the Intent
//            String message = intent.getStringExtra("message");
//            if (message.equals("bottom_sheet_broadcast")) {
//                showBottomSheetDialog();
//                return;
//            }
//            final String roomPlan = intent.getStringExtra("roomPlan");
//            new CountDownTimer(Long.parseLong(message), 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//                    int seconds = (int) (millisUntilFinished / 1000) % 60;
//                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
//                    int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
//                    timeLeft.setText("Your next Game starts in " + hours + ":" + minutes + ":" + seconds);
//                    timeLeft.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                           // startService(new Intent(MainActivity.this, MyService.class));
//                        }
//                    });
//                }
//
//                @Override
//                public void onFinish() {
//                    timeLeft.setText("Finished");
//                }
//            }.start();
            //   setRoomVideo(roomPlan);
        }
    };

    private void setRoomVideo(final String roomPlan) {
        AppController.getInstance().uploadUrl = AppConstant.live_stream + "/" + AppController.getInstance().channelId + "/" + roomPlan + "/";
        mDatabase = FirebaseDatabase.getInstance().getReference(AppController.getInstance().uploadUrl + "room/");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                Log.e("dataSnapshotss", dataSnapshot.child(AppConstant.stopTime).exists() + "");
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child(AppConstant.stopTime).exists()) {
                        Intent intent = new Intent(MainActivity.this, HelloService.class);
                        intent.putExtra("stopTiming", dataSnapshot.child(AppConstant.stopTime).getValue() + "");
                        intent.setAction(HelloService.ACTION_STOP_FOREGROUND_SERVICE);
                        startService(intent);
                        try {
                            appConstant.saveSlot("XYZ");
                        } catch (Exception e) {
                            e.getLocalizedMessage();
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    } else {
                        appConstant.saveSlot(dataSnapshot.child(AppConstant.youtubeId).getValue() + "");
                        if (db.getNotesCount(dataSnapshot.child(AppConstant.youtubeId).getValue() + "") == 0)
                            createNote(roomPlan, dataSnapshot.child(AppConstant.youtubeId).getValue() + "");
                        Intent intent = new Intent(MainActivity.this, HelloService.class);
                        intent.putExtra("roomPlan", roomPlan);
                        intent.setAction(HelloService.ACTION_START_FOREGROUND_SERVICE);
                        startService(intent);
                        if (dataSnapshot.child(AppConstant.backgroundData).child(AppController.getInstance().userId).exists()) {
                            HashMap<String, Object> allBackground = new HashMap<>();
                            for (Note allNote : backgroundDB.getAllNotes()) {
                                if (allNote.getNote() != null)
                                    allBackground.put(System.currentTimeMillis() + "", allNote.getNote());
                                Log.e("backgroundDB", allNote.getTimestamp());
                            }
                            mDatabase.child("backgroundData").child(AppController.getInstance().userId).setValue(allBackground);
                        }
                    }
//                    roomId.setText("Room Id:- " + dataSnapshot.child("roomId").getValue() + "");
//                    roomPassword.setText("Password:- " + dataSnapshot.child("password").getValue() + "");
//                    roomId.setVisibility(View.VISIBLE);
//                    roomPassword.setVisibility(View.VISIBLE);
//                    roomId.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            final int DRAWABLE_LEFT = 0;
//                            if (event.getAction() == MotionEvent.ACTION_UP) {
//                                Log.e("copyClip", roomId.getText().toString());
//                                if (event.getRawX() >= (roomId.getLeft() - roomId.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
//                                    setClipboard(roomId.getText().toString());
//                                    return true;
//                                }
//                            }
//                            return false;
//                        }
//                    });
//                    roomPassword.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            final int DRAWABLE_LEFT = 0;
//                            if (event.getAction() == MotionEvent.ACTION_UP) {
//                                Log.e("copyClip", roomPassword.getText().toString());
//                                if (event.getRawX() >= (roomId.getLeft() - roomId.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
//                                    setClipboard(roomPassword.getText().toString());
//                                    return true;
//                                }
//                            }
//                            return false;
//                        }
//                    });
                    YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
                            getFragmentManager().findFragmentById(R.id.youtubeFragment);
                    youtubeFragment.initialize(AppConstant.youtubeApiKey,
                            new YouTubePlayer.OnInitializedListener() {
                                @Override
                                public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                                    YouTubePlayer youTubePlayer, boolean b) {
                                    // do any work here to cue video, play video, etc.
                                    if (!b) {
                                        youTubePlayer.loadVideo(dataSnapshot.child(AppConstant.youtubeId).getValue() + "");
                                    }
                                    // youTubePlayer.cueVideo(dataSnapshot.child(AppConstant.youtubeId).getValue() + "",1);
                                }

                                @Override
                                public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                                    YouTubeInitializationResult youTubeInitializationResult) {
                                    Log.d("onInitianFailure: ", youTubeInitializationResult.toString());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.moveTaskToBack(true);
    }

    private void setClipboard(String text) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_login_bottom_sheet_fragment, null);
        final BottomSheetDialog dialogBottom = new BottomSheetDialog(MainActivity.this);
        dialogBottom.setContentView(view);
        dialogBottom.show();
        Button btn_ok = view.findViewById(R.id.ok);
//        Button btn_cancel = view.findViewById(R.id.cancel);
        ImageView imageView = view.findViewById(R.id.imageview);
        Glide.with(MainActivity.this).load(R.drawable.login).into(imageView);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) && dialog == null) {
                        //If User was asked permission before and denied
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                        alertDialogBuilder.setTitle("Permission needed");
                        alertDialogBuilder.setMessage("Storage permission needed for accessing Storage");
                        alertDialogBuilder.setPositiveButton("Open Setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(),
                                        null);
                                intent.setData(uri);
                                MainActivity.this.startActivity(intent);
                            }
                        });
                        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

                        dialog = alertDialogBuilder.create();
                        dialog.show();
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                100);
                    }
                } else
                    startActivity(new Intent(MainActivity.this, SigninActivity.class));
            }
        });

//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                context.startActivity(new Intent(context, MainActivity.class));
//                bottomNavigation.getMenu().getItem(0).setChecked(true);
//                dialogBottom.dismiss();
//            }
//        });
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_aboutus:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tcs.com/")));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_privacypolicy:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tcs.com/privacy-policy")));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_tt:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tcs.com/legal-disclaimer")));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_facebook:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Kakelious/")));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_discord:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/uygzekRnhE")));
                        drawer.closeDrawers();
                        return true;

                    case R.id.nav_youtube:
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/UCt3oqOC4xYAQBzWIhiupLBg"));
                        startActivity(intent);
                        drawer.closeDrawers();
                        return true;
                    /*case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_settings:


                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;*/
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

//                loadHomeFragment();

                return true;
            }
        });


        /*ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();*/
    }

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                Intent intent = new Intent(MainActivity.this, SearchFriendActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, SearchFriendActivity.class);
            startActivity(intent);
        }
    }
}