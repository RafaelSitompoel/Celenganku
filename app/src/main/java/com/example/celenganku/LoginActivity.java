package com.example.celenganku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUser;
    private TextInputLayout tilPass;
    private TextInputEditText etUser;
    private TextInputEditText etPass;
    private TextView tvPeringatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tilUser = findViewById(R.id.tilUser);
        tilPass = findViewById(R.id.tilPass);
        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        tvPeringatan = findViewById(R.id.tvPeringatan);

        MaterialButton btnMasuk = findViewById(R.id.btnMasuk);
        TextView tvKeDaftar = findViewById(R.id.tvKeDaftar);

        PasswordToggle.pasang(tilPass, etPass);

        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sembunyikanKeyboard();
                prosesMasuk();
            }
        });

        tvKeDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void prosesMasuk() {
        if (MaintenanceHelper.isMaintenanceActive(this)) {
            Intent intent = new Intent(LoginActivity.this, MaintenanceActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (!NetworkUtils.isOnline(this)) {
            Intent intent = new Intent(LoginActivity.this, NoInternetActivity.class);
            startActivity(intent);
            return;
        }

        tilUser.setError(null);
        tilPass.setError(null);
        tvPeringatan.setVisibility(View.GONE);

        String user = "";
        if (etUser.getText() != null) {
            user = etUser.getText().toString().trim();
        }
        String pass = "";
        if (etPass.getText() != null) {
            pass = etPass.getText().toString();
        }

        boolean valid = true;
        if (user.isEmpty()) {
            tilUser.setError(getString(R.string.error_user_empty));
            valid = false;
        }
        if (pass.isEmpty()) {
            tilPass.setError(getString(R.string.error_pass_empty));
            valid = false;
        }
        if (!valid) {
            return;
        }

        if (!UserStore.adaAkun(this)) {
            tvPeringatan.setText(R.string.warn_no_account);
            tvPeringatan.setVisibility(View.VISIBLE);
            return;
        }

        if (UserStore.cocok(this, user, pass)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            tvPeringatan.setText(R.string.warn_wrong_login);
            tvPeringatan.setVisibility(View.VISIBLE);
        }
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