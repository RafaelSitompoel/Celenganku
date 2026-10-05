package com.example.celenganku;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public final class PasswordToggle {

    private PasswordToggle() {
    }

    public static void pasang(final TextInputLayout layout, final TextInputEditText editText) {
        layout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        layout.setEndIconDrawable(R.drawable.ic_eye);
        layout.setEndIconContentDescription(layout.getContext().getString(R.string.cd_show_password));
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        layout.setEndIconOnClickListener(new View.OnClickListener() {
            private boolean tampil = false;

            @Override
            public void onClick(View v) {
                tampil = !tampil;
                int posisi = editText.getSelectionEnd();

                if (tampil) {
                    editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    layout.setEndIconDrawable(R.drawable.ic_eye_off);
                    layout.setEndIconContentDescription(
                            layout.getContext().getString(R.string.cd_hide_password));
                } else {
                    editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    layout.setEndIconDrawable(R.drawable.ic_eye);
                    layout.setEndIconContentDescription(
                            layout.getContext().getString(R.string.cd_show_password));
                }

                if (posisi >= 0) {
                    editText.setSelection(posisi);
                }
            }
        });
    }
}