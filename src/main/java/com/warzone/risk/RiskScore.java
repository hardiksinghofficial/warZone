package com.warzone.risk;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "risk_snapshots")
public class RiskScore implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "overall_score", nullable = false)
    private BigDecimal overallScore = BigDecimal.ZERO;

    @Column(name = "conflict_score")
    private BigDecimal conflictScore = BigDecimal.ZERO;

    @Column(name = "nuclear_score")
    private BigDecimal nuclearScore = BigDecimal.ZERO;

    @Column(name = "troop_score")
    private BigDecimal troopScore = BigDecimal.ZERO;

    @Column(name = "diplomatic_score")
    private BigDecimal diplomaticScore = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String level = "LOW";

    @Column(name = "top_threat", length = 300)
    private String topThreat;

    @Column(name = "active_conflicts")
    private Integer activeConflicts = 0;

    @Column(name = "nuclear_nations_involved")
    private Integer nuclearNationsInvolved = 0;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt = LocalDateTime.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public BigDecimal getOverallScore() { return overallScore; }
    public void setOverallScore(BigDecimal overallScore) { this.overallScore = overallScore; }
    public BigDecimal getConflictScore() { return conflictScore; }
    public void setConflictScore(BigDecimal conflictScore) { this.conflictScore = conflictScore; }
    public BigDecimal getNuclearScore() { return nuclearScore; }
    public void setNuclearScore(BigDecimal nuclearScore) { this.nuclearScore = nuclearScore; }
    public BigDecimal getTroopScore() { return troopScore; }
    public void setTroopScore(BigDecimal troopScore) { this.troopScore = troopScore; }
    public BigDecimal getDiplomaticScore() { return diplomaticScore; }
    public void setDiplomaticScore(BigDecimal diplomaticScore) { this.diplomaticScore = diplomaticScore; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getTopThreat() { return topThreat; }
    public void setTopThreat(String topThreat) { this.topThreat = topThreat; }
    public Integer getActiveConflicts() { return activeConflicts; }
    public void setActiveConflicts(Integer activeConflicts) { this.activeConflicts = activeConflicts; }
    public Integer getNuclearNationsInvolved() { return nuclearNationsInvolved; }
    public void setNuclearNationsInvolved(Integer nuclearNationsInvolved) { this.nuclearNationsInvolved = nuclearNationsInvolved; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
