package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.dto.ResultDto;
import com.tp.F1WebSite.domain.entities.ResultEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends CrudRepository<ResultEntity, Long> {

    List<ResultEntity> findAllByRace_RaceIdOrderByPositionAsc(Long raceId);

    Boolean existsByRace_RaceIdAndDriver_Url(Long raceId, String driverUrl);
}
