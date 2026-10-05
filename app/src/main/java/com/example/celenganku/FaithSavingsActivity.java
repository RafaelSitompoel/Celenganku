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

public class FaithSavingsActivity extends AppCompatActivity {

    private TextInputEditText etAppDaily;
    private TextView tvResultApp;

    private TextInputEditText etMonthlyIncome;
    private TextInputEditText etOffertoryPercent;
    private TextView tvResultOffertory;

    private TextInputEditText etPilgrimageTarget;
    private TextInputEditText etTargetMonths;
    private TextView tvResultPilgrimage;

    private TextInputEditText etHolidayTarget;
    private TextView tvResultHoliday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faith_savings);

        View btnBack = findViewById(R.id.btnBackFaith);
        btnBack.setOnClickListener(v -> finish());

        TextView tvDailyVerse = findViewById(R.id.tvDailyVerse);
        View cardVerse = findViewById(R.id.cardVerse);
        if (tvDailyVerse != null) {
            tvDailyVerse.setText(VerseProvider.getVerseAuto());
            View.OnClickListener verseListener = v -> tvDailyVerse.setText(VerseProvider.getNextVerse());
            if (cardVerse != null) {
                cardVerse.setOnClickListener(verseListener);
            } else {
                tvDailyVerse.setOnClickListener(verseListener);
            }
        }

        etAppDaily = findViewById(R.id.etAppDaily);
        tvResultApp = findViewById(R.id.tvResultApp);

        etMonthlyIncome = findViewById(R.id.etMonthlyIncome);
        etOffertoryPercent = findViewById(R.id.etOffertoryPercent);
        tvResultOffertory = findViewById(R.id.tvResultOffertory);

        etPilgrimageTarget = findViewById(R.id.etPilgrimageTarget);
        etTargetMonths = findViewById(R.id.etTargetMonths);
        tvResultPilgrimage = findViewById(R.id.tvResultPilgrimage);

        etHolidayTarget = findViewById(R.id.etHolidayTarget);
        tvResultHoliday = findViewById(R.id.tvResultHoliday);

        MaterialButton btnCalcApp = findViewById(R.id.btnCalcApp);
        MaterialButton btnCalcOffertory = findViewById(R.id.btnCalcOffertory);
        MaterialButton btnCalcPilgrimage = findViewById(R.id.btnCalcPilgrimage);
        MaterialButton btnCalcHoliday = findViewById(R.id.btnCalcHoliday);

        btnCalcApp.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungApp();
        });

        btnCalcOffertory.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungPersembahan();
        });

        btnCalcPilgrimage.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungZiarah();
        });

        btnCalcHoliday.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungHariRaya();
        });
    }

    private void hitungApp() {
        long daily = ambilNominal(etAppDaily);
        if (daily <= 0) {
            tvResultApp.setText("Masukkan nominal celengan harian lebih dari 0.");
            tvResultApp.setVisibility(View.VISIBLE);
            return;
        }

        long total40Days = daily * 40L;
        tvResultApp.setText(getString(R.string.result_app, formatRupiah(total40Days)));
        tvResultApp.setVisibility(View.VISIBLE);
    }

    private void hitungPersembahan() {
        long income = ambilNominal(etMonthlyIncome);
        double percent = ambilDouble(etOffertoryPercent, 10.0);

        if (income <= 0) {
            tvResultOffertory.setText("Masukkan total penghasilan bulanan lebih dari 0.");
            tvResultOffertory.setVisibility(View.VISIBLE);
            return;
        }

        long monthlyOffertory = (long) (income * (percent / 100.0));
        long weeklyOffertory = monthlyOffertory / 4L;

        tvResultOffertory.setText(getString(
                R.string.result_offertory,
                formatRupiah(monthlyOffertory),
                formatRupiah(weeklyOffertory)));
        tvResultOffertory.setVisibility(View.VISIBLE);
    }

    private void hitungZiarah() {
        long target = ambilNominal(etPilgrimageTarget);
        long months = ambilNominal(etTargetMonths);
        if (months <= 0) months = 12L;

        if (target <= 0) {
            tvResultPilgrimage.setText("Masukkan target biaya ziarah atau acara lebih dari 0.");
            tvResultPilgrimage.setVisibility(View.VISIBLE);
            return;
        }

        long monthlySetoran = (target + months - 1L) / months;
        long weeklySetoran = (monthlySetoran + 3L) / 4L;

        tvResultPilgrimage.setText(getString(
                R.string.result_pilgrimage,
                "Ziarah / Sakramen",
                formatRupiah(monthlySetoran),
                formatRupiah(weeklySetoran)));
        tvResultPilgrimage.setVisibility(View.VISIBLE);
    }

    private void hitungHariRaya() {
        long target = ambilNominal(etHolidayTarget);
        if (target <= 0) {
            tvResultHoliday.setText("Masukkan target dana Natal/Paskah lebih dari 0.");
            tvResultHoliday.setVisibility(View.VISIBLE);
            return;
        }

        long monthly = (target + 11L) / 12L;
        long weekly = (target + 51L) / 52L;

        tvResultHoliday.setText(getString(
                R.string.result_holiday,
                formatRupiah(target),
                formatRupiah(monthly),
                formatRupiah(weekly)));
        tvResultHoliday.setVisibility(View.VISIBLE);
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