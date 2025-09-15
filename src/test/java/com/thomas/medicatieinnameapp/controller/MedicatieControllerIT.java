package com.thomas.medicatieinnameapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thomas.medicatieinnameapp.service.MedicatieService;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.model.Rol;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MedicatieControllerIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper om;
    @Autowired GebruikerRepository gebruikerRepository;
    @Autowired MedicatieRepository medicatieRepository;
    @Autowired MedicatieService medicatieService;

    Long gebruikerId;

    @BeforeEach
    void seed() {
        var g = new Gebruiker();
        g.setNaam("Patiënt X");
        // maak e-mail uniek per test-run om UNIQUE-constraint te vermijden
        g.setEmail("p+" + System.nanoTime() + "@ex.com");
        g.setRol(Rol.GEBRUIKER);
        g.setWachtwoord("dummy-encoded"); // ivm NOT NULL kolom
        gebruikerId = gebruikerRepository.save(g).getId();
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/gebruiker/{id}/medicatie → 201")
    void createMedicatieForGebruiker_201() throws Exception {
        var payload = """
            {"naam":"Paracetamol"}
        """;

        mockMvc.perform(post("/api/gebruiker/{id}/medicatie", gebruikerId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.naam").value("Paracetamol"))
                .andExpect(jsonPath("$.gebruikerId").value(gebruikerId));
    }

    @Test
    @WithMockUser
    @DisplayName("POST medicatie voor niet-bestaande gebruiker → 404")
    void createMedicatie_user404() throws Exception {
        var payload = """
            {"naam":"Ibuprofen"}
        """;
        mockMvc.perform(post("/api/gebruiker/{id}/medicatie", 9999L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/gebruiker/{id}/medicatie → 200 lijst")
    void listMedicatiesByGebruiker_200() throws Exception {
        medicatieService.createForGebruiker(gebruikerId, "Cetirizine");
        medicatieService.createForGebruiker(gebruikerId, "Vitamine D");

        mockMvc.perform(get("/api/gebruiker/{id}/medicatie", gebruikerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].naam").exists())
                .andExpect(jsonPath("$[0].gebruikerId").value(gebruikerId));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/medicatie/{id}/bijsluiter: blank url → 400")
    void setBijsluiter_blank_400() throws Exception {
        Medicatie m = medicatieService.createForGebruiker(gebruikerId, "Amoxicilline");

        var body = """
            {"url":"  "}
        """;
        mockMvc.perform(patch("/api/medicatie/{id}/bijsluiter", m.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/medicatie/{id}/bijsluiter: geldige url → 2xx")
    void setBijsluiter_ok() throws Exception {
        Medicatie m = medicatieService.createForGebruiker(gebruikerId, "Amoxicilline");

        var body = """
            {"url":"https://example.com/bijsluiter.pdf"}
        """;

        mockMvc.perform(patch("/api/medicatie/{id}/bijsluiter", m.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/medicatie/{id} → 204 en daarna repo leeg")
    void deleteMedicatie_204_then_repoEmpty() throws Exception {
        Medicatie m = medicatieService.createForGebruiker(gebruikerId, "Doxycycline");

        mockMvc.perform(delete("/api/medicatie/{id}", m.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(medicatieRepository.findById(m.getId())).isEmpty();
    }
}

