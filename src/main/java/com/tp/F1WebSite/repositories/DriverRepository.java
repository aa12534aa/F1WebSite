package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverAllInfoDto;
import com.tp.F1WebSite.dto.DriverRacesDto;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends CrudRepository<DriverEntity, Long> {

    @Query(""" 
    SELECT new com.tp.F1WebSite.dto.DriverWinsRacesDto (
        d.driverId,
        d.name,
        COUNT(CASE WHEN r.position = 1 THEN 1 END), 
        Count(r.resultId)
        ) 
    FROM DriverEntity d
    JOIN d.results r
    WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :searchName, '%') ) 
    GROUP BY d.driverId, d.name
    ORDER BY COUNT(CASE WHEN r.position = 1 THEN 1 END) DESC
    """)
    List<DriverWinsRacesDto> findDriversWinsRacesByName(String searchName);

    @Query("""
    SELECT new com.tp.F1WebSite.dto.DriverAllInfoDto (
        d.driverId,
        d.name,
        d.url,
        COUNT(CASE WHEN r.position = 1 THEN 1 END),
        COUNT(CASE WHEN r.position = 2 THEN 1 END),
        COUNT(CASE WHEN r.position = 3 THEN 1 END),
        SUM(r.points),
        COUNT(r.resultId)
        )
    FROM DriverEntity d
    JOIN d.results r
    WHERE d.driverId = :driverId
    GROUP BY d.driverId, d.name, d.url
    """)
    DriverAllInfoDto findDriverAllInfo(Long driverId);

    @Query("""
    SELECT new com.tp.F1WebSite.dto.DriverRacesDto (
        driver.driverId,
        result.grid,
        result.position,
        result.points,
        race.year,
        race.name
        )
    FROM ResultEntity result
    JOIN result.driver driver
    JOIN result.race race
    WHERE driver.driverId = :driverId
    """)
    List<DriverRacesDto> findDriverAllRaces(Long driverId);
}
