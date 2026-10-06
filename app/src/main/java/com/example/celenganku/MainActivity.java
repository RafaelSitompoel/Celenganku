package com.example.celenganku;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final long MAKS_HARI = 36500L; // 100 tahun

    private TextInputLayout tilTarget;
    private TextInputLayout tilSaldo;
    private TextInputLayout tilSetoran;
    private TextInputLayout tilBase;

    private TextInputEditText etTarget;
    private TextInputEditText etSaldo;
    private TextInputEditText etSetoran;
    private TextInputEditText etBase;

    private RadioGroup rgPeriode;
    private View layoutHasil;
    private ProgressBar pbProgress;
    private TextView tvPersen;
    private TextView tvHasil;
    private TextView tvTantangan;

    // Administrator Views
    private View layoutAdminLocked;
    private View layoutAdminUnlocked;
    private TextView tvAdminBadge;
    private TextInputLayout tilAdminPin;
    private TextInputEditText etAdminPin;
    private TextInputLayout tilAdminSaldo;
    private TextInputEditText etAdminSaldo;
    private TextInputLayout tilAdminTarget;
    private TextInputEditText etAdminTarget;
    private SharedPreferences prefs;

    private static final String PREF_NAME = "celenganku_admin_prefs";
    private static final String KEY_PIN = "admin_pin";
    private static final String KEY_SAVED_SALDO = "saved_saldo";
    private static final String KEY_SAVED_TARGET = "saved_target";

    private ImageView ivProfileAvatar;
    private final ActivityResultLauncher<String> pemilihAvatar =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception e) {
                        // Ignored if not persistable
                    }
                    ProfileManager.saveAvatarUri(this, uri.toString());
                    ivProfileAvatar.setImageURI(uri);
                    Toast.makeText(this, "Foto profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        BottomNavHelper.setupNav(this, R.id.navHome);

        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        ivProfileAvatar = findViewById(R.id.ivProfileAvatar);
        String savedAvatar = ProfileManager.getAvatarUri(this);
        if (savedAvatar != null) {
            try {
                ivProfileAvatar.setImageURI(Uri.parse(savedAvatar));
            } catch (Exception e) {
                ivProfileAvatar.setImageResource(R.drawable.ic_person);
            }
        }

        ivProfileAvatar.setOnClickListener(v -> pemilihAvatar.launch("image/*"));

        tilTarget = findViewById(R.id.tilTarget);
        tilSaldo = findViewById(R.id.tilSaldo);
        tilSetoran = findViewById(R.id.tilSetoran);
        tilBase = findViewById(R.id.tilBase);

        etTarget = findViewById(R.id.etTarget);
        etSaldo = findViewById(R.id.etSaldo);
        etSetoran = findViewById(R.id.etSetoran);
        etBase = findViewById(R.id.etBase);

        rgPeriode = findViewById(R.id.rgPeriode);
        layoutHasil = findViewById(R.id.layoutHasil);
        pbProgress = findViewById(R.id.pbProgress);
        tvPersen = findViewById(R.id.tvPersen);
        tvHasil = findViewById(R.id.tvHasil);
        tvTantangan = findViewById(R.id.tvTantangan);

        TextView tvMainVerse = findViewById(R.id.tvMainVerse);
        View cardMainVerse = findViewById(R.id.cardMainVerse);
        if (tvMainVerse != null) {
            tvMainVerse.setText(VerseProvider.getVerseAuto());
            View.OnClickListener verseListener = v -> tvMainVerse.setText(VerseProvider.getNextVerse());
            if (cardMainVerse != null) {
                cardMainVerse.setOnClickListener(verseListener);
            } else {
                tvMainVerse.setOnClickListener(verseListener);
            }
        }

        View btnOpenSettings = findViewById(R.id.btnOpenSettings);
        if (btnOpenSettings != null) {
            btnOpenSettings.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        // Inisialisasi Administrator Views
        layoutAdminLocked = findViewById(R.id.layoutAdminLocked);
        layoutAdminUnlocked = findViewById(R.id.layoutAdminUnlocked);
        tvAdminBadge = findViewById(R.id.tvAdminBadge);
        tilAdminPin = findViewById(R.id.tilAdminPin);
        etAdminPin = findViewById(R.id.etAdminPin);
        tilAdminSaldo = findViewById(R.id.tilAdminSaldo);
        etAdminSaldo = findViewById(R.id.etAdminSaldo);
        tilAdminTarget = findViewById(R.id.tilAdminTarget);
        etAdminTarget = findViewById(R.id.etAdminTarget);

        MaterialButton btnHitung = findViewById(R.id.btnHitung);
        MaterialButton btnReset = findViewById(R.id.btnReset);
        MaterialButton btnTantangan = findViewById(R.id.btnTantangan);
        MaterialButton btnImpian = findViewById(R.id.btnImpian);
        MaterialButton btnRiwayat = findViewById(R.id.btnRiwayat);
        MaterialButton btnLoginAdmin = findViewById(R.id.btnLoginAdmin);
        MaterialButton btnApplyAdminData = findViewById(R.id.btnApplyAdminData);
        MaterialButton btnResetAllAdmin = findViewById(R.id.btnResetAllAdmin);
        MaterialButton btnLockAdminMode = findViewById(R.id.btnLockAdminMode);

        // Muat saldo & target tersimpan jika ada
        long savedSaldo = prefs.getLong(KEY_SAVED_SALDO, 0L);
        long savedTarget = prefs.getLong(KEY_SAVED_TARGET, 0L);
        if (savedSaldo > 0) {
            etSaldo.setText(String.valueOf(savedSaldo));
        }
        if (savedTarget > 0) {
            etTarget.setText(String.valueOf(savedTarget));
        }

        btnHitung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sembunyikanKeyboard();
                hitungTarget();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSimulasi();
            }
        });

        btnTantangan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sembunyikanKeyboard();
                hitungTantangan();
            }
        });

        btnImpian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WishActivity.class);
                startActivity(intent);
            }
        });

        if (btnRiwayat != null) {
            btnRiwayat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                    startActivity(intent);
                }
            });
        }

        View cardRumah = findViewById(R.id.cardRumah);
        if (cardRumah != null) {
            cardRumah.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HouseCalculatorActivity.class)));
        }
        MaterialButton btnRumah = findViewById(R.id.btnRumah);
        if (btnRumah != null) {
            btnRumah.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, HouseCalculatorActivity.class)));
        }

        View cardBunga = findViewById(R.id.cardBunga);
        if (cardBunga != null) {
            cardBunga.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BungaActivity.class)));
        }

        View cardInflasi = findViewById(R.id.cardInflasi);
        if (cardInflasi != null) {
            cardInflasi.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, InflasiActivity.class)));
        }

        View cardDarurat = findViewById(R.id.cardDarurat);
        if (cardDarurat != null) {
            cardDarurat.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DanaDaruratActivity.class)));
        }

        View cardQuiz = findViewById(R.id.cardQuiz);
        if (cardQuiz != null) {
            cardQuiz.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, QuizActivity.class)));
        }

        View cardFreedom = findViewById(R.id.cardFreedom);
        if (cardFreedom != null) {
            cardFreedom.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FinancialFreedomActivity.class)));
        }

        View cardEducation = findViewById(R.id.cardEducation);
        if (cardEducation != null) {
            cardEducation.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, EducationSavingsActivity.class)));
        }

        View cardBarcode = findViewById(R.id.cardBarcode);
        if (cardBarcode != null) {
            cardBarcode.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BarcodeActivity.class)));
        }

        MaterialButton btnCatholic = findViewById(R.id.btnCatholic);
        if (btnCatholic != null) {
            btnCatholic.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CatholicPrapaskahActivity.class);
                startActivity(intent);
            });
        }

        MaterialButton btnProtestant = findViewById(R.id.btnProtestant);
        if (btnProtestant != null) {
            btnProtestant.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProtestantSavingsActivity.class);
                startActivity(intent);
            });
        }

        MaterialButton btnAlatFinansial = findViewById(R.id.btnAlatFinansial);
        if (btnAlatFinansial != null) {
            btnAlatFinansial.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, FinancialToolsActivity.class);
                startActivity(intent);
            });
        }

        // Administrator Listeners
        btnLoginAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sembunyikanKeyboard();
                loginAdmin();
            }
        });

        btnApplyAdminData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sembunyikanKeyboard();
                applyAdminData();
            }
        });

        btnResetAllAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAllAdminData();
            }
        });

        btnLockAdminMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockAdmin();
            }
        });
    }

    private void loginAdmin() {
        String inputPin = etAdminPin.getText() != null ? etAdminPin.getText().toString().trim() : "";
        String storedPin = prefs.getString(KEY_PIN, "1234");

        if (inputPin.equals(storedPin)) {
            tilAdminPin.setError(null);
            etAdminPin.setText("");
            layoutAdminLocked.setVisibility(View.GONE);
            layoutAdminUnlocked.setVisibility(View.VISIBLE);
            tvAdminBadge.setText(getString(R.string.admin_status_active));
            tvAdminBadge.setTextColor(getResources().getColor(R.color.primary));

            // Isi default kolom admin dengan nilai saat ini
            long curSaldo = ambilNominal(etSaldo);
            long curTarget = ambilNominal(etTarget);
            if (curSaldo > 0) etAdminSaldo.setText(String.valueOf(curSaldo));
            if (curTarget > 0) etAdminTarget.setText(String.valueOf(curTarget));

            android.widget.Toast.makeText(this, R.string.success_admin_login, android.widget.Toast.LENGTH_SHORT).show();
        } else {
            tilAdminPin.setError(getString(R.string.error_pin_wrong));
        }
    }

    private void lockAdmin() {
        layoutAdminUnlocked.setVisibility(View.GONE);
        layoutAdminLocked.setVisibility(View.VISIBLE);
        tvAdminBadge.setText("🔒 Terkunci");
        tvAdminBadge.setTextColor(getResources().getColor(R.color.admin_gold));
        tilAdminPin.setError(null);
        etAdminPin.setText("");
    }

    private void applyAdminData() {
        long newSaldo = ambilNominal(etAdminSaldo);
        long newTarget = ambilNominal(etAdminTarget);

        if (newSaldo >= 0) {
            etSaldo.setText(String.valueOf(newSaldo));
            prefs.edit().putLong(KEY_SAVED_SALDO, newSaldo).apply();
        }

        if (newTarget > 0) {
            etTarget.setText(String.valueOf(newTarget));
            prefs.edit().putLong(KEY_SAVED_TARGET, newTarget).apply();
        }

        if (ambilNominal(etSetoran) > 0) {
            hitungTarget();
        }

        android.widget.Toast.makeText(this, R.string.success_admin_applied, android.widget.Toast.LENGTH_SHORT).show();
    }

    private void resetAllAdminData() {
        resetSimulasi();
        prefs.edit().remove(KEY_SAVED_SALDO).remove(KEY_SAVED_TARGET).apply();
        etAdminSaldo.setText("");
        etAdminTarget.setText("");
        android.widget.Toast.makeText(this, R.string.success_admin_reset, android.widget.Toast.LENGTH_SHORT).show();
    }

    private void hitungTarget() {
        tilTarget.setError(null);
        tilSaldo.setError(null);
        tilSetoran.setError(null);

        long target = ambilNominal(etTarget);
        long saldo = ambilNominal(etSaldo);
        long setoran = ambilNominal(etSetoran);

        if (target <= 0) {
            tilTarget.setError(getString(R.string.error_target));
            layoutHasil.setVisibility(View.GONE);
            return;
        }

        if (setoran <= 0) {
            tilSetoran.setError(getString(R.string.error_setoran));
            layoutHasil.setVisibility(View.GONE);
            return;
        }

        int persen = (int) Math.min(100L, (saldo * 100L) / target);
        pbProgress.setProgress(persen);
        tvPersen.setText(getString(R.string.label_progress, persen));
        layoutHasil.setVisibility(View.VISIBLE);

        long sisa = target - saldo;
        if (sisa <= 0) {
            tvHasil.setText(R.string.result_done);
            return;
        }

        long jumlahSetoran = (sisa + setoran - 1) / setoran;

        int hariPerPeriode;
        String namaPeriode;
        int checkedId = rgPeriode.getCheckedRadioButtonId();
        if (checkedId == R.id.rbMingguan) {
            hariPerPeriode = 7;
            namaPeriode = getString(R.string.periode_mingguan);
        } else if (checkedId == R.id.rbBulanan) {
            hariPerPeriode = 30;
            namaPeriode = getString(R.string.periode_bulanan);
        } else {
            hariPerPeriode = 1;
            namaPeriode = getString(R.string.periode_harian);
        }

        long totalHari = jumlahSetoran * hariPerPeriode;
        if (totalHari > MAKS_HARI) {
            tvHasil.setText(getString(R.string.result_too_long, formatRupiah(sisa)));
            return;
        }

        Calendar kalender = Calendar.getInstance();
        kalender.add(Calendar.DAY_OF_YEAR, (int) totalHari);
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        String tanggal = format.format(kalender.getTime());

        String durasi = buatTeksDurasi(totalHari);

        tvHasil.setText(getString(
                R.string.result_target,
                formatRupiah(sisa),
                jumlahSetoran,
                namaPeriode,
                durasi,
                tanggal));
    }

    private void hitungTantangan() {
        tilBase.setError(null);

        long base = ambilNominal(etBase);
        if (base <= 0) {
            tilBase.setError(getString(R.string.error_base));
            tvTantangan.setVisibility(View.GONE);
            return;
        }

        long total = base * 52L * 53L / 2L;
        long terakhir = base * 52L;

        tvTantangan.setText(getString(
                R.string.result_challenge,
                formatRupiah(base),
                formatRupiah(terakhir),
                formatRupiah(total)));
        tvTantangan.setVisibility(View.VISIBLE);
    }

    private void resetSimulasi() {
        etTarget.setText("");
        etSaldo.setText("");
        etSetoran.setText("");
        tilTarget.setError(null);
        tilSaldo.setError(null);
        tilSetoran.setError(null);
        rgPeriode.check(R.id.rbHarian);
        layoutHasil.setVisibility(View.GONE);
        pbProgress.setProgress(0);
        etTarget.requestFocus();
    }

    private String buatTeksDurasi(long totalHari) {
        long tahun = totalHari / 365L;
        long sisaHari = totalHari % 365L;
        long bulan = sisaHari / 30L;
        long hari = sisaHari % 30L;

        StringBuilder sb = new StringBuilder();
        if (tahun > 0) {
            sb.append(tahun).append(' ').append(getString(R.string.unit_year)).append(' ');
        }
        if (bulan > 0) {
            sb.append(bulan).append(' ').append(getString(R.string.unit_month)).append(' ');
        }
        if (hari > 0 || sb.length() == 0) {
            sb.append(hari).append(' ').append(getString(R.string.unit_day));
        }
        return sb.toString().trim();
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