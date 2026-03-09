package com.warzone.nuclear;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NuclearService {

    private final NuclearRepository repo;

    public NuclearService(NuclearRepository repo) {
        this.repo = repo;
    }

    @Cacheable("nuclear")
    public List<NuclearArsenal> getAll() {
        return repo.findAllByOrderByTotalWarheadsDesc();
    }

    public Optional<NuclearArsenal> getByCountry(String code) {
        return repo.findByCountryCode(code.toUpperCase());
    }

    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalWarheads", repo.sumTotalWarheads());
        stats.put("deployedWarheads", repo.sumDeployed());
        stats.put("totalIcbms", repo.sumIcbms());
        stats.put("nuclearNations", repo.count());

        // breakdown by policy
        List<NuclearArsenal> all = repo.findAllByOrderByTotalWarheadsDesc();
        long firstStrike = all.stream().filter(n -> "FIRST_STRIKE".equals(n.getPolicy())).count();
        long noFirstUse = all.stream().filter(n -> "NO_FIRST_USE".equals(n.getPolicy())).count();
        stats.put("firstStrikeNations", firstStrike);
        stats.put("noFirstUseNations", noFirstUse);

        return stats;
    }

    public Map<String, Object> compareArsenals(String codeA, String codeB) {
        Map<String, Object> result = new LinkedHashMap<>();

        Optional<NuclearArsenal> optA = repo.findByCountryCode(codeA.toUpperCase());
        Optional<NuclearArsenal> optB = repo.findByCountryCode(codeB.toUpperCase());

        if (optA.isEmpty() || optB.isEmpty()) {
            result.put("error", "One or both nations not found in nuclear database");
            return result;
        }

        NuclearArsenal a = optA.get();
        NuclearArsenal b = optB.get();

        result.put("countryA", a);
        result.put("countryB", b);
        result.put("warheadRatio", String.format("%.1f:1",
                (double) a.getTotalWarheads() / Math.max(b.getTotalWarheads(), 1)));
        result.put("combinedWarheads", a.getTotalWarheads() + b.getTotalWarheads());

        return result;
    }
}
