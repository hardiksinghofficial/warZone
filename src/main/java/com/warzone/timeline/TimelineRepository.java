package com.warzone.timeline;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TimelineRepository extends JpaRepository<TimelineEvent, UUID> {
    List<TimelineEvent> findAllByOrderByEventTimeDesc(Pageable pageable);
    List<TimelineEvent> findByEventTypeOrderByEventTimeDesc(String eventType, Pageable pageable);
    List<TimelineEvent> findByCountryCodeOrderByEventTimeDesc(String countryCode, Pageable pageable);
}
