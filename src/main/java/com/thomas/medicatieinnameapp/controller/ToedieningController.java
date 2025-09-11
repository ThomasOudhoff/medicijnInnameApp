package com.thomas.medicatieinnameapp.controller;

import com.thomas.medicatieinnameapp.dto.*;
import com.thomas.medicatieinnameapp.model.Toediening;
import com.thomas.medicatieinnameapp.service.ToedieningService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ToedieningController {

    private final ToedieningService service;

    public ToedieningController(ToedieningService service) {
        this.service = service;
    }

    @PostMapping("/gebruiker/{gebruikerId}/medicatie/{medicatieId}/toediening")
    public ResponseEntity<ToedieningResponse> create(
            @PathVariable Long gebruikerId,
            @PathVariable Long medicatieId,
            @Valid @RequestBody ToedieningCreateRequest req) {
        var saved = service.create(gebruikerId, medicatieId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(map(saved));
    }

    @GetMapping("/gebruiker/{gebruikerId}/toediening")
    public ResponseEntity<List<ToedieningResponse>> listByGebruiker(
            @PathVariable Long gebruikerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var items = service.listByGebruiker(gebruikerId, from, to).stream().map(this::map).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/medicatie/{medicatieId}/toediening")
    public ResponseEntity<List<ToedieningResponse>> listByMedicatie(
            @PathVariable Long medicatieId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        var items = service.listByMedicatie(medicatieId, from, to).stream().map(this::map).toList();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/schema/{schemaId}/toediening")
    public ResponseEntity<List<ToedieningResponse>> listBySchema(@PathVariable Long schemaId) {
        var items = service.listBySchema(schemaId).stream().map(this::map).toList();
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/toediening/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ToedieningResponse map(Toediening t) {
        var r = new ToedieningResponse();
        r.setId(t.getId());
        r.setGebruikerId(t.getGebruiker() != null ? t.getGebruiker().getId() : null);
        r.setMedicatieId(t.getMedicatie() != null ? t.getMedicatie().getId() : null);
        r.setSchemaId(t.getSchemaInname() != null ? t.getSchemaInname().getId() : null);
        r.setTijdstip(t.getTijdstip());
        r.setHoeveelheid(t.getHoeveelheid());
        r.setOpmerking(t.getOpmerking());
        return r;
    }
}
