package com.example.celenganku;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class PromptAssistantActivity extends AppCompatActivity {

    private TextInputEditText etPromptInput;
    private TextView tvPromptResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prompt_assistant);

        View btnBack = findViewById(R.id.btnBackPrompt);
        btnBack.setOnClickListener(v -> finish());

        etPromptInput = findViewById(R.id.etPromptInput);
        tvPromptResult = findViewById(R.id.tvPromptResult);

        MaterialButton btnGeneratePrompt = findViewById(R.id.btnGeneratePrompt);
        btnGeneratePrompt.setOnClickListener(v -> {
            sembunyikanKeyboard();
            hasilkanPrompt();
        });
    }

    private void hasilkanPrompt() {
        String query = "";
        if (etPromptInput.getText() != null) {
            query = etPromptInput.getText().toString().trim().toLowerCase();
        }

        if (query.isEmpty()) {
            tvPromptResult.setText("Silakan ketik pertanyaan atau topik keuangan yang ingin Anda konsultasikan.");
            tvPromptResult.setVisibility(View.VISIBLE);
            return;
        }

        String advice;
        if (query.contains("10 juta") || query.contains("tabung")) {
            advice = "💡 **Strategi Menabung Target Khusus:**\nUntuk mengumpulkan 10 juta dalam 6 bulan (~180 hari), Anda perlu menabung sekitar Rp55.500 per hari atau Rp1.666.000 per bulan. Gunakan fitur 'Simulasi Target Tabungan' di beranda dan tetapkan komitmen harian!";
        } else if (query.contains("darurat") || query.contains("krisis")) {
            advice = "💡 **Panduan Dana Darurat:**\nPastikan Anda memiliki simpanan 3 hingga 12 kali pengeluaran bulanan. Simpan dana ini di instrumen yang likuid dan aman (seperti tabungan terpisah atau reksa dana pasar uang).";
        } else if (query.contains("rumah") || query.contains("kpr")) {
            advice = "💡 **Tips Membeli Rumah & KPR:**\nSiapkan Uang Muka (DP) minimal 20% untuk meringankan cicilan bulanan. Gunakan 'Kalkulator Properti & KPR' kami untuk mensimulasikan tenor dan bunga terbaik.";
        } else {
            advice = "💡 **Saran Pakar Finansial Celenganku:**\n" +
                    "1. Terapkan prinsip 50/30/20 (50% kebutuhan, 30% keinginan, 20% tabungan/investasi).\n" +
                    "2. Catat setiap pemasukan dan pengeluaran harian menggunakan fitur 'Jurnal & Riwayat Tabungan'.\n" +
                    "3. Konsisten adalah kunci utama keberhasilan finansial Anda.";
        }

        tvPromptResult.setText(advice);
        tvPromptResult.setVisibility(View.VISIBLE);
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