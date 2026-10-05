package com.example.celenganku;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private static final long DURASI_SPLASH_MS = 5L * 60L * 1000L;

    private ImageView ivLoading;
    private TextView tvMarquee;
    private TextView tvSisaWaktu;

    private CountDownTimer timer;
    private boolean sudahPindah = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MaintenanceHelper.isMaintenanceActive(this)) {
            Intent intent = new Intent(SplashActivity.this, MaintenanceActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (!NetworkUtils.isOnline(this)) {
            Intent intent = new Intent(SplashActivity.this, NoInternetActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);

        ivLoading = findViewById(R.id.ivLoading);
        tvMarquee = findViewById(R.id.tvMarquee);
        tvSisaWaktu = findViewById(R.id.tvSisaWaktu);
        MaterialButton btnLewati = findViewById(R.id.btnLewati);

        if (ivLoading != null) {
            mulaiAnimasiLoading();
        }

        if (tvMarquee != null) {
            tvMarquee.setSelected(true);
        }

        if (btnLewati != null) {
            btnLewati.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bukaHalamanUtama();
                }
            });
        }

        timer = new CountDownTimer(DURASI_SPLASH_MS, 1000L) {
            @Override
            public void onTick(long sisaMs) {
                if (isFinishing() || isDestroyed()) return;
                long totalDetik = sisaMs / 1000L;
                long menit = totalDetik / 60L;
                long detik = totalDetik % 60L;
                String waktu = String.format(Locale.getDefault(), "%02d:%02d", menit, detik);
                if (tvSisaWaktu != null) {
                    tvSisaWaktu.setText(getString(R.string.splash_remaining, waktu));
                }
            }

            @Override
            public void onFinish() {
                bukaHalamanUtama();
            }
        };
        timer.start();
    }

    private void mulaiAnimasiLoading() {
        RotateAnimation putar = new RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        putar.setDuration(1000L);
        putar.setRepeatCount(Animation.INFINITE);
        putar.setInterpolator(new LinearInterpolator());
        ivLoading.startAnimation(putar);
    }

    private void bukaHalamanUtama() {
        if (sudahPindah) {
            return;
        }
        sudahPindah = true;

        if (timer != null) {
            timer.cancel();
        }
        if (ivLoading != null) {
            ivLoading.clearAnimation();
        }

        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }
}