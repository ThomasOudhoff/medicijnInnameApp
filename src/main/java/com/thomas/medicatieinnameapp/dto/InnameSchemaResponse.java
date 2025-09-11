package com.thomas.medicatieinnameapp.dto;

import java.time.LocalDate;

public class InnameSchemaResponse {
    private Long id;
    private Long gebruikerId;
    private Long medicatieId;
    private LocalDate startDatum;
    private LocalDate eindDatum;
    private Integer frequentiePerDag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGebruikerId() { return gebruikerId; }
    public void setGebruikerId(Long gebruikerId) { this.gebruikerId = gebruikerId; }
    public Long getMedicatieId() { return medicatieId; }
    public void setMedicatieId(Long medicatieId) { this.medicatieId = medicatieId; }
    public LocalDate getStartDatum() { return startDatum; }
    public void setStartDatum(LocalDate startDatum) { this.startDatum = startDatum; }
    public LocalDate getEindDatum() { return eindDatum; }
    public void setEindDatum(LocalDate eindDatum) { this.eindDatum = eindDatum; }
    public Integer getFrequentiePerDag() { return frequentiePerDag; }
    public void setFrequentiePerDag(Integer frequentiePerDag) { this.frequentiePerDag = frequentiePerDag; }
}
