package com.thomas.medicatieinnameapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.medicatieinnameapp.dto.GebruikerCreateRequest;
import com.thomas.medicatieinnameapp.dto.GebruikerUpdateRequest;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Rol;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasItem;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GebruikerControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper om;
    @Autowired GebruikerRepository gebruikerRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/gebruiker → 201 en opslaan")
    void createGebruiker_201() throws Exception {
        var req = new GebruikerCreateRequest();
        req.setNaam("Alice");
        req.setEmail("alice@example.com");
        req.setRol(Rol.GEBRUIKER);
        req.setWachtwoord("wachtwoord456");

        mockMvc.perform(post("/api/gebruiker")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.naam").value("Alice"));

        assertThat(gebruikerRepository.existsByEmailIgnoreCase("alice@example.com")).isTrue();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/gebruiker met bestaand e-mail → 409")
    void createGebruiker_conflict409() throws Exception {
        Gebruiker bestaand = new Gebruiker();
        bestaand.setNaam("Bert");
        bestaand.setEmail("dup@example.com");
        bestaand.setRol(Rol.GEBRUIKER);
        bestaand.setWachtwoord("wachtwoord987");
        gebruikerRepository.save(bestaand);

        var req = new GebruikerCreateRequest();
        req.setNaam("Bert2");
        req.setEmail("dup@example.com"); // zelfde e-mail => 409
        req.setRol(Rol.GEBRUIKER);
        req.setWachtwoord("wachtwoord123");

        mockMvc.perform(post("/api/gebruiker")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/gebruiker/{id} niet gevonden → 404")
    void getGebruiker_404() throws Exception {
        mockMvc.perform(get("/api/gebruiker/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/gebruiker → 200 en lijst bevat Listy")
    void listGebruikers_200() throws Exception {
        Gebruiker u = new Gebruiker();
        u.setNaam("Listy");
        u.setEmail("listy@example.com");
        u.setRol(Rol.GEBRUIKER);
        u.setWachtwoord("geheim123");
        gebruikerRepository.save(u);

        mockMvc.perform(get("/api/gebruiker"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", hasItem("listy@example.com")));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/gebruiker/{id} → 200 update en 409 bij e-mail in gebruik")
    void updateGebruiker_200_and_409() throws Exception {
        Gebruiker a = new Gebruiker();
        a.setNaam("Aa");
        a.setEmail("a@ex.com");
        a.setRol(Rol.GEBRUIKER);
        a.setWachtwoord("pw");
        a = gebruikerRepository.save(a);

        Gebruiker b = new Gebruiker();
        b.setNaam("Bb");
        b.setEmail("b@ex.com");
        b.setRol(Rol.GEBRUIKER);
        b.setWachtwoord("pw");
        gebruikerRepository.save(b);

        var okReq = new GebruikerUpdateRequest();
        okReq.setNaam("A-nieuw");
        okReq.setEmail("a2@ex.com");
        okReq.setRol(Rol.GEBRUIKER);

        mockMvc.perform(put("/api/gebruiker/{id}", a.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(okReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("a2@ex.com"));

        var conflict = new GebruikerUpdateRequest();
        conflict.setNaam("Xx");
        conflict.setEmail("b@ex.com"); // al in gebruik door B
        conflict.setRol(Rol.GEBRUIKER);

        mockMvc.perform(put("/api/gebruiker/{id}", a.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(conflict)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/gebruiker/{id} → 204 en daarna 404")
    void deleteGebruiker_204_then_404() throws Exception {
        Gebruiker u = new Gebruiker();
        u.setNaam("Del");
        u.setEmail("del@ex.com");
        u.setRol(Rol.GEBRUIKER);
        u.setWachtwoord("pw");
        u = gebruikerRepository.save(u);

        mockMvc.perform(delete("/api/gebruiker/{id}", u.getId()).with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/gebruiker/{id}", u.getId()))
                .andExpect(status().isNotFound());
    }
}

