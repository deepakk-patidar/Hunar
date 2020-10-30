package com.ics.hunar.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;

public class SharedPreferencesUtil {
    public static final String PROFILE = "profile";
    private static SharedPreferences mSharedPref;
    private static SharedPreferences.Editor prefsEditor;
    public static final String USER_ID = "user_id";
    public static final String FORGOT_USER_ID = "forgot_user_id";
    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String FORGOT_MOBILE_NUMBER = "forgot_mobile_number";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String OTP_USER_ID = "otp_user_id";
    public static final String SUB_CATEGORY_NAME = "sub_category_name";
    public static final String LAST_PLAY_VIDEO = "last_play_video";
    public static final String FB_ID = "fb_id";
    public static final String STATUS = "status";

    public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }


    public static String getDeviceId() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }


    public static void write(String key, String value) {
        if (mSharedPref != null) {
            prefsEditor = mSharedPref.edit();
            prefsEditor.putString(key, value).apply();
        }
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        if (mSharedPref != null) {
            prefsEditor = mSharedPref.edit();
            prefsEditor.putBoolean(key, value).apply();
        }
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        if (mSharedPref != null) {
            prefsEditor = mSharedPref.edit();
            prefsEditor.putInt(key, value).apply();
        }
    }

    public static void removePreference(String key) {
        if (mSharedPref != null) {
            prefsEditor = mSharedPref.edit();
            prefsEditor.remove(key).apply();
        }
    }

    public static void clearAllPreference() {
        if (mSharedPref != null) {
            prefsEditor = mSharedPref.edit();
            prefsEditor.clear().apply();
        }
    }
}
