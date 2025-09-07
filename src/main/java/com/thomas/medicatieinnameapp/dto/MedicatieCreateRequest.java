package com.thomas.medicatieinnameapp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class MedicatieCreateRequest {

    @NotBlank
    @JsonAlias({"naamMedicijn", "naam_medicijn"})
    private String naam;

    public String getNaam() { return naam; }
    public void setNaam(String naam) { this.naam = naam; }
}
