package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverWinsDto;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends CrudRepository<DriverEntity, Long> {

    List<DriverEntity> findByNameContainingIgnoreCase(String name);

    @Query(value = """
        SELECT d.driver_id AS id,
            d.name AS name,
            COUNT(r.result_id) AS firstPlaces
        FROM drivers d
        INNER JOIN results r ON d.driver_id = r.driver_id
        WHERE r.position = 1
        GROUP BY d.driver_id, d.name
        ORDER BY firstPlaces DESC
        """, nativeQuery = true)
    List<DriverWinsDto> findAllDriversWithNumOfFirstPlaces();
}
