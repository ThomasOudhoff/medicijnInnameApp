package com.thomas.medicatieinnameapp.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class InnameSchemaCreateRequest {
    @NotNull
    private LocalDate startDatum;

    private LocalDate eindDatum;

    @Positive
    private Integer frequentiePerDag = 1;

    public LocalDate getStartDatum() { return startDatum; }
    public void setStartDatum(LocalDate startDatum) { this.startDatum = startDatum; }
    public LocalDate getEindDatum() { return eindDatum; }
    public void setEindDatum(LocalDate eindDatum) { this.eindDatum = eindDatum; }
    public Integer getFrequentiePerDag() { return frequentiePerDag; }
    public void setFrequentiePerDag(Integer frequentiePerDag) { this.frequentiePerDag = frequentiePerDag; }
}
