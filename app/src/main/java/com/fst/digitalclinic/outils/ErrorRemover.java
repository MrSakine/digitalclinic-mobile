package com.fst.digitalclinic.outils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class ErrorRemover implements TextWatcher {
    public TextView textView;

    public ErrorRemover(TextView textView) {
        this.textView = textView;
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (!this.textView.getText().equals("")) {
            this.textView.setText("");
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
