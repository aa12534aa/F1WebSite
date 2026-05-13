package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.ConstructorEntity;
import com.tp.F1WebSite.dto.ConstructorAllInfoDto;
import com.tp.F1WebSite.dto.ConstructorRaceDto;
import com.tp.F1WebSite.dto.ConstructorWinsRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConstructorRepository extends CrudRepository<ConstructorEntity, Long>,
        PagingAndSortingRepository<ConstructorEntity, Long> {

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.ConstructorWinsRacesDto (
        constructor.constructorId,
        constructor.name,
        COUNT(CASE WHEN result.position = 1 THEN 1 END),
        COUNT(result.resultId)
        )
    FROM ConstructorEntity constructor
    JOIN constructor.results result
    WHERE LOWER(constructor.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
    GROUP BY constructor.constructorId, constructor.name
    ORDER BY COUNT(CASE WHEN result.position = 1 THEN 1 END) DESC
    """,
    countQuery = """
    SELECT COUNT(DISTINCT constructor.constructorId)
    FROM ConstructorEntity constructor
    WHERE LOWER(constructor.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
    """)
    Page<ConstructorWinsRacesDto> findConstructorsWinsRacesByName(String searchName, Pageable pageable);

    @Query("""
    SELECT new com.tp.F1WebSite.dto.ConstructorAllInfoDto (
        constructor.name,
        COUNT(CASE WHEN result.position = 1 THEN 1 END),
        COUNT(CASE WHEN result.position = 2 THEN 1 END),
        COUNT(CASE WHEN result.position = 3 THEN 1 END),
        SUM(result.points),
        COUNT(result.resultId)
        )
    FROM ConstructorEntity constructor
    JOIN constructor.results result
    WHERE constructor.constructorId = :constructorId
    GROUP BY constructor.constructorId, constructor.name
    """)
    ConstructorAllInfoDto findConstructorAllInfo(Long constructorId);

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.ConstructorRaceDto (
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
    Join race.circuit circuit
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
}
