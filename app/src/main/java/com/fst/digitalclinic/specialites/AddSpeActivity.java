package com.fst.digitalclinic.specialites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.fst.digitalclinic.R;
import com.fst.digitalclinic.objects.Specialite;
import com.fst.digitalclinic.outils.JSONParser;
import com.fst.digitalclinic.outils.TextInputLayoutErrorRemover;
import com.fst.digitalclinic.outils.ValidInput;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import okhttp3.Response;
import org.json.JSONObject;
import java.util.Objects;

public class AddSpeActivity extends AppCompatActivity {
    TextInputLayout textInputLayout;
    TextInputLayout descriptionInputLayout;
    TextInputLayout typeInputLayout;
    TextInputLayout prixInputLayout;
    TextInputEditText textInputEditText;
    TextInputEditText descriptionInputText;
    TextInputEditText typeInputText;
    TextInputEditText prixInputText;
    Button button;
    ValidInput validInput;
    JSONParser jsonParser;
    String token;
    String state;
    Response r;
    Specialite specialite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spe);
        state = getIntent().getStringExtra("state");
        textInputLayout = findViewById(R.id.add_new_spe_layout);
        descriptionInputLayout = findViewById(R.id.add_new_spe_desc_layout);
        typeInputLayout = findViewById(R.id.add_new_spe_type_layout);
        prixInputLayout = findViewById(R.id.add_new_spe_prix_layout);
        textInputEditText = findViewById(R.id.add_new_spe_input);
        descriptionInputText = findViewById(R.id.add_new_spe_desc_input);
        typeInputText = findViewById(R.id.add_new_spe_type_input);
        prixInputText = findViewById(R.id.add_new_spe_prix_input);
        button = findViewById(R.id.add_new_spe_button);
        validInput = new ValidInput();
        jsonParser = new JSONParser();
        if (state.equals("editing")) {
            setTitle(R.string.edit_new_spe);
            specialite = getIntent().getParcelableExtra("details");
            textInputEditText.setText(specialite.getNom());
            typeInputText.setText(specialite.getType());
            prixInputText.setText(String.format("%s", specialite.getPrix()));
            descriptionInputText.setText(specialite.getDescription());
        }
        button.setOnClickListener(v -> {
            String value = Objects.requireNonNull(textInputEditText.getText()).toString(),
                    desc = Objects.requireNonNull(descriptionInputText.getText()).toString(),
                    type = Objects.requireNonNull(typeInputText.getText()).toString(),
                    prix = Objects.requireNonNull(prixInputText.getText()).toString();
            boolean s = validInput.isValidSpe(value),
                    d = validInput.isValidLongText(desc),
                    t = validInput.isValidNonDigit(type),
                    p = validInput.isValidNumber(prix);
            if (s && d && t && p) {
                SharedPreferences sharedPreferences = this.getSharedPreferences("token", Context.MODE_PRIVATE);
                token = sharedPreferences.getString("auth_token", null);
                String data = String.format("{\"spe\": \"%s\", \"desc\": \"%s\", \"type\": \"%s\", \"prix\": \"%s\"}", value, desc, type, prix);
                try {
                    if (state.equals("editing")) {
                        r = jsonParser.putRequest(String.format("/home/spe/edit/%s", specialite.getId()), data, token);
                    }else{
                        r = jsonParser.postRequest("/home/spe/add", data, token);
                    }
                    if (r.code() == 200) {
                        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(r.body()).string());
                        if (jsonObject.getString("message").equals("success")) {
                            Intent returnIntent = new Intent();
                            if (state.equals("reading")){
                                returnIntent.putExtra("new_spe", true);
                            }else{
                                returnIntent.putExtra("edited_spe", true);
                            }
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }else{
                            textInputLayout.setErrorEnabled(true);
                            textInputLayout.setError("Spécialité existante");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (!d && !s && !t && !p){
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError("Vérifier la valeur de votre spécialité");
                descriptionInputLayout.setErrorEnabled(true);
                descriptionInputText.setError("Vérifier la valeur de votre description");
                typeInputLayout.setErrorEnabled(true);
                typeInputText.setError("Vérifier la valeur de votre type");
                prixInputLayout.setErrorEnabled(true);
                prixInputText.setError("Vérifier la valeur de votre ");
            }else if (!s) {
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError("Vérifier la valeur de votre spécialité");
            }else if (!d){
                descriptionInputLayout.setErrorEnabled(true);
                descriptionInputText.setError("Vérifier la valeur de votre description");
            }else if (!t) {
                typeInputLayout.setErrorEnabled(true);
                typeInputText.setError("Vérifier la valeur de votre type");
            }else{
                prixInputLayout.setErrorEnabled(true);
                prixInputText.setError("Vérifier la valeur de votre ");
            }
        });
        textInputEditText.addTextChangedListener(new TextInputLayoutErrorRemover(textInputLayout));
        descriptionInputText.addTextChangedListener(new TextInputLayoutErrorRemover((descriptionInputLayout)));
        typeInputText.addTextChangedListener(new TextInputLayoutErrorRemover(typeInputLayout));
        prixInputText.addTextChangedListener(new TextInputLayoutErrorRemover(prixInputLayout));
    }
}