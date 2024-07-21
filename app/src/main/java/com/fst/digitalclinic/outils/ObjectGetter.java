package com.fst.digitalclinic.outils;

import com.fst.digitalclinic.objects.Medecin;
import com.fst.digitalclinic.objects.Patient;
import com.fst.digitalclinic.objects.Specialite;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ObjectGetter {
    private JSONParser jsonParser;
    private final String token;

    public ObjectGetter(@NotNull JSONParser jsonParser, @NotNull String token) {
        this.jsonParser = jsonParser;
        this.token = token;
    }

    public JSONParser getJsonParser() {
        return jsonParser;
    }

    public void setJsonParser(JSONParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    public String getToken() {
        return token;
    }

    public ArrayList<Medecin> getMedecins() throws Exception {
        Response r = this.getJsonParser().getRequest("/home", this.getToken());
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

    public ArrayList<Patient> getPatients() throws Exception {
        Response res = this.getJsonParser().getRequest("/home/patients", this.getToken());
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

    public ArrayList<Specialite> getSpecialiteArrayList() throws Exception {
        Response r = this.getJsonParser().getRequest("/home/spes", this.getToken());
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
}
