package com.warzone.alert;

import com.warzone.conflict.Conflict;
import com.warzone.risk.RiskScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);
    private final SimpMessagingTemplate messaging;

    public AlertService(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    public void broadcastConflict(Conflict conflict) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("type", "NEW_CONFLICT");
            payload.put("title", conflict.getTitle());
            payload.put("severity", conflict.getSeverity());
            payload.put("source", conflict.getSource());
            payload.put("lat", conflict.getLat());
            payload.put("lng", conflict.getLng());
            payload.put("time", LocalDateTime.now().toString());

            messaging.convertAndSend("/topic/conflicts", payload);
        } catch (Exception e) {
            log.debug("Failed to broadcast conflict: {}", e.getMessage());
        }
    }

    public void broadcastRisk(RiskScore score) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("type", "RISK_UPDATE");
            payload.put("overallScore", score.getOverallScore());
            payload.put("level", score.getLevel());
            payload.put("topThreat", score.getTopThreat());
            payload.put("time", LocalDateTime.now().toString());

            messaging.convertAndSend("/topic/risk", payload);
        } catch (Exception e) {
            log.debug("Failed to broadcast risk: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 30000)
    public void heartbeat() {
        try {
            Map<String, Object> beat = new LinkedHashMap<>();
            beat.put("type", "HEARTBEAT");
            beat.put("status", "LIVE");
            beat.put("time", LocalDateTime.now().toString());
            messaging.convertAndSend("/topic/heartbeat", beat);
        } catch (Exception ignored) {
            // websocket not connected, that's fine
        }
    }
}
