package com.warzone.conflict;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConflictRepository extends JpaRepository<Conflict, UUID> {

    List<Conflict> findByStatusOrderBySeverityDesc(String status);

    @Query("SELECT c FROM Conflict c WHERE c.countryA = :code OR c.countryB = :code ORDER BY c.severity DESC")
    List<Conflict> findByCountry(String code);

    List<Conflict> findByRegionIgnoreCaseOrderBySeverityDesc(String region);

    List<Conflict> findAllByOrderBySeverityDesc();

    boolean existsByExternalId(String externalId);

    @Query("SELECT COUNT(c) FROM Conflict c WHERE c.status = 'ACTIVE_WAR'")
    long countActiveWars();

    @Query("SELECT COUNT(c) FROM Conflict c WHERE c.severity = :severity")
    long countBySeverity(String severity);

    @Query("SELECT COUNT(c) FROM Conflict c WHERE c.status = 'TENSION'")
    long countTensions();

    @Query("SELECT DISTINCT c.region FROM Conflict c WHERE c.region IS NOT NULL")
    List<String> findDistinctRegions();
}
