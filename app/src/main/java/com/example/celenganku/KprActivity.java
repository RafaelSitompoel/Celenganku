package com.example.celenganku;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class KprActivity extends AppCompatActivity {

    private TextInputLayout tilHarga;
    private TextInputLayout tilDp;
    private TextInputLayout tilBunga;
    private TextInputLayout tilTenor;
    private TextInputLayout tilKenaikan;

    private TextInputEditText etHarga;
    private TextInputEditText etDp;
    private TextInputEditText etBunga;
    private TextInputEditText etTenor;
    private TextInputEditText etKenaikan;
    private TextInputEditText etSewa;

    private View layoutHasil;
    private TextView tvHasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpr);

        tilHarga = findViewById(R.id.tilHarga);
        tilDp = findViewById(R.id.tilDp);
        tilBunga = findViewById(R.id.tilBunga);
        tilTenor = findViewById(R.id.tilTenor);
        tilKenaikan = findViewById(R.id.tilKenaikan);

        etHarga = findViewById(R.id.etHarga);
        etDp = findViewById(R.id.etDp);
        etBunga = findViewById(R.id.etBunga);
        etTenor = findViewById(R.id.etTenor);
        etKenaikan = findViewById(R.id.etKenaikan);
        etSewa = findViewById(R.id.etSewa);

        layoutHasil = findViewById(R.id.layoutHasil);
        tvHasil = findViewById(R.id.tvHasil);

        View btnBack = findViewById(R.id.btnBack);
        MaterialButton btnHitung = findViewById(R.id.btnHitung);
        MaterialButton btnBersihkan = findViewById(R.id.btnBersihkan);

        btnBack.setOnClickListener(v -> finish());

        btnHitung.setOnClickListener(v -> {
            Util.sembunyikanKeyboard(KprActivity.this);
            hitung();
        });

        btnBersihkan.setOnClickListener(v -> bersihkan());
    }

    private void hitung() {
        tilHarga.setError(null);
        tilDp.setError(null);
        tilBunga.setError(null);
        tilTenor.setError(null);
        tilKenaikan.setError(null);

        double harga = Util.ambil(etHarga);
        double dpPersen = Util.ambil(etDp);
        double bunga = Util.ambil(etBunga);
        int tenor = (int) Math.round(Util.ambil(etTenor));
        double kenaikan = Util.ambil(etKenaikan);
        double sewa = Util.ambil(etSewa);

        boolean valid = true;

        if (harga <= 0) {
            tilHarga.setError(getString(R.string.error_harga_rumah));
            valid = false;
        }
        if (dpPersen < 0 || dpPersen > 90) {
            tilDp.setError(getString(R.string.error_dp));
            valid = false;
        }
        if (bunga < 0 || bunga > 50) {
            tilBunga.setError(getString(R.string.error_bunga_kpr));
            valid = false;
        }
        if (tenor < 1 || tenor > 40) {
            tilTenor.setError(getString(R.string.error_tenor));
            valid = false;
        }
        if (kenaikan < 0 || kenaikan > 50) {
            tilKenaikan.setError(getString(R.string.error_kenaikan));
            valid = false;
        }

        if (!valid) {
            layoutHasil.setVisibility(View.GONE);
            return;
        }

        int bulan = tenor * 12;
        double dp = harga * dpPersen / 100d;
        double pokok = harga - dp;
        double r = bunga / 100d / 12d;

        double cicilan;
        if (r == 0d) {
            cicilan = pokok / bulan;
        } else {
            cicilan = pokok * r / (1d - Math.pow(1d + r, -bulan));
        }

        double totalCicilan = cicilan * bulan;
        double totalBunga = totalCicilan - pokok;
        double nilaiAkhir = harga * Math.pow(1d + kenaikan / 100d, tenor);
        double totalSewa = sewa * 12d * tenor;
        double yield = sewa * 12d / harga * 100d;
        double totalModal = dp + totalCicilan;
        double hasilBersih = nilaiAkhir + totalSewa - totalModal;

        String labelHasil;
        if (hasilBersih >= 0) {
            labelHasil = getString(R.string.label_untung);
        } else {
            labelHasil = getString(R.string.label_rugi);
        }

        tvHasil.setText(getString(
                R.string.result_rumah,
                Util.rupiah(dp),
                Util.rupiah(pokok),
                Util.rupiah(cicilan),
                Util.rupiah(totalBunga),
                Util.rupiah(totalCicilan),
                String.valueOf(tenor),
                Util.rupiah(nilaiAkhir),
                Util.persen(yield),
                Util.rupiah(totalSewa),
                labelHasil,
                Util.rupiah(Math.abs(hasilBersih))));
        layoutHasil.setVisibility(View.VISIBLE);
    }

    private void bersihkan() {
        etHarga.setText("");
        etDp.setText("");
        etBunga.setText("");
        etTenor.setText("");
        etKenaikan.setText("");
        etSewa.setText("");
        tilHarga.setError(null);
        tilDp.setError(null);
        tilBunga.setError(null);
        tilTenor.setError(null);
        tilKenaikan.setError(null);
        layoutHasil.setVisibility(View.GONE);
        etHarga.requestFocus();
    }
}