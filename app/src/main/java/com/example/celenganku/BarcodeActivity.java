package com.example.celenganku;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

public class BarcodeActivity extends AppCompatActivity {

    private static final int MAKS_QR = 300;
    private static final int MAKS_CODE128 = 60;

    private View layoutHasilScan;
    private TextView tvHasilScan;
    private MaterialButton btnSalin;

    private TextInputLayout tilTeks;
    private TextInputEditText etTeks;
    private RadioGroup rgJenis;
    private ImageView ivBarcode;
    private View layoutHasilBuat;

    private String isiTerakhir = "";

    private final ActivityResultLauncher<ScanOptions> pemindai =
            registerForActivityResult(
                    new ScanContract(),
                    new ActivityResultCallback<ScanIntentResult>() {
                        @Override
                        public void onActivityResult(ScanIntentResult hasil) {
                            if (hasil.getContents() == null) {
                                Toast.makeText(BarcodeActivity.this,
                                        R.string.msg_scan_cancel, Toast.LENGTH_SHORT).show();
                            } else {
                                tampilkanHasilScan(hasil.getContents(), hasil.getFormatName());
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        layoutHasilScan = findViewById(R.id.layoutHasilScan);
        tvHasilScan = findViewById(R.id.tvHasilScan);
        btnSalin = findViewById(R.id.btnSalin);

        tilTeks = findViewById(R.id.tilTeks);
        etTeks = findViewById(R.id.etTeks);
        rgJenis = findViewById(R.id.rgJenis);
        ivBarcode = findViewById(R.id.ivBarcode);
        layoutHasilBuat = findViewById(R.id.layoutHasilBuat);

        View btnBack = findViewById(R.id.btnBack);
        MaterialButton btnPindai = findViewById(R.id.btnPindai);
        MaterialButton btnBuat = findViewById(R.id.btnBuat);
        MaterialButton btnBersihkan = findViewById(R.id.btnBersihkan);

        btnBack.setOnClickListener(v -> finish());
        btnPindai.setOnClickListener(v -> mulaiPindai());
        btnSalin.setOnClickListener(v -> salinIsi());

        btnBuat.setOnClickListener(v -> {
            Util.sembunyikanKeyboard(BarcodeActivity.this);
            buatBarcode();
        });

        btnBersihkan.setOnClickListener(v -> bersihkan());
    }

    private void mulaiPindai() {
        ScanOptions opsi = new ScanOptions();
        opsi.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        opsi.setPrompt(getString(R.string.scan_prompt));
        opsi.setBeepEnabled(true);
        opsi.setOrientationLocked(false);
        pemindai.launch(opsi);
    }

    private void tampilkanHasilScan(String isi, String jenis) {
        isiTerakhir = isi;
        String namaJenis = jenis;
        if (namaJenis == null || namaJenis.isEmpty()) {
            namaJenis = "-";
        }
        tvHasilScan.setText(getString(R.string.result_scan, namaJenis, isi));
        layoutHasilScan.setVisibility(View.VISIBLE);
        btnSalin.setVisibility(View.VISIBLE);
    }

    private void salinIsi() {
        if (isiTerakhir.isEmpty()) {
            return;
        }
        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("barcode", isiTerakhir));
            Toast.makeText(this, R.string.msg_copied, Toast.LENGTH_SHORT).show();
        }
    }

    private void buatBarcode() {
        tilTeks.setError(null);

        String teks = "";
        if (etTeks.getText() != null) {
            teks = etTeks.getText().toString().trim();
        }

        if (teks.isEmpty()) {
            tilTeks.setError(getString(R.string.error_teks_kosong));
            layoutHasilBuat.setVisibility(View.GONE);
            return;
        }

        boolean modeQr = rgJenis.getCheckedRadioButtonId() == R.id.rbQr;

        if (modeQr && teks.length() > MAKS_QR) {
            tilTeks.setError(getString(R.string.error_qr_panjang, MAKS_QR));
            layoutHasilBuat.setVisibility(View.GONE);
            return;
        }

        if (!modeQr) {
            if (teks.length() > MAKS_CODE128) {
                tilTeks.setError(getString(R.string.error_code_panjang, MAKS_CODE128));
                layoutHasilBuat.setVisibility(View.GONE);
                return;
            }
            for (int i = 0; i < teks.length(); i++) {
                if (teks.charAt(i) > 127) {
                    tilTeks.setError(getString(R.string.error_code_ascii));
                    layoutHasilBuat.setVisibility(View.GONE);
                    return;
                }
            }
        }

        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap gambar;
            if (modeQr) {
                gambar = encoder.encodeBitmap(teks, BarcodeFormat.QR_CODE, 600, 600);
            } else {
                gambar = encoder.encodeBitmap(teks, BarcodeFormat.CODE_128, 900, 300);
            }
            ivBarcode.setImageBitmap(gambar);
            layoutHasilBuat.setVisibility(View.VISIBLE);
        } catch (WriterException | IllegalArgumentException e) {
            tilTeks.setError(getString(R.string.error_buat_gagal));
            layoutHasilBuat.setVisibility(View.GONE);
        }
    }

    private void bersihkan() {
        etTeks.setText("");
        tilTeks.setError(null);
        rgJenis.check(R.id.rbQr);
        ivBarcode.setImageDrawable(null);
        layoutHasilBuat.setVisibility(View.GONE);
        layoutHasilScan.setVisibility(View.GONE);
        btnSalin.setVisibility(View.GONE);
        isiTerakhir = "";
        etTeks.requestFocus();
    }
}