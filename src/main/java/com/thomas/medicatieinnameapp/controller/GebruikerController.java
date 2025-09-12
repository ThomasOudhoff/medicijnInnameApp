package com.thomas.medicatieinnameapp.controller;

import com.thomas.medicatieinnameapp.dto.*;
import com.thomas.medicatieinnameapp.mapper.GebruikerMapper;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.service.GebruikerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping
    public List<GebruikerResponse> getAllGebruikers() {
        return gebruikerService.getAllGebruikers()
                .stream()
                .map(GebruikerMapper::toResponse)
                .toList();
    }
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @GetMapping("/{id}")
    public GebruikerResponse getGebruiker(@PathVariable Long id) {
        Gebruiker g = gebruikerService.getGebruikerById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));
        return GebruikerMapper.toResponse(g);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<GebruikerResponse> createGebruiker(@Valid @RequestBody GebruikerCreateRequest req) {
        Gebruiker entity = GebruikerMapper.toEntity(req);
        entity.setWachtwoord(passwordEncoder.encode(entity.getWachtwoord()));

        Gebruiker saved = gebruikerService.saveGebruiker(entity);
        GebruikerResponse body = GebruikerMapper.toResponse(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<GebruikerResponse> updateGebruiker(
            @PathVariable Long id,
            @Valid @RequestBody GebruikerUpdateRequest req) {

        var bijgewerkt = gebruikerService.updateGebruiker(id, req);
        var resp = mapToGebruikerResponse(bijgewerkt);
        return ResponseEntity.ok(resp);
    }
    private GebruikerResponse mapToGebruikerResponse(Gebruiker gebruiker) {
        return GebruikerMapper.toResponse(gebruiker);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGebruiker(@PathVariable Long id) {
        gebruikerService.deleteGebruiker(id);
        return ResponseEntity.noContent().build(); // 204
    }

    @PostMapping("/{id}/medicatie")
    public ResponseEntity<MedicatieResponse> voegMedicatieToe(
            @PathVariable Long id,
            @Valid @RequestBody MedicatieCreateRequest req) {

        Medicatie m = new Medicatie();
        m.setNaamMedicijn(req.getNaam());

        var opgeslagen = gebruikerService.voegMedicatieToeAanGebruiker(id, m);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToMedicatieResponse(opgeslagen));
    }

    @GetMapping("/{id}/medicatie")
    public ResponseEntity<java.util.List<MedicatieResponse>> lijstMedicaties(@PathVariable Long id) {
        var lijst = gebruikerService.getMedicatiesVoorGebruiker(id)
                .stream().map(this::mapToMedicatieResponse).toList();
        return ResponseEntity.ok(lijst);
    }
    private MedicatieResponse mapToMedicatieResponse(Medicatie m) {
        MedicatieResponse r = new MedicatieResponse();
        r.setId(m.getId());
        r.setNaam(m.getNaamMedicijn());
        r.setGebruikerId(m.getGebruiker().getId());
        return r;
    }
}
