package com.fst.digitalclinic.specialites;

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

public class RemoveSpeActivity extends AppCompatActivity {
    JSONParser jsonParser;
    Integer id;
    String fullname;
    TextView delSpeText;
    Button yes;
    Button nope;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_spe);
        delSpeText = findViewById(R.id.del_spe_text);
        yes = findViewById(R.id.del_spe_button_yes);
        nope = findViewById(R.id.del_spe_button_nope);
        nope.setOnClickListener(x -> finish());
        id = getIntent().getIntExtra("spe_id", 0);
        fullname = getIntent().getStringExtra("spe_nom");
        jsonParser = new JSONParser();
        String url = String.format("/home/spe/delete/%s", id);
        SharedPreferences sharedPreferences = this.getSharedPreferences("token", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("auth_token", null);
        delSpeText.setText(getString(R.string.supp_spe, String.format("\"%s\"", fullname)));
        yes.setOnClickListener(y -> {
            try {
                Response response = jsonParser.deleteRequest(url, token);
                if (response.code() == 200) {
                    JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                    if (jsonObject.getString("message").equals("ok")) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("del_spe", true);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
    }
}