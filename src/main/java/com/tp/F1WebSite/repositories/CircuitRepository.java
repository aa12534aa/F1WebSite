package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.CircuitEntity;
import com.tp.F1WebSite.dto.CircuitRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CircuitRepository extends CrudRepository<CircuitEntity, Long>,
        PagingAndSortingRepository<CircuitEntity, Long> {

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.CircuitRacesDto (
        circuit.name,
        circuit.country,
        COUNT(race.raceId)
        )
    FROM CircuitEntity circuit
    JOIN circuit.races race
    WHERE LOWER(circuit.name) LIKE LOWER(CONCAT('%', :searchCircuit, '%'))
    GROUP BY circuit.name, circuit.country
    ORDER BY COUNT(race.raceId) DESC
    """,
            countQuery = """
    SELECT COUNT(DISTINCT circuit.name)
    FROM CircuitEntity circuit
    WHERE LOWER(circuit.name) LIKE LOWER(CONCAT('%', :searchCircuit, '%'))
    """)
    Page<CircuitRacesDto> findTracksRacesByName(String searchCircuit, Pageable pageable);
}
