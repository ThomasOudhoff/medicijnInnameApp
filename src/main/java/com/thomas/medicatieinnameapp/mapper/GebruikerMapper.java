package com.thomas.medicatieinnameapp.mapper;

import com.thomas.medicatieinnameapp.dto.GebruikerCreateRequest;
import com.thomas.medicatieinnameapp.dto.GebruikerResponse;
import com.thomas.medicatieinnameapp.model.Gebruiker;

public final class GebruikerMapper {
    private GebruikerMapper() {}

    public static Gebruiker toEntity(GebruikerCreateRequest req) {
        Gebruiker g = new Gebruiker();
        g.setNaam(req.getNaam());
        g.setEmail(req.getEmail());
        g.setWachtwoord(req.getWachtwoord()); // wordt straks gehasht vóór save
        g.setRol(req.getRol());
        return g;
    }

    public static GebruikerResponse toResponse(Gebruiker g) {
        GebruikerResponse res = new GebruikerResponse();
        res.setId(g.getId());
        res.setNaam(g.getNaam());
        res.setEmail(g.getEmail());
        res.setRol(g.getRol());
        return res;
    }
}
