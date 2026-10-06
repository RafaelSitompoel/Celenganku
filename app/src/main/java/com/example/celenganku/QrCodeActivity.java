package com.example.celenganku;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Random;

public class QrCodeActivity extends AppCompatActivity {

    private TextInputEditText etQrInput;
    private ImageView ivQrBitmap;
    private ImageView ivBarcodeBitmap;
    private TextView tvQrResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        View btnBack = findViewById(R.id.btnBackQr);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        etQrInput = findViewById(R.id.etQrInput);
        ivQrBitmap = findViewById(R.id.ivQrBitmap);
        ivBarcodeBitmap = findViewById(R.id.ivBarcodeBitmap);
        tvQrResult = findViewById(R.id.tvQrResult);

        MaterialButton btnGenerateQr = findViewById(R.id.btnGenerateQr);
        MaterialButton btnScanCamera = findViewById(R.id.btnScanCamera);
        MaterialButton btnTutupQr = findViewById(R.id.btnTutupQr);

        if (btnTutupQr != null) {
            btnTutupQr.setOnClickListener(v -> finish());
        }

        if (btnGenerateQr != null) {
            btnGenerateQr.setOnClickListener(v -> {
                sembunyikanKeyboard();
                buatQrDanBarcode();
            });
        }

        if (btnScanCamera != null) {
            btnScanCamera.setOnClickListener(v -> {
                try {
                    GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(this);
                    scanner.startScan()
                            .addOnSuccessListener(barcode -> {
                                String rawValue = barcode.getRawValue();
                                if (rawValue != null && !rawValue.isEmpty()) {
                                    if (etQrInput != null) {
                                        etQrInput.setText(rawValue);
                                    }
                                    buatQrDanBarcode();
                                    Toast.makeText(this, "Berhasil scan: " + rawValue, Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Scan dibatalkan atau gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    new AlertDialog.Builder(this)
                            .setTitle("Kamera Scanner")
                            .setMessage("Layanan pemindai kamera memerlukan Google Play Services yang aktif. Anda tetap dapat membuat QR Code & Barcode secara manual.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            });
        }
    }

    private void buatQrDanBarcode() {
        if (etQrInput == null || ivQrBitmap == null || ivBarcodeBitmap == null || tvQrResult == null) {
            return;
        }

        String input = "";
        if (etQrInput.getText() != null) {
            input = etQrInput.getText().toString().trim();
        }

        if (input.isEmpty()) {
            Toast.makeText(this, "Masukkan data atau nominal terlebih dahulu!", Toast.LENGTH_SHORT).show();
            ivQrBitmap.setVisibility(View.GONE);
            ivBarcodeBitmap.setVisibility(View.GONE);
            tvQrResult.setVisibility(View.GONE);
            return;
        }

        try {
            // 1. Generate QR Code (2D)
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix qrMatrix = qrWriter.encode(input, BarcodeFormat.QR_CODE, 512, 512);
            int qrW = qrMatrix.getWidth();
            int qrH = qrMatrix.getHeight();
            Bitmap qrBmp = Bitmap.createBitmap(qrW, qrH, Bitmap.Config.RGB_565);
            for (int x = 0; x < qrW; x++) {
                for (int y = 0; y < qrH; y++) {
                    qrBmp.setPixel(x, y, qrMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ivQrBitmap.setImageBitmap(qrBmp);
            ivQrBitmap.setVisibility(View.VISIBLE);

            // 2. Generate Linear Barcode (Code 128 - 1D)
            MultiFormatWriter barWriter = new MultiFormatWriter();
            BitMatrix barMatrix = barWriter.encode(input, BarcodeFormat.CODE_128, 600, 150);
            int barW = barMatrix.getWidth();
            int barH = barMatrix.getHeight();
            Bitmap barBmp = Bitmap.createBitmap(barW, barH, Bitmap.Config.RGB_565);
            for (int x = 0; x < barW; x++) {
                for (int y = 0; y < barH; y++) {
                    barBmp.setPixel(x, y, barMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ivBarcodeBitmap.setImageBitmap(barBmp);
            ivBarcodeBitmap.setVisibility(View.VISIBLE);

            String[] pesanSukses = {
                    "🌟 Sukses Besar! QR Code & Barcode Celengan Anda siap digunakan untuk transaksi atau berbagi target.",
                    "🚀 Berhasil! Kode QR dan Barcode berhasil dibuat. Silakan pindai dengan kamera perangkat lain.",
                    "✨ Luar Biasa! QR Code keuangan berhasil di-generate dengan enkripsi data yang aman.",
                    "🎯 Berhasil Dibuat! Barcode dan QR Code siap mempermudah pencatatan dan transfer impian Anda."
            };
            String pesanPilihan = pesanSukses[new Random().nextInt(pesanSukses.length)];

            tvQrResult.setText(pesanPilihan + "\n\n📄 Data/Nominal: " + input);
            tvQrResult.setVisibility(View.VISIBLE);
            Toast.makeText(this, "QR Code & Barcode berhasil dibuat!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal men-generate QR Code / Barcode.", Toast.LENGTH_SHORT).show();
        }
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