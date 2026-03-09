package com.warzone.risk;

import com.warzone.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk")
public class RiskController {

    private final RiskService service;

    public RiskController(RiskService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getCurrent() {
        return service.getCurrent()
                .map(r -> ResponseEntity.ok(ApiResponse.ok(r)))
                .orElse(ResponseEntity.ok().build());
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestParam(defaultValue = "30") int limit) {
        List<RiskScore> data = service.getHistory(limit);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }
}
