package com.thomas.medicatieinnameapp.controller;

import com.thomas.medicatieinnameapp.dto.BijsluiterUrlRequest;
import com.thomas.medicatieinnameapp.dto.MedicatieCreateRequest;
import com.thomas.medicatieinnameapp.dto.MedicatieResponse;
import com.thomas.medicatieinnameapp.dto.MedicatieUpdateRequest;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.service.MedicatieService;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/medicatie")
public class MedicatieController {

    private final MedicatieService medicatieService;

    public MedicatieController(MedicatieService medicatieService) {
        this.medicatieService = medicatieService;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<MedicatieResponse>> list() {
        List<Medicatie> meds = medicatieService.getAllMedicatie();
        List<MedicatieResponse> dto = meds.stream().map(this::map).toList();
        return ResponseEntity.ok(dto);
    }
    @PreAuthorize("hasRole('ADMIN') or @ownershipLookup.gebruikerIdByMedicatie(#id) == principal.id or (hasRole('VERZORGER') and @ownershipLookup.isVerzorgerOfMedicatie(#id, principal.id))")
    @GetMapping("/{id}")
    public ResponseEntity<MedicatieResponse> get(@PathVariable Long id) {
        Medicatie m = medicatieService.getByIdOr404(id);
        return ResponseEntity.ok(map(m));
    }
    @PreAuthorize("hasRole('ADMIN') or #gebruikerId == principal.id")
    @PostMapping("/gebruiker/{gebruikerId}")
    public ResponseEntity<MedicatieResponse> create(@PathVariable Long gebruikerId,
                                                    @Valid @RequestBody MedicatieCreateRequest req) {
        Medicatie m = medicatieService.createForGebruiker(gebruikerId, req.getNaam());
        return ResponseEntity.status(HttpStatus.CREATED).body(map(m));
    }
    @PreAuthorize("hasRole('ADMIN') or @ownershipLookup.gebruikerIdByMedicatie(#id) == principal.id")
    @PutMapping("{id}")
    public ResponseEntity<MedicatieResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody MedicatieUpdateRequest req) {
        Medicatie updated = medicatieService.update(id, req);
        return ResponseEntity.ok(map(updated));
    }

    @PreAuthorize("hasRole('ADMIN') or @ownershipLookup.gebruikerIdByMedicatie(#id) == principal.id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicatieService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('ADMIN') or @ownershipLookup.gebruikerIdByMedicatie(#id) == principal.id")
    @PostMapping(value = "/{id}/bijsluiter", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadBijsluiter(@PathVariable Long id,
                                                 @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            String ct = file.getContentType();
            if (ct != null && !(ct.startsWith("image/") || ct.equals(MediaType.APPLICATION_PDF_VALUE))) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }
            medicatieService.saveBijsluiterFoto(id, file.getBytes());
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload mislukt");
        }
    }

    @PreAuthorize("hasRole('ADMIN') or @ownershipLookup.gebruikerIdByMedicatie(#id) == principal.id or (hasRole('VERZORGER') and @ownershipLookup.isVerzorgerOfMedicatie(#id, principal.id))")
    @GetMapping("/{id}/bijsluiter")
    public ResponseEntity<ByteArrayResource> downloadBijsluiter(@PathVariable Long id) {
        byte[] data = medicatieService.getBijsluiterFoto(id);
        if (data == null || data.length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Geen bijsluiter gevonden");
        }
        ByteArrayResource body = new ByteArrayResource(data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("bijsluiter-" + id + ".bin")
                .build());
        headers.setContentLength(data.length);

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or @ownershipLookup.gebruikerIdByMedicatie(#id) == principal.id")
    @DeleteMapping("/{id}/bijsluiter")
    public ResponseEntity<Void> deleteBijsluiter(@PathVariable Long id) {
        medicatieService.deleteBijsluiterFoto(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasRole('ADMIN') or @ownershipLookup.gebruikerIdByMedicatie(#id) == principal.id")
    @PutMapping("/{id}/bijsluiter-url")
    public ResponseEntity<Void> setBijsluiterUrl(@PathVariable Long id,
                                                 @Valid @RequestBody BijsluiterUrlRequest req) {
        medicatieService.setBijsluiterUrl(id, req.getUrl());
        return ResponseEntity.noContent().build();
    }
    private MedicatieResponse map(Medicatie m) {
        MedicatieResponse r = new MedicatieResponse();
        r.setId(m.getId());
        r.setNaam(m.getNaamMedicijn());
        r.setGebruikerId(m.getGebruiker() != null ? m.getGebruiker().getId() : null);
        return r;
    }
}
