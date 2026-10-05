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

public class ChristianSavingsActivity extends AppCompatActivity {

    private TextInputEditText etIncome;
    private TextInputEditText etPercent;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_christian_savings);

        findViewById(R.id.btnBackChristian).setOnClickListener(v -> finish());
        etIncome = findViewById(R.id.etChristianIncome);
        etPercent = findViewById(R.id.etChristianPercent);
        tvResult = findViewById(R.id.tvChristianResult);

        MaterialButton btnCalculate = findViewById(R.id.btnCalcChristian);
        btnCalculate.setOnClickListener(v -> {
            hideKeyboard();
            calculateOffering();
        });
    }

    private void calculateOffering() {
        long income = parseAmount(etIncome);
        double percent = parsePercent();
        if (income <= 0) {
            showError(R.string.error_christian_income);
            return;
        }
        if (percent < 0 || percent > 100) {
            showError(R.string.error_christian_percent);
            return;
        }

        long offering = (long) (income * (percent / 100.0));
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        currency.setMaximumFractionDigits(0);
        tvResult.setText(getString(R.string.result_christian, percent, currency.format(offering)));
        tvResult.setVisibility(View.VISIBLE);
    }

    private long parseAmount(TextInputEditText editText) {
        if (editText.getText() == null) return 0L;
        String digits = editText.getText().toString().replaceAll("[^0-9]", "");
        try {
            return digits.isEmpty() ? 0L : Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private double parsePercent() {
        if (etPercent.getText() == null) return 10.0;
        try {
            String value = etPercent.getText().toString().trim().replace(',', '.');
            return value.isEmpty() ? 10.0 : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }

    private void showError(int message) {
        tvResult.setText(message);
        tvResult.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard() {
        View focusedView = getCurrentFocus();
        if (focusedView == null) return;
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
        focusedView.clearFocus();
    }
}