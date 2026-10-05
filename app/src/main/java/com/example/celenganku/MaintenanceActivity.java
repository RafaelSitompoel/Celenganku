package com.example.celenganku;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MaintenanceActivity extends AppCompatActivity {

    private static final String PREF_NAME = "celenganku_admin_prefs";
    private static final String KEY_MAINTENANCE = "maintenance_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        MaterialButton btnCheck = findViewById(R.id.btnCheckMaintenance);
        btnCheck.setOnClickListener(v -> periksaStatus());
    }

    private void periksaStatus() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isMaintenance = prefs.getBoolean(KEY_MAINTENANCE, false);

        if (!isMaintenance) {
            Toast.makeText(this, "Pemeliharaan selesai! Memuat ulang aplikasi...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MaintenanceActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Sistem masih dalam pemeliharaan publik oleh Administrator.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (!prefs.getBoolean(KEY_MAINTENANCE, false)) {
            Intent intent = new Intent(MaintenanceActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
    }
}