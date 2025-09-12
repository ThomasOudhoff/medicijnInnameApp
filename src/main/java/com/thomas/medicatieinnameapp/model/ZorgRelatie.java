package com.thomas.medicatieinnameapp.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "zorgrelaties",
        uniqueConstraints = @UniqueConstraint(columnNames = {"verzorger_id", "gebruiker_id"})
)
public class ZorgRelatie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // de verzorger (heeft rol VERZORGER)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "verzorger_id", nullable = false)
    private Gebruiker verzorger;

    // de patient/gebruiker (heeft rol GEBRUIKER)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gebruiker_id", nullable = false)
    private Gebruiker gebruiker;

    public Long getId() { return id; }
    public Gebruiker getVerzorger() {
        return verzorger; }
    public Gebruiker getGebruiker() {
        return gebruiker; }
    public void setVerzorger(Gebruiker verzorger) {
        this.verzorger = verzorger; }
    public void setGebruiker(Gebruiker gebruiker) {
        this.gebruiker = gebruiker; }
}
