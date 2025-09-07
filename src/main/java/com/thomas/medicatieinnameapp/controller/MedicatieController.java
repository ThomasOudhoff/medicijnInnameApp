package com.thomas.medicatieinnameapp.controller;

import com.thomas.medicatieinnameapp.dto.MedicatieResponse;
import com.thomas.medicatieinnameapp.dto.MedicatieUpdateRequest;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.service.MedicatieService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicatie")
public class MedicatieController {

    private final MedicatieService medicatieService;

    public MedicatieController(MedicatieService medicatieService) {
        this.medicatieService = medicatieService;
    }

    @GetMapping
    public ResponseEntity<List<MedicatieResponse>> list() {
        List<Medicatie> meds = medicatieService.getAllMedicatie();
        List<MedicatieResponse> dto = meds.stream().map(this::map).toList();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicatieResponse> get(@PathVariable Long id) {
        Medicatie m = medicatieService.getByIdOr404(id);
        return ResponseEntity.ok(map(m));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicatieResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MedicatieUpdateRequest req) {
        Medicatie updated = medicatieService.update(id, req);
        return ResponseEntity.ok(map(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicatieService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }

    private MedicatieResponse map(Medicatie m) {
        MedicatieResponse r = new MedicatieResponse();
        r.setId(m.getId());
        r.setNaam(m.getNaamMedicijn());
        r.setGebruikerId(m.getGebruiker() != null ? m.getGebruiker().getId() : null);
        return r;
    }
}
