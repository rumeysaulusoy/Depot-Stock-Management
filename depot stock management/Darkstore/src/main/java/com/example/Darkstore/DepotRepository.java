package com.example.Darkstore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DepotRepository extends JpaRepository<Depot, Long> {
    Depot findByType(String type);
    @Query("SELECT d FROM Depot d WHERE d.type = 'main depot'")
    List<Depot> findMainDepots();
    @Query("SELECT d FROM Depot d WHERE d.type = 'main depot' AND d.id != :excludeId")
    List<Depot> findMainDepotsExcludingCurrent(@Param("excludeId") Long excludeId);

}

