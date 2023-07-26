package com.tournaments.grindbattles.session;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import com.tournaments.grindbattles.activity.IntroActivity;


/**
 * Created by ASHISH on 2/11/2018.
 */

public class SessionManager {
    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Context
    private Context _context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "Pref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // Id (make variable public to access from outside)
    public static final String KEY_ID = "id";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    // WhatsApp (make variable public to access from outside)
    public static final String KEY_PROFILE = "profile";

    // First Name (make variable public to access from outside)
    public static final String KEY_FIRST_NAME = "fname";

    // Last Name (make variable public to access from outside)
    public static final String KEY_LAST_NAME = "lname";

    // Username (make variable public to access from outside)
    public static final String KEY_USERNAME = "username";

    // Password (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    // Email (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Country Code (make variable public to access from outside)
    public static final String KEY_CODE = "code";

    // Mobile(make variable public to access from outside)
    public static final String KEY_MOBILE = "mobile";


    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String id, String profile, String fname, String lname, String username, String password, String email, String code, String mobile,String token){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing id in pref
        editor.putString(KEY_ID, id);
        editor.putString(ACCESS_TOKEN, token);

        // Storing profile in pref
        editor.putString(KEY_PROFILE, profile);

        // Storing fname in pref
        editor.putString(KEY_FIRST_NAME, fname);

        // Storing lname in pref
        editor.putString(KEY_LAST_NAME, lname);

        // Storing username in pref
        editor.putString(KEY_USERNAME, username);

        // Storing password in pref
        editor.putString(KEY_PASSWORD, password);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // Storing code in pref
        editor.putString(KEY_CODE, code);

        // Storing mobile in pref
        editor.putString(KEY_MOBILE, mobile);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            //Intent i = new Intent(_context, SignInActivity.class);
            // Closing all the Activities
            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            //_context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_ID, pref.getString(KEY_ID,null));
        user.put(KEY_PROFILE, pref.getString(KEY_PROFILE,null));
        user.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME,null));
        user.put(KEY_LAST_NAME, pref.getString(KEY_LAST_NAME,null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME,null));
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD,null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL,null));
        user.put(KEY_CODE, pref.getString(KEY_CODE,null));
        user.put(ACCESS_TOKEN, pref.getString(ACCESS_TOKEN,null));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE,null));
        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, IntroActivity.class);

        i.putExtra("finish", true); // if you are checking for this in your other Activities
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
