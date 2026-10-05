package com.example.celenganku;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public final class LocaleHelper {

    private static final String PREF_LANG = "celenganku_lang_prefs";
    private static final String KEY_LANG = "selected_language";

    private LocaleHelper() {
    }

    public static Context setLocale(Context context) {
        String langCode = getSavedLanguage(context);
        return updateResources(context, langCode);
    }

    public static Context setLocale(Context context, String langCode) {
        saveLanguage(context, langCode);
        return updateResources(context, langCode);
    }

    public static String getSavedLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_LANG, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANG, "id");
    }

    private static void saveLanguage(Context context, String langCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_LANG, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANG, langCode).apply();
    }

    private static Context updateResources(Context context, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }
}