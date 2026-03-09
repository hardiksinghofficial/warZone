package com.warzone.news;

import com.warzone.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService service;

    public NewsController(NewsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getLatest(@RequestParam(defaultValue = "20") int limit) {
        List<NewsArticle> data = service.getLatest(limit);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getByCategory(@PathVariable String category,
                                            @RequestParam(defaultValue = "20") int limit) {
        List<NewsArticle> data = service.getByCategory(category, limit);
        return ResponseEntity.ok(ApiResponse.list(data, data.size()));
    }
}
