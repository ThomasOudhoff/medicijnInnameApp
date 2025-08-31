package com.thomas.medicatieinnameapp.service;
import com.thomas.medicatieinnameapp.dto.GebruikerUpdateRequest;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Rol;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class GebruikerService {

    private final GebruikerRepository gebruikerRepository;

    public GebruikerService(GebruikerRepository gebruikerRepository) {
        this.gebruikerRepository = gebruikerRepository;
    }

    public Optional<Gebruiker> getGebruikerById(Long id) {
        return gebruikerRepository.findById(id);
    }

    public Gebruiker getByIdOrThrow(Long id) {
        return gebruikerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gebruiker niet gevonden: " + id));
    }

    @Transactional
    public Gebruiker updateGebruiker(Long id, GebruikerUpdateRequest req) {
        var g = gebruikerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));

        // 409 als e-mail al door een ander id gebruikt wordt
        boolean emailInGebruik = gebruikerRepository
                .existsByEmailIgnoreCaseAndIdNot(req.getEmail(), id);
        if (emailInGebruik) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mailadres is al in gebruik");
        }

        g.setNaam(req.getNaam());
        g.setEmail(req.getEmail());
        g.setRol(req.getRol());

        return gebruikerRepository.save(g);
    }
    public List<Gebruiker> getAllGebruikers() { return gebruikerRepository.findAll(); }
    public Gebruiker saveGebruiker(Gebruiker g) { return gebruikerRepository.save(g); }
}
