package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.driver.DriverAllInfoDto;
import com.tp.F1WebSite.dto.driver.DriverCircuitsWins;
import com.tp.F1WebSite.dto.driver.DriverRaceDto;
import com.tp.F1WebSite.dto.driver.DriverWinsRacesDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<DriverEntity, Long>,
        PagingAndSortingRepository<DriverEntity, Long> {

    @Query(value = """ 
    SELECT new com.tp.F1WebSite.dto.driver.DriverWinsRacesDto (
        driver.driverId,
        driver.name,
        COUNT(CASE WHEN result.position = 1 AND race.isDeleted = false AND circuit.isDeleted = false AND constructor.isDeleted = false THEN 1 END),
        COUNT(CASE WHEN race.isDeleted = false AND circuit.isDeleted = false THEN 1 END)
        )
    FROM DriverEntity driver
    LEFT JOIN driver.results result
    LEFT JOIN result.race race
    LEFT JOIN race.circuit circuit
    LEFT JOIN result.constructor constructor
    WHERE LOWER(driver.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
    GROUP BY driver.driverId, driver.name
    ORDER BY COUNT(CASE WHEN result.position = 1 AND race.isDeleted = false AND circuit.isDeleted = false AND constructor.isDeleted = false THEN 1 END) DESC
    """,
            countQuery = """
    SELECT (COUNT(DISTINCT driver.driverId))
    FROM DriverEntity driver
    WHERE LOWER(driver.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
    """)
    Page<DriverWinsRacesDto> findDriversWinsRacesByName(String searchName, Pageable pageable);

    @Query("""
    SELECT new com.tp.F1WebSite.dto.driver.DriverAllInfoDto (
        driver.name,
        driver.url,
        COUNT(CASE WHEN result.position = 1 THEN 1 END),
        COUNT(CASE WHEN result.position = 2 THEN 1 END),
        COUNT(CASE WHEN result.position = 3 THEN 1 END),
        SUM(result.points),
        COUNT(result.resultId),
        driver.nationality
        )
    FROM DriverEntity driver
    LEFT JOIN driver.results result
    JOIN result.race race
    JOIN race.circuit circuit
    JOIN result.constructor constructor
    WHERE driver.driverId = :driverId AND circuit.isDeleted = false AND constructor.isDeleted = false
    GROUP BY driver.driverId, driver.name, driver.url, driver.nationality
    """)
    DriverAllInfoDto findDriverAllInfo(Long driverId);

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.driver.DriverRaceDto (
        driver.name,
        constructor.name,
        result.grid,
        result.position,
        result.points,
        race.date,
        race.name,
        circuit.country
        )
    FROM ResultEntity result
    JOIN result.race race
    JOIN result.constructor constructor
    JOIN result.driver driver
    JOIN race.circuit circuit
    WHERE driver.driverId = :driverId AND LOWER(circuit.country) LIKE LOWER(CONCAT('%', :searchCountry, '%'))
    ORDER BY race.date DESC
    """,
    countQuery = """
    SELECT COUNT(result.resultId)
    FROM ResultEntity result
    JOIN result.driver driver
    JOIN result.race race
    JOIN race.circuit circuit
    WHERE driver.driverId = :driverId AND LOWER(circuit.country) LIKE LOWER(CONCAT('%', :searchCountry, '%'))
    """)
    Page<DriverRaceDto> findDriverManyRacesByCountry(Long driverId, String searchCountry, Pageable pageable);

    @Query("""
    SELECT COUNT(CASE WHEN qualifying.position = 1 THEN 1 END)
    FROM DriverEntity driver
    LEFT JOIN driver.qualifying qualifying
    JOIN qualifying.race race
    JOIN race.circuit circuit
    JOIN qualifying.constructor constructor
    WHERE driver.driverId = :driverId AND circuit.isDeleted = false AND constructor.isDeleted = false
    """)
    Long findNumOfPolePositions(Long driverId);

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.driver.DriverCircuitsWins (
        circuit.circuitId,
        circuit.name,
        circuit.country,
        COUNT(CASE WHEN result.position = 1 AND constructor.isDeleted = false THEN 1 END)
        )
    FROM DriverEntity driver
    LEFT JOIN driver.results result
    LEFT JOIN result.race race
    LEFT JOIN race.circuit circuit
    LEFT JOIN result.constructor constructor
    WHERE driver.driverId = :driverId AND race.isDeleted = false AND circuit.isDeleted = false
    GROUP BY circuit.circuitId, circuit.name, circuit.country
    """)
    List<DriverCircuitsWins> findDriverCircuitsWins(Long driverId);

    Boolean existsByUrl(String url);

    Optional<DriverEntity> findByUrl(String url);
}
