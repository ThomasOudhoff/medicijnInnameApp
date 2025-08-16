package com.thomas.medicatieinnameapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medicaties")
public class Medicatie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String naamMedicijn;
    private String omschrijving;
    @Lob
    private byte[] bijsluiterFoto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNaamMedicijn() {
        return naamMedicijn;
    }

    public void setNaamMedicijn(String naamMedicijn) {
        this.naamMedicijn = naamMedicijn;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public void setOmschrijving(String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public byte[] getBijsluiterFoto() {
        return bijsluiterFoto;
    }

    public void setBijsluiterFoto(byte[] bijsluiterFoto) {
        this.bijsluiterFoto = bijsluiterFoto;
    }
    public Medicatie() {
    }
}
