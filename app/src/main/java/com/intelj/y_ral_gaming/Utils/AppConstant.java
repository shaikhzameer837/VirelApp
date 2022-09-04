package com.intelj.y_ral_gaming.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intelj.y_ral_gaming.Activity.MainActivity;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.PaymentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AppConstant {
    public static String follow = "follow";
    public static String following_count = "following_count";
    public static String follower_count = "follower_count";
    public static String AppUrl = "http://y-ral-gaming.com/admin/api/";
     private Context _context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences myInfo;
    public static String AppName = "Y-Ral Gaming",
            login = "login",
            profile = "profile",
            phoneNumber = "phoneNumber",
            yralWeb = "yral_web",
            likes = "likes",
            comments = "comments",
            id = "id",
            amount = "amount",
            payment = "payment",
            ad_mobs = "ca-app-pub-4340305355612346/9283952361",
            google_ad_mobs = "ca-app-pub-3940256099942544/2247696110",
            applyMatches = "applyMatches",
            title = "title",
            user = "user",
            contact = "contact",
            package_id = "package_id",
            expiry_date = "expiry_date",
            follower = "follower",
            following = "following",
            success = "success",
            message = "message",
            subject = "subject",
            uniqueId = "uniqueId",
            friends = "friends",
            deviceId = "deviceId",
            ign = "ign",
            saveYTid = "saveYTid",
            search = "search",
            myTeam = "myTeam",
            teamName = "teamName",
            teamMember = "teamMember",
            team = "team",
            token = "token",
            countryCode = "countryCode",
            coin = "coin",
            nextCoinTime = "nextCoinTime",
            myPicUrl = "myPicUrl",
            users = "users",
            referal_id = "referal_id",
            count_win = "winner",
            package_info = "package_info",
            game_slot = "game_slot",
            gameStreaming = "gameStreaming",
            pinfo = "pinfo",
            noti = "noti",
            realTime = "realTime",
            recent = "recent",
            msg = "msg",
            discordId = "discordId",
            bio = "bio",
            verified = "verified",
            pubgId = "pubgId",
            youtubeApiKey = "AIzaSyBQiqtYCe51DtHvGhJOjO20Vv9Y_uzRyks",
            name = "userName",
            userName = "un",
            username_search = "username";
    public static final String[] player_title = {"Sniper", "Noob", "Sentinel", "Camper", "Pro", "Smurf", "Strategic"};
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static long imageExt() {
        return (System.currentTimeMillis() / 120000);
    }

    public static boolean isProduction = false;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public AppConstant(Context _context) {
        this._context = _context;
        setSharedPref();
    }
    public void callingPingApi(String token){

    }
    public void setSharedPref() {
        sharedPreferences = _context.getSharedPreferences(AppName, Context.MODE_PRIVATE);
    }

    public static boolean isTimeAutomatic(Context c) {
            return Settings.System.getInt(c.getContentResolver(), Settings.System.AUTO_TIME, 0) == 1;
    }

    public void saveLogin(String user_id) {
        setSharedPref();
        sharedPreferences.edit().putBoolean(login, true).apply();
        sharedPreferences.edit().putString(id, user_id).apply();
    }

    public String getCountryCode() {
        myInfo = _context.getSharedPreferences(getId(), Context.MODE_PRIVATE);
        return myInfo.getString(countryCode, "+91");
    }

    public void saveUserInfo(String phoneNum, String userid, String tokenId, String name, String _countryCode, String user_bio, String userName) {
        SharedPreferences userInfo = _context.getSharedPreferences(userid, Context.MODE_PRIVATE);
        userInfo.edit().putString(id, userid).apply();
        userInfo.edit().putString(token, tokenId).apply();
        userInfo.edit().putString(phoneNumber, phoneNum).apply();
        if (user_bio != null)
            userInfo.edit().putString(bio, user_bio).apply();
        if (name != null)
            userInfo.edit().putString(AppConstant.name, name).apply();
        userInfo.edit().putString(countryCode, _countryCode).apply();
        userInfo.edit().putString(userName, userName).apply();
    }

    public String getContactName(String phoneNumber) {
        ContentResolver cr = _context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = phoneNumber;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public void savePackage(String packageId, String expiryDate, String uniqueId, String referal) {
        setSharedPref();
        myInfo = _context.getSharedPreferences(getId(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(package_id, packageId).apply();
        sharedPreferences.edit().putString(expiry_date, expiryDate).apply();
        sharedPreferences.edit().putString(referal_id, referal).apply();
        sharedPreferences.edit().putString(id, uniqueId).apply();
    }

    public void addMoney(Context mContext) {
        EditText amountText = new EditText(mContext);
        amountText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext).
                setMessage("Enter the amount").
                setPositiveButton("Add Money", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!amountText.getText().toString().trim().equals("")) {
                            dialog.dismiss();
//                            String user_id = new AppConstant(mContext).getUserId();
//                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://y-ral-gaming.com/paytm/paytm-main/pgRedirect.php?amount="+amountText.getText().toString()+"&&user_id="+user_id));
//                            mContext.startActivity(browserIntent);

                            Intent intent = new Intent(mContext, PaymentActivity.class);
                            intent.putExtra("amount", amountText.getText().toString());
                            mContext.startActivity(intent);
                        }
                    }
                }).
                setView(amountText);
        builder.create().show();
    }

    public String getDataFromShared(String param, String value) {
        setSharedPref();
        return sharedPreferences.getString(param, value);
    }

    public void setDataFromShared(String param, String value) {
        setSharedPref();
        sharedPreferences.edit().putString(param, value).apply();
    }

    public boolean checkLogin() {
        setSharedPref();
        return sharedPreferences.getBoolean(login, false);
    }

    public void logout() {
        sharedPreferences = _context.getSharedPreferences(AppName, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(login, false).apply();
    }

    public int getCoins() {
        myInfo = _context.getSharedPreferences(AppController.getInstance().userId, Context.MODE_PRIVATE);
        return myInfo.getInt(coin, 0);
    }

    public void setCoins(int _coin, long _nextCoinTime) {
        myInfo = _context.getSharedPreferences(AppController.getInstance().userId, Context.MODE_PRIVATE);
        myInfo.edit().putInt(coin, _coin).apply();
        sharedPreferences.edit().putLong(nextCoinTime, _nextCoinTime).apply();
    }

    public long getNextCoinTime() {
        return sharedPreferences.getLong(nextCoinTime, 0);
    }

    public String getUserName() {
        setSharedPref();
        return sharedPreferences.getString(userName, "");
    }

    public String getId() {
        setSharedPref();
        return sharedPreferences.getString(id, "");
    }

    public String getName() {
        SharedPreferences mysharedPreferences = _context.getSharedPreferences(getId(), 0);
        return mysharedPreferences.getString(name, "");
    }

    public String getPhoneNumber() {
        myInfo = _context.getSharedPreferences(getId(), Context.MODE_PRIVATE);
        return myInfo.getString(phoneNumber, "");
    }

    public void setPhoneNumber(String phoneN) {
        myInfo = _context.getSharedPreferences(getId(), Context.MODE_PRIVATE);
        myInfo.edit().putString(phoneNumber, phoneN).apply();
    }

    public boolean getFriendCheck() {
        myInfo = _context.getSharedPreferences(AppController.getInstance().userId, Context.MODE_PRIVATE);
        return myInfo.getBoolean(friends, false);
    }
    public static String getRank(int rankPoint) {
        if (rankPoint < 1000) {
            return "rank1";
        } else if (rankPoint < 2000) {
            return  "rank2" ;
        } else if (rankPoint < 3000) {
            return  "rank3" ;
        } else if (rankPoint < 4000) {
            return  "rank4" ;
        } else if (rankPoint < 5000) {
            return  "rank5"  ;
        } else if (rankPoint < 6000) {
            return  "rank6" ;
        }
        return "";
    }
    public static void setSubscription() {
        FirebaseMessaging.getInstance().subscribeToTopic("FreeFire").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SUBSCRIBED", "SUCCESS Free Fire");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("SUBSCRIBED--1", e.getMessage());
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic("push_yt").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SUBSCRIBED", "SUCCESS");
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic("tournament").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SUBSCRIBED tournament", "SUCCESS");
            }
        });
    }

    public String getDateFromMilli(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
