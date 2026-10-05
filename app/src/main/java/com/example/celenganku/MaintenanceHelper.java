package com.example.celenganku;

import android.content.Context;
import android.content.SharedPreferences;

public final class MaintenanceHelper {

    private static final String PREF_NAME = "celenganku_admin_prefs";
    private static final String KEY_MAINTENANCE = "maintenance_mode";

    private MaintenanceHelper() {
    }

    public static boolean isMaintenanceActive(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_MAINTENANCE, false);
    }
}