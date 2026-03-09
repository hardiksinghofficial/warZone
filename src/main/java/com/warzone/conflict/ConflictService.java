package com.warzone.conflict;

import com.warzone.alert.AlertService;
import com.warzone.timeline.TimelineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConflictService {

    private static final Logger log = LoggerFactory.getLogger(ConflictService.class);

    private final ConflictRepository repo;
    private final GdeltClient gdelt;
    private final AcledClient acled;
    private final AlertService alerts;
    private final TimelineService timeline;

    public ConflictService(ConflictRepository repo, GdeltClient gdelt, AcledClient acled,
                           AlertService alerts, TimelineService timeline) {
        this.repo = repo;
        this.gdelt = gdelt;
        this.acled = acled;
        this.alerts = alerts;
        this.timeline = timeline;
    }

    @Cacheable("conflicts")
    public List<Conflict> getAll() {
        return repo.findAllByOrderBySeverityDesc();
    }

    public Optional<Conflict> getById(UUID id) {
        return repo.findById(id);
    }

    public List<Conflict> getActive() {
        return repo.findByStatusOrderBySeverityDesc("ACTIVE_WAR");
    }

    public List<Conflict> getByCountry(String code) {
        return repo.findByCountry(code.toUpperCase());
    }

    public List<Conflict> getByRegion(String region) {
        return repo.findByRegionIgnoreCaseOrderBySeverityDesc(region);
    }

    public List<String> getRegions() {
        return repo.findDistinctRegions();
    }

    public long countActiveWars() {
        return repo.countActiveWars();
    }

    public long countTensions() {
        return repo.countTensions();
    }

    @Scheduled(fixedDelayString = "${app.conflict.poll-interval:900000}", initialDelay = 30000)
    @CacheEvict(value = {"conflicts", "dashboard"}, allEntries = true)
    public void pollExternalSources() {
        log.info("Polling external conflict sources...");

        int totalSaved = 0;

        // try ACLED first (better quality data)
        if (acled.isConfigured()) {
            try {
                List<Conflict> acledEvents = acled.fetchRecentEvents();
                totalSaved += saveNewEvents(acledEvents);
            } catch (Exception e) {
                log.warn("ACLED polling failed: {}", e.getMessage());
            }
        }

        // always try GDELT as supplementary source
        try {
            List<Conflict> gdeltEvents = gdelt.fetchLatest();
            totalSaved += saveNewEvents(gdeltEvents);
        } catch (Exception e) {
            log.warn("GDELT polling failed: {}", e.getMessage());
        }

        if (totalSaved > 0) {
            log.info("Saved {} new events total from external sources", totalSaved);
        }
    }

    private int saveNewEvents(List<Conflict> events) {
        int count = 0;
        for (Conflict event : events) {
            try {
                if (event.getExternalId() != null && !repo.existsByExternalId(event.getExternalId())) {
                    Conflict saved = repo.save(event);
                    alerts.broadcastConflict(saved);

                    if ("CRITICAL".equals(event.getSeverity()) || "HIGH".equals(event.getSeverity())) {
                        timeline.recordConflictEvent(saved);
                    }

                    count++;
                }
            } catch (Exception e) {
                // duplicate or constraint issue, just skip it
                log.debug("Skipping event: {}", e.getMessage());
            }
        }
        return count;
    }
}
