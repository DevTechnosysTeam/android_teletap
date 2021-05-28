package com.teletap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teletap.model.SignupModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AppPreference {
    private static AppPreference appPreference;
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesClear;
    private  static Context context;

    public static AppPreference getInstance(Context context) {
        if (appPreference == null) {
            appPreference = new AppPreference(context);
            context = context;
        }
        return appPreference;
    }

    public static void idLogout(Context context) {
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = getInstance(context).sharedPreferences.edit();
        editor.remove("userInfo");
        editor.apply();

    }

    public static  void  saveUser(Context context,boolean isChecked){
        SharedPreferences.Editor editor = getInstance(context).sharedPreferences.edit();
        editor.putBoolean("isChecked", isChecked);
        editor.apply();

    }

    public static  void  isLoginWithNumber(Context context,boolean isNumber){
        SharedPreferences.Editor editor = getInstance(context).sharedPreferences.edit();
        editor.putBoolean("isNumber", isNumber);
        editor.apply();
    }

    public static  void  loginCountryCode(Context context,String countryCode){
        SharedPreferences.Editor editor = getInstance(context).sharedPreferences.edit();
        editor.putString("countryCode", countryCode);
        editor.apply();
    }

    public static  void  loginUser(Context context,String user_name){
        SharedPreferences.Editor editor = getInstance(context).sharedPreferences.edit();
        editor.putString("user_name", user_name);
        editor.apply();
    }

    public static  void  loginPassword(Context context,String password){
        SharedPreferences.Editor editor = getInstance(context).sharedPreferences.edit();
        editor.putString("password", password);
        editor.apply();
    }


    public static String getAndroidId(Context mContext) {
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }


    private AppPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("AppPreference", Context.MODE_PRIVATE);
        sharedPreferencesClear= context.getSharedPreferences("AppPreferenceClear", Context.MODE_PRIVATE);

    }

    public static boolean getBoolean(Context context, String key) {
        return getInstance(context).sharedPreferences.getBoolean(key, false);
    }
    public static String getString(Context context, String key) {

        return getInstance(context).sharedPreferences.getString(key, "");
    }
    public static int getInt(Context context, String key) {
        return getInstance(context).sharedPreferences.getInt(key, 1);
    }

    public static void setDeviceFCM(String value) {
        SharedPreferences.Editor prefsEditor = getInstance(context).sharedPreferences.edit();
        prefsEditor.putString("FCM", value);
        prefsEditor.apply();
    }

    public static String getDeviceFCM() {
        if (getInstance(context).sharedPreferences != null) {
            return getInstance(context).sharedPreferences.getString("FCM", "");
        }
        return "";
    }

    public static void saveUserInfo(@Nullable Context applicationContext, @NotNull SignupModel.DataBean data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        getInstance(applicationContext).sharedPreferences.edit().putString("userInfo", json).apply();
    }

    public static SignupModel.DataBean  getUserInfo(Context context){
        Gson gson = new Gson();
        String json = getInstance(context).sharedPreferences.getString("userInfo", null);
        if (json != null) {
        }
        Type type = new TypeToken<SignupModel.DataBean>() {
        }.getType();
        return gson.fromJson(json, type);
    }



    public void setLat(String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("lat", value);
        prefsEditor.apply();
    }

    public String getLat() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString("lat", "");
        }
        return "";
    }

    public void setLng(String value) {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("lng", value);
        prefsEditor.apply();
    }

    public String getLng() {
        if (sharedPreferences != null) {
            return sharedPreferences.getString("lng", "");
        }
        return "";
    }

    public static void saveProfileOne(ArrayList<String > cateFirst) {
        SharedPreferences.Editor editor = getInstance(context).sharedPreferences.edit();
        editor.putString("ProfileOne", String.valueOf(cateFirst));
        editor.apply();
    }

    public static void saveProfileTwo(ArrayList<String > cateFirst) {
        SharedPreferences.Editor editor = getInstance(context).sharedPreferences.edit();
        editor.putString("ProfileTwo", String.valueOf(cateFirst));
        editor.apply();
    }

    public static void saveProfileLeveOne(ArrayList<String > cateFirst) {
        SharedPreferences.Editor editor = getInstance(context).sharedPreferences.edit();
        editor.putString("profileIdsOne", String.valueOf(cateFirst));
        editor.apply();
    }
    public static void saveProfileLeveTwo(ArrayList<String > cateFirst) {
        SharedPreferences.Editor editor = getInstance(context).sharedPreferences.edit();
        editor.putString("profileIdsTwo", String.valueOf(cateFirst));
        editor.apply();
    }

    public static void saveUserId(int cateFirst) {
        SharedPreferences.Editor editor = getInstance(context).sharedPreferences.edit();
        editor.putInt("user_id", cateFirst);
        editor.apply();
    }


//    public static ArrayList<String>  getProfileIdsOne(Context context){
//        Gson gson = new Gson();
//        String json = getInstance(context).sharedPreferences.getString("profileIdsOne", null);
//        if (json != null && !json.isEmpty()) {
//            json = json.replace("[","").replace("]","");
//            String[] ss = json.split(",");
//            ArrayList<String> list = new ArrayList<>();
//            for (int i=0;i<ss.length;i++){
//                list.add(ss[i]);
//            }
//            return  list;
//        } else {
//            return new ArrayList<String>();
//        }
//
//    }

    public static ArrayList<String>  getProfileIdsOne(Context context){
        Gson gson = new Gson();
        String json = getInstance(context).sharedPreferences.getString("profileIdsOne", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String> >() {
            }.getType();
            return gson.fromJson(json, type);
        }
        else {
            return new ArrayList<String>();
        }

    }
    public static ArrayList<String>  getProfileIdsTwo(Context context){
        Gson gson = new Gson();
        String json = getInstance(context).sharedPreferences.getString("profileIdsTwo", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String> >() {
            }.getType();
            return gson.fromJson(json, type);
        }
        else {
            return new ArrayList<String>();
        }

    }

    public static ArrayList<String>  getProfileFirst(Context context){
        Gson gson = new Gson();
        String json = getInstance(context).sharedPreferences.getString("ProfileOne", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String> >() {
            }.getType();
            return gson.fromJson(json, type);
        }
        else {
            return new ArrayList<String>();
        }

    }

 public static ArrayList<String>  getProfileTwo(Context context){
        Gson gson = new Gson();
        String json = getInstance(context).sharedPreferences.getString("ProfileTwo", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String> >() {
            }.getType();
            return gson.fromJson(json, type);
        }
        else {
            return new ArrayList<String>();
        }

    }


}
