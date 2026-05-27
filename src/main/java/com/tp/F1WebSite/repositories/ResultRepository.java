package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.dto.ResultDto;
import com.tp.F1WebSite.domain.entities.ResultEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends CrudRepository<ResultEntity, Long> {

    @Query(value = """
        SELECT result
        FROM ResultEntity result
        JOIN result.race race
        JOIN result.driver driver
        JOIN result.constructor constructor
        JOIN race.circuit circuit
        WHERE race.raceId = :raceId AND driver.isDeleted = false AND constructor.isDeleted = false AND circuit.isDeleted = false
        ORDER BY result.position ASC
        """)
    List<ResultEntity> findAllByRace_RaceIdOrderByPositionAsc(Long raceId);

    Boolean existsByRace_RaceIdAndDriver_Url(Long raceId, String driverUrl);

    Boolean existsByDriver_DriverId(Long driverId);

    Boolean existsByConstructor_ConstructorId(Long constructorConstructorId);

    Boolean existsByRace_RaceId(Long raceId);

    Boolean existsByDriver_Url(String driverUrl);
}
