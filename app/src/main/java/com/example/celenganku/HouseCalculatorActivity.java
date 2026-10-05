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

public class HouseCalculatorActivity extends AppCompatActivity {

    private TextInputEditText etHousePrice;
    private TextInputEditText etDpPercent;
    private TextInputEditText etInterestRate;
    private TextInputEditText etTenorYears;
    private TextView tvResultKpr;

    private TextInputEditText etAppreciationRate;
    private TextView tvResultProjection;

    private TextInputEditText etRentalIncome;
    private TextView tvResultYield;

    private TextInputEditText etTargetDp;
    private TextInputEditText etDpYears;
    private TextView tvResultDpSaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_calculator);

        View btnBack = findViewById(R.id.btnBackHouse);
        btnBack.setOnClickListener(v -> finish());

        etHousePrice = findViewById(R.id.etHousePrice);
        etDpPercent = findViewById(R.id.etDpPercent);
        etInterestRate = findViewById(R.id.etInterestRate);
        etTenorYears = findViewById(R.id.etTenorYears);
        tvResultKpr = findViewById(R.id.tvResultKpr);

        etAppreciationRate = findViewById(R.id.etAppreciationRate);
        tvResultProjection = findViewById(R.id.tvResultProjection);

        etRentalIncome = findViewById(R.id.etRentalIncome);
        tvResultYield = findViewById(R.id.tvResultYield);

        etTargetDp = findViewById(R.id.etTargetDp);
        etDpYears = findViewById(R.id.etDpYears);
        tvResultDpSaving = findViewById(R.id.tvResultDpSaving);

        MaterialButton btnCalcKpr = findViewById(R.id.btnCalcKpr);
        MaterialButton btnCalcProjection = findViewById(R.id.btnCalcProjection);
        MaterialButton btnCalcYield = findViewById(R.id.btnCalcYield);
        MaterialButton btnCalcDpSaving = findViewById(R.id.btnCalcDpSaving);

        btnCalcKpr.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungKpr();
        });

        btnCalcProjection.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungProyeksi();
        });

        btnCalcYield.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungYield();
        });

        btnCalcDpSaving.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungTabunganDp();
        });
    }

    private void hitungKpr() {
        long price = ambilNominal(etHousePrice);
        double dpPercent = ambilDouble(etDpPercent, 20.0);
        double interest = ambilDouble(etInterestRate, 8.5);
        long tenorYears = ambilNominal(etTenorYears);
        if (tenorYears <= 0) tenorYears = 15L;

        if (price <= 0) {
            tvResultKpr.setText("Masukkan harga rumah lebih dari 0");
            tvResultKpr.setVisibility(View.VISIBLE);
            return;
        }

        long dpAmount = (long) (price * (dpPercent / 100.0));
        long principal = price - dpAmount;

        double monthlyRate = (interest / 100.0) / 12.0;
        long totalMonths = tenorYears * 12L;

        double monthlyPayment;
        if (monthlyRate > 0) {
            monthlyPayment = principal * (monthlyRate * Math.pow(1 + monthlyRate, totalMonths)) / (Math.pow(1 + monthlyRate, totalMonths) - 1);
        } else {
            monthlyPayment = (double) principal / totalMonths;
        }

        long totalPayment = (long) (monthlyPayment * totalMonths);
        long totalInterest = totalPayment - principal;

        tvResultKpr.setText(getString(
                R.string.result_kpr,
                formatRupiah(principal),
                formatRupiah(dpAmount),
                formatRupiah((long) monthlyPayment),
                formatRupiah(totalInterest)));
        tvResultKpr.setVisibility(View.VISIBLE);
    }

    private void hitungProyeksi() {
        long price = ambilNominal(etHousePrice);
        double rate = ambilDouble(etAppreciationRate, 6.0) / 100.0;

        if (price <= 0) {
            tvResultProjection.setText("Masukkan harga rumah pada simulasi KPR di atas terlebih dahulu.");
            tvResultProjection.setVisibility(View.VISIBLE);
            return;
        }

        long year5 = (long) (price * Math.pow(1 + rate, 5));
        long year10 = (long) (price * Math.pow(1 + rate, 10));
        long year20 = (long) (price * Math.pow(1 + rate, 20));

        tvResultProjection.setText(getString(
                R.string.result_projection,
                formatRupiah(year5),
                formatRupiah(year10),
                formatRupiah(year20)));
        tvResultProjection.setVisibility(View.VISIBLE);
    }

    private void hitungYield() {
        long price = ambilNominal(etHousePrice);
        long rental = ambilNominal(etRentalIncome);

        if (price <= 0 || rental <= 0) {
            tvResultYield.setText("Masukkan harga rumah dan nominal pemasukan sewa per tahun.");
            tvResultYield.setVisibility(View.VISIBLE);
            return;
        }

        double yield = ((double) rental / price) * 100.0;
        String kategori;
        if (yield >= 7.0) {
            kategori = "Sangat Bagus (Di atas rata-rata pasar)";
        } else if (yield >= 4.0) {
            kategori = "Cukup Menguntungkan";
        } else {
            kategori = "Rendah (Pertimbangkan kenaikan harga sewa)";
        }

        tvResultYield.setText(getString(R.string.result_yield, yield, kategori));
        tvResultYield.setVisibility(View.VISIBLE);
    }

    private void hitungTabunganDp() {
        long targetDp = ambilNominal(etTargetDp);
        long dpYears = ambilNominal(etDpYears);
        if (dpYears <= 0) dpYears = 3L;

        if (targetDp <= 0) {
            tvResultDpSaving.setText("Masukkan target DP rumah lebih dari 0.");
            tvResultDpSaving.setVisibility(View.VISIBLE);
            return;
        }

        long totalMonths = dpYears * 12L;
        long monthly = (targetDp + totalMonths - 1L) / totalMonths;
        long weekly = monthly / 4L;

        tvResultDpSaving.setText(getString(
                R.string.result_dp_saving,
                formatRupiah(targetDp),
                dpYears,
                formatRupiah(monthly),
                formatRupiah(weekly)));
        tvResultDpSaving.setVisibility(View.VISIBLE);
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