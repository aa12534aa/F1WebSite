package com.tp.F1WebSite.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.tp.F1WebSite.dto.BestDriversConstructors;
import com.tp.F1WebSite.dto.ConstructorWinsRacesDto;
import com.tp.F1WebSite.dto.DriverWinsRacesDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HomeIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private TestRestTemplate restTemplate;

    // GET
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnBestDriversList() {
        ResponseEntity<BestDriversConstructors> response =
                restTemplate.getForEntity("/api/home",
                        BestDriversConstructors.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        BestDriversConstructors responseBody = response.getBody();

        assertThat(responseBody).isNotNull();
        List<DriverWinsRacesDto> responseBodyDrivers = responseBody.getBestDrivers();

        assertThat(responseBodyDrivers).isNotEmpty();
        assertThat(responseBodyDrivers).hasSize(4);
        assertThat(responseBodyDrivers).extracting(
                        DriverWinsRacesDto::getName,
                        DriverWinsRacesDto::getNumOfWins,
                        DriverWinsRacesDto::getNumOfRaces
                )
                .containsExactlyInAnyOrder(
                        tuple("Julian Sokołowski", 1L, 2L),
                        tuple("Max Verstappen", 0L, 2L),
                        tuple("Lewis Hamilton", 0L, 2L),
                        tuple("Charles Leclerc", 1L, 2L)
                );
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnBestConstructorsList() {
        ResponseEntity<BestDriversConstructors> response =
                restTemplate.getForEntity("/api/home",
                        BestDriversConstructors.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        BestDriversConstructors responseBody = response.getBody();

        assertThat(responseBody).isNotNull();
        List<ConstructorWinsRacesDto> responseBodyConstructors = responseBody.getBestConstructors();

        assertThat(responseBodyConstructors).isNotEmpty();
        assertThat(responseBodyConstructors).hasSize(2);
        assertThat(responseBodyConstructors).extracting(
                        ConstructorWinsRacesDto::getName,
                        ConstructorWinsRacesDto::getNumOfWins,
                        ConstructorWinsRacesDto::getNumOfRaces
                )
                .containsExactlyInAnyOrder(
                        tuple("ferrari", 2L, 4L),
                        tuple("Mercedes", 0L, 4L)
                );
    }
}
