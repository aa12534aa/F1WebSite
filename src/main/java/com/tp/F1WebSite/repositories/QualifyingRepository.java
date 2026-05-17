package com.tp.F1WebSite.repositories;


import com.tp.F1WebSite.domain.entities.QualifyingEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualifyingRepository extends CrudRepository<QualifyingEntity, Long> {

    Boolean existsByRace_RaceIdAndDriver_Url(Long raceId, String driverUrl);
}
