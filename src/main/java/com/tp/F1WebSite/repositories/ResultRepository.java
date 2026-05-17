package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.ResultEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepository extends CrudRepository<ResultEntity, Long> {

    Boolean existsByRace_RaceIdAndDriver_Url(Long raceId, String driverUrl);
}
