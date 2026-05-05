package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.repositories.DriverRepository;
import com.tp.F1WebSite.services.DriverService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DriverServiceImpl implements DriverService {

    private DriverRepository driverRepository;

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public List<DriverEntity> findAll() {
        Iterable<DriverEntity> drivers = driverRepository.findAll();
        return StreamSupport.stream(drivers.spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverEntity> findManyByName(String searchName) {
        List<DriverEntity> drivers = driverRepository.findByNameContainingIgnoreCase(searchName);
        return drivers;
    }
}
