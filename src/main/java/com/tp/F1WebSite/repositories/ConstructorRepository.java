package com.tp.F1WebSite.repositories;

import com.tp.F1WebSite.domain.entities.ConstructorEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstructorRepository extends CrudRepository<ConstructorEntity, Long> {
}
