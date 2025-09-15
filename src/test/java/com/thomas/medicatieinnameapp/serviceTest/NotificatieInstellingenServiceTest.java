package com.thomas.medicatieinnameapp.serviceTest;

import com.thomas.medicatieinnameapp.service.NotificatieInstellingenService;
import com.thomas.medicatieinnameapp.dto.NotificatieInstellingenRequest;
import com.thomas.medicatieinnameapp.model.Gebruiker;
import com.thomas.medicatieinnameapp.model.NotificatieInstellingen;
import com.thomas.medicatieinnameapp.model.NotificatieKanaal;
import com.thomas.medicatieinnameapp.repository.GebruikerRepository;
import com.thomas.medicatieinnameapp.repository.NotificatieInstellingenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NotificatieInstellingenServiceTest {

    private NotificatieInstellingenRepository repo;
    private GebruikerRepository gebruikerRepo;
    private NotificatieInstellingenService service;

    @BeforeEach
    void setUp() {
        repo = mock(NotificatieInstellingenRepository.class);
        gebruikerRepo = mock(GebruikerRepository.class);
        service = new NotificatieInstellingenService(repo, gebruikerRepo);
    }

    private Gebruiker mkGebruiker(Long id) {
        Gebruiker g = new Gebruiker();
        g.setId(id);
        return g;
    }

    private NotificatieInstellingen mkInstellingen(Long gebruikerId) {
        NotificatieInstellingen n = new NotificatieInstellingen();
        Gebruiker g = new Gebruiker(); g.setId(gebruikerId);
        n.setGebruiker(g);
        n.setKanaal(NotificatieKanaal.email);          // enum
        n.setStilleStart(LocalTime.of(22,0));
        n.setStilleEinde(LocalTime.of(7,0));
        n.setMinutenVooraf(15);
        n.setSnoozeMinuten(5);
        n.setActief(true);
        return n;
    }

    // 1
    @Test
    @DisplayName("getByGebruikerIdOr404: gevonden → entity retour")
    void getByGebruikerIdOr404_found_ok() {
        Long gebruikerId = 10L;
        var n = mkInstellingen(gebruikerId);
        when(repo.findByGebruikerId(gebruikerId)).thenReturn(Optional.of(n));

        var out = service.getByGebruikerIdOr404(gebruikerId);

        assertThat(out).isSameAs(n);
        verify(repo).findByGebruikerId(gebruikerId);
        verifyNoMoreInteractions(repo, gebruikerRepo);
    }

    // 2
    @Test
    @DisplayName("getByGebruikerIdOr404: niet gevonden → 404")
    void getByGebruikerIdOr404_notFound_404() {
        when(repo.findByGebruikerId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByGebruikerIdOr404(99L))
                .isInstanceOf(ResponseStatusException.class);

        verify(repo).findByGebruikerId(99L);
        verifyNoMoreInteractions(repo, gebruikerRepo);
    }

    // 3
    @Test
    @DisplayName("upsert: gebruiker niet gevonden → 404")
    void upsert_userNotFound_404() {
        Long gebruikerId = 11L;
        when(gebruikerRepo.findById(gebruikerId)).thenReturn(Optional.empty());
        var req = mock(NotificatieInstellingenRequest.class);

        assertThatThrownBy(() -> service.upsert(gebruikerId, req))
                .isInstanceOf(ResponseStatusException.class);

        verify(gebruikerRepo).findById(gebruikerId);
        verifyNoInteractions(repo);
    }

    // 4
    @Test
    @DisplayName("upsert: nieuw (orElseGet-pad) → koppelt gebruiker en slaat op")
    void upsert_createNew_ok() {
        Long gebruikerId = 12L;
        when(gebruikerRepo.findById(gebruikerId)).thenReturn(Optional.of(mkGebruiker(gebruikerId)));
        when(repo.findByGebruikerId(gebruikerId)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = mock(NotificatieInstellingenRequest.class);
        when(req.getKanaal()).thenReturn(NotificatieKanaal.push); // enum
        when(req.getStilleStart()).thenReturn(LocalTime.of(23, 0));
        when(req.getStilleEinde()).thenReturn(LocalTime.of(6, 30));
        when(req.getMinutenVooraf()).thenReturn(20);
        when(req.getSnoozeMinuten()).thenReturn(10);
        when(req.getActief()).thenReturn(true);

        var saved = service.upsert(gebruikerId, req);

        assertThat(saved.getGebruiker()).isNotNull();
        assertThat(saved.getGebruiker().getId()).isEqualTo(gebruikerId);
        assertThat(saved.getKanaal()).isEqualTo(NotificatieKanaal.push); // enum-vergelijk
        assertThat(saved.getStilleStart()).isEqualTo(LocalTime.of(23,0));
        assertThat(saved.getStilleEinde()).isEqualTo(LocalTime.of(6,30));
        assertThat(saved.getMinutenVooraf()).isEqualTo(20);
        assertThat(saved.getSnoozeMinuten()).isEqualTo(10);
        assertThat(saved.getActief()).isTrue();

        verify(gebruikerRepo).findById(gebruikerId);
        verify(repo).findByGebruikerId(gebruikerId);
        verify(repo).save(any(NotificatieInstellingen.class));
        verifyNoMoreInteractions(repo, gebruikerRepo);
    }

    // 5
    @Test
    @DisplayName("upsert: bestaand (findByGebruikerId-pad) → werkt velden bij en slaat op")
    void upsert_updateExisting_ok() {
        Long gebruikerId = 13L;
        var existing = mkInstellingen(gebruikerId);
        when(gebruikerRepo.findById(gebruikerId)).thenReturn(Optional.of(mkGebruiker(gebruikerId)));
        when(repo.findByGebruikerId(gebruikerId)).thenReturn(Optional.of(existing));
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = mock(NotificatieInstellingenRequest.class);
        when(req.getKanaal()).thenReturn(NotificatieKanaal.email); // enum
        when(req.getStilleStart()).thenReturn(LocalTime.of(21, 0));
        when(req.getStilleEinde()).thenReturn(LocalTime.of(7, 30));
        when(req.getMinutenVooraf()).thenReturn(5);
        when(req.getSnoozeMinuten()).thenReturn(2);
        when(req.getActief()).thenReturn(false);

        var saved = service.upsert(gebruikerId, req);

        assertThat(saved).isSameAs(existing);
        assertThat(saved.getKanaal()).isEqualTo(NotificatieKanaal.email);
        assertThat(saved.getStilleStart()).isEqualTo(LocalTime.of(21,0));
        assertThat(saved.getStilleEinde()).isEqualTo(LocalTime.of(7,30));
        assertThat(saved.getMinutenVooraf()).isEqualTo(5);
        assertThat(saved.getSnoozeMinuten()).isEqualTo(2);
        assertThat(saved.getActief()).isFalse();

        verify(gebruikerRepo).findById(gebruikerId);
        verify(repo).findByGebruikerId(gebruikerId);
        verify(repo).save(existing);
        verifyNoMoreInteractions(repo, gebruikerRepo);
    }

    // 6
    @Test
    @DisplayName("upsert: alle velden uit request worden doorgezet naar save()")
    void upsert_setsAllFields_captured() {
        Long gebruikerId = 14L;
        when(gebruikerRepo.findById(gebruikerId)).thenReturn(Optional.of(mkGebruiker(gebruikerId)));
        when(repo.findByGebruikerId(gebruikerId)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = mock(NotificatieInstellingenRequest.class);
        when(req.getKanaal()).thenReturn(NotificatieKanaal.push); // enum
        when(req.getStilleStart()).thenReturn(LocalTime.of(22, 15));
        when(req.getStilleEinde()).thenReturn(LocalTime.of(6, 45));
        when(req.getMinutenVooraf()).thenReturn(30);
        when(req.getSnoozeMinuten()).thenReturn(3);
        when(req.getActief()).thenReturn(true);

        service.upsert(gebruikerId, req);

        ArgumentCaptor<NotificatieInstellingen> captor = ArgumentCaptor.forClass(NotificatieInstellingen.class);
        verify(repo).save(captor.capture());
        var entity = captor.getValue();
        assertThat(entity.getGebruiker()).isNotNull();
        assertThat(entity.getGebruiker().getId()).isEqualTo(gebruikerId);
        assertThat(entity.getKanaal()).isEqualTo(NotificatieKanaal.push); // enum-vergelijk
        assertThat(entity.getStilleStart()).isEqualTo(LocalTime.of(22,15));
        assertThat(entity.getStilleEinde()).isEqualTo(LocalTime.of(6,45));
        assertThat(entity.getMinutenVooraf()).isEqualTo(30);
        assertThat(entity.getSnoozeMinuten()).isEqualTo(3);
        assertThat(entity.getActief()).isTrue();

        verify(repo).findByGebruikerId(gebruikerId);
    }

    // 7
    @Test
    @DisplayName("deleteByGebruikerId: OK → deleteByGebruikerId aangeroepen")
    void deleteByGebruikerId_ok() {
        Long gebruikerId = 15L;
        when(repo.existsByGebruikerId(gebruikerId)).thenReturn(true);

        service.deleteByGebruikerId(gebruikerId);

        verify(repo).existsByGebruikerId(gebruikerId);
        verify(repo).deleteByGebruikerId(gebruikerId);
        verifyNoMoreInteractions(repo, gebruikerRepo);
    }

    // 8
    @Test
    @DisplayName("deleteByGebruikerId: niet gevonden → 404")
    void deleteByGebruikerId_notFound_404() {
        Long gebruikerId = 16L;
        when(repo.existsByGebruikerId(gebruikerId)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteByGebruikerId(gebruikerId))
                .isInstanceOf(ResponseStatusException.class);

        verify(repo).existsByGebruikerId(gebruikerId);
        verifyNoMoreInteractions(repo, gebruikerRepo);
    }
}