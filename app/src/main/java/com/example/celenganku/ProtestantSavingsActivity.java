package com.example.celenganku;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;

public class ProtestantSavingsActivity extends AppCompatActivity {

    private TextInputEditText etProtestantIncome;
    private TextView tvResultProtestant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protestant_savings);

        View btnBack = findViewById(R.id.btnBackProtestant);
        btnBack.setOnClickListener(v -> finish());

        etProtestantIncome = findViewById(R.id.etProtestantIncome);
        tvResultProtestant = findViewById(R.id.tvResultProtestant);

        MaterialButton btnCalcProtestant = findViewById(R.id.btnCalcProtestant);
        btnCalcProtestant.setOnClickListener(v -> hitungProtestan());
    }

    private void hitungProtestan() {
        long income = ambilNominal(etProtestantIncome);
        if (income <= 0) {
            tvResultProtestant.setText("Masukkan penghasilan bulanan lebih dari 0.");
            tvResultProtestant.setVisibility(View.VISIBLE);
            return;
        }

        long tithe = (long) (income * 0.10); // Persepuluhan 10%
        long diakonia = (long) (income * 0.05); // Diakonia 5%

        tvResultProtestant.setText("Alokasi Persembahan & Pelayanan:\n• Persepuluhan (10%): " + formatRupiah(tithe) + "\n• Dana Diakonia / Kasih (5%): " + formatRupiah(diakonia) + "\nTuhan memberkati persembahan sukarela Anda.");
        tvResultProtestant.setVisibility(View.VISIBLE);
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