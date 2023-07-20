package com.gnj.e_koperasi;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String PREF_NAME = "E-Koperasi";
    private static final String KEY_HAS_LOGGED_IN = "hasLoggedIn";
    private static final String KEY_USER_ID = "userId"; // New key for user ID
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean hasLoggedIn() {
        return sharedPreferences.getBoolean(KEY_HAS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        editor.putBoolean(KEY_HAS_LOGGED_IN, loggedIn);
        editor.apply();
    }

    // Method to save the user's ID in SharedPreferences
    public void saveUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    // Method to retrieve the user's ID from SharedPreferences
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }
}
