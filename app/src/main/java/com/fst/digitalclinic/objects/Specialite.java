package com.fst.digitalclinic.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class Specialite implements Parcelable {
    private Integer id;
    private String nom;
    private String type;
    private Double prix;
    private String description;

    public Specialite(Integer id, String nom, String type, Double prix, String description) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.prix = prix;
        this.description = description;
    }

    protected Specialite(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        nom = in.readString();
        type = in.readString();
        prix = in.readDouble();
        description = in.readString();
    }

    public static final Creator<Specialite> CREATOR = new Creator<Specialite>() {
        @Override
        public Specialite createFromParcel(Parcel in) {
            return new Specialite(in);
        }

        @Override
        public Specialite[] newArray(int size) {
            return new Specialite[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return this.nom;
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
        parcel.writeString(type);
        parcel.writeDouble(prix);
        parcel.writeString(description);
    }
}
