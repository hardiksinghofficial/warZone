package com.warzone.military;

import com.warzone.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/military")
public class MilitaryController {

    private final MilitaryService service;

    public MilitaryController(MilitaryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<MilitaryPower> data = service.getAll();
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getByCountry(@PathVariable String code) {
        return service.getByCountry(code)
                .map(m -> ResponseEntity.ok(ApiResponse.ok(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/top/{n}")
    public ResponseEntity<?> getTop(@PathVariable int n) {
        if (n < 1 || n > 50) n = 10;
        List<MilitaryPower> data = service.getTopN(n);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/nuclear-powers")
    public ResponseEntity<?> getNuclearPowers() {
        List<MilitaryPower> data = service.getNuclearPowers();
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/compare")
    public ResponseEntity<?> compare(@RequestParam String a, @RequestParam String b) {
        return ResponseEntity.ok(ApiResponse.ok(service.compare(a, b)));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(service.getGlobalStats()));
    }
}
