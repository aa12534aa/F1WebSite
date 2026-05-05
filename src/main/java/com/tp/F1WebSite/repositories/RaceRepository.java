package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.RaceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RaceRepository extends CrudRepository<RaceEntity, Long> {
}
