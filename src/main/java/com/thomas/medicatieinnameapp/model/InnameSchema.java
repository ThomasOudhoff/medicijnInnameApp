package com.thomas.medicatieinnameapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "schema_inname")
public class InnameSchema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gebruiker_id", nullable = false)
    private Gebruiker gebruiker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicatie_id", nullable = false)
    private Medicatie medicatie;

    @Column(name = "start_datum", nullable = false)
    private LocalDate startDatum;

    @Column(name = "eind_datum")
    private LocalDate eindDatum;

    @Column(name = "frequentie_per_dag")
    private Integer frequentiePerDag = 1;


    public Long getId() { return id; }
    public Gebruiker getGebruiker() { return gebruiker; }
    public void setGebruiker(Gebruiker gebruiker) { this.gebruiker = gebruiker; }
    public Medicatie getMedicatie() { return medicatie; }
    public void setMedicatie(Medicatie medicatie) { this.medicatie = medicatie; }
    public LocalDate getStartDatum() { return startDatum; }
    public void setStartDatum(LocalDate startDatum) { this.startDatum = startDatum; }
    public LocalDate getEindDatum() { return eindDatum; }
    public void setEindDatum(LocalDate eindDatum) { this.eindDatum = eindDatum; }
    public Integer getFrequentiePerDag() { return frequentiePerDag; }
    public void setFrequentiePerDag(Integer frequentiePerDag) { this.frequentiePerDag = frequentiePerDag; }
}
