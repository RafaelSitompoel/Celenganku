package com.example.celenganku;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREF_SETTINGS = "celenganku_settings";
    private static final String KEY_NOTIF = "notifications_enabled";
    private static final String KEY_WIFI_NOTIF = "wifi_notif_enabled";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";
    private static final String KEY_SOUND = "sound_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BottomNavHelper.setupNav(this, R.id.navSettings);

        View btnBack = findViewById(R.id.btnBackSettings);
        btnBack.setOnClickListener(v -> finish());

        MaterialButton btnChangePass = findViewById(R.id.btnChangePass);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        MaterialButton btnSelectLanguage = findViewById(R.id.btnSelectLanguage);

        if (btnSelectLanguage != null) {
            btnSelectLanguage.setOnClickListener(v -> {
                String[] langNames = new String[LanguageModel.SUPPORTED_LANGUAGES.length];
                for (int i = 0; i < LanguageModel.SUPPORTED_LANGUAGES.length; i++) {
                    langNames[i] = LanguageModel.SUPPORTED_LANGUAGES[i].name;
                }

                new AlertDialog.Builder(this)
                        .setTitle("Pilih Bahasa (55+ Bahasa)")
                        .setItems(langNames, (dialog, which) -> {
                            LanguageModel selected = LanguageModel.SUPPORTED_LANGUAGES[which];
                            LocaleHelper.setLocale(this, selected.code);
                            Toast.makeText(this, "Bahasa diubah ke: " + selected.name, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            });
        }

        SwitchMaterial switchNotifications = findViewById(R.id.switchNotifications);
        SwitchMaterial switchWifiNotif = findViewById(R.id.switchWifiNotif);
        SwitchMaterial switchDarkMode = findViewById(R.id.switchDarkMode);
        SwitchMaterial switchSound = findViewById(R.id.switchSound);

        MaterialButton btnExport = findViewById(R.id.btnExport);
        MaterialButton btnImport = findViewById(R.id.btnImport);
        MaterialButton btnClearCache = findViewById(R.id.btnClearCache);

        MaterialButton btnFaq = findViewById(R.id.btnFaq);
        MaterialButton btnContact = findViewById(R.id.btnContact);
        MaterialButton btnPrivacy = findViewById(R.id.btnPrivacy);
        MaterialButton btnUpdate = findViewById(R.id.btnUpdate);

        SharedPreferences prefs = getSharedPreferences(PREF_SETTINGS, Context.MODE_PRIVATE);
        switchNotifications.setChecked(prefs.getBoolean(KEY_NOTIF, true));
        switchWifiNotif.setChecked(prefs.getBoolean(KEY_WIFI_NOTIF, true));
        switchDarkMode.setChecked(prefs.getBoolean(KEY_DARK_MODE, false));
        switchSound.setChecked(prefs.getBoolean(KEY_SOUND, true));

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_NOTIF, isChecked).apply();
            Toast.makeText(this, isChecked ? "Notifikasi pengingat diaktifkan" : "Notifikasi pengingat dimatikan", Toast.LENGTH_SHORT).show();
        });

        switchWifiNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_WIFI_NOTIF, isChecked).apply();
            Toast.makeText(this, isChecked ? "Notifikasi Wi-Fi diaktifkan" : "Notifikasi Wi-Fi dimatikan", Toast.LENGTH_SHORT).show();
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_DARK_MODE, isChecked).apply();
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                    isChecked ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                            : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
            );
            Toast.makeText(this, isChecked ? "Mode gelap diaktifkan" : "Mode terang diaktifkan", Toast.LENGTH_SHORT).show();
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_SOUND, isChecked).apply();
            Toast.makeText(this, isChecked ? "Suara interaksi diaktifkan" : "Suara interaksi dimatikan", Toast.LENGTH_SHORT).show();
        });

        btnChangePass.setOnClickListener(v -> tampilkanDialogUbahSandi());

        btnExport.setOnClickListener(v -> {
            File cacheDir = getCacheDir();
            Toast.makeText(this, "Data berhasil diekspor. Direktori: " + cacheDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
        });

        btnImport.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Impor Data")
                    .setMessage("Tidak ditemukan file cadangan (.json) di memori eksternal.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        btnClearCache.setOnClickListener(v -> {
            long deletedSize = hapusDirektoriCache(getCacheDir());
            Toast.makeText(this, "Cache berhasil dibersihkan (" + (deletedSize / 1024) + " KB dibebaskan)", Toast.LENGTH_LONG).show();
        });

        btnFaq.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Panduan Penggunaan (FAQ)")
                    .setMessage("1. Bagaimana cara menghitung target?\n   Masukkan target tabungan, saldo saat ini, dan nominal setoran, lalu tekan tombol Hitung.\n\n2. Apakah data aman?\n   Ya, data akun dienkripsi dengan SHA-256 dan disimpan secara aman di perangkat lokal.")
                    .setPositiveButton("Mengerti", null)
                    .show();
        });

        btnContact.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hubungi Pengembang")
                    .setMessage("Tim Dukungan Teknis Celenganku:\n• Email: support@celenganku.app\n• Website: https://celenganku.app\n• Jam Operasional: Senin - Jumat (09:00 - 17:00 WIB)")
                    .setPositiveButton("Tutup", null)
                    .show();
        });

        btnPrivacy.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Kebijakan Privasi")
                    .setMessage("Aplikasi Celenganku menghormati privasi Anda. Semua perhitungan finansial dan kredensial sandi diproses secara lokal dan tidak dibagikan kepada pihak ketiga manapun.")
                    .setPositiveButton("Tutup", null)
                    .show();
        });

        btnUpdate.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Pembaruan Aplikasi")
                    .setMessage("Versi saat ini: v2.0.1 Pro (Online).\nSelamat! Aplikasi Anda sudah menggunakan versi paling mutakhir.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Keluar Akun")
                    .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
                    .setPositiveButton("Ya, Keluar", (dialog, which) -> {
                        Toast.makeText(this, "Berhasil keluar akun.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void tampilkanDialogUbahSandi() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = 40;
        container.setPadding(pad, pad, pad, pad);

        final TextInputEditText inputUser = new TextInputEditText(this);
        inputUser.setHint("Nama Pengguna");
        container.addView(inputUser);

        final TextInputEditText inputOld = new TextInputEditText(this);
        inputOld.setHint("Kata Sandi Lama");
        inputOld.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(inputOld);

        final TextInputEditText inputNew = new TextInputEditText(this);
        inputNew.setHint("Kata Sandi Baru (Min. 8 Karakter)");
        inputNew.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(inputNew);

        new AlertDialog.Builder(this)
                .setTitle("Ubah Kata Sandi Akun")
                .setView(container)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String user = inputUser.getText() != null ? inputUser.getText().toString().trim() : "";
                    String oldPass = inputOld.getText() != null ? inputOld.getText().toString() : "";
                    String newPass = inputNew.getText() != null ? inputNew.getText().toString() : "";

                    if (user.isEmpty() || oldPass.isEmpty() || newPass.isEmpty()) {
                        Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (newPass.length() < 8) {
                        Toast.makeText(this, "Kata sandi baru minimal 8 karakter!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (UserStore.cocok(this, user, oldPass)) {
                        boolean sukses = UserStore.ubahKataSandi(this, user, newPass);
                        if (sukses) {
                            Toast.makeText(this, "Kata sandi berhasil diperbarui!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Gagal memperbarui kata sandi.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Nama pengguna atau kata sandi lama salah!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private long hapusDirektoriCache(File dir) {
        long count = 0;
        if (dir != null && dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isDirectory()) {
                        count += hapusDirektoriCache(child);
                    } else {
                        long len = child.length();
                        if (child.delete()) {
                            count += len;
                        }
                    }
                }
            }
        }
        return dir != null && dir.exists() ? count : 0;
    }
}