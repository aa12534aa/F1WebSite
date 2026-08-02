package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.ConstructorEntity;
import com.tp.F1WebSite.dto.constructor.ConstructorAllInfoDto;
import com.tp.F1WebSite.dto.constructor.ConstructorRaceDto;
import com.tp.F1WebSite.dto.constructor.ConstructorWinsRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConstructorRepository extends CrudRepository<ConstructorEntity, Long>,
        PagingAndSortingRepository<ConstructorEntity, Long> {

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.constructor.ConstructorWinsRacesDto (
        constructor.constructorId,
        constructor.name,
        COUNT(CASE WHEN result.position = 1 AND race.isDeleted = false AND circuit.isDeleted = false AND driver.isDeleted = false THEN 1 END),
        COUNT(CASE WHEN race.isDeleted = false AND circuit.isDeleted = false THEN 1 END)
        )
    FROM ConstructorEntity constructor
    LEFT JOIN constructor.results result
    LEFT JOIN result.race race
    LEFT JOIN race.circuit circuit
    LEFT JOIN result.driver driver
    WHERE LOWER(constructor.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
    GROUP BY constructor.constructorId, constructor.name
    ORDER BY COUNT(CASE WHEN result.position = 1 AND race.isDeleted = false AND circuit.isDeleted = false AND driver.isDeleted = false THEN 1 END) DESC
    """,
    countQuery = """
    SELECT COUNT(DISTINCT constructor.constructorId)
    FROM ConstructorEntity constructor
    WHERE LOWER(constructor.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
    """)
    Page<ConstructorWinsRacesDto> findConstructorsWinsRacesByName(String searchName, Pageable pageable);

    @Query("""
    SELECT new com.tp.F1WebSite.dto.constructor.ConstructorAllInfoDto (
        constructor.name,
        COUNT(CASE WHEN result.position = 1 THEN 1 END),
        COUNT(CASE WHEN result.position = 2 THEN 1 END),
        COUNT(CASE WHEN result.position = 3 THEN 1 END),
        SUM(result.points),
        COUNT(result.resultId)
        )
    FROM ConstructorEntity constructor
    LEFT JOIN constructor.results result
    JOIN result.race race
    JOIN race.circuit circuit
    JOIN result.driver driver
    WHERE constructor.constructorId = :constructorId AND circuit.isDeleted = false AND driver.isDeleted = false
    GROUP BY constructor.constructorId, constructor.name
    """)
    ConstructorAllInfoDto findConstructorAllInfo(Long constructorId);

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.constructor.ConstructorRaceDto (
        driver.driverId,
        race.raceId,
        constructor.name,
        driver.name,
        result.grid,
        result.position,
        result.points,
        race.date,
        race.name,
        circuit.country
        )
    FROM ResultEntity result
    JOIN result.race race
    JOIN result.driver driver
    JOIN result.constructor constructor
    JOIN race.circuit circuit
    WHERE constructor.constructorId = :constructorId AND LOWER(circuit.country) LIKE LOWER(CONCAT('%', :searchCountry, '%'))
    ORDER BY race.date DESC
    """,
    countQuery = """
    SELECT COUNT(result.resultId)
    FROM ResultEntity result
    JOIN result.constructor constructor
    JOIN result.race race
    JOIN race.circuit circuit
    WHERE constructor.constructorId = :constructorId AND LOWER(circuit.country) LIKE LOWER(CONCAT('%', :searchCountry, '%'))
    """)
    Page<ConstructorRaceDto> findConstructorAllRaces(Long constructorId, String searchCountry, Pageable pageable);

    Boolean existsByName(String name);

    Optional<ConstructorEntity> findByName(String name);
}
