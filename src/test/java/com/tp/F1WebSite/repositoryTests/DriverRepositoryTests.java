package com.tp.F1WebSite.repositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.driver.DriverWinsRacesDto;
import com.tp.F1WebSite.repositories.DriverRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DriverRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    DriverRepository driverRepository;

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldCreateDriver() {
        DriverEntity driver = DriverEntity.builder()
                .name("john son")
                .nationality("UK")
                .url("dskjfo")
                .build();
        DriverEntity savedDriver = driverRepository.save(driver);
        assertThat(savedDriver.getDriverId()).isNotNull();
        DriverEntity driverEntity = driverRepository.findById(driver.getDriverId()).orElse(null);
        assertThat(driverEntity).isNotNull();
        assertThat(driverEntity.getName()).
                isEqualTo(driver.getName());
    }
}
