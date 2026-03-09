package com.warzone.military;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MilitaryRepository extends JpaRepository<MilitaryPower, UUID> {
    Optional<MilitaryPower> findByCountryCode(String code);
    List<MilitaryPower> findAllByOrderByRankAsc();
    List<MilitaryPower> findByNuclearCapableTrue();

    @Query("SELECT COALESCE(SUM(m.activePersonnel), 0) FROM MilitaryPower m")
    long totalActivePersonnel();
}
