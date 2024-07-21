package com.fst.digitalclinic.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Patient implements Parcelable {
    private Integer id;
    private String nom;
    private String prenom;
    private Boolean genre;
    private String dateNaissance;
    private String adresse;
    private String telephone;
    private String email;

    public Patient(Integer id, String nom, String prenom, Boolean genre, String dateNaissance, String adresse, String telephone, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.genre = genre;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
    }

    protected Patient(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        nom = in.readString();
        prenom = in.readString();
        byte tmpGenre = in.readByte();
        genre = tmpGenre == 0 ? null : tmpGenre == 1;
        dateNaissance = in.readString();
        adresse = in.readString();
        telephone = in.readString();
        email = in.readString();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Boolean getGenre() {
        return genre;
    }

    public void setGenre(Boolean genre) {
        this.genre = genre;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (id == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(id);
        }
        parcel.writeString(nom);
        parcel.writeString(prenom);
        parcel.writeByte((byte) (genre == null ? 0 : genre ? 1 : 2));
        parcel.writeString(dateNaissance);
        parcel.writeString(adresse);
        parcel.writeString(telephone);
        parcel.writeString(email);
    }
}
