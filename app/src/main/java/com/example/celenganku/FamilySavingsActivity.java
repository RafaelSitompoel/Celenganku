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

public class FamilySavingsActivity extends AppCompatActivity {

    private TextInputEditText etFamilyTarget;
    private TextInputEditText etFamilyMembers;
    private TextInputEditText etFamilyMonths;
    private TextView tvResultFamily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_savings);

        View btnBack = findViewById(R.id.btnBackFamily);
        btnBack.setOnClickListener(v -> finish());

        etFamilyTarget = findViewById(R.id.etFamilyTarget);
        etFamilyMembers = findViewById(R.id.etFamilyMembers);
        etFamilyMonths = findViewById(R.id.etFamilyMonths);
        tvResultFamily = findViewById(R.id.tvResultFamily);

        MaterialButton btnCalcFamilys = findViewById(R.id.btnCalcFamilys);
        btnCalcFamilys.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hitungKeluarga();
        });
    }

    private void hitungKeluarga() {
        long target = ambilNominal(etFamilyTarget);
        long members = ambilNominal(etFamilyMembers);
        if (members <= 0) members = 1L;
        long months = ambilNominal(etFamilyMonths);
        if (months <= 0) months = 12L;

        if (target <= 0) {
            tvResultFamily.setText("Masukkan target impian bersama lebih dari 0.");
            tvResultFamily.setVisibility(View.VISIBLE);
            return;
        }

        long monthlyTotal = (target + months - 1L) / months;
        long monthlyPerMember = monthlyTotal / members;

        tvResultFamily.setText(getString(
                R.string.result_family,
                formatRupiah(target),
                (int) members,
                (int) months,
                formatRupiah(monthlyTotal),
                formatRupiah(monthlyPerMember)));
        tvResultFamily.setVisibility(View.VISIBLE);
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