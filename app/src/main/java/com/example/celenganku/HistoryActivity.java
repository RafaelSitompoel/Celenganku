package com.example.celenganku;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private static final String PREFS_HISTORY = "celenganku_history_prefs";
    private static final String KEY_HISTORY_ITEMS = "history_items_json";

    private TextInputLayout tilHistoryAmount;
    private TextInputLayout tilHistoryNote;
    private TextInputEditText etHistoryAmount;
    private TextInputEditText etHistoryNote;
    private TextView tvTotalHistory;
    private TextView tvEmptyHistory;
    private LinearLayout layoutHistoryContainer;
    private MaterialButton btnClearHistory;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        BottomNavHelper.setupNav(this, R.id.navHistory);

        prefs = getSharedPreferences(PREFS_HISTORY, Context.MODE_PRIVATE);

        View btnBack = findViewById(R.id.btnBackHistory);
        tilHistoryAmount = findViewById(R.id.tilHistoryAmount);
        tilHistoryNote = findViewById(R.id.tilHistoryNote);
        etHistoryAmount = findViewById(R.id.etHistoryAmount);
        etHistoryNote = findViewById(R.id.etHistoryNote);
        tvTotalHistory = findViewById(R.id.tvTotalHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        layoutHistoryContainer = findViewById(R.id.layoutHistoryContainer);
        MaterialButton btnAddHistory = findViewById(R.id.btnAddHistory);
        btnClearHistory = findViewById(R.id.btnClearHistory);

        btnBack.setOnClickListener(v -> finish());

        btnAddHistory.setOnClickListener(v -> {
            sembunyikanKeyboard();
            tambahSetoran();
        });

        btnClearHistory.setOnClickListener(v -> hapusSemuaRiwayat());

        tampilkanRiwayat();
    }

    private void tambahSetoran() {
        tilHistoryAmount.setError(null);
        tilHistoryNote.setError(null);

        long nominal = ambilNominal(etHistoryAmount);
        String catatan = etHistoryNote.getText() != null ? etHistoryNote.getText().toString().trim() : "";
        if (catatan.isEmpty()) {
            catatan = "Setoran Tabungan";
        }

        if (nominal <= 0) {
            tilHistoryAmount.setError(getString(R.string.error_history_amount));
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("id", "ID"));
        String tanggalStr = sdf.format(new Date());

        try {
            String jsonRaw = prefs.getString(KEY_HISTORY_ITEMS, "[]");
            JSONArray jsonArray = new JSONArray(jsonRaw);

            JSONObject newItem = new JSONObject();
            newItem.put("amount", nominal);
            newItem.put("note", catatan);
            newItem.put("date", tanggalStr);

            jsonArray.put(newItem);

            prefs.edit().putString(KEY_HISTORY_ITEMS, jsonArray.toString()).apply();

            etHistoryAmount.setText("");
            etHistoryNote.setText("");

            Toast.makeText(this, R.string.success_add_history, Toast.LENGTH_SHORT).show();
            tampilkanRiwayat();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tampilkanRiwayat() {
        layoutHistoryContainer.removeAllViews();
        long totalSaldo = 0L;

        try {
            String jsonRaw = prefs.getString(KEY_HISTORY_ITEMS, "[]");
            JSONArray jsonArray = new JSONArray(jsonRaw);

            if (jsonArray.length() == 0) {
                tvEmptyHistory.setVisibility(View.VISIBLE);
                btnClearHistory.setVisibility(View.GONE);
                tvTotalHistory.setText(getString(R.string.label_total_history, formatRupiah(0)));
                return;
            }

            tvEmptyHistory.setVisibility(View.GONE);
            btnClearHistory.setVisibility(View.VISIBLE);

            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                JSONObject item = jsonArray.getJSONObject(i);
                long amount = item.optLong("amount", 0L);
                String note = item.optString("note", "Setoran");
                String date = item.optString("date", "");

                totalSaldo += amount;

                View itemView = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, layoutHistoryContainer, false);
                TextView text1 = itemView.findViewById(android.R.id.text1);
                TextView text2 = itemView.findViewById(android.R.id.text2);

                text1.setText(note + " - " + formatRupiah(amount));
                text1.setTextColor(getResources().getColor(R.color.text_primary));
                text1.setTextSize(16f);

                text2.setText(date);
                text2.setTextColor(getResources().getColor(R.color.text_secondary));
                text2.setTextSize(13f);

                itemView.setPadding(0, 12, 0, 12);
                layoutHistoryContainer.addView(itemView);
            }

            tvTotalHistory.setText(getString(R.string.label_total_history, formatRupiah(totalSaldo)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hapusSemuaRiwayat() {
        prefs.edit().remove(KEY_HISTORY_ITEMS).apply();
        Toast.makeText(this, R.string.success_clear_history, Toast.LENGTH_SHORT).show();
        tampilkanRiwayat();
    }

    private long ambilNominal(TextInputEditText editText) {
        if (editText.getText() == null) {
            return 0L;
        }
        String angka = editText.getText().toString().replaceAll("[^0-9]", "");
        if (angka.isEmpty()) {
            return 0L;
        }
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
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(fokus.getWindowToken(), 0);
            }
            fokus.clearFocus();
        }
    }
}