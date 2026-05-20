package com.tp.F1WebSite.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.tp.F1WebSite.CustomPageImpl;
import com.tp.F1WebSite.dto.CircuitAllInfoDto;
import com.tp.F1WebSite.dto.CircuitDriverWinsDto;
import com.tp.F1WebSite.dto.CircuitRacesDto;
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
    private CircuitRepository circuitRepository;

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
        assertThat(responseBody.getContent()).hasSize(2);
        assertThat(responseBody.getContent()).extracting(
                        CircuitRacesDto::getName,
                        CircuitRacesDto::getCountry,
                        CircuitRacesDto::getNumOfRaces
                )
                .containsExactlyInAnyOrder(
                        tuple("Tor Poznan", "Poland", 1L),
                        tuple("Circuit de Barcelona", "Spain", 1L)
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
        assertThat(responseBody.getNumOfRaces()).isEqualTo(1L);
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
}
