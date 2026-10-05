package com.example.celenganku;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

public final class BottomNavHelper {

    private BottomNavHelper() {
    }

    public static void setupNav(Activity activity, int currentNavId) {
        View navHome = activity.findViewById(R.id.navHome);
        View navWish = activity.findViewById(R.id.navWish);
        View navHistory = activity.findViewById(R.id.navHistory);
        View navSettings = activity.findViewById(R.id.navSettings);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                if (!(activity instanceof MainActivity)) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
        }

        if (navWish != null) {
            navWish.setOnClickListener(v -> {
                if (!(activity instanceof WishActivity)) {
                    Intent intent = new Intent(activity, WishActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
        }

        if (navHistory != null) {
            navHistory.setOnClickListener(v -> {
                if (!(activity instanceof HistoryActivity)) {
                    Intent intent = new Intent(activity, HistoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
        }

        if (navSettings != null) {
            navSettings.setOnClickListener(v -> {
                if (!(activity instanceof SettingsActivity)) {
                    Intent intent = new Intent(activity, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }
            });
        }
    }
}