package com.fst.digitalclinic.adapters;

import android.app.Dialog;
import android.content.*;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.fst.digitalclinic.R;
import com.fst.digitalclinic.specialites.ChooseSpeActivity;
import com.fst.digitalclinic.users.RemoveMedecinActivity;
import com.fst.digitalclinic.objects.Medecin;
import com.fst.digitalclinic.outils.ErrorRemover;
import com.fst.digitalclinic.outils.JSONParser;
import com.fst.digitalclinic.outils.ValidInput;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class UserListView extends ArrayAdapter<Medecin> {
    ImageView imageView;
    TextView textView;
    ImageView see, delete;
    ValidInput validInput;
    Boolean has_copied;
    String state;
    ActivityResultLauncher<Intent> launcher;
    ActivityResultLauncher<Intent> launcherDelMed;
    ImageView success_image_password_anim;
    Dialog dialog;
    Dialog dialog_pass_edited;

    public UserListView(@NonNull Context context, int resource, @NonNull ArrayList<Medecin> objects,
                        @Nullable ActivityResultLauncher<Intent> launcher,
                        @Nullable ActivityResultLauncher<Intent> launcherDelMed,
                        String state) {
        super(context, resource, objects);
        this.launcher = launcher;
        this.launcherDelMed = launcherDelMed;
        this.state = state;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.userlist, parent, false);
        }
        Medecin medecin = getItem(position);
        imageView = convertView.findViewById(R.id.userlist_icon);
        textView = convertView.findViewById(R.id.userlist_text);
        see = convertView.findViewById(R.id.userlist_see);
        delete = convertView.findViewById(R.id.userlist_delete);
        delete.setOnClickListener(el -> {
            Intent deleteMed = new Intent(getContext(), RemoveMedecinActivity.class);
            deleteMed.putExtra("medecin_id", medecin.getId());
            deleteMed.putExtra("medecin_fullname", medecin.getNom() + ' ' + medecin.getPrenom());
            deleteMed.putExtra("state", this.state);
            if (this.launcher != null) {
                this.launcher.launch(deleteMed);
            }else {
                if (this.launcherDelMed != null) {
                    this.launcherDelMed.launch(deleteMed);
                }
            }
        });
        see.setOnClickListener(v -> {
            Animation animFadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.medinfo);
            dialog.setCancelable(false);
            final TextView name = dialog.findViewById(R.id.med_info_name);
            final TextView surname = dialog.findViewById(R.id.med_info_surname);
            final TextView genre = dialog.findViewById(R.id.med_info_genre);
            final TextView dob = dialog.findViewById(R.id.med_info_dob);
            final TextView adresse = dialog.findViewById(R.id.med_info_adresse);
            final TextView tel = dialog.findViewById(R.id.med_info_tel);
            final TextView mail = dialog.findViewById(R.id.med_info_mail);
            final TextView hop = dialog.findViewById(R.id.med_info_hop);
            final TextView spe = dialog.findViewById(R.id.med_info_specialite);
            final Button close = dialog.findViewById(R.id.med_info_close_btn);
            final ImageView edit_pass = dialog.findViewById(R.id.med_info_new_password_icon);
            final ImageView edit_close = dialog.findViewById(R.id.med_info_new_password_icon_close);
            final LinearLayout linearLayout = dialog.findViewById(R.id.med_info_new_password_layout);
            final EditText linearLayoutPassword = linearLayout.findViewById(R.id.med_info_new_password_input);
            final Button linearLayoutButton = dialog.findViewById(R.id.med_info_new_password_button);
            final TextView linearLayoutTextview = dialog.findViewById(R.id.med_info_new_password_error_text);
            name.setText(medecin.getNom().toLowerCase());
            surname.setText(medecin.getPrenom().toLowerCase());
            genre.setText(medecin.getGenre() ? "Homme".toLowerCase() : "Femme".toLowerCase());
            dob.setText(medecin.getDateNaissance());
            adresse.setText(medecin.getAdresse().toLowerCase());
            tel.setText(medecin.getTelephone());
            mail.setText(medecin.getEmail().toLowerCase());
            hop.setText(medecin.getHopital().toLowerCase());
            spe.setText(medecin.getSpecialite().toLowerCase());
            spe.setOnClickListener(g -> {
                Intent editMedSpe = new Intent(getContext(), ChooseSpeActivity.class);
                editMedSpe.putExtra("med_details", medecin);
                if (this.launcher != null) {
                    this.launcher.launch(editMedSpe);
                }else{
                    if (this.launcherDelMed != null) {
                        this.launcherDelMed.launch(editMedSpe);
                    }
                }
                dialog.dismiss();
            });
            close.setOnClickListener(x -> dialog.dismiss());
            edit_pass.setOnClickListener(w -> {
                close.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                linearLayoutButton.setVisibility(View.VISIBLE);
                edit_pass.setVisibility(View.GONE);
                animFadeIn.reset();
                edit_close.setVisibility(View.VISIBLE);
                edit_close.clearAnimation();
                edit_close.startAnimation(animFadeIn);
                linearLayoutPassword.addTextChangedListener(new ErrorRemover(linearLayoutTextview));
                linearLayoutButton.setOnClickListener(o -> {
                    validInput = new ValidInput();
                    JSONParser jsonParser = new JSONParser();
                    boolean isValid = validInput.isValidPassword(linearLayoutPassword.getText().toString());
                    if (isValid) {
                        String url = String.format("/home/edit/%s", medecin.getId());
                        String data = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", medecin.getEmail(), linearLayoutPassword.getText().toString());
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
                        String token = sharedPreferences.getString("auth_token", null);
                        try {
                            Response res = jsonParser.putRequest(url, data, token);
                            JSONObject object = new JSONObject(Objects.requireNonNull(res.body()).string());
                            if (res.code() == 200 && object.getBoolean("status")) {
                                has_copied = false;
                                dialog_pass_edited = new Dialog(getContext());
                                dialog_pass_edited.setContentView(R.layout.success_edit_password_dialog);
                                dialog_pass_edited.setCancelable(false);
                                TextView old_pass = dialog_pass_edited.findViewById(R.id.med_info_new_password_dialog_pass1);
                                TextView new_pass = dialog_pass_edited.findViewById(R.id.med_info_new_password_dialog_pass2);
                                ImageView icon_copy = dialog_pass_edited.findViewById(R.id.med_info_new_password_dialog_copy_icon);
                                success_image_password_anim = dialog_pass_edited.findViewById(R.id.success_image_password_anim);
                                Button confirm = dialog_pass_edited.findViewById(R.id.med_info_new_password_dialog_button);
                                final AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) success_image_password_anim.getDrawable();
                                animatedVectorDrawable.start();
                                icon_copy.setOnClickListener(r -> {
                                    String text = String.format("Email: %s\nMot de passe: %s", medecin.getEmail(), linearLayoutPassword.getText().toString());
                                    ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("identifiants", text);
                                    clipboardManager.setPrimaryClip(clipData);
                                    Toast.makeText(getContext(), R.string.copy_done, Toast.LENGTH_SHORT).show();
                                    has_copied = true;
                                });
                                old_pass.setText(medecin.getEmail());
                                new_pass.setText(linearLayoutPassword.getText().toString());
                                confirm.setOnClickListener(t -> {
                                    if (has_copied) {
                                        dialog_pass_edited.dismiss();
                                        linearLayout.setVisibility(View.GONE);
                                        linearLayoutButton.setVisibility(View.GONE);
                                        edit_close.setVisibility(View.GONE);
                                        edit_pass.setVisibility(View.VISIBLE);
                                    }else{
                                        Toast.makeText(getContext(), "Vous n'avez pas encore copier les identifiants", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                dialog_pass_edited.show();
                            }else{
                                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        linearLayoutTextview.setText(R.string.pass_error);
                    }
                });
            });
            edit_close.setOnClickListener(g -> {
                close.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
                linearLayoutButton.setVisibility(View.GONE);
                edit_close.setVisibility(View.GONE);
                linearLayoutTextview.setVisibility(View.GONE);
                edit_pass.setVisibility(View.VISIBLE);
            });
            dialog.show();
        });
        if (medecin.getGenre()) {
            imageView.setImageResource(R.drawable.man);
            textView.setText(String.format("Mr. %s %s", medecin.getNom() , medecin.getPrenom()));
        }else{
            imageView.setImageResource(R.drawable.woman);
            textView.setText(String.format("Mme. %s %s", medecin.getNom() , medecin.getPrenom()));
        }
        return convertView;
    }
}
