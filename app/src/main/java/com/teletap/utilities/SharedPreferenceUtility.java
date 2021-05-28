package com.teletap.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.teletap.api.AppController;


public class SharedPreferenceUtility {

    public static SharedPreferences sharedPreferences_1;
    private static SharedPreferenceUtility instance1;
    public static final String SHARED_PREFS_NAME  = "thaiSweetheart";
    public static String CUSID1 = "value";

    public static synchronized SharedPreferenceUtility getInstance() {
        if (instance1 == null) {
            instance1 = new SharedPreferenceUtility();
        }
        return instance1;
    }

    private SharedPreferenceUtility() {
        instance1 = this;
        sharedPreferences_1 = AppController.getInstance().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void delete(String key) {
        if (sharedPreferences_1.contains(key)) {
            getEditor().remove(key).commit();
        }
    }

    public  void save(String key, Object value) {
        SharedPreferences.Editor editor = getEditor();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-supported preference");
        }
        editor.commit();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) sharedPreferences_1.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defValue) {
        T returnValue = (T) sharedPreferences_1.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    public boolean has(String key) {
        return sharedPreferences_1.contains(key);
    }

    public SharedPreferences.Editor getEditor() {
        return sharedPreferences_1.edit();
    }

    public void clearSharedPreferences() {
        SharedPreferences.Editor editor = getEditor();
        editor.clear();
        editor.commit();
    }
}
