package com.warzone.conflict;

import com.warzone.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/conflicts")
public class ConflictController {

    private final ConflictService service;

    public ConflictController(ConflictService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<Conflict> data = service.getAll();
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActive() {
        List<Conflict> data = service.getActive();
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return service.getById(id)
                .map(c -> ResponseEntity.ok(ApiResponse.ok(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/country/{code}")
    public ResponseEntity<?> getByCountry(@PathVariable String code) {
        List<Conflict> data = service.getByCountry(code);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<?> getByRegion(@PathVariable String region) {
        List<Conflict> data = service.getByRegion(region);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/regions")
    public ResponseEntity<?> getRegions() {
        return ResponseEntity.ok(ApiResponse.ok(service.getRegions()));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        var stats = new java.util.LinkedHashMap<String, Object>();
        stats.put("totalConflicts", service.getAll().size());
        stats.put("activeWars", service.countActiveWars());
        stats.put("tensions", service.countTensions());
        stats.put("regions", service.getRegions());
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
