package com.warzone.base;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BaseService {

    private final BaseRepository repo;

    public BaseService(BaseRepository repo) {
        this.repo = repo;
    }

    @Cacheable("bases")
    public List<MilitaryBase> getAll() {
        return repo.findAll();
    }

    public List<MilitaryBase> getByCountry(String code) {
        return repo.findByCountryCode(code.toUpperCase());
    }

    public List<MilitaryBase> getByType(String type) {
        return repo.findByBaseType(type.toUpperCase());
    }

    public List<MilitaryBase> getOverseas() {
        return repo.findByIsOverseasTrue();
    }

    public List<MilitaryBase> getByAlliance(String alliance) {
        return repo.findByAlliance(alliance);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<MilitaryBase> all = repo.findAll();
        stats.put("totalBases", all.size());
        stats.put("overseasBases", repo.findByIsOverseasTrue().size());
        stats.put("types", repo.findDistinctTypes());
        stats.put("alliances", repo.findDistinctAlliances());
        stats.put("totalPersonnel", all.stream().mapToInt(b -> b.getPersonnel() != null ? b.getPersonnel() : 0).sum());
        return stats;
    }
}
