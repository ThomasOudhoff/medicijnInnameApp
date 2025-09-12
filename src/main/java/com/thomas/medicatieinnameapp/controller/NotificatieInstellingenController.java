package com.thomas.medicatieinnameapp.controller;

import com.thomas.medicatieinnameapp.dto.NotificatieInstellingenRequest;
import com.thomas.medicatieinnameapp.dto.NotificatieInstellingenResponse;
import com.thomas.medicatieinnameapp.model.NotificatieInstellingen;
import com.thomas.medicatieinnameapp.service.NotificatieInstellingenService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gebruiker/{gebruikerId}/instellingen")
public class NotificatieInstellingenController {

    private final NotificatieInstellingenService service;

    public NotificatieInstellingenController(NotificatieInstellingenService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<NotificatieInstellingenResponse> get(@PathVariable Long gebruikerId) {
        NotificatieInstellingen n = service.getByGebruikerIdOr404(gebruikerId);
        return ResponseEntity.ok(map(n));
    }

    // PUT = idempotent upsert: maakt aan als het nog niet bestaat
    @PutMapping
    public ResponseEntity<NotificatieInstellingenResponse> put(
            @PathVariable Long gebruikerId,
            @Valid @RequestBody NotificatieInstellingenRequest req) {
        boolean bestond = false;
        try {
            service.getByGebruikerIdOr404(gebruikerId);
            bestond = true;
        } catch (org.springframework.web.server.ResponseStatusException ignored) {}
        NotificatieInstellingen saved = service.upsert(gebruikerId, req);
        return bestond ? ResponseEntity.ok(map(saved))
                : ResponseEntity.status(HttpStatus.CREATED).body(map(saved));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long gebruikerId) {
        service.deleteByGebruikerId(gebruikerId);
        return ResponseEntity.noContent().build();
    }

    private NotificatieInstellingenResponse map(NotificatieInstellingen n) {
        var r = new NotificatieInstellingenResponse();
        r.setId(n.getId());
        r.setGebruikerId(n.getGebruiker().getId());
        r.setStilleStart(n.getStilleStart());
        r.setStilleEinde(n.getStilleEinde());
        r.setKanaal(n.getKanaal());
        r.setMinutenVooraf(n.getMinutenVooraf());
        r.setSnoozeMinuten(n.getSnoozeMinuten());
        r.setActief(n.getActief());
        return r;
    }
}
