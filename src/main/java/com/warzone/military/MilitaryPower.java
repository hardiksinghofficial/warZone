package com.warzone.military;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "military_power")
public class MilitaryPower implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "country_code", unique = true, nullable = false, length = 3)
    private String countryCode;

    @Column(name = "country_name", nullable = false, length = 100)
    private String countryName;

    @Column(name = "flag_emoji", length = 10)
    private String flagEmoji;

    @Column(nullable = false)
    private Integer rank;

    @Column(name = "power_index", nullable = false)
    private BigDecimal powerIndex;

    @Column(name = "active_personnel") private Integer activePersonnel = 0;
    @Column(name = "reserve_personnel") private Integer reservePersonnel = 0;
    private Integer paramilitary = 0;
    private Integer tanks = 0;
    @Column(name = "armored_vehicles") private Integer armoredVehicles = 0;
    private Integer artillery = 0;
    @Column(name = "rocket_launchers") private Integer rocketLaunchers = 0;
    @Column(name = "total_aircraft") private Integer totalAircraft = 0;
    @Column(name = "fighter_jets") private Integer fighterJets = 0;
    @Column(name = "attack_helicopters") private Integer attackHelicopters = 0;
    @Column(name = "transport_aircraft") private Integer transportAircraft = 0;
    @Column(name = "naval_vessels") private Integer navalVessels = 0;
    @Column(name = "aircraft_carriers") private Integer aircraftCarriers = 0;
    private Integer submarines = 0;
    private Integer destroyers = 0;
    private Integer frigates = 0;
    @Column(name = "defense_budget") private BigDecimal defenseBudget = BigDecimal.ZERO;
    @Column(name = "oil_production") private Integer oilProduction = 0;
    private Integer airports = 0;
    private Integer ports = 0;
    @Column(name = "nuclear_capable") private Boolean nuclearCapable = false;
    @Column(length = 200) private String alliances;
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
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public BigDecimal getPowerIndex() { return powerIndex; }
    public void setPowerIndex(BigDecimal powerIndex) { this.powerIndex = powerIndex; }
    public Integer getActivePersonnel() { return activePersonnel; }
    public void setActivePersonnel(Integer activePersonnel) { this.activePersonnel = activePersonnel; }
    public Integer getReservePersonnel() { return reservePersonnel; }
    public void setReservePersonnel(Integer reservePersonnel) { this.reservePersonnel = reservePersonnel; }
    public Integer getParamilitary() { return paramilitary; }
    public void setParamilitary(Integer paramilitary) { this.paramilitary = paramilitary; }
    public Integer getTanks() { return tanks; }
    public void setTanks(Integer tanks) { this.tanks = tanks; }
    public Integer getArmoredVehicles() { return armoredVehicles; }
    public void setArmoredVehicles(Integer armoredVehicles) { this.armoredVehicles = armoredVehicles; }
    public Integer getArtillery() { return artillery; }
    public void setArtillery(Integer artillery) { this.artillery = artillery; }
    public Integer getRocketLaunchers() { return rocketLaunchers; }
    public void setRocketLaunchers(Integer rocketLaunchers) { this.rocketLaunchers = rocketLaunchers; }
    public Integer getTotalAircraft() { return totalAircraft; }
    public void setTotalAircraft(Integer totalAircraft) { this.totalAircraft = totalAircraft; }
    public Integer getFighterJets() { return fighterJets; }
    public void setFighterJets(Integer fighterJets) { this.fighterJets = fighterJets; }
    public Integer getAttackHelicopters() { return attackHelicopters; }
    public void setAttackHelicopters(Integer attackHelicopters) { this.attackHelicopters = attackHelicopters; }
    public Integer getTransportAircraft() { return transportAircraft; }
    public void setTransportAircraft(Integer transportAircraft) { this.transportAircraft = transportAircraft; }
    public Integer getNavalVessels() { return navalVessels; }
    public void setNavalVessels(Integer navalVessels) { this.navalVessels = navalVessels; }
    public Integer getAircraftCarriers() { return aircraftCarriers; }
    public void setAircraftCarriers(Integer aircraftCarriers) { this.aircraftCarriers = aircraftCarriers; }
    public Integer getSubmarines() { return submarines; }
    public void setSubmarines(Integer submarines) { this.submarines = submarines; }
    public Integer getDestroyers() { return destroyers; }
    public void setDestroyers(Integer destroyers) { this.destroyers = destroyers; }
    public Integer getFrigates() { return frigates; }
    public void setFrigates(Integer frigates) { this.frigates = frigates; }
    public BigDecimal getDefenseBudget() { return defenseBudget; }
    public void setDefenseBudget(BigDecimal defenseBudget) { this.defenseBudget = defenseBudget; }
    public Integer getOilProduction() { return oilProduction; }
    public void setOilProduction(Integer oilProduction) { this.oilProduction = oilProduction; }
    public Integer getAirports() { return airports; }
    public void setAirports(Integer airports) { this.airports = airports; }
    public Integer getPorts() { return ports; }
    public void setPorts(Integer ports) { this.ports = ports; }
    public Boolean getNuclearCapable() { return nuclearCapable; }
    public void setNuclearCapable(Boolean nuclearCapable) { this.nuclearCapable = nuclearCapable; }
    public String getAlliances() { return alliances; }
    public void setAlliances(String alliances) { this.alliances = alliances; }
    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }
    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // handy derived fields for comparison
    public int getTotalPersonnel() {
        return (activePersonnel != null ? activePersonnel : 0)
             + (reservePersonnel != null ? reservePersonnel : 0)
             + (paramilitary != null ? paramilitary : 0);
    }

    public int getTotalLandAssets() {
        return (tanks != null ? tanks : 0)
             + (armoredVehicles != null ? armoredVehicles : 0)
             + (artillery != null ? artillery : 0);
    }
}
