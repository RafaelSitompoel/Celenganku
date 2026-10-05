package com.example.celenganku;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static boolean wasOffline = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.isOnline(context)) {
            // Cek apakah sebelumnya offline atau baru saja terhubung via Wi-Fi
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                Network network = cm.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities caps = cm.getNetworkCapabilities(network);
                    if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        if (wasOffline) {
                            NotificationHelper.showWifiConnectedNotification(context);
                            wasOffline = false;
                        }
                    }
                }
            }
        } else {
            wasOffline = true;
        }
    }
}