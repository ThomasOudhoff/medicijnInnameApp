package com.thomas.medicatieinnameapp.security;

import com.thomas.medicatieinnameapp.repository.InnameSchemaRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import com.thomas.medicatieinnameapp.repository.ZorgRelatieRepository;
import org.springframework.stereotype.Component;

@Component("ownershipLookup")
public class OwnershipLookup {

    private final MedicatieRepository medicatieRepo;
    private final InnameSchemaRepository schemaRepo;
    private final ZorgRelatieRepository zorgRepo;

    public OwnershipLookup(MedicatieRepository medicatieRepo,
                           InnameSchemaRepository schemaRepo,
                           ZorgRelatieRepository zorgRepo) {
        this.medicatieRepo = medicatieRepo;
        this.schemaRepo = schemaRepo;
        this.zorgRepo = zorgRepo;
    }
    public Long gebruikerIdByMedicatie(Long medicatieId) {
        return medicatieRepo.findById(medicatieId)
                .map(m -> m.getGebruiker().getId())
                .orElse(null);
    }
    public Long gebruikerIdBySchema(Long schemaId) {
        return schemaRepo.findById(schemaId)
                // jouw InnameSchema heeft én een gebruiker én een medicatie; beide zijn oké:
                .map(s -> s.getGebruiker().getId())
                // of: .map(s -> s.getMedicatie().getGebruiker().getId())
                .orElse(null);
    }
    public boolean isVerzorgerOfMedicatie(Long medicatieId, Long verzorgerId) {
        Long ownerId = gebruikerIdByMedicatie(medicatieId);
        return ownerId != null && zorgRepo.existsByVerzorger_IdAndGebruiker_Id(verzorgerId, ownerId);
    }
    public boolean isVerzorgerOfSchema(Long schemaId, Long verzorgerId) {
        Long ownerId = gebruikerIdBySchema(schemaId);
        return ownerId != null && zorgRepo.existsByVerzorger_IdAndGebruiker_Id(verzorgerId, ownerId);
    }
}

