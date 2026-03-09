package com.warzone.risk;

import com.warzone.conflict.Conflict;
import com.warzone.conflict.ConflictRepository;
import com.warzone.nuclear.NuclearArsenal;
import com.warzone.nuclear.NuclearRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RiskEngine {

    private static final Logger log = LoggerFactory.getLogger(RiskEngine.class);

    // nuclear-armed country codes
    private static final Set<String> NUCLEAR_COUNTRIES = Set.of(
        "US", "RU", "CN", "FR", "GB", "PK", "IN", "IL", "KP"
    );

    // pairs that would be catastrophic if they go to war
    private static final List<String[]> FLASHPOINT_PAIRS = List.of(
        new String[]{"US", "RU"},
        new String[]{"US", "CN"},
        new String[]{"RU", "UA"},   // ongoing, proxy for NATO-RU
        new String[]{"IN", "PK"},
        new String[]{"CN", "TW"},
        new String[]{"IL", "IR"},
        new String[]{"KP", "KR"}
    );

    private final ConflictRepository conflictRepo;
    private final NuclearRepository nuclearRepo;

    public RiskEngine(ConflictRepository conflictRepo, NuclearRepository nuclearRepo) {
        this.conflictRepo = conflictRepo;
        this.nuclearRepo = nuclearRepo;
    }

    public RiskScore calculate() {
        List<Conflict> allConflicts = conflictRepo.findAllByOrderBySeverityDesc();

        long criticalCount = allConflicts.stream().filter(c -> "CRITICAL".equals(c.getSeverity())).count();
        long highCount = allConflicts.stream().filter(c -> "HIGH".equals(c.getSeverity())).count();
        long mediumCount = allConflicts.stream().filter(c -> "MEDIUM".equals(c.getSeverity())).count();
        long activeWars = allConflicts.stream().filter(c -> "ACTIVE_WAR".equals(c.getStatus())).count();
        long tensions = allConflicts.stream().filter(c -> "TENSION".equals(c.getStatus())).count();

        // which nuclear nations are directly involved in conflicts?
        Set<String> involvedNuclearNations = new HashSet<>();
        for (Conflict c : allConflicts) {
            if ("ACTIVE_WAR".equals(c.getStatus()) || "TENSION".equals(c.getStatus())) {
                if (NUCLEAR_COUNTRIES.contains(c.getCountryA())) involvedNuclearNations.add(c.getCountryA());
                if (c.getCountryB() != null && NUCLEAR_COUNTRIES.contains(c.getCountryB())) involvedNuclearNations.add(c.getCountryB());
            }
        }

        // check how many flashpoint pairs are active
        int activeFlashpoints = 0;
        for (String[] pair : FLASHPOINT_PAIRS) {
            boolean found = allConflicts.stream().anyMatch(c ->
                (pair[0].equals(c.getCountryA()) && pair[1].equals(c.getCountryB())) ||
                (pair[1].equals(c.getCountryA()) && pair[0].equals(c.getCountryB()))
            );
            if (found) activeFlashpoints++;
        }

        // conflict component (0-100) - weighted by severity
        double conflictRaw = (criticalCount * 12 + highCount * 6 + mediumCount * 2 + activeWars * 4 + tensions * 1);
        double conflictScore = Math.min(conflictRaw / 60.0 * 100, 100);

        // nuclear component (0-100)
        int deployedTotal = nuclearRepo.sumDeployed();
        double nuclearBase = involvedNuclearNations.size() * 15.0;
        if (deployedTotal > 1000) nuclearBase += 10;
        if (involvedNuclearNations.size() >= 2) nuclearBase += 15;  // two nuclear states in conflict
        if (activeFlashpoints > 0) nuclearBase += activeFlashpoints * 8;
        double nuclearScore = Math.min(nuclearBase, 100);

        // troop mobilization component (proxy: use active wars with high casualty counts)
        long highCasualtyConflicts = allConflicts.stream()
                .filter(c -> c.getCasualties() != null && c.getCasualties() > 10000).count();
        double troopScore = Math.min((activeWars * 6 + highCasualtyConflicts * 10), 100);

        // diplomatic tension component
        double diplo = (activeFlashpoints * 12 + tensions * 3 + criticalCount * 8);
        double diplomaticScore = Math.min(diplo, 100);

        // overall weighted
        double overall = conflictScore * 0.35
                        + nuclearScore * 0.30
                        + troopScore * 0.15
                        + diplomaticScore * 0.20;
        overall = Math.min(overall, 100);

        // determine level
        String level;
        if (overall >= 75) level = "CRITICAL";
        else if (overall >= 55) level = "HIGH";
        else if (overall >= 35) level = "ELEVATED";
        else if (overall >= 18) level = "GUARDED";
        else level = "LOW";

        // determine top threat
        String topThreat = determineTopThreat(allConflicts, involvedNuclearNations, activeFlashpoints);

        RiskScore score = new RiskScore();
        score.setOverallScore(toBd(overall));
        score.setConflictScore(toBd(conflictScore));
        score.setNuclearScore(toBd(nuclearScore));
        score.setTroopScore(toBd(troopScore));
        score.setDiplomaticScore(toBd(diplomaticScore));
        score.setLevel(level);
        score.setTopThreat(topThreat);
        score.setActiveConflicts((int) activeWars);
        score.setNuclearNationsInvolved(involvedNuclearNations.size());
        score.setCalculatedAt(LocalDateTime.now());

        log.info("Risk calculated: {} ({}) | Nuclear nations active: {}, Flashpoints: {}",
                score.getOverallScore(), level, involvedNuclearNations.size(), activeFlashpoints);

        return score;
    }

    private String determineTopThreat(List<Conflict> conflicts, Set<String> nuclearInvolved, int flashpoints) {
        if (nuclearInvolved.size() >= 3) {
            return "Multiple nuclear-armed nations involved in active conflicts";
        }
        if (flashpoints >= 3) {
            return "Several major geopolitical flashpoints simultaneously active";
        }
        if (nuclearInvolved.contains("US") && nuclearInvolved.contains("RU")) {
            return "US-Russia direct or proxy conflict escalation risk";
        }
        if (nuclearInvolved.contains("US") && nuclearInvolved.contains("CN")) {
            return "US-China tensions with military dimensions";
        }
        if (nuclearInvolved.contains("IN") && nuclearInvolved.contains("PK")) {
            return "India-Pakistan nuclear flashpoint active";
        }

        // fall back to the most severe conflict
        Optional<Conflict> worst = conflicts.stream()
                .filter(c -> "CRITICAL".equals(c.getSeverity()))
                .findFirst();
        if (worst.isPresent()) {
            return worst.get().getTitle() + " - critical severity";
        }

        long activeWars = conflicts.stream().filter(c -> "ACTIVE_WAR".equals(c.getStatus())).count();
        if (activeWars > 5) {
            return activeWars + " simultaneous active conflicts worldwide";
        }

        return "Elevated regional tensions across multiple zones";
    }

    private BigDecimal toBd(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
