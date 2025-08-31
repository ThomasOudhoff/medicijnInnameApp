package com.thomas.medicatieinnameapp.controller;

import com.thomas.medicatieinnameapp.dto.GebruikerCreateRequest;
import com.thomas.medicatieinnameapp.dto.GebruikerResponse;
import com.thomas.medicatieinnameapp.dto.GebruikerUpdateRequest;
import com.thomas.medicatieinnameapp.mapper.GebruikerMapper;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.service.GebruikerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/gebruiker")
public class GebruikerController {

    private final GebruikerService gebruikerService;
    private final PasswordEncoder passwordEncoder;

    public GebruikerController(GebruikerService gebruikerService,
                               PasswordEncoder passwordEncoder) {
        this.gebruikerService = gebruikerService;
        this.passwordEncoder = passwordEncoder;
    }

    // GET lijst -> altijd DTO's terug (zonder wachtwoord)
    @GetMapping
    public List<GebruikerResponse> getAllGebruikers() {
        return gebruikerService.getAllGebruikers()
                .stream()
                .map(GebruikerMapper::toResponse)
                .toList();
    }

    // GET by id -> DTO terug, 404 als niet gevonden
    @GetMapping("/{id}")
    public GebruikerResponse getGebruiker(@PathVariable Long id) {
        // Pas deze regel eventueel aan naar jouw service-signatuur
        Gebruiker g = gebruikerService.getGebruikerById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));
        return GebruikerMapper.toResponse(g);
    }

    // POST -> Request-DTO in, hash wachtwoord, DTO terug (zonder wachtwoord)
    @PostMapping
    public ResponseEntity<GebruikerResponse> createGebruiker(@Valid @RequestBody GebruikerCreateRequest req) {
        Gebruiker entity = GebruikerMapper.toEntity(req);
        entity.setWachtwoord(passwordEncoder.encode(entity.getWachtwoord()));

        Gebruiker saved = gebruikerService.saveGebruiker(entity);
        GebruikerResponse body = GebruikerMapper.toResponse(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
    @PutMapping("/{id}")
    public ResponseEntity<GebruikerResponse> updateGebruiker(
            @PathVariable Long id,
            @Valid @RequestBody GebruikerUpdateRequest req) {

        var bijgewerkt = gebruikerService.updateGebruiker(id, req);
        var resp = mapToGebruikerResponse(bijgewerkt);
        return ResponseEntity.ok(resp);
    }
    private GebruikerResponse mapToGebruikerResponse(Gebruiker gebruiker) {
        GebruikerResponse resp = new GebruikerResponse();
        resp.setId(gebruiker.getId());
        resp.setNaam(gebruiker.getNaam());
        resp.setEmail(gebruiker.getEmail());
        resp.setRol(gebruiker.getRol());
        return resp;
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGebruiker(@PathVariable Long id) {
        gebruikerService.deleteGebruiker(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
