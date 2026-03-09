package com.warzone.timeline;

import com.warzone.conflict.Conflict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TimelineService {

    private final TimelineRepository repo;

    public TimelineService(TimelineRepository repo) {
        this.repo = repo;
    }

    @Cacheable("timeline")
    public List<TimelineEvent> getRecent(int limit) {
        return repo.findAllByOrderByEventTimeDesc(PageRequest.of(0, Math.min(limit, 100)));
    }

    public List<TimelineEvent> getByType(String type, int limit) {
        return repo.findByEventTypeOrderByEventTimeDesc(type.toUpperCase(), PageRequest.of(0, limit));
    }

    public List<TimelineEvent> getByCountry(String code, int limit) {
        return repo.findByCountryCodeOrderByEventTimeDesc(code.toUpperCase(), PageRequest.of(0, limit));
    }

    public void recordConflictEvent(Conflict conflict) {
        TimelineEvent event = new TimelineEvent();
        event.setTitle(conflict.getTitle());
        event.setDescription(conflict.getDescription());
        event.setEventType("CONFLICT");
        event.setSeverity(conflict.getSeverity());
        event.setCountryCode(conflict.getCountryA());
        event.setLat(conflict.getLat());
        event.setLng(conflict.getLng());
        event.setEventTime(LocalDateTime.now());
        event.setSource(conflict.getSource());
        repo.save(event);
    }
}
