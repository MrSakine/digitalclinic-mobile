package com.fst.digitalclinic.specialites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.fst.digitalclinic.R;
import com.fst.digitalclinic.adapters.SpeArrayAdapter;
import com.fst.digitalclinic.objects.Specialite;
import com.fst.digitalclinic.outils.JSONParser;
import com.fst.digitalclinic.outils.ObjectGetter;
import com.fst.digitalclinic.users.SearchMedecinActivity;
import java.util.ArrayList;
import java.util.Objects;

public class SpesActivity extends AppCompatActivity {
    JSONParser jsonParser;
    String token;
    ObjectGetter objectGetter;
    boolean first_time_use;
    ListView spe_listview;
    TextView spe_textview;
    SpeArrayAdapter speArrayAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<Specialite> specialiteArrayList;

    ActivityResultLauncher<Intent> launcherSpe = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK){
                    assert result.getData() != null;
                    if (result.getData().getBooleanExtra("new_spe", false)) {
                        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                        dialog.setTitleText("Succès");
                        dialog.setContentText("Spécialité ajoutée avec succès");
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setConfirmText("Fermer");
                        dialog.setConfirmClickListener(sweetAlertDialog -> dialog.cancel());
                        dialog.show();
                    }else if (result.getData().getBooleanExtra("del_spe", false)) {
                        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                        dialog.setTitleText("Succès");
                        dialog.setContentText("Spécialité supprimée avec succès");
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setConfirmText("Fermer");
                        dialog.setConfirmClickListener(sweetAlertDialog -> dialog.cancel());
                        dialog.show();
                    }else{
                        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                        dialog.setTitleText("Succès");
                        dialog.setContentText("Spécialité modifiée avec succès");
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setConfirmText("Fermer");
                        dialog.setConfirmClickListener(sweetAlertDialog -> dialog.cancel());
                        dialog.show();
                    }
                    speArrayAdapter.clear();
                    try {
                        speArrayAdapter.addAll(objectGetter.getSpecialiteArrayList());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    speArrayAdapter.notifyDataSetChanged();
                }
            }
    );

    public void el(ArrayList<Specialite> specialites) {
        if (specialites.size() > 0) {
            spe_textview.setVisibility(View.GONE);
            spe_listview.setVisibility(View.VISIBLE);
            speArrayAdapter = new SpeArrayAdapter(this, R.layout.spe_list, specialites, launcherSpe);
            spe_listview.setAdapter(speArrayAdapter);
        }else{
            spe_listview.setVisibility(View.GONE);
            spe_textview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedP = getSharedPreferences("first_time", Context.MODE_PRIVATE);
        first_time_use = sharedP.getBoolean("first_time_use", false);
        setContentView(R.layout.activity_spes);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        spe_listview = findViewById(R.id.spe_list_adapter);
        spe_textview = findViewById(R.id.spe_list_textview);
        spe_listview.setDivider(null);
        swipeRefreshLayout = findViewById(R.id.spe_list_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                el(objectGetter.getSpecialiteArrayList());
            } catch (Exception e) {
                e.printStackTrace();
            }

            swipeRefreshLayout.setRefreshing(false);
        });
        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("auth_token", null);
        jsonParser = new JSONParser();
        objectGetter = new ObjectGetter(jsonParser, token);
        try {
            specialiteArrayList = objectGetter.getSpecialiteArrayList();
            el(specialiteArrayList);
            if (first_time_use && specialiteArrayList.size() <= 0) {
                SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
                dialog.setTitleText("Info");
                dialog.setContentText("Vous n'avez pas encore créé de spécialités. " +
                        "Les spécialités par default seront ajoutés");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setConfirmText("Accepter");
                dialog.setConfirmClickListener(sweetAlertDialog -> {
                    dialog.cancel();
                    try {
                        setup();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                dialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setup() throws Exception {
        jsonParser.getRequest("/home/spes/setup", token);
        SharedPreferences s = getSharedPreferences("first_time", Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor ed = s.edit();
        ed.putBoolean("first_time_use", false);
        ed.apply();
        el(objectGetter.getSpecialiteArrayList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.spe_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }else if (item.getItemId() == R.id.spe_add_menu) {
            Intent addNewSpe = new Intent(getApplicationContext(), AddSpeActivity.class);
            addNewSpe.putExtra("state", "reading");
            launcherSpe.launch(addNewSpe);
        }else{
            Intent search = new Intent(getApplicationContext(), SearchMedecinActivity.class);
            search.putExtra("state", "SPE");
            try {
                search.putParcelableArrayListExtra("spes", objectGetter.getSpecialiteArrayList());
                startActivity(search);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        speArrayAdapter.clear();
        try {
            speArrayAdapter.addAll(objectGetter.getSpecialiteArrayList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        speArrayAdapter.notifyDataSetChanged();
    }
}