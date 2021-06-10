package com.teletap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.teletap.activity.HomeActivity;
import com.teletap.activity.LoginOptions;
import com.teletap.utilities.SharedPreferenceUtility;


public class Session {
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Context
    private Context _context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "TeletapPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Constructor
    @SuppressLint("CommitPrefEdits")
    public Session(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // commit changes
        editor.commit();
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginOptions.class);
            //SessionManager.saveUser(_context,false);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            _context.startActivity(i);

        }else {
                Intent i = new Intent(_context, HomeActivity.class);
                i.putExtra("cameFrom", "splash");
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Staring Login Activity
                _context.startActivity(i);
        }
    }

    private boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
        SharedPreferenceUtility.getInstance().clearSharedPreferences();
       // SessionManager.Logout(_context);
//        editor.remove("");
//        Intent i = new Intent(_context, LoginActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        _context.startActivity(i);
    }

}
