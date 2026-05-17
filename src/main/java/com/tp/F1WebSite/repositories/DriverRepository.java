package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverAllInfoDto;
import com.tp.F1WebSite.dto.DriverCircuitsWins;
import com.tp.F1WebSite.dto.DriverRaceDto;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;

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
    SELECT new com.tp.F1WebSite.dto.DriverWinsRacesDto (
        driver.driverId,
        driver.name,
        COUNT(CASE WHEN result.position = 1 THEN 1 END),
        Count(result.resultId)
        )
    FROM DriverEntity driver
    LEFT JOIN driver.results result
    WHERE LOWER(driver.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
    GROUP BY driver.driverId, driver.name
    ORDER BY COUNT(CASE WHEN result.position = 1 THEN 1 END) DESC
    """,
            countQuery = """
    SELECT (COUNT(DISTINCT driver.driverId))
    FROM DriverEntity driver
    WHERE LOWER(driver.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
    """)
    Page<DriverWinsRacesDto> findDriversWinsRacesByName(String searchName, Pageable pageable);

    @Query("""
    SELECT new com.tp.F1WebSite.dto.DriverAllInfoDto (
        driver.name,
        driver.url,
        COUNT(CASE WHEN result.position = 1 THEN 1 END),
        COUNT(CASE WHEN result.position = 2 THEN 1 END),
        COUNT(CASE WHEN result.position = 3 THEN 1 END),
        SUM(result.points),
        COUNT(result.resultId)
        )
    FROM DriverEntity driver
    LEFT JOIN driver.results result
    WHERE driver.driverId = :driverId
    GROUP BY driver.driverId, driver.name, driver.url
    """)
    DriverAllInfoDto findDriverAllInfo(Long driverId);

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.DriverRaceDto (
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
    WHERE driver.driverId = :driverId
    """)
    Long findNumOfPolePositions(Long driverId);

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.DriverCircuitsWins (
        circuit.name,
        circuit.country,
        COUNT(CASE WHEN result.position = 1 THEN 1 END)
        )
    FROM ResultEntity result
    JOIN result.race race
    JOIN race.circuit circuit
    WHERE result.driver.driverId = :driverId
    GROUP BY circuit.name, circuit.country
    ORDER BY COUNT(CASE WHEN result.position = 1 THEN 1 END) DESC
    """)
    List<DriverCircuitsWins> findDriverTracksWins(Long driverId);

    Boolean existsByUrl(String url);

    Optional<DriverEntity> findByUrl(String url);
}
