package com.thomas.medicatieinnameapp.dto;

import com.thomas.medicatieinnameapp.model.NotificatieKanaal;
import jakarta.validation.constraints.*;

import java.time.LocalTime;

public class NotificatieInstellingenRequest {

    private LocalTime stilleStart;
    private LocalTime stilleEinde;

    @NotNull
    private NotificatieKanaal kanaal;

    @NotNull @Min(0) @Max(1440)
    private Integer minutenVooraf;

    @NotNull @Min(0) @Max(1440)
    private Integer snoozeMinuten;

    @NotNull
    private Boolean actief;

    public LocalTime getStilleStart() {
        return stilleStart; }
    public void setStilleStart(LocalTime stilleStart) { this.stilleStart = stilleStart; }
    public LocalTime getStilleEinde() {
        return stilleEinde; }
    public void setStilleEinde(LocalTime stilleEinde) { this.stilleEinde = stilleEinde; }
    public NotificatieKanaal getKanaal() {
        return kanaal; }
    public void setKanaal(NotificatieKanaal kanaal) { this.kanaal = kanaal; }
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
