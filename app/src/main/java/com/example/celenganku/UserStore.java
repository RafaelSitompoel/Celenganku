package com.example.celenganku;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;

public final class UserStore {

    private static final String NAMA_PREF = "celenganku_akun";
    private static final String AWAL_HASH = "hash_";
    private static final String AWAL_SALT = "salt_";

    private UserStore() {
    }

    private static SharedPreferences pref(Context context) {
        return context.getSharedPreferences(NAMA_PREF, Context.MODE_PRIVATE);
    }

    private static String kunci(String namaPengguna) {
        return namaPengguna.trim().toLowerCase(Locale.ROOT);
    }

    public static boolean adaAkun(Context context) {
        return !pref(context).getAll().isEmpty();
    }

    public static boolean penggunaSudahAda(Context context, String namaPengguna) {
        return pref(context).contains(AWAL_HASH + kunci(namaPengguna));
    }

    public static boolean daftar(Context context, String namaPengguna, String kataSandi) {
        if (penggunaSudahAda(context, namaPengguna)) {
            return false;
        }

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        String hash = buatHash(kataSandi, salt);
        if (hash == null) {
            return false;
        }

        String k = kunci(namaPengguna);
        pref(context).edit()
                .putString(AWAL_SALT + k, Base64.encodeToString(salt, Base64.NO_WRAP))
                .putString(AWAL_HASH + k, hash)
                .apply();
        return true;
    }

    public static boolean cocok(Context context, String namaPengguna, String kataSandi) {
        String k = kunci(namaPengguna);
        String saltTersimpan = pref(context).getString(AWAL_SALT + k, null);
        String hashTersimpan = pref(context).getString(AWAL_HASH + k, null);

        if (saltTersimpan == null || hashTersimpan == null) {
            return false;
        }

        byte[] salt = Base64.decode(saltTersimpan, Base64.NO_WRAP);
        String hashBaru = buatHash(kataSandi, salt);
        if (hashBaru == null) {
            return false;
        }

        return MessageDigest.isEqual(
                hashBaru.getBytes(StandardCharsets.UTF_8),
                hashTersimpan.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean ubahKataSandi(Context context, String namaPengguna, String kataSandiBaru) {
        if (!penggunaSudahAda(context, namaPengguna)) {
            return false;
        }

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        String hash = buatHash(kataSandiBaru, salt);
        if (hash == null) {
            return false;
        }

        String k = kunci(namaPengguna);
        pref(context).edit()
                .putString(AWAL_SALT + k, Base64.encodeToString(salt, Base64.NO_WRAP))
                .putString(AWAL_HASH + k, hash)
                .apply();
        return true;
    }

    private static String buatHash(String kataSandi, byte[] salt) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(salt);
            byte[] hasil = sha.digest(kataSandi.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(hasil, Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }
}