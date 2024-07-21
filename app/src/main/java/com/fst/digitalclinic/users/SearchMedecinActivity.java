package com.fst.digitalclinic.users;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.fst.digitalclinic.R;
import com.fst.digitalclinic.adapters.PatientArrayAdapter;
import com.fst.digitalclinic.adapters.SpeArrayAdapter;
import com.fst.digitalclinic.adapters.UserListView;
import com.fst.digitalclinic.objects.Medecin;
import com.fst.digitalclinic.objects.Patient;
import com.fst.digitalclinic.objects.Specialite;
import com.fst.digitalclinic.outils.JSONParser;
import com.fst.digitalclinic.outils.ItemSearcher;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Objects;

public class SearchMedecinActivity extends AppCompatActivity {
    ArrayList<Medecin> allMeds;
    ArrayList<Patient> allPats;
    ArrayList<Specialite> allSpes;
    ListView listView;
    UserListView userListView;
    PatientArrayAdapter patientArrayAdapter;
    SpeArrayAdapter speArrayAdapter;
    EditText input;
    LinearLayout no_med_found;
    ItemSearcher itemSearcher;
    JSONParser jsonParser;
    String token;
    String state;
    TextView no_user_found;

    ActivityResultLauncher<Intent> launcherDelMed = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    if (Objects.equals(state, "MED")) {
                        if (result.getData().getBooleanExtra("spe_edited", false)) {
                            SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setTitleText("Succès");
                            dialog.setContentText("Spécialité modifiée avec succès");
                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setConfirmText("Fermer");
                            dialog.setConfirmClickListener(sweetAlertDialog -> dialog.cancel());
                            dialog.show();
                        }
                        userListView.clear();
                        try {
                            userListView.addAll(getMedecins(jsonParser));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        userListView.notifyDataSetChanged();
                    }else if (Objects.equals(state, "PAT")){
                        patientArrayAdapter.clear();
                        try {
                            patientArrayAdapter.addAll(getPatients(jsonParser));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        patientArrayAdapter.notifyDataSetChanged();
                    }else{
                        speArrayAdapter.clear();
                        try {
                            speArrayAdapter.addAll(getSpecialiteArrayList(jsonParser));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        speArrayAdapter.notifyDataSetChanged();
                    }
                    input.setText("");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_medecin);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        jsonParser = new JSONParser();
        SharedPreferences sharedPreferences = this.getSharedPreferences("token", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("auth_token", null);
        listView = findViewById(R.id.med_search_listview);
        listView.setDivider(null);
        input = findViewById(R.id.med_search_edittext);
        no_med_found = findViewById(R.id.no_med_found_layout);
        no_user_found = findViewById(R.id.no_user_found);
        state = getIntent().getStringExtra("state");
        if (Objects.equals(state, "MED")) {
            setTitle("Recherche d'un medecin");
            allMeds = this.getIntent().getParcelableArrayListExtra("medecins");
            userListView = new UserListView(this, R.layout.userlist, allMeds, null, launcherDelMed, state);
            listView.setAdapter(userListView);
            itemSearcher = new ItemSearcher(listView, userListView, no_med_found, null, null, state, no_user_found);
        }else if (Objects.equals(state, "PAT")){
            setTitle("Recherche d'un patient");
            allPats = this.getIntent().getParcelableArrayListExtra("patients");
            patientArrayAdapter = new PatientArrayAdapter(this, R.layout.patient_list, allPats, null, launcherDelMed, state);
            listView.setAdapter(patientArrayAdapter);
            itemSearcher = new ItemSearcher(listView, null, no_med_found, patientArrayAdapter, null, state, no_user_found);
        }else{
            setTitle("Recherche d'une spécialité");
            allSpes = this.getIntent().getParcelableArrayListExtra("spes");
            speArrayAdapter = new SpeArrayAdapter(this, R.layout.userlist, allSpes, launcherDelMed);
            listView.setAdapter(speArrayAdapter);
            itemSearcher = new ItemSearcher(listView, null, no_med_found, null, speArrayAdapter, state, no_user_found);
        }
        input.addTextChangedListener(itemSearcher);
    }

    public ArrayList<Medecin> getMedecins(JSONParser jsonParser) throws Exception {
        Response r = jsonParser.getRequest("/home", token);
        ArrayList<Medecin> medecins = new ArrayList<>();
        if (r.code() == 200) {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(r.body()).string());
            JSONArray jsonArray = jsonObject.getJSONArray("details");
            for(int i=0; i < jsonArray.length(); i++) {
                medecins.add(
                        new Medecin(
                                jsonArray.getJSONObject(i).getInt("id"),
                                jsonArray.getJSONObject(i).getString("nom"),
                                jsonArray.getJSONObject(i).getString("prenom"),
                                jsonArray.getJSONObject(i).getBoolean("genre"),
                                jsonArray.getJSONObject(i).getString("dob"),
                                jsonArray.getJSONObject(i).getString("adresse"),
                                jsonArray.getJSONObject(i).getString("telephone"),
                                jsonArray.getJSONObject(i).getString("email"),
                                null,
                                jsonArray.getJSONObject(i).getString("hopital"),
                                jsonArray.getJSONObject(i).getString("specialite")
                        )
                );
            }
        }

        return medecins;
    }

    public ArrayList<Patient> getPatients(JSONParser jsonParser) throws Exception {
        Response res = jsonParser.getRequest("/home/patients", token);
        ArrayList<Patient> patients = new ArrayList<>();
        if (res.code() == 200) {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(res.body()).string());
            JSONArray jsonArray = jsonObject.getJSONArray("details");
            for(int i=0; i < jsonArray.length(); i++) {
                patients.add(
                        new Patient(
                                jsonArray.getJSONObject(i).getInt("id"),
                                jsonArray.getJSONObject(i).getString("nom"),
                                jsonArray.getJSONObject(i).getString("prenom"),
                                jsonArray.getJSONObject(i).getBoolean("genre"),
                                jsonArray.getJSONObject(i).getString("dob"),
                                jsonArray.getJSONObject(i).getString("adresse"),
                                jsonArray.getJSONObject(i).getString("telephone"),
                                jsonArray.getJSONObject(i).getString("email")
                        )
                );
            }
        }
        return patients;
    }

    public ArrayList<Specialite> getSpecialiteArrayList(@NotNull JSONParser jsonParser) throws Exception {
        Response r = jsonParser.getRequest("/home/spes", token);
        ArrayList<Specialite> specialites = new ArrayList<>();
        if (r.code() == 200) {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(r.body()).string());
            JSONArray jsonArray = jsonObject.getJSONArray("details");
            for (int i=0; i < jsonArray.length(); i++) {
                specialites.add(
                        new Specialite(
                                jsonArray.getJSONObject(i).getInt("id"),
                                jsonArray.getJSONObject(i).getString("nom"),
                                jsonArray.getJSONObject(i).getString("type"),
                                jsonArray.getJSONObject(i).getDouble("prix"),
                                jsonArray.getJSONObject(i).getString("description")
                        )
                );
            }
        }

        return specialites;
    }

    @SuppressLint("InflateParams")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}