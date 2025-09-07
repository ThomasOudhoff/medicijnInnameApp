package com.thomas.medicatieinnameapp.service;

import com.thomas.medicatieinnameapp.dto.GebruikerUpdateRequest;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class GebruikerService {

    private final GebruikerRepository gebruikerRepository;
    private final MedicatieRepository medicatieRepository;

    public GebruikerService(GebruikerRepository gebruikerRepository,
                            MedicatieRepository medicatieRepository) {
        this.gebruikerRepository = gebruikerRepository;
        this.medicatieRepository = medicatieRepository;
    }
    public Optional<Gebruiker> getGebruikerById(Long id) {
        return gebruikerRepository.findById(id);
    }

    public List<Gebruiker> getAllGebruikers() {
        return gebruikerRepository.findAll();
    }

    @Transactional
    public Gebruiker saveGebruiker(Gebruiker g) {
        // 409 als e-mail al bestaat
        if (g.getEmail() != null && gebruikerRepository.existsByEmailIgnoreCase(g.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mailadres is al in gebruik");
        }
        return gebruikerRepository.save(g);
    }

    public Gebruiker getByIdOrThrow(Long id) {
        return gebruikerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden: " + id));
    }

    @Transactional
    public Gebruiker updateGebruiker(Long id, GebruikerUpdateRequest req) {
        Gebruiker g = gebruikerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));

        // 409 als e-mail al door iemand anders gebruikt wordt
        if (gebruikerRepository.existsByEmailIgnoreCaseAndIdNot(req.getEmail(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mailadres is al in gebruik");
        }

        g.setNaam(req.getNaam());
        g.setEmail(req.getEmail());
        g.setRol(req.getRol());

        return gebruikerRepository.save(g);
    }

    @Transactional
    public void deleteGebruiker(Long id) {
        if (!gebruikerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden");
        }
        gebruikerRepository.deleteById(id);
    }

    @Transactional
    public Medicatie voegMedicatieToeAanGebruiker(Long gebruikerId, Medicatie nieuwe) {
        Gebruiker gebruiker = gebruikerRepository.findById(gebruikerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));

        nieuwe.setGebruiker(gebruiker);
        return medicatieRepository.save(nieuwe);
    }

    public List<Medicatie> getMedicatiesVoorGebruiker(Long gebruikerId) {
        if (!gebruikerRepository.existsById(gebruikerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden");
        }
        return medicatieRepository.findByGebruikerId(gebruikerId);
    }
}
