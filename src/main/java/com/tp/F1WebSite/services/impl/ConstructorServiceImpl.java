package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.repositories.ConstructorRepository;
import com.tp.F1WebSite.services.ConstructorService;
import org.springframework.stereotype.Service;

@Service
public class ConstructorServiceImpl implements ConstructorService {

    private ConstructorRepository constructorRepository;

    public ConstructorServiceImpl(ConstructorRepository constructorRepository) {
        this.constructorRepository = constructorRepository;
    }
}
