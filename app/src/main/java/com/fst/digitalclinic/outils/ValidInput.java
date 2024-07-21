package com.fst.digitalclinic.outils;

import android.os.Build;
import androidx.annotation.RequiresApi;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Pattern;

public class ValidInput {
    public static final Pattern validMail = Pattern.compile("^\\S+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]{2,3})$");
    public static final Pattern validPhone = Pattern.compile("^([5-9])[0-9]{7}$");
    public static final Pattern validNonDigit = Pattern.compile("^[a-zA-Z\\s]+$");
    public static final Pattern validLongText = Pattern.compile("^[a-zA-Z\\D][^#(0-9)]+$");
    public static final Pattern validNonUnicode = Pattern.compile("^[a-zA-Z0-9\\s]+$");
    public static final Pattern validSpe = Pattern.compile("^[a-zA-Z\\s\\W]+$");
    public static final Pattern validNumber = Pattern.compile("^[0-9]+(|\\.[0-9]{1,2})$");

    public boolean isValidMail(String mail) {
        return validMail.matcher(mail).matches();
    }

    public boolean isValidPassword(String password) {
        return password.length() > 6;
    }

    public boolean isValidPhoneNumber(String phone) {
        return validPhone.matcher(phone).matches();
    }

    public boolean isValidNonDigit(String value) {
        return validNonDigit.matcher(value).matches();
    }

    public boolean isValidLongText(String value) {
        return validLongText.matcher(value).matches();
    }

    public boolean isValidNonUnicode(String value) {
        return validNonUnicode.matcher(value).matches();
    }

    public boolean isValidSpe(String value) { return validSpe.matcher(value).matches(); }

    public boolean isValidNumber(String value) { return validNumber.matcher(value).matches(); }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isValidDate(String value) {
        if (Objects.equals(value, "")) {
            return false;
        }
        Integer med_year = Integer.valueOf(value.split("/")[2]);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy");
        Integer year = Integer.valueOf(dateTimeFormatter.format(LocalDateTime.now()));
        return med_year < year && year - med_year >= 18;
    }

    public String genPassword() {
        String alphas = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVXYZ0123456789!@#$%^&*())_+";
        StringBuilder password = new StringBuilder();
        for(int j=0; j<=15; j++) {
            double r = Math.floor(Math.random() * alphas.length());
            password.append(alphas.charAt((int) r));
        }
        return password.toString();
    }
}
