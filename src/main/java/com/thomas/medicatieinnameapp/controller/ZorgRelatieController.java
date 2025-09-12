package com.thomas.medicatieinnameapp.controller;

import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Rol;
import com.thomas.medicatieinnameapp.model.ZorgRelatie;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.repository.ZorgRelatieRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/relaties")
public class ZorgRelatieController {

    private final ZorgRelatieRepository zorgRepo;
    private final GebruikerRepository gebRepo;

    public ZorgRelatieController(ZorgRelatieRepository zorgRepo, GebruikerRepository gebRepo) {
        this.zorgRepo = zorgRepo;
        this.gebRepo = gebRepo;
    }
    public record RelatieRequest(@NotNull Long verzorgerId, @NotNull Long gebruikerId) {}
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody RelatieRequest req) {
        if (req == null || req.verzorgerId() == null || req.gebruikerId() == null) {
            throw new ResponseStatusException(BAD_REQUEST, "verzorgerId en gebruikerId zijn verplicht");
        }

        Gebruiker verzorger = gebRepo.findById(req.verzorgerId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Verzorger niet gevonden"));
        Gebruiker patient = gebRepo.findById(req.gebruikerId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Gebruiker (patiënt) niet gevonden"));

        if (verzorger.getRol() != Rol.VERZORGER) {
            throw new ResponseStatusException(BAD_REQUEST, "verzorgerId is geen VERZORGER");
        }
        if (patient.getRol() != Rol.GEBRUIKER) {
            throw new ResponseStatusException(BAD_REQUEST, "gebruikerId is geen GEBRUIKER");
        }

        if (zorgRepo.existsByVerzorger_IdAndGebruiker_Id(verzorger.getId(), patient.getId())) {
            return ResponseEntity.noContent().build(); // al gekoppeld
        }

        ZorgRelatie zr = new ZorgRelatie();
        zr.setVerzorger(verzorger);
        zr.setGebruiker(patient);
        ZorgRelatie saved = zorgRepo.save(zr);

        return ResponseEntity.created(URI.create("/api/relaties/" + saved.getId())).build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam Long verzorgerId, @RequestParam Long gebruikerId) {
        long deleted = zorgRepo.deleteByVerzorger_IdAndGebruiker_Id(verzorgerId, gebruikerId);
        if (deleted == 0) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
