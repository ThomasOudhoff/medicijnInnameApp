package com.thomas.medicatieinnameapp.serviceTest;

import com.thomas.medicatieinnameapp.service.ToedieningService;
import com.thomas.medicatieinnameapp.dto.ToedieningCreateRequest;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.Medicatie;
import com.thomas.medicatieinnameapp.model.InnameSchema;
import com.thomas.medicatieinnameapp.model.Toediening;
import com.thomas.medicatieinnameapp.repository.ToedieningRepository;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.repository.MedicatieRepository;
import com.thomas.medicatieinnameapp.repository.InnameSchemaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ToedieningServiceTest {

    private ToedieningRepository repo;
    private GebruikerRepository gebruikerRepo;
    private MedicatieRepository medicatieRepo;
    private InnameSchemaRepository schemaRepo;

    private ToedieningService service;

    @BeforeEach
    void setUp() {
        repo = mock(ToedieningRepository.class);
        gebruikerRepo = mock(GebruikerRepository.class);
        medicatieRepo = mock(MedicatieRepository.class);
        schemaRepo = mock(InnameSchemaRepository.class);
        service = new ToedieningService(repo, gebruikerRepo, medicatieRepo, schemaRepo);
    }


    private Gebruiker mkGebruiker(Long id) {
        Gebruiker g = new Gebruiker();
        g.setId(id);
        return g;
    }

    private Medicatie mkMedicatie(Long id, Long gebruikerId) {
        Medicatie m = new Medicatie();
        m.setId(id);
        Gebruiker g = new Gebruiker();
        g.setId(gebruikerId);
        m.setGebruiker(g);
        return m;
    }

    private InnameSchema mkSchema(Long id, Long gebruikerId, Long medicatieId) {
        InnameSchema s = new InnameSchema();
        try {
            s.getClass().getMethod("setId", Long.class).invoke(s, id);
        } catch (Exception ignore) { /* ok, geen setter voor id */ }
        Gebruiker g = new Gebruiker();
        g.setId(gebruikerId);
        Medicatie m = new Medicatie();
        m.setId(medicatieId);
        s.setGebruiker(g);
        s.setMedicatie(m);
        return s;
    }

    // 2
    @Test
    @DisplayName("getByIdOr404: niet gevonden → 404")
    void getByIdOr404_notFound_404() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdOr404(999L))
                .isInstanceOf(ResponseStatusException.class);

        verify(repo).findById(999L);
        verifyNoMoreInteractions(repo, gebruikerRepo, medicatieRepo, schemaRepo);
    }

    // 3
    @Test
    @DisplayName("create: OK zonder schema → bewaart met gebruiker/medicatie en default tijdstip")
    void create_ok_noSchema() {
        Long gebruikerId = 10L;
        Long medicatieId = 20L;

        when(gebruikerRepo.findById(gebruikerId)).thenReturn(Optional.of(mkGebruiker(gebruikerId)));
        when(medicatieRepo.findById(medicatieId)).thenReturn(Optional.of(mkMedicatie(medicatieId, gebruikerId)));
        when(repo.save(any(Toediening.class))).thenAnswer(inv -> inv.getArgument(0));

        var req = mock(ToedieningCreateRequest.class);
        when(req.getSchemaId()).thenReturn(null);
        when(req.getTijdstip()).thenReturn(null); // → service zet LocalDateTime.now()
        when(req.getHoeveelheid()).thenReturn(1);
        when(req.getOpmerking()).thenReturn("na het eten");

        var created = service.create(gebruikerId, medicatieId, req);

        assertThat(created.getGebruiker()).isNotNull();
        assertThat(created.getMedicatie()).isNotNull();
        assertThat(created.getTijdstip()).isNotNull(); // defaulted naar now()
        assertThat(created.getHoeveelheid()).isEqualTo(1);
        assertThat(created.getOpmerking()).isEqualTo("na het eten");

        verify(gebruikerRepo).findById(gebruikerId);
        verify(medicatieRepo).findById(medicatieId);
        verify(repo).save(any(Toediening.class));
        verifyNoMoreInteractions(repo, gebruikerRepo, medicatieRepo, schemaRepo);
    }

    // 4
    @Test
    @DisplayName("create: gebruiker niet gevonden → 404")
    void create_userNotFound_404() {
        Long gebruikerId = 10L;
        when(gebruikerRepo.findById(gebruikerId)).thenReturn(Optional.empty());

        var req = mock(ToedieningCreateRequest.class);

        assertThatThrownBy(() -> service.create(gebruikerId, 20L, req))
                .isInstanceOf(ResponseStatusException.class);

        verify(gebruikerRepo).findById(gebruikerId);
        verifyNoMoreInteractions(repo, gebruikerRepo, medicatieRepo, schemaRepo);
    }

    // 5
    @Test
    @DisplayName("create: medicatie niet gevonden → 404")
    void create_medicatieNotFound_404() {
        Long gebruikerId = 10L;
        Long medicatieId = 20L;

        when(gebruikerRepo.findById(gebruikerId)).thenReturn(Optional.of(mkGebruiker(gebruikerId)));
        when(medicatieRepo.findById(medicatieId)).thenReturn(Optional.empty());

        var req = mock(ToedieningCreateRequest.class);

        assertThatThrownBy(() -> service.create(gebruikerId, medicatieId, req))
                .isInstanceOf(ResponseStatusException.class);

        verify(gebruikerRepo).findById(gebruikerId);
        verify(medicatieRepo).findById(medicatieId);
        verifyNoMoreInteractions(repo, gebruikerRepo, medicatieRepo, schemaRepo);
    }

    // 6
    @Test
    @DisplayName("create: medicatie hoort niet bij gebruiker → 409/Conflict")
    void create_medicatieHoortNietBijGebruiker_conflict() {
        Long gebruikerId = 10L;
        Long medicatieId = 20L;

        when(gebruikerRepo.findById(gebruikerId)).thenReturn(Optional.of(mkGebruiker(gebruikerId)));
        // medicatie gekoppeld aan ándere gebruiker
        when(medicatieRepo.findById(medicatieId)).thenReturn(Optional.of(mkMedicatie(medicatieId, 99L)));

        var req = mock(ToedieningCreateRequest.class);

        assertThatThrownBy(() -> service.create(gebruikerId, medicatieId, req))
                .isInstanceOf(ResponseStatusException.class);

        verify(gebruikerRepo).findById(gebruikerId);
        verify(medicatieRepo).findById(medicatieId);
        // geen save op repo
        verify(repo, never()).save(any());
    }

    // 7
    @Test
    @DisplayName("create: schemaId opgegeven en geldig → bewaart met exact dat schema")
    void create_withSchema_ok() {
        Long gebruikerId = 10L;
        Long medicatieId = 20L;
        Long schemaId = 30L;

        var g = mkGebruiker(gebruikerId);
        var m = mkMedicatie(medicatieId, gebruikerId);
        var s = mkSchema(schemaId, gebruikerId, medicatieId);

        when(gebruikerRepo.findById(gebruikerId)).thenReturn(Optional.of(g));
        when(medicatieRepo.findById(medicatieId)).thenReturn(Optional.of(m));
        when(schemaRepo.findById(schemaId)).thenReturn(Optional.of(s));
        when(repo.save(any(Toediening.class))).thenAnswer(inv -> inv.getArgument(0));

        var req = mock(ToedieningCreateRequest.class);
        when(req.getSchemaId()).thenReturn(schemaId);
        when(req.getTijdstip()).thenReturn(LocalDateTime.of(2030, 1, 1, 8, 0));
        when(req.getHoeveelheid()).thenReturn(2);
        when(req.getOpmerking()).thenReturn("met water");

        var created = service.create(gebruikerId, medicatieId, req);

        ArgumentCaptor<Toediening> toedCaptor = ArgumentCaptor.forClass(Toediening.class);
        verify(repo).save(toedCaptor.capture());
        Toediening saved = toedCaptor.getValue();
        assertThat(saved.getSchemaInname()).isSameAs(s);

        verify(schemaRepo).findById(schemaId);
        verify(gebruikerRepo).findById(gebruikerId);
        verify(medicatieRepo).findById(medicatieId);
    }

    // 8
    @Test
    @DisplayName("create: schemaId opgegeven maar schema niet gevonden → 404")
    void create_schemaNotFound_404() {
        Long gebruikerId = 10L;
        Long medicatieId = 20L;
        Long schemaId = 30L;

        when(gebruikerRepo.findById(gebruikerId)).thenReturn(Optional.of(mkGebruiker(gebruikerId)));
        when(medicatieRepo.findById(medicatieId)).thenReturn(Optional.of(mkMedicatie(medicatieId, gebruikerId)));
        when(schemaRepo.findById(schemaId)).thenReturn(Optional.empty());

        var req = mock(ToedieningCreateRequest.class);
        when(req.getSchemaId()).thenReturn(schemaId);

        assertThatThrownBy(() -> service.create(gebruikerId, medicatieId, req))
                .isInstanceOf(ResponseStatusException.class);

        verify(gebruikerRepo).findById(gebruikerId);
        verify(medicatieRepo).findById(medicatieId);
        verify(schemaRepo).findById(schemaId);
        verify(repo, never()).save(any());
    }

    // 9
    @Test
    @DisplayName("create: schema hoort niet bij dezelfde gebruiker/medicatie → 409/Conflict")
    void create_schemaHoortNietBijGebruikerOfMedicatie_conflict() {
        Long gebruikerId = 10L;
        Long medicatieId = 20L;
        Long schemaId = 30L;

        var g = mkGebruiker(gebruikerId);
        var m = mkMedicatie(medicatieId, gebruikerId);
        var s = mkSchema(schemaId, 99L, medicatieId);
    }
}
