package com.example.celenganku;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private static final int MIN_USER = 4;
    private static final int MIN_PASS = 8;
    private static final int MAKS_PASS = 32;

    private TextInputLayout tilUser;
    private TextInputLayout tilPass;
    private TextInputLayout tilKonfirmasi;
    private TextInputEditText etUser;
    private TextInputEditText etPass;
    private TextInputEditText etKonfirmasi;

    private TextView tvPeringatan;
    private TextView ruleLength;
    private TextView ruleUpper;
    private TextView ruleLower;
    private TextView ruleDigit;
    private TextView ruleSymbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tilUser = findViewById(R.id.tilUser);
        tilPass = findViewById(R.id.tilPass);
        tilKonfirmasi = findViewById(R.id.tilKonfirmasi);
        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        etKonfirmasi = findViewById(R.id.etKonfirmasi);

        tvPeringatan = findViewById(R.id.tvPeringatan);
        ruleLength = findViewById(R.id.ruleLength);
        ruleUpper = findViewById(R.id.ruleUpper);
        ruleLower = findViewById(R.id.ruleLower);
        ruleDigit = findViewById(R.id.ruleDigit);
        ruleSymbol = findViewById(R.id.ruleSymbol);

        View btnBack = findViewById(R.id.btnBack);
        MaterialButton btnDaftar = findViewById(R.id.btnDaftar);
        TextView tvKeMasuk = findViewById(R.id.tvKeMasuk);

        PasswordToggle.pasang(tilPass, etPass);
        PasswordToggle.pasang(tilKonfirmasi, etKonfirmasi);

        perbaruiAturan("");

        etUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                periksaNamaPengguna(s.toString());
            }
        });

        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                perbaruiAturan(s.toString());
                periksaKonfirmasi();
            }
        });

        etKonfirmasi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                periksaKonfirmasi();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvKeMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sembunyikanKeyboard();
                prosesDaftar();
            }
        });
    }

    private void periksaNamaPengguna(String user) {
        if (user.isEmpty()) {
            tilUser.setError(null);
            return;
        }
        if (!user.matches("[A-Za-z0-9_]+")) {
            tilUser.setError(getString(R.string.warn_user_chars));
        } else if (user.length() < MIN_USER) {
            tilUser.setError(getString(R.string.warn_user_short));
        } else {
            tilUser.setError(null);
        }
    }

    private void periksaKonfirmasi() {
        String pass = teks(etPass);
        String konfirmasi = teks(etKonfirmasi);

        if (konfirmasi.isEmpty() || pass.equals(konfirmasi)) {
            tilKonfirmasi.setError(null);
        } else {
            tilKonfirmasi.setError(getString(R.string.warn_pass_mismatch));
        }
    }

    private void perbaruiAturan(String pass) {
        boolean panjang = pass.length() >= MIN_PASS && pass.length() <= MAKS_PASS;
        boolean besar = false;
        boolean kecil = false;
        boolean angka = false;
        boolean simbol = false;
        boolean adaSpasi = false;

        for (int i = 0; i < pass.length(); i++) {
            char c = pass.charAt(i);
            if (Character.isWhitespace(c)) {
                adaSpasi = true;
            } else if (Character.isUpperCase(c)) {
                besar = true;
            } else if (Character.isLowerCase(c)) {
                kecil = true;
            } else if (Character.isDigit(c)) {
                angka = true;
            } else {
                simbol = true;
            }
        }

        aturSyarat(ruleLength, panjang);
        aturSyarat(ruleUpper, besar);
        aturSyarat(ruleLower, kecil);
        aturSyarat(ruleDigit, angka);
        aturSyarat(ruleSymbol, simbol);

        boolean semuaTerpenuhi = panjang && besar && kecil && angka && simbol && !adaSpasi;

        if (adaSpasi) {
            tvPeringatan.setText(R.string.warn_pass_space);
            tvPeringatan.setVisibility(View.VISIBLE);
        } else if (!pass.isEmpty() && !semuaTerpenuhi) {
            tvPeringatan.setText(R.string.warn_pass_incomplete);
            tvPeringatan.setVisibility(View.VISIBLE);
        } else {
            tvPeringatan.setVisibility(View.GONE);
        }
    }

    private void aturSyarat(TextView tv, boolean terpenuhi) {
        int ikon = terpenuhi ? R.drawable.ic_check : R.drawable.ic_warning;
        int warna = ContextCompat.getColor(this, terpenuhi ? R.color.primary : R.color.warning);

        tv.setCompoundDrawablesRelativeWithIntrinsicBounds(ikon, 0, 0, 0);
        tv.setTextColor(warna);
        TextViewCompat.setCompoundDrawableTintList(tv, ColorStateList.valueOf(warna));
    }

    private boolean passwordValid(String pass) {
        if (pass.length() < MIN_PASS || pass.length() > MAKS_PASS) {
            return false;
        }
        boolean besar = false;
        boolean kecil = false;
        boolean angka = false;
        boolean simbol = false;

        for (int i = 0; i < pass.length(); i++) {
            char c = pass.charAt(i);
            if (Character.isWhitespace(c)) {
                return false;
            } else if (Character.isUpperCase(c)) {
                besar = true;
            } else if (Character.isLowerCase(c)) {
                kecil = true;
            } else if (Character.isDigit(c)) {
                angka = true;
            } else {
                simbol = true;
            }
        }
        return besar && kecil && angka && simbol;
    }

    private void prosesDaftar() {
        String user = teks(etUser).trim();
        String pass = teks(etPass);
        String konfirmasi = teks(etKonfirmasi);

        boolean valid = true;

        if (user.length() < MIN_USER || !user.matches("[A-Za-z0-9_]+")) {
            tilUser.setError(getString(R.string.warn_user_chars));
            valid = false;
        } else if (UserStore.penggunaSudahAda(this, user)) {
            tilUser.setError(getString(R.string.warn_user_exists));
            valid = false;
        }

        if (!passwordValid(pass)) {
            tilPass.setError(getString(R.string.warn_pass_incomplete_short));
            valid = false;
        } else {
            tilPass.setError(null);
        }

        if (!pass.equals(konfirmasi)) {
            tilKonfirmasi.setError(getString(R.string.warn_pass_mismatch));
            valid = false;
        }

        if (!valid) {
            return;
        }

        boolean berhasil = UserStore.daftar(this, user, pass);
        if (berhasil) {
            Toast.makeText(this, R.string.msg_register_success, Toast.LENGTH_LONG).show();
            finish();
        } else {
            tilUser.setError(getString(R.string.warn_user_exists));
        }
    }

    private String teks(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString();
    }

    private void sembunyikanKeyboard() {
        View fokus = getCurrentFocus();
        if (fokus != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(fokus.getWindowToken(), 0);
            }
            fokus.clearFocus();
        }
    }
}