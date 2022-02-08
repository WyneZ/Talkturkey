package com.example.talkturkey.database;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private final SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public PreferenceManager(Context context){
        preferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return preferences.getString(key, key);
    }

    public void putBoolean(String key, Boolean value){
        editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public void clear() {
        editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}
