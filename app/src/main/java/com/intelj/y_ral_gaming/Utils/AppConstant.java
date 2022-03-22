package com.intelj.y_ral_gaming.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.intelj.y_ral_gaming.AppController;
import com.intelj.y_ral_gaming.PaymentActivity;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class AppConstant {
    public static String follow = "follow";
    private Context _context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences myInfo;
    public static String AppName = "Y-Ral Gaming",
            login = "login",
            phoneNumber = "phoneNumber",
            userId = "userId",
            id = "id",
            amount = "amount",
            ad_mobs = "ca-app-pub-4340305355612346/9283952361",
            google_ad_mobs = "ca-app-pub-3940256099942544/2247696110",
            applyMatches = "applyMatches",
            title = "title",
            user = "user",
            package_id = "package_id",
            package_name = "package_name",
            expiry_date = "expiry_date",
            follower = "follower",
            following = "following",
            success = "success",
            message = "message",
            uniqueId = "uniqueId",
            live_stream = "live_stream",
            friends = "friends",
            member = "member",
            mobile_info = "mobile_info",
            youtubeId = "youtubeId",
            winner_id = "winner_id",
            deviceId = "deviceId",
            ign = "ign",
            saveYTid = "saveYTid",
            stopTime = "stopTime",
            backgroundData = "backgroundData",
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
            count_win = "winner",
            package_info = "package_info",
            game_slot = "game_slot",
            gameStreaming = "gameStreaming",
            pinfo = "pinfo",
            realTime = "realTime",
            paymentHistory = "phist",
            discordId = "discordId",
            pubgId = "pubgId",
            youtubeApiKey = "AIzaSyBQiqtYCe51DtHvGhJOjO20Vv9Y_uzRyks",
            splashscreen = "splashscreen",
            userName = "userName",
            bookingid = "bookingid",
            defaultImg = "https://yt3.ggpht.com/QqynpcFTpxocimFedssJVvoqGQppiMZRH5SUbIZdX3_BZH4zrlBMrWApGFUusYNziMGLdC9DpA=w2120-fcrop64=1,00005a57ffffa5a8-k-c0xffffffff-no-nd-rj",
            username_search = "username";
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

    public static boolean isProduction = false;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public AppConstant(Context _context) {
        this._context = _context;
        setSharedPref();
    }

    public void setSharedPref() {
        sharedPreferences = _context.getSharedPreferences(AppName, Context.MODE_PRIVATE);
    }

    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    public void saveLogin(String user_id, String _phoneNumber, int _coin, String _countryCode) {
        setSharedPref();
        myInfo = _context.getSharedPreferences(userId, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(login, true).apply();
        sharedPreferences.edit().putString(userId, user_id).apply();
        myInfo.edit().putInt(coin, _coin).apply();
        myInfo.edit().putString(countryCode, _countryCode).apply();
        myInfo.edit().putString(phoneNumber, _phoneNumber).apply();
    }

    public void savePackage(String packageId, String expiryDate, String uniqueId) {
        setSharedPref();
        myInfo = _context.getSharedPreferences(userId, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(package_id, packageId).apply();
        sharedPreferences.edit().putString(expiry_date, expiryDate).apply();
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

    public void saveUserInfo(Context context, DataSnapshot childDataSnap) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(childDataSnap.getKey(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editors = sharedpreferences.edit();
        editors.putString(AppConstant.userName, childDataSnap.child(AppConstant.pinfo).child(AppConstant.userName).getValue() + "");
        editors.putString(AppConstant.phoneNumber, childDataSnap.child(AppConstant.phoneNumber).getValue() + "");
        editors.putString(AppConstant.myPicUrl, childDataSnap.child(AppConstant.pinfo).child(AppConstant.myPicUrl).getValue() + "");
        editors.putString(AppConstant.discordId, childDataSnap.child(AppConstant.pinfo).child(AppConstant.discordId).getValue() + "");
        for (Map.Entry<String, Boolean> entry : AppController.getInstance().gameNameHashmap.entrySet()) {
            editors.putString(entry.getKey(), childDataSnap.child(AppConstant.pinfo).child(entry.getKey()).getValue() + "");
        }
        editors.apply();
    }

    public void saveSlot(String _uniqueId) {
        setSharedPref();
        sharedPreferences.edit().putString(saveYTid, _uniqueId).apply();
    }

    public void savebooking(long booking) {
        setSharedPref();
        sharedPreferences.edit().putLong(bookingid, booking).apply();
    }

    public Long getbooking() {
        setSharedPref();
        return sharedPreferences.getLong(bookingid, 0);
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

    public String getUserId() {
        setSharedPref();
        return sharedPreferences.getString(userId, "");
    }

    public String getId() {
        setSharedPref();
        return sharedPreferences.getString(id, "");
    }

    public String getPhoneNumber() {
        myInfo = _context.getSharedPreferences(AppController.getInstance().userId, Context.MODE_PRIVATE);
        return myInfo.getString(phoneNumber, "");
    }

    public boolean getFriendCheck() {
        myInfo = _context.getSharedPreferences(AppController.getInstance().userId, Context.MODE_PRIVATE);
        return myInfo.getBoolean(friends, false);
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

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}