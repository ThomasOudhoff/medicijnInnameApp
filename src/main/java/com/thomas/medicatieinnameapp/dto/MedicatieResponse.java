package com.thomas.medicatieinnameapp.dto;

public class MedicatieResponse {

    private Long id;
    private String naam;
    private Long gebruikerId;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNaam() {
        return naam;
    }
    public void setNaam(String naam) {
        this.naam = naam; }

    public Long getGebruikerId() {
        return gebruikerId;
    }
    public void setGebruikerId(Long gebruikerId) {
        this.gebruikerId = gebruikerId;
    }
}
