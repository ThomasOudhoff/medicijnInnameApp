package com.thomas.medicatieinnameapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BijsluiterUrlRequest {
    @NotBlank
    @Size(max = 255)
    private String url;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
