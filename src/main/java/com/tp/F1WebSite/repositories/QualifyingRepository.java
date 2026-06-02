package com.tp.F1WebSite.repositories;


import com.tp.F1WebSite.domain.entities.QualifyingEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualifyingRepository extends CrudRepository<QualifyingEntity, Long> {

    @Query(value = """
        SELECT qualifying
        FROM QualifyingEntity qualifying
        JOIN qualifying.race race
        JOIN qualifying.driver driver
        JOIN qualifying.constructor constructor
        JOIN race.circuit circuit
        WHERE race.raceId = :raceId AND driver.isDeleted = false AND constructor.isDeleted = false AND circuit.isDeleted = false
        ORDER BY qualifying.position ASC
        """)
    List<QualifyingEntity> findAllByRace_RaceIdOrderByPositionAsc(Long raceId);

    Boolean existsByRace_RaceIdAndDriver_Url(Long raceId, String driverUrl);

    Boolean existsByDriver_DriverId(Long driverId);

    Boolean existsByConstructor_ConstructorId(Long constructorConstructorId);

    Boolean existsByRace_RaceId(Long raceId);

    Boolean existsByDriver_UrlAndRace_RaceId(String driverUrl, Long raceId);
}
