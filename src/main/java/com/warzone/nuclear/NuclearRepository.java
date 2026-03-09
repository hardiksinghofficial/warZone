package com.warzone.nuclear;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NuclearRepository extends JpaRepository<NuclearArsenal, UUID> {
    Optional<NuclearArsenal> findByCountryCode(String code);
    List<NuclearArsenal> findAllByOrderByTotalWarheadsDesc();

    @Query("SELECT COALESCE(SUM(n.totalWarheads), 0) FROM NuclearArsenal n")
    int sumTotalWarheads();

    @Query("SELECT COALESCE(SUM(n.deployed), 0) FROM NuclearArsenal n")
    int sumDeployed();

    @Query("SELECT COALESCE(SUM(n.icbmCount), 0) FROM NuclearArsenal n")
    int sumIcbms();
}
