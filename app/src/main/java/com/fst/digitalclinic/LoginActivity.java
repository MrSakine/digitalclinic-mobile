package com.fst.digitalclinic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import com.fst.digitalclinic.outils.ErrorRemover;
import com.fst.digitalclinic.outils.JSONParser;
import com.fst.digitalclinic.outils.ValidInput;
import okhttp3.Response;
import org.json.JSONObject;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText mail, pass;
    Button login;
    String m, p;
    ValidInput validInput;
    TextView error_mail, error_pass;
    JSONParser jsonParser;

    public void FirstTimeUse(String text, String key) {
        SharedPreferences s = getSharedPreferences(text, Context.MODE_PRIVATE);
        boolean first_time_use = s.getBoolean(key, true);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor ed = s.edit();
        ed.putBoolean(key, first_time_use);
        ed.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        Objects.requireNonNull(getSupportActionBar()).hide();
        FirstTimeUse("first_time", "first_time_use");
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        login = findViewById(R.id.button_login);
        mail = findViewById(R.id.mail_input);
        pass = findViewById(R.id.pass_input);
        error_mail = findViewById(R.id.mail_input_text_error);
        error_pass = findViewById(R.id.pass_input_text_error);
        validInput = new ValidInput();
        jsonParser = new JSONParser();
        login.setOnClickListener(v -> {
            m = mail.getText().toString();
            p = pass.getText().toString();
            boolean v_m = validInput.isValidMail(m);
            boolean v_p = validInput.isValidPassword(p);
            if (!v_m && !v_p){
                error_mail.setText(R.string.mail_error);
                error_pass.setText(R.string.pass_error);
            }else if (!v_m){
                error_mail.setText(R.string.mail_error);
            }else if (!v_p){
                error_pass.setText(R.string.pass_error);
            }else{
                error_mail.setText("");
                error_pass.setText("");
                String data = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", m, p);
                try {
                    Response r = jsonParser.postRequest("/login", data, null);
                    JSONObject jsonObject = new JSONObject(Objects.requireNonNull(r.body()).string());
                    if (r.code() == 401) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Erreur");
                        builder.setMessage(R.string.login_error);
                        builder.setCancelable(false);
                        builder.setNeutralButton(R.string.close, (dialog, which) -> {});
                        builder.create();
                        builder.show();
                    }else{
                        SharedPreferences sharedPref = getSharedPreferences("token", Context.MODE_PRIVATE);
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("auth_token", jsonObject.getString("access_token"));
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mail.addTextChangedListener(new ErrorRemover(error_mail));
        pass.addTextChangedListener(new ErrorRemover(error_pass));
    }
}