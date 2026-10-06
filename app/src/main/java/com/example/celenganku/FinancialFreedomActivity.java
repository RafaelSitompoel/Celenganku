package com.example.celenganku;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;

public class FinancialFreedomActivity extends AppCompatActivity {

    private TextInputEditText etFreedomMonthly;
    private TextInputEditText etFreedomCurrent;
    private TextView tvResultFreedom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_freedom);

        View btnBack = findViewById(R.id.btnBackFreedom);
        btnBack.setOnClickListener(v -> finish());

        etFreedomMonthly = findViewById(R.id.etFreedomMonthly);
        etFreedomCurrent = findViewById(R.id.etFreedomCurrent);
        tvResultFreedom = findViewById(R.id.tvResultFreedom);

        MaterialButton btnCalcFreedom = findViewById(R.id.btnCalcFreedom);
        btnCalcFreedom.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungKebebasan();
        });
    }

    private void hitungKebebasan() {
        long monthlyExpense = ambilNominal(etFreedomMonthly);
        long currentSavings = ambilNominal(etFreedomCurrent);

        if (monthlyExpense <= 0) {
            tvResultFreedom.setText("Masukkan pengeluaran bulanan lebih dari 0.");
            tvResultFreedom.setVisibility(View.VISIBLE);
            return;
        }

        long annualExpense = monthlyExpense * 12L;
        long targetNetWorth = annualExpense * 25L; // Rule of 25
        long gap = Math.max(0L, targetNetWorth - currentSavings);
        int percentage = (int) Math.min(100L, (currentSavings * 100L) / Math.max(1L, targetNetWorth));

        String hasil = "🎯 **Hasil Simulasi Kebebasan Finansial:**\n" +
                "• Pengeluaran Tahunan: " + formatRupiah(annualExpense) + "\n" +
                "• Target Kekayaan (Rule of 25): " + formatRupiah(targetNetWorth) + "\n" +
                "• Tabungan Saat Ini: " + formatRupiah(currentSavings) + " (" + percentage + "% tercapai)\n" +
                "• Kekurangan Dana: " + formatRupiah(gap) + "\n\n" +
                (gap <= 0 ? "🎉 Selamat! Anda telah mencapai kebebasan finansial!" : "💡 Tips: Sisihkan pendapatan secara rutin dan investasikan pada instrumen dengan imbal hasil stabil.");

        tvResultFreedom.setText(hasil);
        tvResultFreedom.setVisibility(View.VISIBLE);
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

    private void sembunyikanKeyboard() {
        View fokus = getCurrentFocus();
        if (fokus != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(fokus.getWindowToken(), 0);
            }
            fokus.clearFocus();
        }
    }
}