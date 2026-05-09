package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.dto.CircuitRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RaceRepository extends CrudRepository<RaceEntity, Long>,
        PagingAndSortingRepository<RaceEntity, Long> {

}
