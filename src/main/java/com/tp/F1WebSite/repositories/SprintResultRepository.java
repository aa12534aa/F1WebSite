package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.SprintResultEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintResultRepository extends CrudRepository<SprintResultEntity, Long> {

    @Query(value = """
        SELECT sprintResult
        FROM SprintResultEntity sprintResult
        JOIN sprintResult.race race
        JOIN sprintResult.driver driver
        JOIN sprintResult.constructor constructor
        JOIN race.circuit circuit
        WHERE race.raceId = :raceId AND driver.isDeleted = false AND constructor.isDeleted = false AND circuit.isDeleted = false
        ORDER BY sprintResult.position ASC
        """)
    List<SprintResultEntity> findAllByRace_RaceIdOrderByPositionAsc(Long raceId);

    Boolean existsByRace_RaceIdAndDriver_Url(Long raceId, String driverUrl);

    Boolean existsByDriver_DriverId(Long driverId);

    Boolean existsByConstructor_ConstructorId(Long constructorConstructorId);

    Boolean existsByRace_RaceId(Long raceId);

    Boolean existsByDriver_UrlAndRace_RaceId(String driverUrl, Long raceId);
}
