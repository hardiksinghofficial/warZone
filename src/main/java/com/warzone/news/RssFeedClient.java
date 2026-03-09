package com.warzone.news;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RssFeedClient {

    private static final Logger log = LoggerFactory.getLogger(RssFeedClient.class);
    private final WebClient webClient;

    // free RSS feeds that reliably provide conflict/world news
    private static final String[] FEED_URLS = {
        "https://feeds.bbci.co.uk/news/world/rss.xml",
        "https://rss.nytimes.com/services/xml/rss/nyt/World.xml",
        "https://feeds.reuters.com/reuters/worldNews"
    };

    private static final String[] FEED_NAMES = {
        "BBC World", "NY Times World", "Reuters World"
    };

    // pattern to find title and link in RSS XML
    private static final Pattern ITEM_PATTERN = Pattern.compile(
            "<item>.*?<title>(?:<\\!\\[CDATA\\[)?(.*?)(?:\\]\\]>)?</title>.*?<link>(?:<\\!\\[CDATA\\[)?(.*?)(?:\\]\\]>)?</link>.*?(?:<description>(?:<\\!\\[CDATA\\[)?(.*?)(?:\\]\\]>)?</description>)?.*?</item>",
            Pattern.DOTALL
    );

    // keywords that make an article relevant
    private static final String[] CONFLICT_KEYWORDS = {
        "war", "military", "conflict", "attack", "missile", "bomb",
        "nuclear", "troops", "invasion", "airstrike", "killed",
        "gaza", "ukraine", "russia", "china", "taiwan", "korea",
        "iran", "syria", "weapons", "nato", "defense", "army",
        "navy", "sanctions", "ceasefire", "rebel", "insurgent"
    };

    public RssFeedClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<NewsArticle> fetchAll() {
        List<NewsArticle> allNews = new ArrayList<>();

        for (int i = 0; i < FEED_URLS.length; i++) {
            try {
                List<NewsArticle> batch = fetchFeed(FEED_URLS[i], FEED_NAMES[i]);
                allNews.addAll(batch);
            } catch (Exception e) {
                log.warn("RSS feed {} failed: {}", FEED_NAMES[i], e.getMessage());
            }
        }

        log.info("Fetched {} relevant news articles from RSS", allNews.size());
        return allNews;
    }

    private List<NewsArticle> fetchFeed(String feedUrl, String sourceName) {
        List<NewsArticle> articles = new ArrayList<>();

        try {
            String xml = webClient.get()
                    .uri(feedUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (xml == null || xml.isBlank()) return articles;

            Matcher matcher = ITEM_PATTERN.matcher(xml);
            int count = 0;

            while (matcher.find() && count < 15) {
                String title = cleanHtml(matcher.group(1));
                String link = cleanHtml(matcher.group(2));
                String desc = matcher.groupCount() >= 3 ? cleanHtml(matcher.group(3)) : "";

                if (title.isBlank() || !isRelevant(title + " " + desc)) continue;

                NewsArticle article = new NewsArticle();
                article.setTitle(title.length() > 495 ? title.substring(0, 495) : title);
                article.setSummary(desc != null && desc.length() > 500 ? desc.substring(0, 500) : desc);
                article.setSourceName(sourceName);
                article.setSourceUrl(link);
                article.setCategory(categorize(title));
                article.setPublishedAt(LocalDateTime.now());
                article.setFetchedAt(LocalDateTime.now());
                article.setExternalHash(hash(title + link));

                articles.add(article);
                count++;
            }
        } catch (Exception e) {
            log.debug("Error parsing feed {}: {}", sourceName, e.getMessage());
        }

        return articles;
    }

    private boolean isRelevant(String text) {
        String lower = text.toLowerCase();
        for (String keyword : CONFLICT_KEYWORDS) {
            if (lower.contains(keyword)) return true;
        }
        return false;
    }

    private String categorize(String title) {
        String lower = title.toLowerCase();
        if (lower.contains("nuclear") || lower.contains("warhead")) return "NUCLEAR";
        if (lower.contains("war") || lower.contains("invasion") || lower.contains("conflict")) return "WAR";
        if (lower.contains("missile") || lower.contains("airstrike") || lower.contains("bomb")) return "MILITARY";
        if (lower.contains("sanctions") || lower.contains("nato") || lower.contains("diplomacy")) return "DIPLOMATIC";
        return "GENERAL";
    }

    private String cleanHtml(String input) {
        if (input == null) return "";
        return input.replaceAll("<[^>]+>", "").replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("&quot;", "\"").replaceAll("&#39;", "'").trim();
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes());
            return HexFormat.of().formatHex(digest).substring(0, 16);
        } catch (Exception e) {
            return String.valueOf(input.hashCode());
        }
    }
}
