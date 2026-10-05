package com.example.celenganku;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class QuizActivity extends AppCompatActivity {

    private RadioGroup rgQ1;
    private RadioGroup rgQ2;
    private RadioGroup rgQ3;
    private TextView tvResultQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        View btnBack = findViewById(R.id.btnBackQuiz);
        btnBack.setOnClickListener(v -> finish());

        rgQ1 = findViewById(R.id.rgQ1);
        rgQ2 = findViewById(R.id.rgQ2);
        rgQ3 = findViewById(R.id.rgQ3);
        tvResultQuiz = findViewById(R.id.tvResultQuiz);

        MaterialButton btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz);
        btnSubmitQuiz.setOnClickListener(v -> nilaiKuis());
    }

    private void nilaiKuis() {
        int score = 0;

        if (rgQ1.getCheckedRadioButtonId() == R.id.q1a2) score += 34; // B (10-20%)
        if (rgQ2.getCheckedRadioButtonId() == R.id.q2a2) score += 33; // B (Persiapan krisis)
        if (rgQ3.getCheckedRadioButtonId() == R.id.q3a2) score += 33; // B (Menyisihkan di awal)

        String kategori;
        if (score >= 90) {
            kategori = "Pakar Keuangan (Sangat Baik)";
        } else if (score >= 60) {
            kategori = "Perencana Bijak (Baik)";
        } else {
            kategori = "Pemula (Perlu Peningkatan Literasi)";
        }

        tvResultQuiz.setText(getString(R.string.result_quiz, score, kategori));
        tvResultQuiz.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Kuis selesai! Skor Anda: " + score, Toast.LENGTH_LONG).show();
    }
}