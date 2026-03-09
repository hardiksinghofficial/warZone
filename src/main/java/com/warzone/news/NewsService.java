package com.warzone.news;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);

    private final NewsRepository repo;
    private final RssFeedClient rssFeed;

    public NewsService(NewsRepository repo, RssFeedClient rssFeed) {
        this.repo = repo;
        this.rssFeed = rssFeed;
    }

    @Cacheable("news")
    public List<NewsArticle> getLatest(int limit) {
        return repo.findAllByOrderByPublishedAtDesc(PageRequest.of(0, Math.min(limit, 50)));
    }

    public List<NewsArticle> getByCategory(String category, int limit) {
        return repo.findByCategoryOrderByPublishedAtDesc(
                category.toUpperCase(), PageRequest.of(0, Math.min(limit, 50)));
    }

    @Scheduled(fixedDelayString = "${app.news.poll-interval:600000}", initialDelay = 45000)
    @CacheEvict(value = {"news", "dashboard"}, allEntries = true)
    public void pollFeeds() {
        log.info("Polling RSS news feeds...");
        try {
            List<NewsArticle> articles = rssFeed.fetchAll();
            int saved = 0;
            for (NewsArticle article : articles) {
                if (!repo.existsByExternalHash(article.getExternalHash())) {
                    repo.save(article);
                    saved++;
                }
            }
            if (saved > 0) {
                log.info("Saved {} new news articles", saved);
            }
        } catch (Exception e) {
            log.warn("News poll failed: {}", e.getMessage());
        }
    }
}
