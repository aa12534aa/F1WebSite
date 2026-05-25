package com.tp.F1WebSite.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.tp.F1WebSite.CustomPageImpl;
import com.tp.F1WebSite.domain.dto.CircuitDto;
import com.tp.F1WebSite.domain.dto.DriverDto;
import com.tp.F1WebSite.dto.circuit.CircuitAllInfoDto;
import com.tp.F1WebSite.dto.circuit.CircuitDriverWinsDto;
import com.tp.F1WebSite.dto.circuit.CircuitRacesDto;
import com.tp.F1WebSite.repositories.CircuitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CircuitsIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // GET
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnCircuitsPage() {
        ResponseEntity<CustomPageImpl<CircuitRacesDto>> response =
                restTemplate.exchange("/api/circuits",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<CustomPageImpl<CircuitRacesDto>>() {
                        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PageImpl<CircuitRacesDto> responseBody = response.getBody();
        assertThat(responseBody).isNotEmpty();
        assertThat(responseBody.getContent()).hasSize(3);
        assertThat(responseBody.getContent()).extracting(
                        CircuitRacesDto::getName,
                        CircuitRacesDto::getCountry,
                        CircuitRacesDto::getNumOfRaces
                )
                .containsExactlyInAnyOrder(
                        tuple("Tor Poznan", "Poland", 2L),
                        tuple("Circuit de Barcelona", "Spain", 1L),
                        tuple("SPA", "Belgium", 0L)
                );
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnCircuitAllInfo() {
        ResponseEntity<CircuitAllInfoDto> response =
                restTemplate.getForEntity("/api/circuits/1",
                        CircuitAllInfoDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CircuitAllInfoDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCircuitName()).isEqualTo("Tor Poznan");
        assertThat(responseBody.getCountry()).isEqualTo("Poland");
        assertThat(responseBody.getUrl()).isEqualTo("http://poznantor");
        assertThat(responseBody.getNumOfRaces()).isEqualTo(2L);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnCircuitBestDriversList() {
        ResponseEntity<CircuitAllInfoDto> response =
                restTemplate.getForEntity("/api/circuits/1",
                        CircuitAllInfoDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        CircuitAllInfoDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();

        List<CircuitDriverWinsDto> responseBodyBestDrivers = responseBody.getBestDrivers();
        assertThat(responseBodyBestDrivers).hasSize(1);
        assertThat(responseBodyBestDrivers.getFirst().getDriverName()).isEqualTo("Julian Sokołowski");
        assertThat(responseBodyBestDrivers.getFirst().getNationality()).isEqualTo("Poland");
        assertThat(responseBodyBestDrivers.getFirst().getNumOfWins()).isEqualTo(1L);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenCircuitDoesNotExist() {
        ResponseEntity<CircuitAllInfoDto> response =
                restTemplate.getForEntity("/api/circuits/100",
                        CircuitAllInfoDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // POST
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldCreateDriver() {
        CircuitDto newCircuit = CircuitDto.builder()
                .name("Hungaroring")
                .country("Hungary")
                .url("http://hungaroring")
                .build();

        ResponseEntity<CircuitDto> response = restTemplate
                .postForEntity("/api/circuits",
                        newCircuit,
                        CircuitDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        CircuitDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getCircuitId()).isEqualTo(4L);
        assertThat(responseBody.getName()).isEqualTo(newCircuit.getName());
        assertThat(responseBody.getCountry()).isEqualTo(newCircuit.getCountry());
        assertThat(responseBody.getUrl()).isEqualTo(newCircuit.getUrl());
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnConflictWhenCreatingCircuitThatAlreadyExists() {
        DriverDto newDriver = DriverDto.builder()
                .name("Tor Poznan")
                .nationality("Poland")
                .url("http://poznantor")
                .build();

        ResponseEntity<DriverDto> response = restTemplate
                .postForEntity("/api/circuits",
                        newDriver,
                        DriverDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    // DELETE
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldHardDeleteCircuitWhenCircuitHasNoAnyResultsQualifying() {
        ResponseEntity<String> response = restTemplate.exchange("/api/circuits/3",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM circuits WHERE circuit_id = ?",
                Integer.class,
                3L
        );
        assertThat(count).isEqualTo(0);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldSoftDeleteDriverWhenDriverHasResultsQualifying() {
        ResponseEntity<String> response = restTemplate.exchange("/api/circuits/1",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM circuits WHERE circuit_id = ?",
                Boolean.class, 1L
        );
        assertThat(isDeleted).isTrue();
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingCircuit() {
        ResponseEntity<String> response = restTemplate.exchange("/api/circuits/100",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
