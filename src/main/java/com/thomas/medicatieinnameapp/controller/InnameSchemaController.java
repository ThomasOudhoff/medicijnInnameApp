package com.thomas.medicatieinnameapp.controller;

import com.thomas.medicatieinnameapp.dto.InnameSchemaCreateRequest;
import com.thomas.medicatieinnameapp.dto.InnameSchemaResponse;
import com.thomas.medicatieinnameapp.dto.InnameSchemaUpdateRequest;
import com.thomas.medicatieinnameapp.model.InnameSchema;
import com.thomas.medicatieinnameapp.service.InnameSchemaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicatie/{medicatieId}/schema")
public class InnameSchemaController {

    private final InnameSchemaService service;

    public InnameSchemaController(InnameSchemaService service) {
        this.service = service;
    }

    /** CREATE schema voor een medicatie (admin of eigenaar van de medicatie) */
    @PreAuthorize("hasRole('ADMIN') or @ownershipLookup.gebruikerIdByMedicatie(#medicatieId) == principal.id")
    @PostMapping
    public ResponseEntity<InnameSchemaResponse> create(@PathVariable Long medicatieId,
                                                       @Valid @RequestBody InnameSchemaCreateRequest req) {
        InnameSchema saved = service.create(medicatieId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(map(saved));
    }

    /** LIST schema’s van een medicatie (admin, eigenaar, of verzorger van de patiënt) */
    @PreAuthorize("""
        hasRole('ADMIN')
        or @ownershipLookup.gebruikerIdByMedicatie(#medicatieId) == principal.id
        or (hasRole('VERZORGER') and @ownershipLookup.isVerzorgerOfMedicatie(#medicatieId, principal.id))
    """)
    @GetMapping
    public ResponseEntity<List<InnameSchemaResponse>> listByMedicatie(@PathVariable Long medicatieId) {
        List<InnameSchemaResponse> items = service.listByMedicatie(medicatieId)
                .stream().map(this::map).toList();
        return ResponseEntity.ok(items);
    }

    /** UPDATE schema (admin of eigenaar van het schema) */
    @PreAuthorize("hasRole('ADMIN') or @ownershipLookup.gebruikerIdBySchema(#schemaId) == principal.id")
    @PutMapping("/{schemaId}")
    public ResponseEntity<InnameSchemaResponse> update(@PathVariable Long medicatieId,
                                                       @PathVariable Long schemaId,
                                                       @Valid @RequestBody InnameSchemaUpdateRequest req) {
        InnameSchema updated = service.update(schemaId, req);
        return ResponseEntity.ok(map(updated));
    }

    /** DELETE schema (admin of eigenaar van het schema) */
    @PreAuthorize("hasRole('ADMIN') or @ownershipLookup.gebruikerIdBySchema(#schemaId) == principal.id")
    @DeleteMapping("/{schemaId}")
    public ResponseEntity<Void> delete(@PathVariable Long medicatieId,
                                       @PathVariable Long schemaId) {
        service.delete(schemaId);
        return ResponseEntity.noContent().build();
    }

    private InnameSchemaResponse map(InnameSchema s) {
        InnameSchemaResponse r = new InnameSchemaResponse();
        r.setId(s.getId());
        r.setGebruikerId(s.getGebruiker() != null ? s.getGebruiker().getId() : null);
        r.setMedicatieId(s.getMedicatie() != null ? s.getMedicatie().getId() : null);
        r.setStartDatum(s.getStartDatum());
        r.setEindDatum(s.getEindDatum());
        r.setFrequentiePerDag(s.getFrequentiePerDag());
        return r;
    }
    //comment voor pull request
}
