package com.warzone.nuclear;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "nuclear_arsenal")
public class NuclearArsenal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "country_code", unique = true, nullable = false, length = 3)
    private String countryCode;

    @Column(name = "country_name", nullable = false, length = 100)
    private String countryName;

    @Column(name = "flag_emoji", length = 10)
    private String flagEmoji;

    @Column(name = "total_warheads") private Integer totalWarheads = 0;
    private Integer deployed = 0;
    private Integer reserve = 0;
    private Integer retired = 0;
    @Column(name = "icbm_count") private Integer icbmCount = 0;
    @Column(name = "slbm_count") private Integer slbmCount = 0;
    @Column(name = "strategic_bombers") private Integer strategicBombers = 0;
    @Column(name = "max_range_km") private Integer maxRangeKm = 0;
    @Column(name = "first_test_year") private Integer firstTestYear;
    @Column(name = "last_test_year") private Integer lastTestYear;
    @Column(length = 50) private String policy;
    @Column(length = 30) private String status;
    private Double lat;
    private Double lng;
    @Column(name = "updated_at") private LocalDateTime updatedAt = LocalDateTime.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public String getFlagEmoji() { return flagEmoji; }
    public void setFlagEmoji(String flagEmoji) { this.flagEmoji = flagEmoji; }
    public Integer getTotalWarheads() { return totalWarheads; }
    public void setTotalWarheads(Integer totalWarheads) { this.totalWarheads = totalWarheads; }
    public Integer getDeployed() { return deployed; }
    public void setDeployed(Integer deployed) { this.deployed = deployed; }
    public Integer getReserve() { return reserve; }
    public void setReserve(Integer reserve) { this.reserve = reserve; }
    public Integer getRetired() { return retired; }
    public void setRetired(Integer retired) { this.retired = retired; }
    public Integer getIcbmCount() { return icbmCount; }
    public void setIcbmCount(Integer icbmCount) { this.icbmCount = icbmCount; }
    public Integer getSlbmCount() { return slbmCount; }
    public void setSlbmCount(Integer slbmCount) { this.slbmCount = slbmCount; }
    public Integer getStrategicBombers() { return strategicBombers; }
    public void setStrategicBombers(Integer strategicBombers) { this.strategicBombers = strategicBombers; }
    public Integer getMaxRangeKm() { return maxRangeKm; }
    public void setMaxRangeKm(Integer maxRangeKm) { this.maxRangeKm = maxRangeKm; }
    public Integer getFirstTestYear() { return firstTestYear; }
    public void setFirstTestYear(Integer firstTestYear) { this.firstTestYear = firstTestYear; }
    public Integer getLastTestYear() { return lastTestYear; }
    public void setLastTestYear(Integer lastTestYear) { this.lastTestYear = lastTestYear; }
    public String getPolicy() { return policy; }
    public void setPolicy(String policy) { this.policy = policy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getActiveWarheads() {
        return (deployed != null ? deployed : 0) + (reserve != null ? reserve : 0);
    }

    public int getDeliveryVehicles() {
        return (icbmCount != null ? icbmCount : 0) +
               (slbmCount != null ? slbmCount : 0) +
               (strategicBombers != null ? strategicBombers : 0);
    }
}
