package com.tp.F1WebSite.services;

import com.tp.F1WebSite.domain.entities.DriverEntity;

import java.util.List;

public interface DriverService {


    List<DriverEntity> findAll();

    List<DriverEntity> findManyByName(String searchName);
}
