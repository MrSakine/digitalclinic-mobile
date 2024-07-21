package com.fst.digitalclinic.outils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.fst.digitalclinic.*;
import com.fst.digitalclinic.adapters.PatientArrayAdapter;
import com.fst.digitalclinic.adapters.SpeArrayAdapter;
import com.fst.digitalclinic.adapters.UserListView;
import com.fst.digitalclinic.objects.Medecin;
import com.fst.digitalclinic.objects.Patient;
import com.fst.digitalclinic.objects.Specialite;
import java.util.ArrayList;
import java.util.Objects;

public class ItemSearcher implements TextWatcher {
    ListView listView;
    UserListView userListView;
    PatientArrayAdapter patientArrayAdapter;
    SpeArrayAdapter speArrayAdapter;
    LinearLayout linearLayout;
    JSONParser jsonParser;
    String token;
    ObjectGetter objectGetter;
    String state;
    TextView noUserFound;

    public ItemSearcher(ListView listView,
                        @Nullable UserListView userListView, LinearLayout linearLayout, @Nullable PatientArrayAdapter patientArrayAdapter,
                        SpeArrayAdapter speArrayAdapter,
                        String state,
                        TextView noUserFound) {
        this.listView = listView;
        this.userListView = userListView;
        this.patientArrayAdapter = patientArrayAdapter;
        this.speArrayAdapter = speArrayAdapter;
        this.linearLayout = linearLayout;
        this.state = state;
        this.noUserFound = noUserFound;
        jsonParser = new JSONParser();
        SharedPreferences sharedPreferences = this.listView.getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("auth_token", null);
        objectGetter = new ObjectGetter(jsonParser, token);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (Objects.equals(this.state, "MED")) {
            ArrayList<Medecin> found_meds = new ArrayList<>();
            if (!editable.toString().equals("")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        for(Medecin value : objectGetter.getMedecins()) {
                            if (
                                    value.getNom().toLowerCase().contains(editable.toString().toLowerCase())
                                            || value.getPrenom().toLowerCase().contains(editable.toString().toLowerCase())) {
                                found_meds.add(value);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (found_meds.size() > 0) {
                        userListView.clear();
                        linearLayout.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        userListView.addAll(found_meds);
                        userListView.notifyDataSetChanged();
                    }else {
                        listView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        this.noUserFound.setText(R.string.no_med_found);
                    }
                }
            }else{
                linearLayout.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                try {
                    userListView.clear();
                    userListView.addAll(objectGetter.getMedecins());
                    userListView.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else if (Objects.equals(this.state, "PAT")){
            ArrayList<Patient> found_pats = new ArrayList<>();
            if (!editable.toString().equals("")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        for(Patient value : objectGetter.getPatients()) {
                            if (
                                    value.getNom().toLowerCase().contains(editable.toString().toLowerCase())
                                            || value.getPrenom().toLowerCase().contains(editable.toString().toLowerCase())) {
                                found_pats.add(value);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (found_pats.size() > 0) {
                        patientArrayAdapter.clear();
                        linearLayout.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        patientArrayAdapter.addAll(found_pats);
                        patientArrayAdapter.notifyDataSetChanged();
                    }else {
                        listView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        this.noUserFound.setText(R.string.no_pat_found);
                    }
                }
            }else{
                linearLayout.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                try {
                    patientArrayAdapter.clear();
                    patientArrayAdapter.addAll(objectGetter.getPatients());
                    patientArrayAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            ArrayList<Specialite> found_spes = new ArrayList<>();
            if (!editable.toString().equals("")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        for(Specialite value : objectGetter.getSpecialiteArrayList()) {
                            if (
                                    value.getNom().toLowerCase().contains(editable.toString().toLowerCase())
                                            || value.getNom().toLowerCase().contains(editable.toString().toLowerCase())) {
                                found_spes.add(value);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (found_spes.size() > 0) {
                        speArrayAdapter.clear();
                        linearLayout.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        speArrayAdapter.addAll(found_spes);
                        speArrayAdapter.notifyDataSetChanged();
                    }else {
                        listView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        this.noUserFound.setText(R.string.no_spe_found);
                    }
                }
            }else{
                linearLayout.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                try {
                    speArrayAdapter.clear();
                    speArrayAdapter.addAll(objectGetter.getSpecialiteArrayList());
                    speArrayAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
