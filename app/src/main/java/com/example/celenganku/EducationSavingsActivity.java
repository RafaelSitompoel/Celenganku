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

public class EducationSavingsActivity extends AppCompatActivity {

    private TextInputEditText etEduCostCurrent;
    private TextInputEditText etEduYears;
    private TextInputEditText etEduInflation;
    private TextView tvResultEducation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_savings);

        View btnBack = findViewById(R.id.btnBackEducation);
        btnBack.setOnClickListener(v -> finish());

        etEduCostCurrent = findViewById(R.id.etEduCostCurrent);
        etEduYears = findViewById(R.id.etEduYears);
        etEduInflation = findViewById(R.id.etEduInflation);
        tvResultEducation = findViewById(R.id.tvResultEducation);

        MaterialButton btnCalcEducation = findViewById(R.id.btnCalcEducation);
        btnCalcEducation.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungPendidikan();
        });
    }

    private void hitungPendidikan() {
        long currentCost = ambilNominal(etEduCostCurrent);
        long years = ambilNominal(etEduYears);
        if (years <= 0) years = 1L;
        double inflationRate = ambilDouble(etEduInflation);
        if (inflationRate <= 0) inflationRate = 10.0;

        if (currentCost <= 0) {
            tvResultEducation.setText("Masukkan biaya pendidikan saat ini lebih dari 0.");
            tvResultEducation.setVisibility(View.VISIBLE);
            return;
        }

        double futureCostDbl = currentCost * Math.pow(1.0 + (inflationRate / 100.0), years);
        long futureCost = (long) futureCostDbl;
        long totalMonths = years * 12L;
        long monthlyDeposit = futureCost / totalMonths;

        String hasil = "🎓 **Proyeksi Dana Pendidikan Anak:**\n" +
                "• Biaya Saat Ini: " + formatRupiah(currentCost) + "\n" +
                "• Perkiraan Biaya Masuk (" + years + " Tahun Lagi, Inflasi " + (int) inflationRate + "%): " + formatRupiah(futureCost) + "\n" +
                "• Setoran Wajib per Bulan: " + formatRupiah(monthlyDeposit) + " selama " + totalMonths + " bulan.\n\n" +
                "💡 **Saran:** Mulailah menyisihkan dana pendidikan sejak dini agar masa depan pendidikan anak terjamin!";

        tvResultEducation.setText(hasil);
        tvResultEducation.setVisibility(View.VISIBLE);
    }

    private long ambilNominal(TextInputEditText editText) {
        if (editText == null || editText.getText() == null) return 0L;
        String angka = editText.getText().toString().replaceAll("[^0-9]", "");
        if (angka.isEmpty()) return 0L;
        try {
            return Long.parseLong(angka);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private double ambilDouble(TextInputEditText editText) {
        if (editText == null || editText.getText() == null) return 0.0;
        String val = editText.getText().toString().trim().replace(',', '.');
        if (val.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return 0.0;
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