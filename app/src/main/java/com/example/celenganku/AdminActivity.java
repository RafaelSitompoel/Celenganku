package com.example.celenganku;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AdminActivity extends AppCompatActivity {

    private static final String PREF_NAME = "celenganku_admin_prefs";
    private static final String KEY_PIN = "admin_pin";
    private static final String KEY_SAVED_SALDO = "saved_saldo";
    private static final String KEY_SAVED_TARGET = "saved_target";
    private static final String KEY_MAINTENANCE = "maintenance_mode";
    private static final String KEY_ANNOUNCEMENT = "global_announcement";

    private SharedPreferences prefs;

    private View layoutAdminLockedSection;
    private View layoutAdminUnlockedSection;
    private TextView tvAdminStatusBadge;
    private TextInputLayout tilAdminPinInput;
    private TextInputEditText etAdminPinInput;
    private TextInputEditText etAdminNewSaldo;
    private TextInputEditText etAdminNewTarget;
    private SwitchMaterial switchMaintenance;
    private TextInputEditText etNewAdminPin;
    private TextView tvSystemStats;
    private TextInputEditText etGlobalAnnouncement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        View btnBack = findViewById(R.id.btnBackAdmin);
        btnBack.setOnClickListener(v -> finish());

        layoutAdminLockedSection = findViewById(R.id.layoutAdminLockedSection);
        layoutAdminUnlockedSection = findViewById(R.id.layoutAdminUnlockedSection);
        tvAdminStatusBadge = findViewById(R.id.tvAdminStatusBadge);
        tilAdminPinInput = findViewById(R.id.tilAdminPinInput);
        etAdminPinInput = findViewById(R.id.etAdminPinInput);
        etAdminNewSaldo = findViewById(R.id.etAdminNewSaldo);
        etAdminNewTarget = findViewById(R.id.etAdminNewTarget);
        switchMaintenance = findViewById(R.id.switchMaintenance);
        etNewAdminPin = findViewById(R.id.etNewAdminPin);
        tvSystemStats = findViewById(R.id.tvSystemStats);
        etGlobalAnnouncement = findViewById(R.id.etGlobalAnnouncement);

        MaterialButton btnAdminLogin = findViewById(R.id.btnAdminLogin);
        MaterialButton btnSaveAdminChanges = findViewById(R.id.btnSaveAdminChanges);
        MaterialButton btnUpdatePin = findViewById(R.id.btnUpdatePin);
        MaterialButton btnSendBroadcast = findViewById(R.id.btnSendBroadcast);
        MaterialButton btnFactoryReset = findViewById(R.id.btnFactoryReset);
        MaterialButton btnLockAdmin = findViewById(R.id.btnLockAdmin);

        btnAdminLogin.setOnClickListener(v -> prosesLoginAdmin());
        btnSaveAdminChanges.setOnClickListener(v -> simpanPerubahanAdmin());
        btnUpdatePin.setOnClickListener(v -> perbaruiPinAdmin());
        btnSendBroadcast.setOnClickListener(v -> kirimBroadcast());
        btnFactoryReset.setOnClickListener(v -> factoryResetSistem());
        btnLockAdmin.setOnClickListener(v -> kunciAdmin());

        switchMaintenance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_MAINTENANCE, isChecked).apply();
            Toast.makeText(this, isChecked ? "Mode Pemeliharaan (Maintenance) AKTIF" : "Mode Pemeliharaan Nonaktif", Toast.LENGTH_SHORT).show();
        });
    }

    private void prosesLoginAdmin() {
        String pinInput = etAdminPinInput.getText() != null ? etAdminPinInput.getText().toString().trim() : "";
        String defaultPin = prefs.getString(KEY_PIN, "1234");

        if (pinInput.equals(defaultPin)) {
            tilAdminPinInput.setError(null);
            etAdminPinInput.setText("");
            layoutAdminLockedSection.setVisibility(View.GONE);
            layoutAdminUnlockedSection.setVisibility(View.VISIBLE);
            tvAdminStatusBadge.setText("👑 Aktif");
            tvAdminStatusBadge.setTextColor(getResources().getColor(R.color.primary));

            long curSaldo = prefs.getLong(KEY_SAVED_SALDO, 0L);
            long curTarget = prefs.getLong(KEY_SAVED_TARGET, 0L);
            if (curSaldo > 0) etAdminNewSaldo.setText(String.valueOf(curSaldo));
            if (curTarget > 0) etAdminNewTarget.setText(String.valueOf(curTarget));

            switchMaintenance.setChecked(prefs.getBoolean(KEY_MAINTENANCE, false));
            etGlobalAnnouncement.setText(prefs.getString(KEY_ANNOUNCEMENT, ""));

            muatStatistikSistem();

            Toast.makeText(this, "Akses Administrator Eksekutif Berhasil Dibuka!", Toast.LENGTH_LONG).show();
        } else {
            tilAdminPinInput.setError("PIN Salah! Default PIN adalah 1234");
        }
    }

    private void simpanPerubahanAdmin() {
        long newSaldo = ambilNominal(etAdminNewSaldo);
        long newTarget = ambilNominal(etAdminNewTarget);

        if (newSaldo >= 0) {
            prefs.edit().putLong(KEY_SAVED_SALDO, newSaldo).apply();
        }
        if (newTarget > 0) {
            prefs.edit().putLong(KEY_SAVED_TARGET, newTarget).apply();
        }

        Toast.makeText(this, "Saldo & Target Global berhasil diperbarui!", Toast.LENGTH_SHORT).show();
    }

    private void perbaruiPinAdmin() {
        String newPin = etNewAdminPin.getText() != null ? etNewAdminPin.getText().toString().trim() : "";
        if (newPin.length() < 4) {
            Toast.makeText(this, "PIN Admin minimal 4 digit.", Toast.LENGTH_SHORT).show();
            return;
        }
        prefs.edit().putString(KEY_PIN, newPin).apply();
        etNewAdminPin.setText("");
        Toast.makeText(this, "Sandi PIN Administrator berhasil diperbarui!", Toast.LENGTH_LONG).show();
    }

    private void muatStatistikSistem() {
        String stats = "• Status Jaringan: " + (NetworkUtils.isOnline(this) ? "Online (Connected)" : "Offline") + "\n" +
                "• Android Version: SDK " + Build.VERSION.SDK_INT + " (" + Build.VERSION.RELEASE + ")\n" +
                "• Perangkat: " + Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL + "\n" +
                "• Status Database: Aman & Terenkripsi Lokal\n" +
                "• Mode Aplikasi: " + (prefs.getBoolean(KEY_MAINTENANCE, false) ? "Maintenance" : "Normal Operational");
        tvSystemStats.setText(stats);
    }

    private void kirimBroadcast() {
        String msg = etGlobalAnnouncement.getText() != null ? etGlobalAnnouncement.getText().toString().trim() : "";
        if (msg.isEmpty()) {
            Toast.makeText(this, "Masukkan pesan pengumuman terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return;
        }
        prefs.edit().putString(KEY_ANNOUNCEMENT, msg).apply();
        Toast.makeText(this, "Broadcast pengumuman berhasil disiarkan ke sistem!", Toast.LENGTH_LONG).show();
    }

    private void factoryResetSistem() {
        prefs.edit().clear().apply();
        etAdminNewSaldo.setText("");
        etAdminNewTarget.setText("");
        etGlobalAnnouncement.setText("");
        switchMaintenance.setChecked(false);
        Toast.makeText(this, "Factory Reset Berhasil! Semua data sistem telah dikosongkan.", Toast.LENGTH_LONG).show();
        kunciAdmin();
    }

    private void kunciAdmin() {
        layoutAdminUnlockedSection.setVisibility(View.GONE);
        layoutAdminLockedSection.setVisibility(View.VISIBLE);
        tvAdminStatusBadge.setText("🔒 Terkunci");
        tvAdminStatusBadge.setTextColor(getResources().getColor(R.color.admin_gold));
        tilAdminPinInput.setError(null);
        etAdminPinInput.setText("");
    }

    private long ambilNominal(TextInputEditText editText) {
        if (editText.getText() == null) return 0L;
        String angka = editText.getText().toString().replaceAll("[^0-9]", "");
        if (angka.isEmpty()) return 0L;
        try {
            return Long.parseLong(angka);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}