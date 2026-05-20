package com.tp.F1WebSite.repositories;


import com.tp.F1WebSite.domain.entities.QualifyingEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualifyingRepository extends CrudRepository<QualifyingEntity, Long> {

    List<QualifyingEntity> findAllByRace_RaceIdOrderByPositionAsc(Long raceId);

    Boolean existsByRace_RaceIdAndDriver_Url(Long raceId, String driverUrl);
}
