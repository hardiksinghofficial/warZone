package com.warzone.risk;

import com.warzone.alert.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RiskService {

    private static final Logger log = LoggerFactory.getLogger(RiskService.class);

    private final RiskRepository repo;
    private final RiskEngine engine;
    private final AlertService alerts;

    public RiskService(RiskRepository repo, RiskEngine engine, AlertService alerts) {
        this.repo = repo;
        this.engine = engine;
        this.alerts = alerts;
    }

    @Cacheable("risk")
    public Optional<RiskScore> getCurrent() {
        return repo.findTopByOrderByCalculatedAtDesc();
    }

    public List<RiskScore> getHistory(int limit) {
        return repo.findAllByOrderByCalculatedAtDesc(PageRequest.of(0, Math.min(limit, 100)));
    }

    @Scheduled(fixedDelayString = "${app.risk.calc-interval:300000}", initialDelay = 20000)
    @CacheEvict(value = {"risk", "dashboard"}, allEntries = true)
    public void recalculate() {
        try {
            RiskScore score = engine.calculate();
            repo.save(score);
            alerts.broadcastRisk(score);
        } catch (Exception e) {
            log.error("Risk calculation failed: {}", e.getMessage());
        }
    }
}
