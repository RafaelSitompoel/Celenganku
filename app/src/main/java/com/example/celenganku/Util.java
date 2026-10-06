package com.example.celenganku;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.Locale;

public final class Util {

    private static final Locale LOKAL = Locale.forLanguageTag("id-ID");

    private Util() {
    }

    public static double ambil(TextInputEditText editText) {
        if (editText.getText() == null) {
            return 0d;
        }
        String s = editText.getText().toString().trim().replace(',', '.');
        if (s.isEmpty()) {
            return 0d;
        }
        try {
            double nilai = Double.parseDouble(s);
            if (Double.isNaN(nilai) || Double.isInfinite(nilai)) {
                return 0d;
            }
            return nilai;
        } catch (NumberFormatException e) {
            return 0d;
        }
    }

    public static String rupiah(double nilai) {
        if (Double.isNaN(nilai) || Double.isInfinite(nilai) || Math.abs(nilai) > 1.0e15) {
            return "Terlalu besar";
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance(LOKAL);
        nf.setMaximumFractionDigits(0);
        return nf.format(Math.round(nilai));
    }

    public static String persen(double nilai) {
        return String.format(LOKAL, "%.2f", nilai) + "%";
    }

    public static void sembunyikanKeyboard(Activity activity) {
        View fokus = activity.getCurrentFocus();
        if (fokus != null) {
            InputMethodManager imm =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(fokus.getWindowToken(), 0);
            }
            fokus.clearFocus();
        }
    }
}