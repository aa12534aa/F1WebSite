package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverAllInfoDto;
import com.tp.F1WebSite.dto.DriverTrackWins;
import com.tp.F1WebSite.dto.DriverRaceDto;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends CrudRepository<DriverEntity, Long>,
        PagingAndSortingRepository<DriverEntity, Long> {

    @Query(value = """ 
    SELECT new com.tp.F1WebSite.dto.DriverWinsRacesDto (
        driver.driverId,
        driver.name,
        COUNT(CASE WHEN result.position = 1 THEN 1 END),
        Count(result.resultId)
        )
    FROM DriverEntity driver
    JOIN driver.results result
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
    JOIN driver.results result
    WHERE driver.driverId = :driverId
    GROUP BY driver.driverId, driver.name, driver.url
    """)
    DriverAllInfoDto findDriverAllInfo(Long driverId);

    @Query("""
    SELECT new com.tp.F1WebSite.dto.DriverRaceDto (
        constructor.name,
        result.grid,
        result.position,
        result.points,
        race.date,
        race.name
        )
    FROM ResultEntity result
    JOIN result.race race
    JOIN result.constructor constructor
    WHERE result.driver.driverId = :driverId
    ORDER BY race.date DESC
    """)
    List<DriverRaceDto> findDriverAllRaces(Long driverId);

    @Query("""
    SELECT COUNT(CASE WHEN qualifying.position = 1 THEN 1 END)
    FROM DriverEntity driver
    JOIN driver.qualifying qualifying
    WHERE driver.driverId = :driverId
    """)
    Long findNumOfPolePositions(Long driverId);

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.DriverTrackWins (
        race.circuit.name,
        race.circuit.country,
        COUNT(CASE WHEN result.position = 1 THEN 1 END)
        )
    FROM ResultEntity result
    JOIN result.race race
    WHERE result.driver.driverId = :driverId
    GROUP BY race.circuit.name, race.circuit.country
    ORDER BY COUNT(CASE WHEN result.position = 1 THEN 1 END) DESC
    """)
    List<DriverTrackWins> findDriverTracksWins(Long driverId);
}
