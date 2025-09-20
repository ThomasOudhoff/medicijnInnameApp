
package com.thomas.medicatieinnameapp.controller;

import com.thomas.medicatieinnameapp.dto.ToedieningCreateRequest;
import com.thomas.medicatieinnameapp.dto.ToedieningResponse;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.model.Toediening;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import com.thomas.medicatieinnameapp.service.ToedieningService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ToedieningController {

    private final ToedieningService service;
    private final MedicatieRepository medicatieRepo;

    public ToedieningController(ToedieningService service, MedicatieRepository medicatieRepo) {
        this.service = service;
        this.medicatieRepo = medicatieRepo;
    }

    @PreAuthorize("""
        hasRole('ADMIN')
        or #gebruikerId == principal.id
        or (hasRole('VERZORGER') and @ownershipLookup.isVerzorgerOfMedicatie(#medicatieId, principal.id))
    """)
    @PostMapping("/api/gebruiker/{gebruikerId}/medicatie/{medicatieId}/toedieningen")
    public ResponseEntity<ToedieningResponse> createForUserAndMed(
            @PathVariable Long gebruikerId,
            @PathVariable Long medicatieId,
            @Valid @RequestBody ToedieningCreateRequest req) {

        Toediening t = service.create(gebruikerId, medicatieId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(map(t));
    }

    @PreAuthorize("""
        hasRole('ADMIN')
        or @ownershipLookup.gebruikerIdByMedicatie(#medicatieId) == principal.id
        or (hasRole('VERZORGER') and @ownershipLookup.isVerzorgerOfMedicatie(#medicatieId, principal.id))
    """)
    @PostMapping("/api/medicatie/{medicatieId}/toedieningen")
    public ResponseEntity<ToedieningResponse> createForMed(
            @PathVariable Long medicatieId,
            @Valid @RequestBody ToedieningCreateRequest req) {

        Medicatie m = medicatieRepo.findById(medicatieId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Medicatie niet gevonden"));
        Toediening t = service.create(m.getGebruiker().getId(), medicatieId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(map(t));
    }

    @PreAuthorize("""
        hasRole('ADMIN')
        or @ownershipLookup.gebruikerIdByMedicatie(#medicatieId) == principal.id
        or (hasRole('VERZORGER') and @ownershipLookup.isVerzorgerOfMedicatie(#medicatieId, principal.id))
    """)
    @GetMapping("/api/medicatie/{medicatieId}/toedieningen")
    public ResponseEntity<List<ToedieningResponse>> listByMedicatie(
            @PathVariable Long medicatieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<ToedieningResponse> out = service.listByMedicatie(medicatieId, from, to)
                .stream().map(this::map).toList();
        return ResponseEntity.ok(out);
    }

    @PreAuthorize("""
        hasRole('ADMIN')
        or @ownershipLookup.gebruikerIdBySchema(#schemaId) == principal.id
        or (hasRole('VERZORGER') and @ownershipLookup.isVerzorgerOfSchema(#schemaId, principal.id))
    """)
    @GetMapping("/api/schema/{schemaId}/toedieningen")
    public ResponseEntity<List<ToedieningResponse>> listBySchema(@PathVariable Long schemaId) {
        List<ToedieningResponse> out = service.listBySchema(schemaId)
                .stream().map(this::map).toList();
        return ResponseEntity.ok(out);
    }

    @PreAuthorize("hasRole('ADMIN') or #gebruikerId == principal.id")
    @GetMapping("/api/gebruiker/{gebruikerId}/toedieningen")
    public ResponseEntity<List<ToedieningResponse>> listByGebruiker(
            @PathVariable Long gebruikerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<ToedieningResponse> out = service.listByGebruiker(gebruikerId, from, to)
                .stream().map(this::map).toList();
        return ResponseEntity.ok(out);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/toedieningen/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ToedieningResponse map(Toediening t) {
        ToedieningResponse r = new ToedieningResponse();
        r.setId(t.getId());
        r.setGebruikerId(t.getGebruiker() != null ? t.getGebruiker().getId() : null);
        r.setMedicatieId(t.getMedicatie() != null ? t.getMedicatie().getId() : null);
        r.setSchemaId(t.getSchemaInname() != null ? t.getSchemaInname().getId() : null);
        r.setTijdstip(t.getTijdstip());
        r.setHoeveelheid(t.getHoeveelheid());
        r.setOpmerking(t.getOpmerking());
        return r;
    }
    @PreAuthorize("""
    hasRole('ADMIN')
    or @ownershipLookup.gebruikerIdBySchema(#schemaId) == principal.id
    or (hasRole('VERZORGER') and @ownershipLookup.isVerzorgerOfSchema(#schemaId, principal.id))
""")
    @PostMapping("/api/schema/{schemaId}/toedieningen")
    public ResponseEntity<ToedieningResponse> createForSchema(
            @PathVariable Long schemaId,
            @jakarta.validation.Valid @RequestBody ToedieningCreateRequest req) {

        var t = service.createForSchema(schemaId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(map(t));
    }
}
