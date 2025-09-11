package com.thomas.medicatieinnameapp.dto;

import com.thomas.medicatieinnameapp.model.Rol;
import jakarta.validation.constraints.*;

public class GebruikerCreateRequest {
    @NotBlank
    private String naam;

    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 8)
    private String wachtwoord;

    @NotNull
    private Rol rol;

    public String getNaam() {
        return naam; }
    public void setNaam(String naam) {
        this.naam = naam; }

    public String getEmail() {
        return email; }
    public void setEmail(String email) {
        this.email = email; }

    public String getWachtwoord() {
        return wachtwoord; }
    public void setWachtwoord(String wachtwoord) {
        this.wachtwoord = wachtwoord; }

    public Rol getRol() {
        return rol; }
    public void setRol(Rol rol) {
        this.rol = rol; }
}
