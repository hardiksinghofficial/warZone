package com.warzone.military;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MilitaryService {

    private final MilitaryRepository repo;

    public MilitaryService(MilitaryRepository repo) {
        this.repo = repo;
    }

    @Cacheable("military")
    public List<MilitaryPower> getAll() {
        return repo.findAllByOrderByRankAsc();
    }

    public Optional<MilitaryPower> getByCountry(String code) {
        return repo.findByCountryCode(code.toUpperCase());
    }

    public List<MilitaryPower> getTopN(int n) {
        List<MilitaryPower> all = repo.findAllByOrderByRankAsc();
        return all.subList(0, Math.min(n, all.size()));
    }

    public List<MilitaryPower> getNuclearPowers() {
        return repo.findByNuclearCapableTrue();
    }

    public Map<String, Object> compare(String codeA, String codeB) {
        Map<String, Object> result = new LinkedHashMap<>();

        Optional<MilitaryPower> optA = repo.findByCountryCode(codeA.toUpperCase());
        Optional<MilitaryPower> optB = repo.findByCountryCode(codeB.toUpperCase());

        if (optA.isEmpty() || optB.isEmpty()) {
            result.put("error", "One or both countries not found");
            return result;
        }

        MilitaryPower a = optA.get();
        MilitaryPower b = optB.get();

        result.put("countryA", a);
        result.put("countryB", b);

        // build a simple advantage breakdown
        Map<String, String> advantages = new LinkedHashMap<>();
        advantages.put("personnel", a.getTotalPersonnel() > b.getTotalPersonnel() ? a.getCountryCode() : b.getCountryCode());
        advantages.put("tanks", safe(a.getTanks()) > safe(b.getTanks()) ? a.getCountryCode() : b.getCountryCode());
        advantages.put("aircraft", safe(a.getTotalAircraft()) > safe(b.getTotalAircraft()) ? a.getCountryCode() : b.getCountryCode());
        advantages.put("navalVessels", safe(a.getNavalVessels()) > safe(b.getNavalVessels()) ? a.getCountryCode() : b.getCountryCode());
        advantages.put("budget", a.getDefenseBudget().compareTo(b.getDefenseBudget()) > 0 ? a.getCountryCode() : b.getCountryCode());
        advantages.put("nuclear", Boolean.TRUE.equals(a.getNuclearCapable()) && !Boolean.TRUE.equals(b.getNuclearCapable()) ? a.getCountryCode()
                : Boolean.TRUE.equals(b.getNuclearCapable()) && !Boolean.TRUE.equals(a.getNuclearCapable()) ? b.getCountryCode() : "BOTH/NEITHER");

        result.put("advantages", advantages);

        // count who leads in more categories
        long aWins = advantages.values().stream().filter(v -> v.equals(a.getCountryCode())).count();
        long bWins = advantages.values().stream().filter(v -> v.equals(b.getCountryCode())).count();
        result.put("overallEdge", aWins > bWins ? a.getCountryName() : bWins > aWins ? b.getCountryName() : "Roughly Even");

        return result;
    }

    public Map<String, Object> getGlobalStats() {
        List<MilitaryPower> all = repo.findAllByOrderByRankAsc();
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalCountries", all.size());
        stats.put("totalActivePersonnel", repo.totalActivePersonnel());
        stats.put("nuclearPowers", repo.findByNuclearCapableTrue().size());
        stats.put("totalDefenseBudget",
                all.stream().map(MilitaryPower::getDefenseBudget).reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        stats.put("topByPersonnel",
                all.stream().max(Comparator.comparingInt(MilitaryPower::getTotalPersonnel))
                        .map(MilitaryPower::getCountryName).orElse("N/A"));
        stats.put("topByBudget",
                all.stream().max(Comparator.comparing(MilitaryPower::getDefenseBudget))
                        .map(MilitaryPower::getCountryName).orElse("N/A"));
        return stats;
    }

    private int safe(Integer val) {
        return val != null ? val : 0;
    }
}
