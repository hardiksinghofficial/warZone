package com.warzone.conflict;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warzone.common.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class GdeltClient {

    private static final Logger log = LoggerFactory.getLogger(GdeltClient.class);

    private final WebClient webClient;
    private final ObjectMapper mapper;

    @Value("${app.gdelt.base-url}")
    private String baseUrl;

    public GdeltClient(WebClient webClient, ObjectMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }

    public List<Conflict> fetchLatest() {
        List<Conflict> results = new ArrayList<>();

        String[] queries = {
            "war conflict military attack",
            "bombing missile airstrike killed",
            "nuclear threat troops deployed"
        };

        for (String query : queries) {
            try {
                List<Conflict> batch = fetchQuery(query);
                results.addAll(batch);
                if (results.size() >= 30) break;
            } catch (Exception e) {
                log.warn("GDELT query failed for '{}': {}", query, e.getMessage());
            }
        }

        log.info("GDELT returned {} total results", results.size());
        return results;
    }

    private List<Conflict> fetchQuery(String query) {
        List<Conflict> events = new ArrayList<>();

        try {
            String encodedQuery = query.replace(" ", "%20");
            String url = baseUrl + "?query=" + encodedQuery
                    + "&mode=artlist&maxrecords=20&format=json&timespan=60min";

            String body = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (body == null || body.isBlank()) return events;

            JsonNode root = mapper.readTree(body);
            JsonNode articles = root.path("articles");

            if (!articles.isArray()) return events;

            for (JsonNode article : articles) {
                Conflict parsed = parseArticle(article);
                if (parsed != null) {
                    events.add(parsed);
                }
            }
        } catch (Exception e) {
            log.debug("GDELT parse error: {}", e.getMessage());
        }

        return events;
    }

    private Conflict parseArticle(JsonNode article) {
        try {
            String title = article.path("title").asText("").trim();
            if (title.isBlank() || title.length() < 15) return null;

            String url = article.path("url").asText("");
            String country = article.path("sourcecountry").asText("XX");
            String domain = article.path("domain").asText("");

            // try to get real coordinates, fall back to country center
            double lat = article.path("sourcelat").asDouble(0);
            double lng = article.path("sourcelon").asDouble(0);

            if (lat == 0 && lng == 0) {
                String cc = country.length() >= 2 ? country.substring(0, 2).toUpperCase() : "XX";
                double[] coords = GeoUtils.coordsFor(cc);
                // add small random offset so markers don't stack
                lat = coords[0] + (Math.random() - 0.5) * 2;
                lng = coords[1] + (Math.random() - 0.5) * 2;
            }

            String countryCode = country.length() >= 2 ? country.substring(0, 2).toUpperCase() : "XX";

            Conflict c = new Conflict();
            c.setTitle(title.length() > 295 ? title.substring(0, 295) + "..." : title);
            c.setDescription(title);
            c.setCountryA(countryCode);
            c.setRegion(guessRegion(countryCode));
            c.setLat(lat);
            c.setLng(lng);
            c.setSeverity(guessSeverity(title));
            c.setStatus("ACTIVE_WAR");
            c.setConflictType("NEWS_EVENT");
            c.setSource("GDELT");
            c.setSourceUrl(url);
            c.setExternalId("gdelt-" + Math.abs((url + title).hashCode()));
            c.setFetchedAt(LocalDateTime.now());

            return c;
        } catch (Exception e) {
            return null;
        }
    }

    private String guessSeverity(String title) {
        String lower = title.toLowerCase();
        if (lower.contains("nuclear") || lower.contains("invasion") || lower.contains("massacre")
                || lower.contains("chemical") || lower.contains("genocide")) {
            return "CRITICAL";
        }
        if (lower.contains("bombing") || lower.contains("airstrike") || lower.contains("killed")
                || lower.contains("missile") || lower.contains("explosion") || lower.contains("dead")) {
            return "HIGH";
        }
        if (lower.contains("attack") || lower.contains("military") || lower.contains("clash")
                || lower.contains("troops") || lower.contains("combat")) {
            return "MEDIUM";
        }
        return "LOW";
    }

    private String guessRegion(String code) {
        return switch (code) {
            case "UA", "RU", "PL", "RO", "DE", "FR", "GB" -> "Europe";
            case "IL", "PS", "SY", "IQ", "IR", "YE", "SA", "LB" -> "Middle East";
            case "CN", "TW", "KP", "KR", "JP", "MM", "PH" -> "East Asia";
            case "IN", "PK", "AF" -> "South Asia";
            case "SD", "SO", "ET", "CD", "NG", "ML", "BF" -> "Africa";
            case "US", "MX", "CO" -> "Americas";
            default -> "Other";
        };
    }
}
