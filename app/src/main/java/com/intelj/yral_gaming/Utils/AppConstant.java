package com.intelj.yral_gaming.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;

import java.text.SimpleDateFormat;

public class AppConstant {
    private Context _context;
    private SharedPreferences sharedPreferences;
    public static String AppName = "dumniGaming",
            login = "login",
            phoneNumber = "phoneNumber",
            userId = "userId",
            deviceId = "deviceId",
            uniqueId = "uniqueId",
            live_stream = "live_stream",
            friends = "friends",
            member = "member",
            mobile_info = "mobile_info",
            youtubeId="youtubeId",
            saveYTid="saveYTid",
            stopTime="stopTime",
            backgroundData ="backgroundData",
            search = "search",
            myTeam = "myTeam",
            token = "token",
            myPicUrl = "myPicUrl",
            users = "users",
            count_win = "winner",
            pinfo = "pinfo",
            paymentHistory = "phist",
            gameBio = "gbio",
            youtubeApiKey = "AIzaSyBQiqtYCe51DtHvGhJOjO20Vv9Y_uzRyks",
            userName = "userName",
            bookingid = "bookingid";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");

    public AppConstant(Context _context) {
        this._context = _context;
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
    public void saveLogin(String user_id, String _phoneNumber) {
        setSharedPref();
        sharedPreferences.edit().putBoolean(login, true).apply();
        sharedPreferences.edit().putString(userId, user_id).apply();
        sharedPreferences.edit().putString(phoneNumber, _phoneNumber).apply();
    }
    public void saveSlot(String _uniqueId) {
        setSharedPref();
        sharedPreferences.edit().putString(saveYTid, _uniqueId).apply();
    }

    public void savebooking(long booking) {
        setSharedPref();
        sharedPreferences.edit().putLong(bookingid,booking).apply();
    }

    public Long getbooking() {
        setSharedPref();
        return sharedPreferences.getLong(bookingid,0);
    }

    public String getDataFromShared(String param){
        setSharedPref();
        return sharedPreferences.getString(param, "");
    }
    public boolean checkLogin() {
        setSharedPref();
        return sharedPreferences.getBoolean(login, false);
    }

    public String getUserId() {
        setSharedPref();
        return sharedPreferences.getString(userId, "");
    }

    public String getPhoneNumber() {
        setSharedPref();
        return sharedPreferences.getString(phoneNumber, "");
    }
}
