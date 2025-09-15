package com.thomas.medicatieinnameapp.serviceTest;

import com.thomas.medicatieinnameapp.service.InnameSchemaService;
import com.thomas.medicatieinnameapp.dto.InnameSchemaCreateRequest;
import com.thomas.medicatieinnameapp.dto.InnameSchemaUpdateRequest;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.InnameSchema;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.repository.InnameSchemaRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InnameSchemaServiceTest {

    private InnameSchemaRepository repo;
    private MedicatieRepository medicatieRepo;
    private InnameSchemaService service;

    @BeforeEach
    void setUp() {
        repo = mock(InnameSchemaRepository.class);
        medicatieRepo = mock(MedicatieRepository.class);
        service = new InnameSchemaService(repo, medicatieRepo);
    }

    private Medicatie mkMedicatie(Long medicatieId, Long gebruikerId) {
        var g = new Gebruiker(); g.setId(gebruikerId);
        var m = new Medicatie(); m.setId(medicatieId); m.setGebruiker(g);
        return m;
    }

    private InnameSchema mkSchema(Long id, Long gebruikerId, Long medicatieId,
                                  LocalDate start, LocalDate eind, Integer freq) {
        var s = new InnameSchema();
        try { s.getClass().getMethod("setId", Long.class).invoke(s, id); } catch (Exception ignore) {}
        var g = new Gebruiker(); g.setId(gebruikerId);
        var m = new Medicatie(); m.setId(medicatieId);
        s.setGebruiker(g);
        s.setMedicatie(m);
        s.setStartDatum(start);
        s.setEindDatum(eind);
        s.setFrequentiePerDag(freq);
        return s;
    }

    // 1
    @Test
    @DisplayName("getByIdOr404: gevonden → entity terug")
    void getByIdOr404_found_ok() {
        var s = mkSchema(1L, 10L, 20L, LocalDate.now(), null, 1);
        when(repo.findById(1L)).thenReturn(Optional.of(s));

        var out = service.getByIdOr404(1L);

        assertThat(out).isSameAs(s);
        verify(repo).findById(1L);
        verifyNoMoreInteractions(repo, medicatieRepo);
    }

    // 2
    @Test
    @DisplayName("getByIdOr404: niet gevonden → 404")
    void getByIdOr404_notFound_404() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdOr404(999L))
                .isInstanceOf(ResponseStatusException.class);

        verify(repo).findById(999L);
        verifyNoMoreInteractions(repo, medicatieRepo);
    }

    // 3
    @Test
    @DisplayName("create: OK → startDatum verplicht; eindDatum ≥ startDatum; eigenaar = medicatie.gebruiker")
    void create_ok() {
        Long medicatieId = 20L, gebruikerId = 10L;
        when(medicatieRepo.findById(medicatieId))
                .thenReturn(Optional.of(mkMedicatie(medicatieId, gebruikerId)));
        when(repo.save(any(InnameSchema.class))).thenAnswer(inv -> {
            InnameSchema saved = inv.getArgument(0);
            try { saved.getClass().getMethod("setId", Long.class).invoke(saved, 100L); } catch (Exception ignore) {}
            return saved;
        });

        var req = new InnameSchemaCreateRequest();
        req.setStartDatum(LocalDate.of(2030, 1, 1));
        req.setEindDatum(LocalDate.of(2030, 1, 10));
        req.setFrequentiePerDag(3);

        var created = service.create(medicatieId, req);

        assertThat(created.getStartDatum()).isEqualTo(LocalDate.of(2030, 1, 1));
        assertThat(created.getEindDatum()).isEqualTo(LocalDate.of(2030, 1, 10));
        assertThat(created.getFrequentiePerDag()).isEqualTo(3);
        assertThat(created.getMedicatie()).isNotNull();
        assertThat(created.getGebruiker()).isNotNull();

        verify(medicatieRepo).findById(medicatieId);
        verify(repo).save(any(InnameSchema.class));
        verifyNoMoreInteractions(repo, medicatieRepo);
    }

    // 4
    @Test
    @DisplayName("create: medicatie niet gevonden → 404")
    void create_medicatieNotFound_404() {
        when(medicatieRepo.findById(111L)).thenReturn(Optional.empty());

        var req = new InnameSchemaCreateRequest();
        req.setStartDatum(LocalDate.now());

        assertThatThrownBy(() -> service.create(111L, req))
                .isInstanceOf(ResponseStatusException.class);

        verify(medicatieRepo).findById(111L);
        verifyNoMoreInteractions(repo, medicatieRepo);
    }

    // 5
    @Test
    @DisplayName("create: startDatum ontbreekt → 400/Bad Request")
    void create_missingStartDate_badRequest() {
        Long medicatieId = 20L;
        when(medicatieRepo.findById(medicatieId))
                .thenReturn(Optional.of(mkMedicatie(medicatieId, 10L)));

        var req = new InnameSchemaCreateRequest();
        req.setStartDatum(null);

        assertThatThrownBy(() -> service.create(medicatieId, req))
                .isInstanceOf(ResponseStatusException.class);

        verify(medicatieRepo).findById(medicatieId);
        verifyNoMoreInteractions(repo, medicatieRepo);
    }

    // 6
    @Test
    @DisplayName("update: OK – past alleen meegegeven velden aan en valideert datums")
    void update_ok() {
        Long id = 5L;
        var bestaand = mkSchema(id, 10L, 20L, LocalDate.of(2030,1,1), null, 1);
        when(repo.findById(id)).thenReturn(Optional.of(bestaand));
        when(repo.save(any(InnameSchema.class))).thenAnswer(inv -> inv.getArgument(0));

        var req = new InnameSchemaUpdateRequest();
        req.setStartDatum(LocalDate.of(2030,1,2));
        req.setEindDatum(LocalDate.of(2030,1,10));
        req.setFrequentiePerDag(2);

        var updated = service.update(id, req);

        assertThat(updated.getStartDatum()).isEqualTo(LocalDate.of(2030,1,2));
        assertThat(updated.getEindDatum()).isEqualTo(LocalDate.of(2030,1,10));
        assertThat(updated.getFrequentiePerDag()).isEqualTo(2);

        verify(repo).findById(id);
        verify(repo).save(any(InnameSchema.class));
        verifyNoMoreInteractions(repo, medicatieRepo);
    }

    // 7
    @Test
    @DisplayName("update: eindDatum vóór startDatum → 400/Bad Request")
    void update_endBeforeStart_badRequest() {
        Long id = 6L;
        var bestaand = mkSchema(id, 10L, 20L, LocalDate.of(2030,1,10), null, 1);
        when(repo.findById(id)).thenReturn(Optional.of(bestaand));

        var req = new InnameSchemaUpdateRequest();
        req.setEindDatum(LocalDate.of(2030,1,1)); // vóór start

        assertThatThrownBy(() -> service.update(id, req))
                .isInstanceOf(ResponseStatusException.class);

        verify(repo).findById(id);
        verifyNoMoreInteractions(repo, medicatieRepo);
    }

    // 8
    @Test
    @DisplayName("delete: OK → repo.deleteById")
    void delete_ok() {
        when(repo.existsById(9L)).thenReturn(true);

        service.delete(9L);

        verify(repo).existsById(9L);
        verify(repo).deleteById(9L);
        verifyNoMoreInteractions(repo, medicatieRepo);
    }
}
