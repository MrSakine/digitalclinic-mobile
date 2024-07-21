package com.fst.digitalclinic.outils;

import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputLayout;

public class TextInputLayoutErrorRemover implements TextWatcher {
    public TextInputLayout textInputLayout;

    public TextInputLayoutErrorRemover(TextInputLayout t) {
        this.textInputLayout = t;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        this.textInputLayout.setErrorEnabled(false);
        this.textInputLayout.setError("");
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
