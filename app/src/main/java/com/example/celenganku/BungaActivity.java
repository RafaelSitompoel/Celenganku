package com.example.celenganku;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class BungaActivity extends AppCompatActivity {

    private TextInputLayout tilModal;
    private TextInputLayout tilBunga;
    private TextInputLayout tilTahun;

    private TextInputEditText etModal;
    private TextInputEditText etSetoran;
    private TextInputEditText etBunga;
    private TextInputEditText etTahun;

    private View layoutHasil;
    private TextView tvHasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bunga);

        tilModal = findViewById(R.id.tilModal);
        tilBunga = findViewById(R.id.tilBunga);
        tilTahun = findViewById(R.id.tilTahun);

        etModal = findViewById(R.id.etModal);
        etSetoran = findViewById(R.id.etSetoran);
        etBunga = findViewById(R.id.etBunga);
        etTahun = findViewById(R.id.etTahun);

        layoutHasil = findViewById(R.id.layoutHasil);
        tvHasil = findViewById(R.id.tvHasil);

        View btnBack = findViewById(R.id.btnBack);
        MaterialButton btnHitung = findViewById(R.id.btnHitung);
        MaterialButton btnBersihkan = findViewById(R.id.btnBersihkan);

        btnBack.setOnClickListener(v -> finish());

        btnHitung.setOnClickListener(v -> {
            Util.sembunyikanKeyboard(BungaActivity.this);
            hitung();
        });

        btnBersihkan.setOnClickListener(v -> bersihkan());
    }

    private void hitung() {
        tilModal.setError(null);
        tilBunga.setError(null);
        tilTahun.setError(null);

        double modal = Util.ambil(etModal);
        double setoran = Util.ambil(etSetoran);
        double bunga = Util.ambil(etBunga);
        int tahun = (int) Math.round(Util.ambil(etTahun));

        boolean valid = true;

        if (modal <= 0 && setoran <= 0) {
            tilModal.setError(getString(R.string.error_modal));
            valid = false;
        }
        if (bunga < 0 || bunga > 100) {
            tilBunga.setError(getString(R.string.error_bunga_tabungan));
            valid = false;
        }
        if (tahun < 1 || tahun > 60) {
            tilTahun.setError(getString(R.string.error_lama_tahun));
            valid = false;
        }

        if (!valid) {
            layoutHasil.setVisibility(View.GONE);
            return;
        }

        int bulan = tahun * 12;
        double rb = bunga / 100d / 12d;
        double saldo = modal;
        StringBuilder rincian = new StringBuilder();

        for (int i = 1; i <= bulan; i++) {
            saldo = saldo * (1d + rb) + setoran;
            if (i % 12 == 0) {
                int th = i / 12;
                if (th == 1 || th % 5 == 0 || th == tahun) {
                    rincian.append(getString(R.string.line_year, th, Util.rupiah(saldo)));
                    rincian.append('\n');
                }
            }
        }

        double totalSetoran = modal + setoran * bulan;
        double bungaDidapat = saldo - totalSetoran;

        tvHasil.setText(getString(
                R.string.result_bunga,
                Util.rupiah(totalSetoran),
                Util.rupiah(bungaDidapat),
                Util.rupiah(saldo),
                rincian.toString().trim()));
        layoutHasil.setVisibility(View.VISIBLE);
    }

    private void bersihkan() {
        etModal.setText("");
        etSetoran.setText("");
        etBunga.setText("");
        etTahun.setText("");
        tilModal.setError(null);
        tilBunga.setError(null);
        tilTahun.setError(null);
        layoutHasil.setVisibility(View.GONE);
        etModal.requestFocus();
    }
}