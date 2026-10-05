package com.example.celenganku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        MaterialButton btnRetry = findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(v -> periksaKoneksi());
    }

    private void periksaKoneksi() {
        if (NetworkUtils.isOnline(this)) {
            Toast.makeText(this, "Terhubung ke internet!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NoInternetActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Masih offline. Harap aktifkan Wi-Fi rumah atau data seluler.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtils.isOnline(this)) {
            Intent intent = new Intent(NoInternetActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
    }
}