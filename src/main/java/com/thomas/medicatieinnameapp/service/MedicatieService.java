package com.thomas.medicatieinnameapp.service;

import com.thomas.medicatieinnameapp.dto.MedicatieUpdateRequest;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.model.MedicatieBijsluiter;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieBijsluiterRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class MedicatieService {

    private final MedicatieRepository medicatieRepository;
    private final GebruikerRepository gebruikerRepository;
    private final MedicatieBijsluiterRepository bijsluiterRepo;

    public MedicatieService(MedicatieRepository medicatieRepository,
                            GebruikerRepository gebruikerRepository,
                            MedicatieBijsluiterRepository bijsluiterRepo) {
        this.medicatieRepository = medicatieRepository;
        this.gebruikerRepository = gebruikerRepository;
        this.bijsluiterRepo = bijsluiterRepo;
    }

    public Medicatie saveMedicatie(Medicatie medicatie) {
        return medicatieRepository.save(medicatie);
    }

    public List<Medicatie> getAllMedicatie() {
        return medicatieRepository.findAll();
    }

    public Medicatie getByIdOr404(Long id) {
        return medicatieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicatie niet gevonden"));
    }

    @Transactional
    public Medicatie update(Long id, MedicatieUpdateRequest req) {
        Medicatie m = getByIdOr404(id);
        m.setNaamMedicijn(req.getNaam());
        m.setOmschrijving(req.getOmschrijving());
        m.setBijsluiterUrl(req.getBijsluiterUrl());
        return medicatieRepository.save(m);
    }

    @Transactional
    public Medicatie createForGebruiker(Long gebruikerId, String naam) {
        Gebruiker g = gebruikerRepository.findById(gebruikerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gebruiker niet gevonden"));
        Medicatie m = new Medicatie();
        m.setNaamMedicijn(naam);
        m.setGebruiker(g);
        return medicatieRepository.save(m);
    }

    @Transactional
    public void delete(Long id) {
        if (!medicatieRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicatie niet gevonden");
        }
        medicatieRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<MedicatieBijsluiter> getBijsluiter(Long medicatieId) {
        return bijsluiterRepo.findById(medicatieId);
    }

    @Transactional(readOnly = true)
    public byte[] getBijsluiterFoto(Long medicatieId) {
        MedicatieBijsluiter b = bijsluiterRepo.findById(medicatieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Geen bijsluiter"));
        byte[] data = b.getData();
        if (data == null || data.length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Geen bijsluiter");
        }
        return data;
    }

    @Transactional
    public void saveBijsluiterFoto(Long medicatieId, byte[] bytes,
                                   String contentType, String filename, long sizeBytes) {
        if (bytes == null || bytes.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Leeg bestand");
        }
        if (bytes.length > 5 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Bestand is te groot (max 5MB)");
        }

        Medicatie m = getByIdOr404(medicatieId);

        MedicatieBijsluiter b = bijsluiterRepo.findById(medicatieId).orElseGet(() -> {
            MedicatieBijsluiter nb = new MedicatieBijsluiter();
            nb.setMedicatie(m);
            return nb;
        });

        String ct = (contentType == null || contentType.isBlank())
                ? "application/octet-stream" : contentType;

        b.setData(bytes);
        b.setSizeBytes((long) bytes.length);
        b.setContentType(ct);
        b.setFilename(filename);
        bijsluiterRepo.save(b);
    }

    @Transactional
    public void saveBijsluiterFoto(Long medicatieId, byte[] bytes) {
        saveBijsluiterFoto(medicatieId, bytes, null, null, bytes != null ? bytes.length : 0);
    }

    @Transactional
    public void deleteBijsluiterFoto(Long medicatieId) {
        if (!bijsluiterRepo.existsById(medicatieId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Geen bijsluiter");
        }
        bijsluiterRepo.deleteById(medicatieId);
    }

    @Transactional
    public void setBijsluiterUrl(Long medicatieId, String url) {
        if (url == null || url.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ongeldige URL");
        }
        String trimmed = url.trim();
        try {
            java.net.URI u = java.net.URI.create(trimmed);
            String scheme = u.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ongeldige URL");
        }

        Medicatie m = getByIdOr404(medicatieId);

        var optBijsluiter = bijsluiterRepo.findById(medicatieId);
        if (optBijsluiter.isPresent()) {
            MedicatieBijsluiter b = optBijsluiter.get();
            b.setUrl(trimmed);
            bijsluiterRepo.save(b);
        } else {

            m.setBijsluiterUrl(trimmed);
        }

        medicatieRepository.save(m);
    }

}
