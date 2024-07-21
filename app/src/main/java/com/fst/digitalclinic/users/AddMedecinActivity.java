package com.fst.digitalclinic.users;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.*;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.fst.digitalclinic.objects.Specialite;
import com.fst.digitalclinic.outils.ErrorRemover;
import com.fst.digitalclinic.outils.JSONParser;
import com.fst.digitalclinic.R;
import com.fst.digitalclinic.outils.ValidInput;
import com.fst.digitalclinic.specialites.ChooseSpeActivity;
import okhttp3.Response;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddMedecinActivity extends AppCompatActivity {
    ScrollView content;
    EditText med_dob;
    TextView med_dob_error;
    EditText med_nom;
    TextView med_nom_error;
    EditText med_prenom;
    TextView med_prenom_error;
    RadioButton med_man;
    RadioButton med_woman;
    EditText med_adresse;
    TextView med_adresse_error;
    EditText med_phone;
    TextView med_phone_error;
    EditText med_email;
    TextView med_email_error;
    EditText med_hopital;
    TextView med_hopital_error;
    Button add_med;
    ImageView imageView;
    Calendar calendar = Calendar.getInstance();
    ImageView spe_image;
    TextView spe_text;
    TextView spe_error;
    ArrayList<Specialite> checkedItems;
    Dialog dialog;
    ValidInput validInput;
    String token;
    JSONParser jsonParser;
    AlertDialog dialog_progress;
    TextView dialog_success_med_email;
    TextView dialog_success_med_password;
    Button dialog_success_med_button_ok;
    ImageView clipboard;
    ImageView success_image_anim;
    Boolean has_copied;

    ActivityResultLauncher<Intent> launcherSpe = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                checkedItems.clear();
                if (result.getResultCode() == Activity.RESULT_OK){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        assert result.getData() != null;
                        boolean isEmpty = result.getData().getBooleanExtra("empty", false);
                        if (isEmpty) {
                            spe_text.setText(R.string.specialite_hint);
                        }else{
                            checkedItems = result.getData().getParcelableArrayListExtra("speChecked");
                            spe_text.setText(join(",", checkedItems));
                        }
                    }
                }
            }
    );

    @SuppressLint("InflateParams")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medecin);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        content = findViewById(R.id.add_med_info_scroll);
        med_nom = findViewById(R.id.med_info_name2);
        med_nom_error = findViewById(R.id.med_info_name_error);
        med_prenom = findViewById(R.id.med_info_surname2);
        med_prenom_error = findViewById(R.id.med_info_surname_error);
        med_man = findViewById(R.id.genre_man_image);
        med_woman = findViewById(R.id.genre_woman_image);
        med_dob = findViewById(R.id.med_info_dob2);
        med_dob_error = findViewById(R.id.med_info_dob_error);
        imageView = findViewById(R.id.med_info_dob_image);
        med_adresse = findViewById(R.id.med_info_adresse2);
        med_adresse_error = findViewById(R.id.med_info_adresse_error);
        med_phone = findViewById(R.id.med_info_tel2);
        med_phone_error = findViewById(R.id.med_info_tel_error);
        med_email = findViewById(R.id.med_info_mail2);
        med_email_error = findViewById(R.id.med_info_mail_error);
        med_hopital = findViewById(R.id.med_info_hop2);
        med_hopital_error = findViewById(R.id.med_info_hop_error);
        spe_image = findViewById(R.id.med_info_specialite_icon);
        spe_text = findViewById(R.id.med_info_specialite_text);
        spe_error =  findViewById(R.id.med_info_specialite_error);
        add_med = findViewById(R.id.add_med_button);
        checkedItems = new ArrayList<>();
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, i, i1, i2) -> {
            calendar.set(Calendar.YEAR, i);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DAY_OF_MONTH, i2);
            getDate();
        };
        imageView.setOnClickListener(v -> new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        );
        spe_image.setOnClickListener(v -> {
            Intent chooseSpe = new Intent(getApplicationContext(), ChooseSpeActivity.class);
            chooseSpe.putParcelableArrayListExtra("speChecked", checkedItems);
            launcherSpe.launch(chooseSpe);
        });
        add_med.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                validInput = new ValidInput();
                boolean v_n = validInput.isValidNonDigit(med_nom.getText().toString()),
                        v_p = validInput.isValidNonDigit(med_prenom.getText().toString()),
                        v_date = validInput.isValidDate(med_dob.getText().toString()),
                        v_ad = validInput.isValidNonUnicode(med_adresse.getText().toString()),
                        v_phone = validInput.isValidPhoneNumber(med_phone.getText().toString()),
                        v_mail = validInput.isValidMail(med_email.getText().toString()),
                        v_hop = validInput.isValidNonDigit(med_hopital.getText().toString()),
                        v_spe = validSpe(checkedItems);
                String genre = getGenre(med_man, med_woman),
                        password = validInput.genPassword();
                if (!v_n && !v_p && !v_date && !v_ad && !v_phone && !v_mail && !v_hop && !v_spe) {
                    content.smoothScrollTo(0, med_nom.getTop());
                    med_nom_error.setText(R.string.name_hint_error);
                    med_prenom_error.setText(R.string.surname_hint_error);
                    med_dob_error.setText(R.string.date_helper_text_error);
                    med_adresse_error.setText(R.string.adresse_hint_error);
                    med_phone_error.setText(R.string.tel_hint_error);
                    med_email_error.setText(R.string.email_hint_error);
                    med_hopital_error.setText(R.string.hopital_hint_error);
                    spe_error.setText(R.string.specialite_hint_error);
                }else if (!v_n) {
                    med_nom_error.setText(R.string.name_hint_error);
                    content.smoothScrollTo(0, med_nom.getTop());
                }else if (!v_p) {
                    med_prenom_error.setText(R.string.surname_hint_error);
                    content.smoothScrollTo(0, med_prenom.getTop());
                }else if (!v_date) {
                    med_dob_error.setText(R.string.date_helper_text_error);
                    content.smoothScrollTo(0, med_dob.getTop());
                }else if (!v_ad) {
                    med_adresse_error.setText(R.string.adresse_hint_error);
                    content.smoothScrollTo(0, med_adresse.getTop());
                }else if (!v_phone) {
                    med_phone_error.setText(R.string.tel_hint_error);
                }else if (!v_mail) {
                    med_email_error.setText(R.string.email_hint_error);
                }else if (!v_hop) {
                    med_hopital_error.setText(R.string.hopital_hint_error);
                }else if (!v_spe) {
                    spe_error.setText(R.string.specialite_hint_error);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setView(getLayoutInflater().inflate(R.layout.custom_progress_bar, null));
                    builder.setCancelable(true);
                    dialog_progress = builder.create();
                    dialog_progress.show();
                    String data = String.format(
                            "{\"nom\": \"%s\", \"prenom\": \"%s\", \"genre\": \"%s\", \"dob\": \"%s\", \"adresse\": \"%s\", " +
                                    "\"phone\": \"%s\", \"email\": \"%s\", \"password\": \"%s\", " +
                                    "\"hopital\": \"%s\", \"specialties\": \"%s\"}"
                            , med_nom.getText().toString(),
                            med_prenom.getText().toString(),
                            genre,
                            med_dob.getText().toString(),
                            med_adresse.getText().toString(),
                            med_phone.getText().toString(),
                            med_email.getText().toString(),
                            password,
                            med_hopital.getText().toString(),
                            spe_text.getText().toString());
                    SharedPreferences sharedPreferences = this.getSharedPreferences("token", Context.MODE_PRIVATE);
                    token = sharedPreferences.getString("auth_token", null);
                    jsonParser = new JSONParser();
                    try {
                        Response response = jsonParser.postRequest("/home/add", data, token);
                        if (response.code() == 200) {
                            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                            if (jsonObject.getString("code").equals("EMAIL_EXIST")) {
                                dialog_progress.dismiss();
                                med_email_error.setText(jsonObject.getString("message"));
                                content.smoothScrollTo(0, med_email.getTop());
                            }else if (jsonObject.getString("code").equals("NUMBER_EXIST")) {
                                dialog_progress.dismiss();
                                med_phone_error.setText(jsonObject.getString("message"));
                                content.smoothScrollTo(0, med_phone.getTop());
                            }else{
                                if (jsonObject.getString("code").equals("SUCCESS")) {
                                    dialog_progress.dismiss();
                                    has_copied = false;
                                    final Dialog dialog_success = new Dialog(this);
                                    dialog_success.setContentView(R.layout.success_custom_dialog);
                                    dialog_success.setCancelable(false);
                                    success_image_anim = dialog_success.findViewById(R.id.success_image_anim);
                                    dialog_success_med_email = dialog_success.findViewById(R.id.med_detail_email);
                                    dialog_success_med_password = dialog_success.findViewById(R.id.med_detail_password);
                                    dialog_success_med_button_ok = dialog_success.findViewById(R.id.med_detail_button_ok);
                                    clipboard = dialog_success.findViewById(R.id.med_detail_copy_icon);
                                    final AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) success_image_anim.getDrawable();
                                    animatedVectorDrawable.start();
                                    clipboard.setOnClickListener(y -> {
                                        String text = String.format("Email: %s\nMot de passe: %s", med_email.getText().toString(), password);
                                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clipData = ClipData.newPlainText("identifiants", text);
                                        clipboardManager.setPrimaryClip(clipData);
                                        Toast.makeText(this, R.string.copy_done, Toast.LENGTH_SHORT).show();
                                        has_copied = true;
                                    });
                                    dialog_success_med_email.setText(med_email.getText().toString());
                                    dialog_success_med_password.setText(password);
                                    assert dialog_success_med_button_ok != null;
                                    dialog_success_med_button_ok.setOnClickListener(z -> {
                                        if (has_copied) {
                                            dialog_success.dismiss();
                                            Intent returnIntent = new Intent();
                                            setResult(Activity.RESULT_OK, returnIntent);
                                            finish();
                                        }else{
                                            Toast.makeText(this, "Vous n'avez pas encore copier les identifiants", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    dialog_success.show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        med_nom.addTextChangedListener(new ErrorRemover(med_nom_error));
        med_prenom.addTextChangedListener(new ErrorRemover(med_prenom_error));
        med_dob.addTextChangedListener(new ErrorRemover(med_dob_error));
        med_adresse.addTextChangedListener(new ErrorRemover(med_adresse_error));
        med_phone.addTextChangedListener(new ErrorRemover(med_phone_error));
        med_email.addTextChangedListener(new ErrorRemover(med_email_error));
        med_hopital.addTextChangedListener(new ErrorRemover(med_hopital_error));
    }

    public void getDate() {
        String format = "dd/MM/YYYY";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            @SuppressLint("WeekBasedYear") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.FRANCE);
            med_dob.setText(simpleDateFormat.format(calendar.getTime()));
        }
    }

    public String getGenre(RadioButton r1, RadioButton r2) {
        return r1.isChecked() ? "Homme" : r2.isChecked() ? "Femme" : "Inconnu";
    }

    public boolean validSpe(ArrayList<Specialite> arrayList) {
        return arrayList.size() > 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String join(String delimiter, ArrayList<Specialite> speArrayList) {
        StringJoiner stringJoiner = new StringJoiner(delimiter);
        speArrayList.forEach((value) -> stringJoiner.add(value.getNom()));
        return stringJoiner.toString();
    }

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

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null) dialog.dismiss();
        if (dialog_progress != null ) dialog_progress.dismiss();
    }
}