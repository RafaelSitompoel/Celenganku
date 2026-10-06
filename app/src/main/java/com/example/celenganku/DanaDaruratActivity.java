package com.example.celenganku;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class DanaDaruratActivity extends AppCompatActivity {

    private TextInputLayout tilPengeluaran;
    private TextInputEditText etPengeluaran;
    private TextInputEditText etSaldo;
    private TextInputEditText etSetoran;

    private RadioGroup rgStatus;
    private View layoutHasil;
    private ProgressBar pbProgress;
    private TextView tvPersen;
    private TextView tvHasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_darurat);

        tilPengeluaran = findViewById(R.id.tilPengeluaran);
        etPengeluaran = findViewById(R.id.etPengeluaran);
        etSaldo = findViewById(R.id.etSaldo);
        etSetoran = findViewById(R.id.etSetoran);

        rgStatus = findViewById(R.id.rgStatus);
        layoutHasil = findViewById(R.id.layoutHasil);
        pbProgress = findViewById(R.id.pbProgress);
        tvPersen = findViewById(R.id.tvPersen);
        tvHasil = findViewById(R.id.tvHasil);

        View btnBack = findViewById(R.id.btnBack);
        MaterialButton btnHitung = findViewById(R.id.btnHitung);
        MaterialButton btnBersihkan = findViewById(R.id.btnBersihkan);

        btnBack.setOnClickListener(v -> finish());

        btnHitung.setOnClickListener(v -> {
            Util.sembunyikanKeyboard(DanaDaruratActivity.this);
            hitung();
        });

        btnBersihkan.setOnClickListener(v -> bersihkan());
    }

    private void hitung() {
        tilPengeluaran.setError(null);

        double pengeluaran = Util.ambil(etPengeluaran);
        double saldo = Util.ambil(etSaldo);
        double setoran = Util.ambil(etSetoran);

        if (pengeluaran <= 0) {
            tilPengeluaran.setError(getString(R.string.error_pengeluaran));
            layoutHasil.setVisibility(View.GONE);
            return;
        }

        int kali;
        String status;
        int terpilih = rgStatus.getCheckedRadioButtonId();
        if (terpilih == R.id.rbMenikah) {
            kali = 9;
            status = getString(R.string.status_menikah);
        } else if (terpilih == R.id.rbKeluarga) {
            kali = 12;
            status = getString(R.string.status_keluarga);
        } else {
            kali = 6;
            status = getString(R.string.status_lajang);
        }

        double target = pengeluaran * kali;
        double kurang = Math.max(0d, target - saldo);
        int persen = (int) Math.min(100d, saldo * 100d / target);

        pbProgress.setProgress(persen);
        tvPersen.setText(getString(R.string.label_progress, persen));

        StringBuilder sb = new StringBuilder();
        sb.append(getString(
                R.string.result_darurat_target,
                status,
                String.valueOf(kali),
                Util.rupiah(target),
                Util.rupiah(saldo),
                Util.rupiah(kurang)));

        if (kurang <= 0d) {
            sb.append("\n\n").append(getString(R.string.result_darurat_done));
        } else if (setoran > 0d) {
            long bulan = (long) Math.ceil(kurang / setoran);
            if (bulan > 1200L) {
                sb.append("\n\n").append(getString(R.string.result_darurat_too_long));
            } else {
                long tahun = bulan / 12L;
                long sisaBulan = bulan % 12L;
                sb.append("\n\n").append(getString(
                        R.string.result_darurat_time,
                        Util.rupiah(setoran),
                        String.valueOf(bulan),
                        String.valueOf(tahun),
                        String.valueOf(sisaBulan)));
            }
        } else {
            sb.append("\n\n").append(getString(R.string.result_darurat_hint));
        }

        tvHasil.setText(sb.toString());
        layoutHasil.setVisibility(View.VISIBLE);
    }

    private void bersihkan() {
        etPengeluaran.setText("");
        etSaldo.setText("");
        etSetoran.setText("");
        tilPengeluaran.setError(null);
        rgStatus.check(R.id.rbLajang);
        pbProgress.setProgress(0);
        layoutHasil.setVisibility(View.GONE);
        etPengeluaran.requestFocus();
    }
}