package com.thomas.medicatieinnameapp.dto;

import com.thomas.medicatieinnameapp.model.NotificatieKanaal;

import java.time.LocalTime;

public class NotificatieInstellingenResponse {
    private Long id;
    private Long gebruikerId;
    private LocalTime stilleStart;
    private LocalTime stilleEinde;
    private NotificatieKanaal kanaal;
    private Integer minutenVooraf;
    private Integer snoozeMinuten;
    private Boolean actief;

    public Long getId() {
        return id; }
    public void setId(Long id) {
        this.id = id; }
    public Long getGebruikerId() {
        return gebruikerId; }
    public void setGebruikerId(Long gebruikerId) {
        this.gebruikerId = gebruikerId; }
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
