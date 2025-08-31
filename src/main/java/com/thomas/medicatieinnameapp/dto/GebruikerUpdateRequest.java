package com.thomas.medicatieinnameapp.dto;

import com.thomas.medicatieinnameapp.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GebruikerUpdateRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String naam;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private Rol rol;

    public String getNaam() { return naam; }
    public void setNaam(String naam) { this.naam = naam; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
