package com.tp.F1WebSite.repositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.tp.F1WebSite.domain.entities.DriverEntity;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;
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

    @Sql(scripts = "/testData/PrepareData.sql")
    @Test
    void shouldReturnDriversWinsRacesPage() {
        Pageable pageable = PageRequest.of(0, 50);
        List<DriverWinsRacesDto> driverWinsRaces = driverRepository
                .findDriversWinsRacesByName("", pageable).getContent();
        assertThat(driverWinsRaces.size()).isEqualTo(4);
    }

    @Test
    void shouldCreateDriver() {
        DriverEntity driver = DriverEntity.builder()
                .name("john son")
                .url("dskjfo")
                .build();
        DriverEntity savedDriver = driverRepository.save(driver);
        assertThat(savedDriver.getDriverId()).isNotNull();
        assertThat(driverRepository.findById(driver.getDriverId())).isPresent();
        assertThat(driverRepository.findById(driver.getDriverId()).get().getName()).
                isEqualTo("john son");
    }
}
