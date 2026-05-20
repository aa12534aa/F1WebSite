package com.tp.F1WebSite.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.tp.F1WebSite.CustomPageImpl;
import com.tp.F1WebSite.domain.dto.*;
import com.tp.F1WebSite.dto.RaceCircuitDto;
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

import java.time.LocalDate;
import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RaceIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private TestRestTemplate restTemplate;

    // GET
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void shouldReturnRaceCircuitPage() {
        ResponseEntity<CustomPageImpl<RaceCircuitDto>> response = restTemplate
                .exchange("/api/races",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<CustomPageImpl<RaceCircuitDto>>() {
                        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PageImpl<RaceCircuitDto> responseBody = response.getBody();
        assertThat(responseBody).isNotEmpty();
        assertThat(responseBody.getContent()).hasSize(2);
        assertThat(responseBody.getContent()).extracting(
                        RaceCircuitDto::getRaceName,
                        RaceCircuitDto::getCircuitName,
                        RaceCircuitDto::getCountry,
                        RaceCircuitDto::getWinnerName,
                        RaceCircuitDto::getConstructorName
                )
                .containsExactlyInAnyOrder(
                        tuple("GP Poznan", "Tor Poznan", "Poland", "Julian Sokołowski", "ferrari"),
                        tuple("GP Barcelona", "Circuit de Barcelona", "Spain", "Charles Leclerc", "ferrari")
                );
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void shouldReturnRaceDto() {
        ResponseEntity<RaceDto> response = restTemplate
                .getForEntity("/api/races/1",
                        RaceDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        RaceDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getDate()).isEqualTo(LocalDate.of(2025, 10, 12));
        assertThat(responseBody.getName()).isEqualTo("GP Poznan");

        CircuitDto expectedCircuit = CircuitDto.builder()
                .circuitId(1L)
                .name("Tor Poznan")
                .country("Poland")
                .url("http://poznantor")
                .build();
        assertThat(responseBody.getCircuit())
                .usingRecursiveComparison()
                .isEqualTo(expectedCircuit);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void shouldReturnRaceResultsList() {
        ResponseEntity<List<ResultDto>> response = restTemplate
                .exchange("/api/races/1/results",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<ResultDto>>() {
                        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<ResultDto> responseBody = response.getBody();
        assertThat(responseBody).isNotEmpty();
        assertThat(responseBody).hasSize(4);

        ResultDto firstResult = responseBody.getFirst();

        assertThat(firstResult.getDriver())
                .usingRecursiveComparison()
                .isEqualTo(DriverDto.builder()
                        .driverId(1L)
                        .name("Julian Sokołowski")
                        .nationality("Poland")
                        .url("http://julsok1")
                        .build());

        assertThat(firstResult.getConstructor())
                .usingRecursiveComparison()
                .isEqualTo(ConstructorDto.builder()
                        .constructorId(1L)
                        .name("ferrari")
                        .build());

        assertThat(responseBody)
                .extracting(ResultDto::getPosition, ResultDto::getGrid, ResultDto::getPoints)
                .containsExactlyInAnyOrder(
                        tuple(1, 1, 25.0),
                        tuple(2, 2, 18.0),
                        tuple(3, 3, 15.0),
                        tuple(4, 4, 12.0)
                );
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void shouldReturnRaceQualifyingList() {
        ResponseEntity<List<QualifyingDto>> response = restTemplate
                .exchange("/api/races/1/qualifying",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<QualifyingDto>>() {
                        });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<QualifyingDto> responseBody = response.getBody();
        assertThat(responseBody).isNotEmpty();
        assertThat(responseBody).hasSize(4);

        QualifyingDto firstResult = responseBody.getFirst();

        assertThat(firstResult.getDriver())
                .usingRecursiveComparison()
                .isEqualTo(DriverDto.builder()
                        .driverId(1L)
                        .name("Julian Sokołowski")
                        .nationality("Poland")
                        .url("http://julsok1")
                        .build());

        assertThat(firstResult.getConstructor())
                .usingRecursiveComparison()
                .isEqualTo(ConstructorDto.builder()
                        .constructorId(1L)
                        .name("ferrari")
                        .build());

        assertThat(responseBody)
                .extracting(QualifyingDto::getPosition)
                .containsExactlyInAnyOrder(
                        1, 2, 3, 4
                );
    }
}
