package com.example.celenganku;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.util.Locale;

public class WishActivity extends AppCompatActivity {

    private static final long BULAN_TARGET = 12L;

    private ImageView ivPreview;
    private View layoutPlaceholder;
    private TextInputLayout tilNama;
    private TextInputLayout tilHarga;
    private TextInputEditText etNama;
    private TextInputEditText etHarga;
    private View layoutHasilImpian;
    private TextView tvHasilImpian;

    private Uri gambarUri = null;

    private final ActivityResultLauncher<String> pemilihGambar =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    new ActivityResultCallback<Uri>() {
                        @Override
                        public void onActivityResult(Uri uri) {
                            if (uri != null) {
                                tampilkanGambar(uri);
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish);

        BottomNavHelper.setupNav(this, R.id.navWish);

        ivPreview = findViewById(R.id.ivPreview);
        layoutPlaceholder = findViewById(R.id.layoutPlaceholder);
        tilNama = findViewById(R.id.tilNama);
        tilHarga = findViewById(R.id.tilHarga);
        etNama = findViewById(R.id.etNama);
        etHarga = findViewById(R.id.etHarga);
        layoutHasilImpian = findViewById(R.id.layoutHasilImpian);
        tvHasilImpian = findViewById(R.id.tvHasilImpian);

        View btnBack = findViewById(R.id.btnBack);
        MaterialButton btnPilihGambar = findViewById(R.id.btnPilihGambar);
        MaterialButton btnTampilkan = findViewById(R.id.btnTampilkan);
        MaterialButton btnBersihkan = findViewById(R.id.btnBersihkan);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPilihGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pemilihGambar.launch("image/*");
            }
        });

        layoutPlaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pemilihGambar.launch("image/*");
            }
        });

        btnTampilkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sembunyikanKeyboard();
                tampilkanImpian();
            }
        });

        btnBersihkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bersihkan();
            }
        });
    }

    private void tampilkanGambar(Uri uri) {
        gambarUri = uri;
        ivPreview.setImageURI(uri);
        ivPreview.setVisibility(View.VISIBLE);
        layoutPlaceholder.setVisibility(View.GONE);
    }

    private void tampilkanImpian() {
        tilNama.setError(null);
        tilHarga.setError(null);

        String nama = "";
        if (etNama.getText() != null) {
            nama = etNama.getText().toString().trim();
        }
        long harga = ambilNominal(etHarga);

        if (gambarUri == null) {
            tvHasilImpian.setText(R.string.error_wish_image);
            layoutHasilImpian.setVisibility(View.VISIBLE);
            return;
        }

        if (nama.isEmpty()) {
            tilNama.setError(getString(R.string.error_wish_name));
            layoutHasilImpian.setVisibility(View.GONE);
            return;
        }

        if (harga <= 0) {
            tilHarga.setError(getString(R.string.error_wish_price));
            layoutHasilImpian.setVisibility(View.GONE);
            return;
        }

        long perBulan = (harga + BULAN_TARGET - 1L) / BULAN_TARGET;

        tvHasilImpian.setText(getString(
                R.string.result_wish,
                nama,
                formatRupiah(harga),
                formatRupiah(perBulan)));
        layoutHasilImpian.setVisibility(View.VISIBLE);
    }

    private void bersihkan() {
        gambarUri = null;
        ivPreview.setImageDrawable(null);
        ivPreview.setVisibility(View.GONE);
        layoutPlaceholder.setVisibility(View.VISIBLE);
        etNama.setText("");
        etHarga.setText("");
        tilNama.setError(null);
        tilHarga.setError(null);
        layoutHasilImpian.setVisibility(View.GONE);
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
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(fokus.getWindowToken(), 0);
            }
            fokus.clearFocus();
        }
    }
}