package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.RaceEntity;
import com.tp.F1WebSite.dto.TracksRacesDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RaceRepository extends CrudRepository<RaceEntity, Long>,
        PagingAndSortingRepository<RaceEntity, Long> {

    @Query(value = """
    SELECT new com.tp.F1WebSite.dto.TracksRacesDto (
        race.name,
        COUNT(race.raceId)
        )
    FROM RaceEntity race
    WHERE LOWER(race.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
    GROUP BY race.name
    ORDER BY COUNT(race.raceId) DESC
    """,
    countQuery = """
    SELECT COUNT(DISTINCT race.name)
    FROM RaceEntity race
    WHERE LOWER(race.name) LIKE LOWER(CONCAT('%', :searchName, '%'))
    """)
    Page<TracksRacesDto> findTracksRacesByName(String searchName, Pageable pageable);
}
