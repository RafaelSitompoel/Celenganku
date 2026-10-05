package com.example.celenganku;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;

public class FinancialToolsActivity extends AppCompatActivity {

    private TextInputEditText etMonthlyExpense;
    private TextView tvResultEmergency;

    private TextInputEditText etPresentValue;
    private TextInputEditText etInflationRate;
    private TextInputEditText etFutureYears;
    private TextView tvResultInflation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_tools);

        View btnBack = findViewById(R.id.btnBackTools);
        btnBack.setOnClickListener(v -> finish());

        etMonthlyExpense = findViewById(R.id.etMonthlyExpense);
        tvResultEmergency = findViewById(R.id.tvResultEmergency);

        etPresentValue = findViewById(R.id.etPresentValue);
        etInflationRate = findViewById(R.id.etInflationRate);
        etFutureYears = findViewById(R.id.etFutureYears);
        tvResultInflation = findViewById(R.id.tvResultInflation);

        MaterialButton btnCalcEmergency = findViewById(R.id.btnCalcEmergency);
        MaterialButton btnCalcInflation = findViewById(R.id.btnCalcInflation);

        btnCalcEmergency.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungDanaDarurat();
        });

        btnCalcInflation.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungInflasi();
        });
    }

    private void hitungDanaDarurat() {
        long expense = ambilNominal(etMonthlyExpense);
        if (expense <= 0) {
            tvResultEmergency.setText("Masukkan nominal pengeluaran bulanan lebih dari 0.");
            tvResultEmergency.setVisibility(View.VISIBLE);
            return;
        }

        long m3 = expense * 3L;
        long m6 = expense * 6L;
        long m12 = expense * 12L;

        tvResultEmergency.setText(getString(
                R.string.result_emergency,
                formatRupiah(m3),
                formatRupiah(m6),
                formatRupiah(m12)));
        tvResultEmergency.setVisibility(View.VISIBLE);
    }

    private void hitungInflasi() {
        long presentValue = ambilNominal(etPresentValue);
        double rate = ambilDouble(etInflationRate, 4.5);
        long years = ambilNominal(etFutureYears);
        if (years <= 0) years = 5L;

        if (presentValue <= 0) {
            tvResultInflation.setText("Masukkan nilai uang sekarang lebih dari 0.");
            tvResultInflation.setVisibility(View.VISIBLE);
            return;
        }

        double futureValue = presentValue * Math.pow(1 + (rate / 100.0), years);

        tvResultInflation.setText(getString(
                R.string.result_inflation,
                years,
                rate,
                formatRupiah((long) futureValue)));
        tvResultInflation.setVisibility(View.VISIBLE);
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

    private double ambilDouble(TextInputEditText editText, double defaultValue) {
        if (editText.getText() == null) return defaultValue;
        try {
            String val = editText.getText().toString().trim();
            if (val.isEmpty()) return defaultValue;
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private String formatRupiah(long nilai) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        nf.setMaximumFractionDigits(0);
        return nf.format(nilai);
    }

    private void sembunyikanKeyboard() {
        View fokus = getCurrentFocus();
        if (fokus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(fokus.getWindowToken(), 0);
            }
            fokus.clearFocus();
        }
    }
}