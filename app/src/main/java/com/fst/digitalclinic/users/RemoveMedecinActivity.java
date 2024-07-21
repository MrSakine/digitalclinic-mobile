package com.fst.digitalclinic.users;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.fst.digitalclinic.R;
import com.fst.digitalclinic.outils.JSONParser;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Objects;

public class RemoveMedecinActivity extends AppCompatActivity {
    JSONParser jsonParser;
    Integer id;
    String fullname;
    TextView delMedText;
    Button yes;
    Button nope;
    String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_medecin);
        this.setFinishOnTouchOutside(true);
        state = getIntent().getStringExtra("state");
        delMedText = findViewById(R.id.del_med_text);
        yes = findViewById(R.id.del_med_button_yes);
        nope = findViewById(R.id.del_med_button_nope);
        jsonParser = new JSONParser();
        if (Objects.equals(state, "MED")) {
            id = getIntent().getIntExtra("medecin_id", 0);
            fullname = getIntent().getStringExtra("medecin_fullname");
            nope.setOnClickListener(v -> finish());
            delMedText.setText(getString(R.string.supp_med, String.format("\"%s\"", fullname)));
            String url = String.format("/home/delete/%s", id);
            SharedPreferences sharedPreferences = this.getSharedPreferences("token", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("auth_token", null);
            yes.setOnClickListener(v -> {
                try {
                    Response response = jsonParser.deleteRequest(url, token);
                    if (response.code() == 200) {
                        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                        if (jsonObject.getString("message").equals("ok")) {
                            SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setTitleText("Succès");
                            dialog.setContentText("Medecin supprimé avec succès");
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setConfirmText("Fermer");
                            dialog.setConfirmClickListener(sweetAlertDialog -> {
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                dialog.cancel();
                                finish();
                            });
                            dialog.show();
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            });
        }else{
            setTitle(R.string.del_pat);
            id = getIntent().getIntExtra("patient_id", 0);
            fullname = getIntent().getStringExtra("patient_fullname");
            nope.setOnClickListener(v -> finish());
            delMedText.setText(getString(R.string.supp_pat, String.format("\"%s\"", fullname)));
            String url = String.format("/home/patient/delete/%s", id);
            SharedPreferences sharedPreferences = this.getSharedPreferences("token", Context.MODE_PRIVATE);
            String token = sharedPreferences.getString("auth_token", null);
            yes.setOnClickListener(v -> {
                try {
                    Response response = jsonParser.deleteRequest(url, token);
                    if (response.code() == 200) {
                        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                        if (jsonObject.getString("message").equals("ok")) {
                            SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setTitleText("Succès");
                            dialog.setContentText("Patient supprimé avec succès");
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setConfirmText("Fermer");
                            dialog.setConfirmClickListener(sweetAlertDialog -> {
                                Intent returnIntent = new Intent();
                                setResult(Activity.RESULT_OK, returnIntent);
                                sweetAlertDialog.cancel();
                                finish();
                            });
                            dialog.show();
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}