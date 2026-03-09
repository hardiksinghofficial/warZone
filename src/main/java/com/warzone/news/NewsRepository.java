package com.warzone.news;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NewsRepository extends JpaRepository<NewsArticle, UUID> {
    List<NewsArticle> findAllByOrderByPublishedAtDesc(Pageable pageable);
    List<NewsArticle> findByCategoryOrderByPublishedAtDesc(String category, Pageable pageable);
    boolean existsByExternalHash(String hash);
}
