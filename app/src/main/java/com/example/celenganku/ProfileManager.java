package com.example.celenganku;

import android.content.Context;
import android.content.SharedPreferences;

public final class ProfileManager {

    private static final String PREF_PROFILE = "celenganku_profile_prefs";
    private static final String KEY_AVATAR_URI = "avatar_uri_string";

    private ProfileManager() {
    }

    public static void saveAvatarUri(Context context, String uriString) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_PROFILE, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_AVATAR_URI, uriString).apply();
    }

    public static String getAvatarUri(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_PROFILE, Context.MODE_PRIVATE);
        return prefs.getString(KEY_AVATAR_URI, null);
    }
}