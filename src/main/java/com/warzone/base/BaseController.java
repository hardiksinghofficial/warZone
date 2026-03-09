package com.warzone.base;

import com.warzone.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bases")
public class BaseController {

    private final BaseService service;

    public BaseController(BaseService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<MilitaryBase> data = service.getAll();
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/country/{code}")
    public ResponseEntity<?> getByCountry(@PathVariable String code) {
        List<MilitaryBase> data = service.getByCountry(code);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getByType(@PathVariable String type) {
        List<MilitaryBase> data = service.getByType(type);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/overseas")
    public ResponseEntity<?> getOverseas() {
        List<MilitaryBase> data = service.getOverseas();
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/alliance/{alliance}")
    public ResponseEntity<?> getByAlliance(@PathVariable String alliance) {
        List<MilitaryBase> data = service.getByAlliance(alliance);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(ApiResponse.ok(service.getStats()));
    }
}
