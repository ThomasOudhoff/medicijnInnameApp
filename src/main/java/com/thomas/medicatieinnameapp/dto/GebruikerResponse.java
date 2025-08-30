package com.thomas.medicatieinnameapp.dto;

import com.thomas.medicatieinnameapp.model.Rol;

public class GebruikerResponse {
    private Long id;
    private String naam;
    private String email;
    private Rol rol;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNaam() { return naam; }
    public void setNaam(String naam) { this.naam = naam; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
}
