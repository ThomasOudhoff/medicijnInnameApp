package com.thomas.medicatieinnameapp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MedicatieUpdateRequest {

    @NotBlank
    @JsonAlias({"naamMedicijn", "naam_medicijn"})
    @Size(max = 100)
    private String naam;

    @Size(max = 2000)
    private String omschrijving;

    @Size(max = 255)
    private String bijsluiterUrl;

    public String getNaam() { return naam; }
    public void setNaam(String naam) { this.naam = naam; }

    public String getOmschrijving() { return omschrijving; }
    public void setOmschrijving(String omschrijving) { this.omschrijving = omschrijving; }

    public String getBijsluiterUrl() { return bijsluiterUrl; }
    public void setBijsluiterUrl(String bijsluiterUrl) { this.bijsluiterUrl = bijsluiterUrl; }
}
