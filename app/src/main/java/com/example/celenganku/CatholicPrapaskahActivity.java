package com.example.celenganku;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;

public class CatholicPrapaskahActivity extends AppCompatActivity {

    private TextInputEditText etCatholicDaily;
    private TextView tvResultCatholic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catholic_prapaskah);

        View btnBack = findViewById(R.id.btnBackCatholic);
        btnBack.setOnClickListener(v -> finish());

        etCatholicDaily = findViewById(R.id.etCatholicDaily);
        tvResultCatholic = findViewById(R.id.tvResultCatholic);

        MaterialButton btnCalcCatholic = findViewById(R.id.btnCalcCatholic);
        btnCalcCatholic.setOnClickListener(v -> hitungPrapaskah());
    }

    private void hitungPrapaskah() {
        long daily = ambilNominal(etCatholicDaily);
        if (daily <= 0) {
            tvResultCatholic.setText("Masukkan nominal derma harian lebih dari 0.");
            tvResultCatholic.setVisibility(View.VISIBLE);
            return;
        }

        long total40 = daily * 40L;
        tvResultCatholic.setText("Total Tabungan Aksi Puasa Pembangunan (APP) Selama 40 Hari Prapaskah: " + formatRupiah(total40) + "\nSemoga derma dan pantang kita berkenan di hadapan Tuhan.");
        tvResultCatholic.setVisibility(View.VISIBLE);
    }

    private long ambilNominal(TextInputEditText editText) {
        if (editText.getText() == null) return 0L;
        String angka = editText.getText().toString().replaceAll("[^0-9]", "");
        if (angka.isEmpty()) return 0L;
        try {
            return Long.parseLong(angka);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String formatRupiah(long nilai) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        nf.setMaximumFractionDigits(0);
        return nf.format(nilai);
    }
}