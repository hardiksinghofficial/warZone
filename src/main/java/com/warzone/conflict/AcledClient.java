package com.warzone.conflict;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warzone.common.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class AcledClient {

    private static final Logger log = LoggerFactory.getLogger(AcledClient.class);

    private final WebClient webClient;
    private final ObjectMapper mapper;

    @Value("${app.acled.base-url}")
    private String baseUrl;

    @Value("${app.acled.api-key:}")
    private String apiKey;

    @Value("${app.acled.email:}")
    private String email;

    public AcledClient(WebClient webClient, ObjectMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank() && email != null && !email.isBlank();
    }

    public List<Conflict> fetchRecentEvents() {
        List<Conflict> results = new ArrayList<>();

        if (!isConfigured()) {
            log.info("ACLED not configured (no API key), skipping");
            return results;
        }

        try {
            String weekAgo = LocalDate.now().minusDays(7).format(DateTimeFormatter.ISO_DATE);

            String url = baseUrl
                    + "?key=" + apiKey
                    + "&email=" + email
                    + "&event_date=" + weekAgo
                    + "&event_date_where=%3E%3D"
                    + "&event_type=Battles|Explosions/Remote violence|Violence against civilians"
                    + "&limit=50"
                    + "&fields=event_id_cnty|event_date|event_type|sub_event_type|country|latitude|longitude|fatalities|notes|source";

            String body = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (body == null) return results;

            JsonNode root = mapper.readTree(body);
            JsonNode data = root.path("data");

            if (!data.isArray()) return results;

            for (JsonNode item : data) {
                Conflict c = parseAcledEvent(item);
                if (c != null) results.add(c);
            }

            log.info("ACLED returned {} conflict events", results.size());
        } catch (Exception e) {
            log.error("ACLED fetch failed: {}", e.getMessage());
        }

        return results;
    }

    private Conflict parseAcledEvent(JsonNode item) {
        try {
            String eventId = item.path("event_id_cnty").asText("");
            String notes = item.path("notes").asText("");
            String eventType = item.path("event_type").asText("");
            String subType = item.path("sub_event_type").asText("");
            String country = item.path("country").asText("Unknown");
            double lat = item.path("latitude").asDouble(0);
            double lng = item.path("longitude").asDouble(0);
            int fatalities = item.path("fatalities").asInt(0);
            String dateStr = item.path("event_date").asText("");

            if (eventId.isBlank()) return null;

            // build a readable title
            String title = subType.isBlank() ? eventType : subType;
            title = title + " in " + country;
            if (fatalities > 0) title = title + " (" + fatalities + " killed)";

            Conflict c = new Conflict();
            c.setTitle(title.length() > 295 ? title.substring(0, 295) : title);
            c.setDescription(notes.length() > 500 ? notes.substring(0, 500) : notes);
            c.setCountryA(countryToCode(country));
            c.setRegion(guessRegion(country));
            c.setLat(lat);
            c.setLng(lng);
            c.setSeverity(fatalities >= 50 ? "CRITICAL" : fatalities >= 10 ? "HIGH" : fatalities >= 1 ? "MEDIUM" : "LOW");
            c.setStatus("ACTIVE_WAR");
            c.setConflictType(eventType);
            c.setCasualties(fatalities);
            c.setSource("ACLED");
            c.setExternalId("acled-" + eventId);
            c.setFetchedAt(LocalDateTime.now());

            if (!dateStr.isBlank()) {
                try {
                    c.setStartedAt(LocalDate.parse(dateStr));
                } catch (Exception ignored) {}
            }

            return c;
        } catch (Exception e) {
            return null;
        }
    }

    private String countryToCode(String name) {
        // basic mapping for common conflict countries
        return switch (name.toLowerCase().trim()) {
            case "ukraine" -> "UA";
            case "russia" -> "RU";
            case "israel" -> "IL";
            case "palestine" -> "PS";
            case "sudan" -> "SD";
            case "syria" -> "SY";
            case "iraq" -> "IQ";
            case "yemen" -> "YE";
            case "somalia" -> "SO";
            case "myanmar" -> "MM";
            case "ethiopia" -> "ET";
            case "democratic republic of congo" -> "CD";
            case "nigeria" -> "NG";
            case "mali" -> "ML";
            case "burkina faso" -> "BF";
            case "mozambique" -> "MZ";
            case "pakistan" -> "PK";
            case "india" -> "IN";
            case "afghanistan" -> "AF";
            case "cameroon" -> "CM";
            case "libya" -> "LY";
            case "iran" -> "IR";
            case "lebanon" -> "LB";
            case "mexico" -> "MX";
            case "colombia" -> "CO";
            case "haiti" -> "HT";
            default -> "XX";
        };
    }

    private String guessRegion(String country) {
        String lower = country.toLowerCase();
        if (lower.contains("ukraine") || lower.contains("russia")) return "Eastern Europe";
        if (lower.contains("israel") || lower.contains("palestine") || lower.contains("syria")
                || lower.contains("iraq") || lower.contains("yemen") || lower.contains("iran")
                || lower.contains("lebanon")) return "Middle East";
        if (lower.contains("sudan") || lower.contains("somalia") || lower.contains("ethiopia")
                || lower.contains("congo") || lower.contains("nigeria") || lower.contains("mali")
                || lower.contains("burkina") || lower.contains("mozambique") || lower.contains("cameroon")
                || lower.contains("libya")) return "Africa";
        if (lower.contains("myanmar") || lower.contains("china") || lower.contains("korea")
                || lower.contains("philippine")) return "East Asia";
        if (lower.contains("india") || lower.contains("pakistan") || lower.contains("afghan")) return "South Asia";
        return "Other";
    }
}
