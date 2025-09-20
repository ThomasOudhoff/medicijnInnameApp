package com.thomas.medicatieinnameapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medicatie_bijsluiter")
public class MedicatieBijsluiter {

    @Id
    @Column(name = "medicatie_id")
    private Long medicatieId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "medicatie_id")
    private Medicatie medicatie;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "data", nullable = false)
    private byte[] data;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "filename", length = 255)
    private String filename;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    public MedicatieBijsluiter() {}

    public Long getMedicatieId() {
        return medicatieId;
    }

    public void setMedicatieId(Long medicatieId) {
        this.medicatieId = medicatieId;
    }

    public Medicatie getMedicatie() {
        return medicatie;
    }

    public void setMedicatie(Medicatie medicatie) {
        this.medicatie = medicatie;
        if (medicatie != null && medicatie.getBijsluiter() != this) {
            medicatie.setBijsluiter(this);
        }
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }
    @Transient
    public String getUrl() {
        return medicatie != null ? medicatie.getBijsluiterUrl() : null;
    }

    public void setUrl(String url) {
        if (medicatie == null) throw new IllegalStateException("Medicatie is null");
        medicatie.setBijsluiterUrl(url);
    }
}
