package com.warzone.dashboard;

import com.warzone.base.BaseService;
import com.warzone.common.ApiResponse;
import com.warzone.conflict.ConflictService;
import com.warzone.military.MilitaryService;
import com.warzone.news.NewsService;
import com.warzone.nuclear.NuclearService;
import com.warzone.risk.RiskService;
import com.warzone.timeline.TimelineService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ConflictService conflictService;
    private final MilitaryService militaryService;
    private final NuclearService nuclearService;
    private final BaseService baseService;
    private final RiskService riskService;
    private final NewsService newsService;
    private final TimelineService timelineService;

    public DashboardController(ConflictService conflictService, MilitaryService militaryService,
                                NuclearService nuclearService, BaseService baseService,
                                RiskService riskService, NewsService newsService,
                                TimelineService timelineService) {
        this.conflictService = conflictService;
        this.militaryService = militaryService;
        this.nuclearService = nuclearService;
        this.baseService = baseService;
        this.riskService = riskService;
        this.newsService = newsService;
        this.timelineService = timelineService;
    }

    @GetMapping
    @Cacheable("dashboard")
    public ResponseEntity<?> getDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        // risk assessment at the top
        dashboard.put("riskAssessment", riskService.getCurrent().orElse(null));

        // active conflicts
        dashboard.put("activeConflicts", conflictService.getActive());

        // top 10 military powers
        dashboard.put("topMilitary", militaryService.getTopN(10));

        // nuclear powers
        dashboard.put("nuclearArsenals", nuclearService.getAll());

        // military bases
        dashboard.put("militaryBases", baseService.getAll());

        // latest news
        dashboard.put("latestNews", newsService.getLatest(10));

        // recent timeline events
        dashboard.put("recentTimeline", timelineService.getRecent(15));

        // aggregate stats
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalConflicts", conflictService.getAll().size());
        stats.put("activeWars", conflictService.countActiveWars());
        stats.put("tensions", conflictService.countTensions());
        stats.put("nuclearStats", nuclearService.getGlobalStats());
        stats.put("baseStats", baseService.getStats());
        stats.put("militaryStats", militaryService.getGlobalStats());
        stats.put("activeRegions", conflictService.getRegions());
        dashboard.put("globalStats", stats);

        dashboard.put("generatedAt", LocalDateTime.now().toString());

        return ResponseEntity.ok(ApiResponse.ok(dashboard));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("riskLevel", riskService.getCurrent().map(r -> r.getLevel()).orElse("UNKNOWN"));
        summary.put("riskScore", riskService.getCurrent().map(r -> r.getOverallScore()).orElse(null));
        summary.put("activeWars", conflictService.countActiveWars());
        summary.put("tensions", conflictService.countTensions());
        summary.put("nuclearNations", nuclearService.getAll().size());
        summary.put("totalWarheads", nuclearService.getGlobalStats().get("totalWarheads"));
        summary.put("militaryBases", baseService.getAll().size());
        summary.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }
}
