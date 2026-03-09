package com.warzone.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BaseRepository extends JpaRepository<MilitaryBase, UUID> {
    List<MilitaryBase> findByCountryCode(String code);
    List<MilitaryBase> findByBaseType(String type);
    List<MilitaryBase> findByIsOverseasTrue();
    List<MilitaryBase> findByAlliance(String alliance);

    @Query("SELECT DISTINCT b.baseType FROM MilitaryBase b")
    List<String> findDistinctTypes();

    @Query("SELECT DISTINCT b.alliance FROM MilitaryBase b WHERE b.alliance IS NOT NULL")
    List<String> findDistinctAlliances();
}
