package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends CrudRepository<DriverEntity, Long> {

    List<DriverEntity> findByNameContainingIgnoreCase(String name);
}
