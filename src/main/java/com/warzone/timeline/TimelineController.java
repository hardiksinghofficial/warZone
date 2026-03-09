package com.warzone.timeline;

import com.warzone.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
public class TimelineController {

    private final TimelineService service;

    public TimelineController(TimelineService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getRecent(@RequestParam(defaultValue = "50") int limit) {
        List<TimelineEvent> data = service.getRecent(limit);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getByType(@PathVariable String type,
                                        @RequestParam(defaultValue = "30") int limit) {
        List<TimelineEvent> data = service.getByType(type, limit);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/country/{code}")
    public ResponseEntity<?> getByCountry(@PathVariable String code,
                                           @RequestParam(defaultValue = "30") int limit) {
        List<TimelineEvent> data = service.getByCountry(code, limit);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }
}
