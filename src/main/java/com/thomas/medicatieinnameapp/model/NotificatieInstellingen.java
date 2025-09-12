package com.thomas.medicatieinnameapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "notificatie_instellingen")
public class NotificatieInstellingen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "gebruiker_id", nullable = false, unique = true)
    @JsonIgnore
    private Gebruiker gebruiker;

    @Column(name = "stille_start")
    private LocalTime stilleStart;

    @Column(name = "stille_einde")
    private LocalTime stilleEinde;

    @Enumerated(EnumType.STRING)
    @Column(name = "kanaal", nullable = false, length = 20)
    private NotificatieKanaal kanaal = NotificatieKanaal.email;

    @Setter
    @Column(name = "minuten_vooraf")
    private Integer minutenVooraf = 30;

    @Column(name = "snooze_minuten")
    private Integer snoozeMinuten = 10;

    @Column(name = "actief")
    private Boolean actief = true;

    public Long getId() {
        return id; }
    public Gebruiker getGebruiker() {
        return gebruiker; }
    public void setGebruiker(Gebruiker gebruiker) {
        this.gebruiker = gebruiker; }
    public LocalTime getStilleStart() {
        return stilleStart; }
    public void setStilleStart(LocalTime stilleStart) {
        this.stilleStart = stilleStart; }
    public LocalTime getStilleEinde() {
        return stilleEinde; }
    public void setStilleEinde(LocalTime stilleEinde) {
        this.stilleEinde = stilleEinde; }
    public NotificatieKanaal getKanaal() {
        return kanaal; }
    public void setKanaal(NotificatieKanaal kanaal) {
        this.kanaal = kanaal; }
    public Integer getMinutenVooraf() {
        return minutenVooraf; }
    public void setMinutenVooraf(Integer minutenVooraf) {
        this.minutenVooraf = minutenVooraf; }
    public Integer getSnoozeMinuten() {
        return snoozeMinuten; }
    public void setSnoozeMinuten(Integer snoozeMinuten) {
        this.snoozeMinuten = snoozeMinuten; }
    public Boolean getActief() {
        return actief; }
    public void setActief(Boolean actief) {
        this.actief = actief; }
}
