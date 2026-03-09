package com.warzone.nuclear;

import com.warzone.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nuclear")
public class NuclearController {

    private final NuclearService service;

    public NuclearController(NuclearService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<NuclearArsenal> data = service.getAll();
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getByCountry(@PathVariable String code) {
        return service.getByCountry(code)
                .map(n -> ResponseEntity.ok(ApiResponse.ok(n)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(service.getGlobalStats()));
    }

    @GetMapping("/compare")
    public ResponseEntity<?> compare(@RequestParam String a, @RequestParam String b) {
        return ResponseEntity.ok(ApiResponse.ok(service.compareArsenals(a, b)));
    }
}
