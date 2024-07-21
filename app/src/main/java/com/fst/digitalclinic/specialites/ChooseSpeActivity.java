package com.fst.digitalclinic.specialites;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.fst.digitalclinic.R;
import com.fst.digitalclinic.objects.Medecin;
import com.fst.digitalclinic.objects.Specialite;
import com.fst.digitalclinic.outils.JSONParser;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringJoiner;

public class ChooseSpeActivity extends AppCompatActivity {
    ArrayList<Specialite> allSpes;
    ArrayList<Specialite> speChecked;
    ArrayList<Specialite> speCheckedFromIntent;
    JSONParser jsonParser;
    String token;
    ListView listView;
    Button button;
    TextView error;
    Boolean isSpeEdited;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_spe);
        setFinishOnTouchOutside(false);
        SharedPreferences sharedPreferences = this.getSharedPreferences("token", Context.MODE_PRIVATE);
        Medecin medecin = getIntent().getParcelableExtra("med_details");
        isSpeEdited = false;
        token = sharedPreferences.getString("auth_token", null);
        jsonParser = new JSONParser();
        listView = findViewById(R.id.med_info_specialite_listview);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        button = findViewById(R.id.med_info_specialite_button);
        error = findViewById(R.id.med_info_specialite_text_error);
        speChecked = new ArrayList<>();
        speCheckedFromIntent = getIntent().getParcelableArrayListExtra("speChecked");
        try {
            Response r = jsonParser.getRequest("/home/spes", token);
            ArrayList<Specialite> specialiteArrayList = new ArrayList<>();
            if (r.code() == 200) {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(r.body()).string());
                JSONArray jsonArray = jsonObject.getJSONArray("details");
                for (int i=0; i < jsonArray.length(); i++) {
                    specialiteArrayList.add(
                            new Specialite(
                                    jsonArray.getJSONObject(i).getInt("id"),
                                    jsonArray.getJSONObject(i).getString("nom"),
                                    jsonArray.getJSONObject(i).getString("type"),
                                    jsonArray.getJSONObject(i).getDouble("prix"),
                                    jsonArray.getJSONObject(i).getString("description")
                            )
                    );
                }
                allSpes = specialiteArrayList;
                ArrayAdapter<Specialite> arrayAdapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_multiple_choice, allSpes
                );
                listView.setAdapter(arrayAdapter);
                if (medecin != null) {
                    for (int j=0; j<arrayAdapter.getCount(); j++) {
                        Specialite specialite = arrayAdapter.getItem(j);
                        for (String spe : medecin.getSpecialite().split(",")) {
                            if (Objects.equals(specialite.getNom(), spe)) {
                                listView.setItemChecked(j, true);
                                speChecked.add(specialite);
                            }
                        }
                    }
                }else{
                    if (speCheckedFromIntent.size() > 0) {
                        for (int j=0; j<arrayAdapter.getCount(); j++) {
                            Specialite specialite = arrayAdapter.getItem(j);
                            for (Specialite spe : speCheckedFromIntent) {
                                if (Objects.equals(specialite.getNom(), spe.getNom())) {
                                    listView.setItemChecked(j, true);
                                    speChecked.add(specialite);
                                }
                            }
                        }
                    }
                }
                listView.setOnItemClickListener((adapterView, view, i, l) -> {
                    error.setText("");
                    isSpeEdited = true;
                    Specialite spe = (Specialite) listView.getItemAtPosition(i);
                    if (listView.isItemChecked(i)) {
                        listView.setItemChecked(i, true);
                        speChecked.add(spe);
                    }else{
                        listView.setItemChecked(i, false);
                        speChecked.remove(spe);
                    }
                });
                button.setOnClickListener(v -> {
                    if (speChecked.size() > 0) {
                        if (medecin == null) {
                            Intent returnIntent = new Intent();
                            returnIntent.putParcelableArrayListExtra("speChecked", speChecked);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }else{
                            if (!isSpeEdited) {
                                SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                                dialog.setTitleText("Erreur");
                                dialog.setContentText("Aucune modification n'a été effectuée");
                                dialog.setCancelable(true);
                                dialog.setCanceledOnTouchOutside(true);
                                dialog.setConfirmText("Ok");
                                dialog.setConfirmClickListener(sweetAlertDialog -> dialog.cancel());
                                dialog.show();
                            }else {
                                String url = String.format("/home/spe/editMed/spe/%s", medecin.getId());
                                String data = String.format("{\"spe\": \"%s\"}", join(",", speChecked));
                                try {
                                    Response response = jsonParser.putRequest(url, data, token);
                                    if (response.code() == 200) {
                                        JSONObject jsonObject2 = new JSONObject(Objects.requireNonNull(response.body()).string());
                                        if (jsonObject2.getString("code").equals("SUCCESS")) {
                                            Intent returnIntent = new Intent();
                                            returnIntent.putExtra("spe_edited", true);
                                            setResult(Activity.RESULT_OK, returnIntent);
                                            finish();
                                        }
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }else{
                        error.setText(R.string.specialite_text_err);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String join(String delimiter, ArrayList<Specialite> speArrayList) {
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        speArrayList.forEach((value) -> stringJoiner.add(value.getNom()));
        return stringJoiner.toString();
    }

    @Override
    public void onBackPressed() {
        if (speChecked.size() == 0) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("empty", true);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        super.onBackPressed();
    }
}