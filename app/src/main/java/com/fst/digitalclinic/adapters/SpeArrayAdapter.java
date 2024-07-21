package com.fst.digitalclinic.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import com.fst.digitalclinic.R;
import com.fst.digitalclinic.objects.Specialite;
import com.fst.digitalclinic.specialites.AddSpeActivity;
import com.fst.digitalclinic.specialites.RemoveSpeActivity;
import java.util.ArrayList;

public class SpeArrayAdapter extends ArrayAdapter<Specialite> {
    ImageView imageView;
    TextView textView;
    ImageView button;
    ImageView see;
    ImageView edit;
    ActivityResultLauncher<Intent> launcher;

    public SpeArrayAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Specialite> specialites,
                           @Nullable ActivityResultLauncher<Intent> launcher) {
        super(context, resource, specialites);
        this.launcher = launcher;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spe_list, parent, false);
        }
        Specialite specialite = getItem(position);
        imageView = convertView.findViewById(R.id.spe_list_icon);
        textView = convertView.findViewById(R.id.spe_list_text);
        button = convertView.findViewById(R.id.spe_list_button);
        see = convertView.findViewById(R.id.spe_list_see);
        edit = convertView.findViewById(R.id.spe_list_edit);
        textView.setText(specialite.getNom());
        button.setOnClickListener(v -> {
            Intent delSpe = new Intent(getContext(), RemoveSpeActivity.class);
            delSpe.putExtra("spe_id", specialite.getId());
            delSpe.putExtra("spe_nom", specialite.getNom());
            if (this.launcher != null) {
                this.launcher.launch(delSpe);
            }
        });
        see.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.spe_info_dialog);
            dialog.setCancelable(true);
            final TextView spe_title = dialog.findViewById(R.id.spe_dialog_title);
            final TextView spe_content = dialog.findViewById(R.id.spe_dialog_content);
            final TextView spe_nom = dialog.findViewById(R.id.spe_list_info_nom);
            final TextView spe_type = dialog.findViewById(R.id.spe_list_info_type);
            final TextView spe_prix = dialog.findViewById(R.id.spe_list_info_prix);
            spe_title.setText(HtmlCompat.fromHtml(String.format("<u>%s</u>", specialite.getNom()), HtmlCompat.FROM_HTML_MODE_LEGACY));
            spe_content.setText(specialite.getDescription());
            spe_nom.setText(specialite.getNom());
            spe_type.setText(specialite.getType());
            spe_prix.setText(String.format("%s", specialite.getPrix()));
            dialog.show();
        });
        edit.setOnClickListener(v -> {
            Intent editSpe = new Intent(getContext(), AddSpeActivity.class);
            editSpe.putExtra("details", specialite);
            editSpe.putExtra("state", "editing");
            if (this.launcher != null) {
                this.launcher.launch(editSpe);
            }
        });

        return convertView;
    }
}
