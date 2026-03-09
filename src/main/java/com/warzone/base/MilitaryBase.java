package com.warzone.base;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "military_bases")
public class MilitaryBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;

    @Column(name = "host_country", length = 3)
    private String hostCountry;

    @Column(nullable = false) private Double lat;
    @Column(nullable = false) private Double lng;

    @Column(name = "base_type", nullable = false, length = 30)
    private String baseType;

    private Integer personnel = 0;

    @Column(name = "is_overseas")
    private Boolean isOverseas = false;

    @Column(length = 50)
    private String alliance;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getHostCountry() { return hostCountry; }
    public void setHostCountry(String hostCountry) { this.hostCountry = hostCountry; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public String getBaseType() { return baseType; }
    public void setBaseType(String baseType) { this.baseType = baseType; }
    public Integer getPersonnel() { return personnel; }
    public void setPersonnel(Integer personnel) { this.personnel = personnel; }
    public Boolean getIsOverseas() { return isOverseas; }
    public void setIsOverseas(Boolean overseas) { isOverseas = overseas; }
    public String getAlliance() { return alliance; }
    public void setAlliance(String alliance) { this.alliance = alliance; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
