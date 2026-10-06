package com.example.celenganku;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class InflasiActivity extends AppCompatActivity {

    private TextInputLayout tilHarga;
    private TextInputLayout tilInflasi;
    private TextInputLayout tilTahun;

    private TextInputEditText etHarga;
    private TextInputEditText etInflasi;
    private TextInputEditText etTahun;

    private View layoutHasil;
    private TextView tvHasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inflasi);

        tilHarga = findViewById(R.id.tilHarga);
        tilInflasi = findViewById(R.id.tilInflasi);
        tilTahun = findViewById(R.id.tilTahun);

        etHarga = findViewById(R.id.etHarga);
        etInflasi = findViewById(R.id.etInflasi);
        etTahun = findViewById(R.id.etTahun);

        layoutHasil = findViewById(R.id.layoutHasil);
        tvHasil = findViewById(R.id.tvHasil);

        View btnBack = findViewById(R.id.btnBack);
        MaterialButton btnHitung = findViewById(R.id.btnHitung);
        MaterialButton btnBersihkan = findViewById(R.id.btnBersihkan);

        btnBack.setOnClickListener(v -> finish());

        btnHitung.setOnClickListener(v -> {
            Util.sembunyikanKeyboard(InflasiActivity.this);
            hitung();
        });

        btnBersihkan.setOnClickListener(v -> bersihkan());
    }

    private double hargaMasaDepan(double harga, double inflasi, int tahun) {
        return harga * Math.pow(1d + inflasi / 100d, tahun);
    }

    private void hitung() {
        tilHarga.setError(null);
        tilInflasi.setError(null);
        tilTahun.setError(null);

        double harga = Util.ambil(etHarga);
        double inflasi = Util.ambil(etInflasi);
        int tahun = (int) Math.round(Util.ambil(etTahun));

        boolean valid = true;

        if (harga <= 0) {
            tilHarga.setError(getString(R.string.error_harga_barang));
            valid = false;
        }
        if (inflasi < 0 || inflasi > 50) {
            tilInflasi.setError(getString(R.string.error_inflasi));
            valid = false;
        }
        if (tahun < 1 || tahun > 50) {
            tilTahun.setError(getString(R.string.error_tahun_inflasi));
            valid = false;
        }

        if (!valid) {
            layoutHasil.setVisibility(View.GONE);
            return;
        }

        double nanti = hargaMasaDepan(harga, inflasi, tahun);
        double kenaikan = nanti - harga;
        double dayaBeli = harga / Math.pow(1d + inflasi / 100d, tahun);

        tvHasil.setText(getString(
                R.string.result_inflasi,
                Util.rupiah(harga),
                String.valueOf(tahun),
                Util.rupiah(nanti),
                Util.rupiah(kenaikan),
                Util.rupiah(dayaBeli)));
        layoutHasil.setVisibility(View.VISIBLE);
    }

    private void bersihkan() {
        etHarga.setText("");
        etInflasi.setText("");
        etTahun.setText("");
        tilHarga.setError(null);
        tilInflasi.setError(null);
        tilTahun.setError(null);
        layoutHasil.setVisibility(View.GONE);
        etHarga.requestFocus();
    }
}