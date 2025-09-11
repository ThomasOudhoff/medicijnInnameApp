package com.thomas.medicatieinnameapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "toedieningen")
public class Toediening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gebruiker_id", nullable = false)
    private Gebruiker gebruiker;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicatie_id", nullable = false)
    private Medicatie medicatie;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schema_id")
    private InnameSchema schemaInname;

    @Column(name = "tijdstip", nullable = false)
    private LocalDateTime tijdstip;

    @Column(name = "hoeveelheid")
    private Integer hoeveelheid;

    @Column(name = "opmerking", columnDefinition = "text")
    private String opmerking;

    public Long getId() { return id; }
    public Gebruiker getGebruiker() { return gebruiker; }
    public void setGebruiker(Gebruiker gebruiker) { this.gebruiker = gebruiker; }
    public Medicatie getMedicatie() { return medicatie; }
    public void setMedicatie(Medicatie medicatie) { this.medicatie = medicatie; }
    public InnameSchema getSchemaInname() { return schemaInname; }
    public void setSchemaInname(InnameSchema schemaInname) { this.schemaInname = schemaInname; }
    public LocalDateTime getTijdstip() { return tijdstip; }
    public void setTijdstip(LocalDateTime tijdstip) { this.tijdstip = tijdstip; }
    public Integer getHoeveelheid() { return hoeveelheid; }
    public void setHoeveelheid(Integer hoeveelheid) { this.hoeveelheid = hoeveelheid; }
    public String getOpmerking() { return opmerking; }
    public void setOpmerking(String opmerking) { this.opmerking = opmerking; }
}
