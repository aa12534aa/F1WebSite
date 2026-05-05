package com.tp.F1WebSite.services.impl;

import com.tp.F1WebSite.repositories.ResultRepository;
import com.tp.F1WebSite.services.ResultService;
import org.springframework.stereotype.Service;

@Service
public class ResultServiceImpl implements ResultService {

    private ResultRepository resultRepository;

    public ResultServiceImpl(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }
}
