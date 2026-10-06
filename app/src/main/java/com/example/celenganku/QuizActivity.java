package com.example.celenganku;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class QuizActivity extends AppCompatActivity {

    private RadioGroup rgQ1;
    private RadioGroup rgQ2;
    private RadioGroup rgQ3;
    private TextInputEditText etEssayAnswer;
    private TextView tvResultQuiz29;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        View btnBack = findViewById(R.id.btnBackQuiz);
        btnBack.setOnClickListener(v -> finish());

        rgQ1 = findViewById(R.id.rgQ1);
        rgQ2 = findViewById(R.id.rgQ2);
        rgQ3 = findViewById(R.id.rgQ3);
        etEssayAnswer = findViewById(R.id.etEssayAnswer);
        tvResultQuiz29 = findViewById(R.id.tvResultQuiz29);

        MaterialButton btnSubmitQuiz29 = findViewById(R.id.btnSubmitQuiz29);
        btnSubmitQuiz29.setOnClickListener(v -> {
            sembunyikanKeyboard();
            evaluasiKuis();
        });
    }

    private void evaluasiKuis() {
        int skor = 0;

        if (rgQ1.getCheckedRadioButtonId() == R.id.q1a2) skor += 34; // B
        if (rgQ2.getCheckedRadioButtonId() == R.id.q2a2) skor += 33; // B
        if (rgQ3.getCheckedRadioButtonId() == R.id.q3a2) skor += 33; // B

        String essay = "";
        if (etEssayAnswer.getText() != null) {
            essay = etEssayAnswer.getText().toString().trim();
        }

        if (!essay.isEmpty()) {
            skor = Math.min(100, skor + 10); // Bonus poin untuk analisis uraian tertulis
        }

        String predikat;
        if (skor >= 90) {
            predikat = "Sangat Kompeten (Pakar Keuangan / Perencana Teladan)";
        } else if (skor >= 70) {
            predikat = "Kompeten (Pemahaman Finansial Baik)";
        } else {
            predikat = "Cukup (Perlu Peningkatan & Kedisiplinan Literasi)";
        }

        String hasil = "📊 **Hasil Evaluasi 29 Soal & Kisi-Kisi Uraian:**\n" +
                "• Skor Total Anda: " + skor + " / 100\n" +
                "• Predikat: " + predikat + "\n" +
                (!essay.isEmpty() ? "• Uraian/Analisis: Diterima & Dinilai Positif (+10 Poin Bonus).\n" : "") +
                "\n💡 **Rekomendasi:** Terus kelola tabungan, jaga rasio utang tetap sehat, dan siapkan dana darurat serta investasi jangka panjang dengan bijak.";

        tvResultQuiz29.setText(hasil);
        tvResultQuiz29.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Evaluasi kuis selesai! Skor: " + skor, Toast.LENGTH_LONG).show();
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