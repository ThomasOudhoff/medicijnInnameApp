package com.thomas.medicatieinnameapp.controller;

import com.thomas.medicatieinnameapp.dto.*;
import com.thomas.medicatieinnameapp.model.InnameSchema;
import com.thomas.medicatieinnameapp.service.InnameSchemaService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InnameSchemaController {

    private final InnameSchemaService service;

    public InnameSchemaController(InnameSchemaService service) {
        this.service = service;
    }

    // aanmaken voor specifieke gebruiker + medicatie
    @PostMapping("/gebruiker/{gebruikerId}/medicatie/{medicatieId}/schema")
    public ResponseEntity<InnameSchemaResponse> create(
            @PathVariable Long gebruikerId,
            @PathVariable Long medicatieId,
            @Valid @RequestBody InnameSchemaCreateRequest req) {
        var saved = service.create(gebruikerId, medicatieId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(map(saved));
    }

    // lijst per gebruiker
    @GetMapping("/gebruiker/{gebruikerId}/schema")
    public ResponseEntity<List<InnameSchemaResponse>> listByGebruiker(@PathVariable Long gebruikerId) {
        var items = service.listByGebruiker(gebruikerId).stream().map(this::map).toList();
        return ResponseEntity.ok(items);
    }

    // lijst per medicatie
    @GetMapping("/medicatie/{medicatieId}/schema")
    public ResponseEntity<List<InnameSchemaResponse>> listByMedicatie(@PathVariable Long medicatieId) {
        var items = service.listByMedicatie(medicatieId).stream().map(this::map).toList();
        return ResponseEntity.ok(items);
    }

    @PutMapping("/schema/{id}")
    public ResponseEntity<InnameSchemaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody InnameSchemaUpdateRequest req) {
        var updated = service.update(id, req);
        return ResponseEntity.ok(map(updated));
    }

    @DeleteMapping("/schema/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private InnameSchemaResponse map(InnameSchema s) {
        var r = new InnameSchemaResponse();
        r.setId(s.getId());
        r.setGebruikerId(s.getGebruiker() != null ? s.getGebruiker().getId() : null);
        r.setMedicatieId(s.getMedicatie() != null ? s.getMedicatie().getId() : null);
        r.setStartDatum(s.getStartDatum());
        r.setEindDatum(s.getEindDatum());
        r.setFrequentiePerDag(s.getFrequentiePerDag());
        return r;
    }
}
