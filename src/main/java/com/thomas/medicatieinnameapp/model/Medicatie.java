package com.thomas.medicatieinnameapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medicaties")
public class Medicatie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "naam_medicijn", nullable = false, length = 100)
    private String naamMedicijn;

    @Column(columnDefinition = "text")
    private String omschrijving;

    @Column(name = "bijsluiter_url", length = 255)
    private String bijsluiterUrl;

    @Lob
    @Column(name = "bijsluiter_foto")
    private byte[] bijsluiterFoto;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "gebruiker_id", nullable = false)
    private Gebruiker gebruiker;

    public Medicatie() {}

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

    public String getBijsluiterUrl() {
        return bijsluiterUrl;
    }
    public void setBijsluiterUrl(String bijsluiterUrl) {
        this.bijsluiterUrl = bijsluiterUrl;
    }

    public byte[] getBijsluiterFoto() {
        return bijsluiterFoto;
    }
    public void setBijsluiterFoto(byte[] bijsluiterFoto) {
        this.bijsluiterFoto = bijsluiterFoto;
    }

    public Gebruiker getGebruiker() {
        return gebruiker;
    }
    public void setGebruiker(Gebruiker gebruiker) {
        this.gebruiker = gebruiker;
    }
    @OneToOne(mappedBy = "medicatie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MedicatieBijsluiter bijsluiter;

    public MedicatieBijsluiter getBijsluiter() {
        return bijsluiter; }
    public void setBijsluiter(MedicatieBijsluiter b) {
        this.bijsluiter = b; }
}
