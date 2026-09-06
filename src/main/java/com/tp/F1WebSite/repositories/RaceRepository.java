package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.dto.race.RaceCircuitDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RaceRepository extends CrudRepository<RaceEntity, Long>,
        PagingAndSortingRepository<RaceEntity, Long> {

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.race.RaceCircuitDto (
        race.raceId,
        race.name,
        race.date,
        circuit.name,
        circuit.country,
        driver.name,
        constructor.name,
        race.sprintAppearance
        )
    FROM RaceEntity race
    LEFT JOIN race.results result ON result.position = 1
    LEFT JOIN race.circuit circuit
    LEFT JOIN result.driver driver
    LEFT JOIN result.constructor constructor
    LEFT JOIN race.sprintResults sprintResults
    WHERE circuit.isDeleted = false AND (LOWER(race.name) LIKE LOWER(CONCAT('%', :searchName, '%')) OR 	to_char(race.date, 'YYYY-MM-DD') LIKE CONCAT('%', :searchName, '%'))
    GROUP BY race.raceId, race.name, race.date, circuit.name, circuit.country, driver.name, constructor.name, race.sprintAppearance
    ORDER BY race.date DESC
    """, countQuery = """
    SELECT COUNT(DISTINCT race.raceId)
    FROM RaceEntity race
    LEFT JOIN race.circuit circuit
    WHERE circuit.isDeleted = false
      AND (LOWER(race.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
        OR to_char(race.date, 'YYYY-MM-DD') LIKE CONCAT('%', :searchName, '%'))
    """)
    Page<RaceCircuitDto> findRacesCircuitsByName(String searchName, Pageable pageable);

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.race.RaceCircuitDto (
        race.raceId,
        race.name,
        race.date,
        circuit.name,
        circuit.country,
        driver.name,
        constructor.name,
        race.sprintAppearance
        )
    FROM RaceEntity race
    LEFT JOIN race.results result ON result.position = 1
    LEFT JOIN race.circuit circuit
    LEFT JOIN result.driver driver
    LEFT JOIN result.constructor constructor
    LEFT JOIN race.sprintResults sprintResults
    WHERE circuit.isDeleted = false AND (LOWER(race.name) LIKE LOWER(CONCAT('%', :searchName, '%')) OR 	to_char(race.date, 'YYYY-MM-DD') LIKE CONCAT('%', :searchName, '%')) AND (race.sprintAppearance = :sprint)
    GROUP BY race.raceId, race.name, race.date, circuit.name, circuit.country, driver.name, constructor.name, race.sprintAppearance
    ORDER BY race.date DESC
    """, countQuery = """
    SELECT COUNT(DISTINCT race.raceId)
    FROM RaceEntity race
    LEFT JOIN race.circuit circuit
    WHERE circuit.isDeleted = false
      AND (LOWER(race.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
        OR to_char(race.date, 'YYYY-MM-DD') LIKE CONCAT('%', :searchName, '%'))
    """)
    Page<RaceCircuitDto> findRacesCircuitsByNameAndSprint(String searchName, Boolean sprint, Pageable pageable);

    Boolean existsByDate(LocalDate date);

    Boolean existsByCircuit_CircuitId(Long circuitId);
}
