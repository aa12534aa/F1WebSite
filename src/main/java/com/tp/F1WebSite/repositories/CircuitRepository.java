package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.CircuitEntity;
import com.tp.F1WebSite.dto.circuit.CircuitAllInfoDto;
import com.tp.F1WebSite.dto.circuit.CircuitDriverWinsDto;
import com.tp.F1WebSite.dto.circuit.CircuitRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CircuitRepository extends CrudRepository<CircuitEntity, Long>,
        PagingAndSortingRepository<CircuitEntity, Long> {

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.circuit.CircuitRacesDto (
        circuit.circuitId,
        circuit.name,
        circuit.country,
        COUNT(race.raceId)
        )
    FROM CircuitEntity circuit
    LEFT JOIN circuit.races race
    WHERE LOWER(circuit.name) LIKE LOWER(CONCAT('%', :searchCircuit, '%'))
    GROUP BY circuit.circuitId, circuit.name, circuit.country
    ORDER BY COUNT(race.raceId) DESC
    """,
            countQuery = """
    SELECT COUNT(DISTINCT circuit.name)
    FROM CircuitEntity circuit
    WHERE LOWER(circuit.name) LIKE LOWER(CONCAT('%', :searchCircuit, '%'))
    """)
    Page<CircuitRacesDto> findCircuitsRacesByName(String searchCircuit, Pageable pageable);

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.circuit.CircuitDriverWinsDto (
        driver.driverId,
        driver.name,
        driver.nationality,
        COUNT(CASE WHEN result.position = 1 THEN 1 END)
        )
    FROM CircuitEntity circuit
    LEFT JOIN circuit.races race
    LEFT JOIN race.results result
    LEFT JOIN result.driver driver
    WHERE circuit.circuitId = :circuitId AND driver.isDeleted = false
    GROUP BY driver.driverId, driver.name, driver.nationality
    """)
    List<CircuitDriverWinsDto> findCircuitDriversWins(Long circuitId);

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.circuit.CircuitAllInfoDto(
        circuit.name,
        circuit.country,
        circuit.url,
        COUNT(race.raceId)
        )
    FROM RaceEntity race
    JOIN race.circuit circuit
    WHERE circuit.circuitId = :circuitId
    GROUP BY circuit.name, circuit.country, circuit.url
    """)
    CircuitAllInfoDto findCircuitAllInfo(Long circuitId);

    Boolean existsByName(String name);

    Optional<CircuitEntity> findByName(String name);
}
