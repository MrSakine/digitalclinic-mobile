package com.fst.digitalclinic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.fst.digitalclinic.adapters.PatientArrayAdapter;
import com.fst.digitalclinic.adapters.UserListView;
import com.fst.digitalclinic.objects.Medecin;
import com.fst.digitalclinic.objects.Patient;
import com.fst.digitalclinic.outils.JSONParser;
import com.fst.digitalclinic.outils.ObjectGetter;
import com.fst.digitalclinic.specialites.SpesActivity;
import com.fst.digitalclinic.users.AddMedecinActivity;
import com.fst.digitalclinic.users.SearchMedecinActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import okhttp3.Response;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    JSONParser jsonParser;
    String token;
    ObjectGetter objectGetter;
    ListView listView;
    UserListView userListView;
    PatientArrayAdapter patientArrayAdapter;
    ArrayList<Medecin> allMeds;
    ArrayList<Patient> allPats;
    TextView no_med;
    BottomNavigationView bottomNavigationView;
    MenuItem add;
    Boolean hide;
    String STATE;
    SwipeRefreshLayout swipeRefreshLayout;

    ActivityResultLauncher<Intent> launcherMed = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK){
                    assert result.getData() != null;
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
                        userListView.addAll(objectGetter.getMedecins());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    userListView.notifyDataSetChanged();
                }
            }
    );

    ActivityResultLauncher<Intent> launcherPat = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    patientArrayAdapter.clear();
                    try {
                        patientArrayAdapter.addAll(objectGetter.getPatients());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    patientArrayAdapter.notifyDataSetChanged();
                }
            }
    );

    public void el(ArrayList<Medecin> meds, ArrayList<Patient> pats) {
        if (Objects.equals(STATE, "MED")) {
            if (meds.size() > 0) {
                no_med.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                userListView = new UserListView(this, R.layout.userlist, meds, launcherMed, null, STATE);
                listView.setAdapter(userListView);
            }else{
                no_med.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        }else{
            if (pats.size() > 0) {
                no_med.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                patientArrayAdapter = new PatientArrayAdapter(this, R.layout.patient_list, pats, launcherPat, null, STATE);
                listView.setAdapter(patientArrayAdapter);
            }else{
                no_med.setVisibility(View.VISIBLE);
                no_med.setText(R.string.no_pat);
                listView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hide = false;
        STATE = "MED";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        listView = findViewById(R.id.listview_user);
        listView.setDivider(null);
        no_med = findViewById(R.id.no_med);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                el(objectGetter.getMedecins(), objectGetter.getPatients());
            } catch (Exception e) {
                e.printStackTrace();
            }
            swipeRefreshLayout.setRefreshing(false);
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.meds) {
                STATE = "MED";
                hide = false;
            }else{
                STATE = "PAT";
                hide = true;
            }
            this.invalidateOptionsMenu();
            el(allMeds, allPats);
            return true;
        });
        SharedPreferences sharedPreferences = this.getSharedPreferences("token", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("auth_token", null);
        jsonParser = new JSONParser();
        objectGetter = new ObjectGetter(jsonParser, token);
        try {
            allMeds = objectGetter.getMedecins();
            allPats = objectGetter.getPatients();
            el(allMeds, allPats);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu3, menu);
        add = menu.findItem(R.id.add_menu);
        add.setVisible(!hide);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout_menu) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Êtes-vous sûr ?")
                    .setContentText("Voulez-vous vraiment vous déconnecter ?")
                    .setConfirmText("Oui")
                    .setCancelText("Non")
                    .showCancelButton(true)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        try {
                            Response r = jsonParser.postRequest("/logout", "", token);
                            if (r.code() == 200) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                builder.setTitle("Déconnexion");
                                builder.setMessage(R.string.logout_message);
                                builder.setCancelable(false);
                                builder.setNeutralButton(R.string.close, (dialog, which) -> finish());
                                builder.create();
                                builder.show();
                                SharedPreferences sharedPreferences = this.getSharedPreferences("token", Context.MODE_PRIVATE);
                                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("auth_token", null);
                                editor.apply();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
                    .setCancelClickListener(SweetAlertDialog::cancel)
                    .show();
            return true;
        }else if (item.getItemId() == R.id.add_menu) {
            Intent addMed = new Intent(MainActivity.this, AddMedecinActivity.class);
            launcherMed.launch(addMed);
        }else if (item.getItemId() == R.id.search_menu) {
            Intent search = new Intent(MainActivity.this, SearchMedecinActivity.class);
            try {
                Bundle bundle = new Bundle();
                if (Objects.equals(STATE, "MED")) {
                    search.putParcelableArrayListExtra("medecins", objectGetter.getMedecins());
                }else{
                    search.putParcelableArrayListExtra("patients", objectGetter.getPatients());
                }
                search.putExtra("state", STATE);
                search.putExtras(bundle);
                startActivity(search);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (item.getItemId() == R.id.add_spe_menu) {
            Intent spe = new Intent(MainActivity.this, SpesActivity.class);
            startActivity(spe);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (Objects.equals(STATE, "MED")) {
            userListView.clear();
            try {
                userListView.addAll(objectGetter.getMedecins());
            } catch (Exception e) {
                e.printStackTrace();
            }
            userListView.notifyDataSetChanged();
        }else{
            patientArrayAdapter.clear();
            try {
                patientArrayAdapter.addAll(objectGetter.getPatients());
            } catch (Exception e) {
                e.printStackTrace();
            }
            patientArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {}
}