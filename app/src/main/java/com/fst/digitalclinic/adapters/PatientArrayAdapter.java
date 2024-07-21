package com.fst.digitalclinic.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.fst.digitalclinic.R;
import com.fst.digitalclinic.users.RemoveMedecinActivity;
import com.fst.digitalclinic.objects.Patient;

import java.util.ArrayList;

public class PatientArrayAdapter extends ArrayAdapter<Patient> {
    ImageView imageView;
    TextView textView;
    ImageView see, delete;
    String state;
    ActivityResultLauncher<Intent> launcher;
    ActivityResultLauncher<Intent> launcherDelPat;

    public PatientArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Patient> patients,
                               @Nullable ActivityResultLauncher<Intent> launcher,
                               @Nullable ActivityResultLauncher<Intent> launcherDelPat,
                               String state) {
        super(context, resource, patients);
        this.launcher = launcher;
        this.launcherDelPat = launcherDelPat;
        this.state = state;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.patient_list, parent, false);
        }
        Patient patient = getItem(position);
        imageView = convertView.findViewById(R.id.patient_list_icon);
        textView = convertView.findViewById(R.id.patient_list_text);
        see = convertView.findViewById(R.id.patient_list_see);
        see.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.patinfo);
            dialog.setCancelable(false);
            final TextView name = dialog.findViewById(R.id.pat_info_name);
            final TextView surname = dialog.findViewById(R.id.pat_info_surname);
            final TextView genre = dialog.findViewById(R.id.pat_info_genre);
            final TextView dob = dialog.findViewById(R.id.pat_info_dob);
            final TextView adresse = dialog.findViewById(R.id.pat_info_adresse);
            final TextView tel = dialog.findViewById(R.id.pat_info_tel);
            final TextView mail = dialog.findViewById(R.id.pat_info_mail);
            final Button close = dialog.findViewById(R.id.pat_info_close_btn);
            name.setText(patient.getNom().toLowerCase());
            surname.setText(patient.getPrenom().toLowerCase());
            genre.setText(patient.getGenre() ? "Homme".toLowerCase() : "Femme".toLowerCase());
            dob.setText(patient.getDateNaissance());
            adresse.setText(patient.getAdresse().toLowerCase());
            tel.setText(patient.getTelephone());
            mail.setText(patient.getEmail().toLowerCase());
            close.setOnClickListener(x -> dialog.dismiss());
            dialog.show();
        });
        delete = convertView.findViewById(R.id.patient_list_delete);
        delete.setOnClickListener(v -> {
            Intent deletePat = new Intent(getContext(), RemoveMedecinActivity.class);
            deletePat.putExtra("patient_id", patient.getId());
            deletePat.putExtra("patient_fullname", patient.getNom() + ' ' + patient.getPrenom());
            deletePat.putExtra("state", this.state);
            if (this.launcher != null) {
                this.launcher.launch(deletePat);
            }else {
                if (this.launcherDelPat != null) {
                    this.launcherDelPat.launch(deletePat);
                }
            }
        });
        if (patient.getGenre()) {
            imageView.setImageResource(R.drawable.man);
            textView.setText(String.format("Mr. %s %s", patient.getNom() , patient.getPrenom()));
        }else{
            imageView.setImageResource(R.drawable.woman);
            textView.setText(String.format("Mme. %s %s", patient.getNom() , patient.getPrenom()));
        }
        return convertView;
    }
}
