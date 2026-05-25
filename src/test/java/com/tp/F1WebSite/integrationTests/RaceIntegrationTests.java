package com.tp.F1WebSite.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.tp.F1WebSite.CustomPageImpl;
import com.tp.F1WebSite.domain.dto.*;
import com.tp.F1WebSite.dto.race.QualifyingCreationDto;
import com.tp.F1WebSite.dto.race.RaceCircuitDto;
import com.tp.F1WebSite.dto.race.RaceCreationDto;
import com.tp.F1WebSite.dto.race.ResultCreationDto;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
        assertThat(responseBody.getContent()).hasSize(3);
        assertThat(responseBody.getContent()).extracting(
                        RaceCircuitDto::getRaceName,
                        RaceCircuitDto::getCircuitName,
                        RaceCircuitDto::getCountry,
                        RaceCircuitDto::getWinnerName,
                        RaceCircuitDto::getConstructorName
                )
                .containsExactlyInAnyOrder(
                        tuple("GP Poznan", "Tor Poznan", "Poland", "Julian Sokołowski", "ferrari"),
                        tuple("GP Barcelona", "Circuit de Barcelona", "Spain", "Charles Leclerc", "ferrari"),
                        tuple("GP Poznan", "Tor Poznan", "Poland", null, null)
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
    public void shouldReturnNotFoundWhenRaceDoesntExist() {
        ResponseEntity<RaceDto> response = restTemplate
                .getForEntity("/api/races/100",
                        RaceDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
    public void shouldReturnNotFoundWhenRequestingResultsForNonExistingRace() {
        ResponseEntity<ResultDto> response = restTemplate
                .getForEntity("/api/races/100/results",
                        ResultDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void shouldReturnNotFoundWhenRequestingQualifyingForNonExistingRace() {
        ResponseEntity<QualifyingDto> response = restTemplate
                .getForEntity("/api/races/100/qualifying",
                        QualifyingDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // POST
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldCreateRace() {
        RaceCreationDto newRace = RaceCreationDto.builder()
                .name("GP PL")
                .date(LocalDate.of(2025, 12, 12))
                .circuitName("Tor Poznan")
                .build();

        ResponseEntity<RaceDto> response = restTemplate
                .postForEntity("/api/races",
                        newRace,
                        RaceDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        RaceDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getRaceId()).isEqualTo(4L);
        assertThat(responseBody.getName()).isEqualTo(newRace.getName());
        assertThat(responseBody.getDate()).isEqualTo(newRace.getDate());
        assertThat(responseBody.getCircuit()).extracting(
                        CircuitDto::getCircuitId,
                        CircuitDto::getName,
                        CircuitDto::getCountry,
                        CircuitDto::getUrl
                )
                .containsExactlyInAnyOrder(1L, "Tor Poznan", "Poland", "http://poznantor");
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnConflictWhenCreatingRaceThatAlreadyExists() {
        RaceCreationDto newRace = RaceCreationDto.builder()
                .name("GP Poznan")
                .date(LocalDate.of(2025, 10, 12))
                .circuitName("Tor Poznan")
                .build();

        ResponseEntity<RaceDto> response = restTemplate
                .postForEntity("/api/races",
                        newRace,
                        RaceDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenCreatingRaceWithCircuitThatDoesntExist() {
        RaceCreationDto newRace = RaceCreationDto.builder()
                .name("GP PL")
                .date(LocalDate.of(2025, 12, 12))
                .circuitName("Tor Warszawa")
                .build();

        ResponseEntity<RaceDto> response = restTemplate
                .postForEntity("/api/races",
                        newRace,
                        RaceDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldCreateResult() {
        ResultCreationDto newResult = ResultCreationDto.builder()
                .driverUrl("http://franco")
                .constructorName("ferrari")
                .grid(5)
                .position(5)
                .points(10.0)
                .build();

        ResponseEntity<ResultDto> response = restTemplate
                .postForEntity("/api/races/1/results",
                        newResult,
                        ResultDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResultDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getResultId()).isEqualTo(9L);
        assertThat(responseBody.getGrid()).isEqualTo(newResult.getGrid());
        assertThat(responseBody.getPosition()).isEqualTo(newResult.getPosition());
        assertThat(responseBody.getPoints()).isEqualTo(newResult.getPoints());
        assertThat(responseBody.getDriver()).extracting(
                        DriverDto::getDriverId,
                        DriverDto::getName,
                        DriverDto::getNationality,
                        DriverDto::getUrl
                )
                .containsExactlyInAnyOrder(5L, "Franco Colapinto", "Argentina", newResult.getDriverUrl());
        assertThat(responseBody.getConstructor()).extracting(
                        ConstructorDto::getConstructorId,
                        ConstructorDto::getName
                )
                .containsExactlyInAnyOrder(1L, newResult.getConstructorName());

        CircuitDto circuit = CircuitDto.builder()
                        .circuitId(1L)
                        .name("Tor Poznan")
                        .country("Poland")
                        .url("http://poznantor")
                        .build();

        assertThat(responseBody.getRace()).extracting(
                        RaceDto::getRaceId,
                        RaceDto::getName,
                        RaceDto::getDate,
                        RaceDto::getCircuit
                )
                .containsExactlyInAnyOrder(1L, "GP Poznan", LocalDate.of(2025, 10, 12), circuit);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnConflictWhenCreatingResultThatAlreadyExists() {
        ResultCreationDto newResult = ResultCreationDto.builder()
                .driverUrl("http://julsok1")
                .constructorName("ferrari")
                .grid(5)
                .position(5)
                .points(10.0)
                .build();

        ResponseEntity<ResultDto> response = restTemplate
                .postForEntity("/api/races/1/results",
                        newResult,
                        ResultDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenCreatingResultWithDriverThatDoesntExist() {
        ResultCreationDto newResult = ResultCreationDto.builder()
                .driverUrl("http://johnson")
                .constructorName("ferrari")
                .grid(5)
                .position(5)
                .points(10.0)
                .build();

        ResponseEntity<ResultDto> response = restTemplate
                .postForEntity("/api/races/1/results",
                        newResult,
                        ResultDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenCreatingResultWithConstructorThatDoesntExist() {
        ResultCreationDto newResult = ResultCreationDto.builder()
                .driverUrl("http://franco")
                .constructorName("maclaren")
                .grid(5)
                .position(5)
                .points(10.0)
                .build();

        ResponseEntity<ResultDto> response = restTemplate
                .postForEntity("/api/races/1/results",
                        newResult,
                        ResultDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldCreateQualifying() {
        QualifyingCreationDto newQualifying = QualifyingCreationDto.builder()
                .driverUrl("http://franco")
                .constructorName("ferrari")
                .position(5)
                .build();

        ResponseEntity<QualifyingDto> response = restTemplate
                .postForEntity("/api/races/1/qualifying",
                        newQualifying,
                        QualifyingDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        QualifyingDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getQualifyingId()).isEqualTo(9L);
        assertThat(responseBody.getPosition()).isEqualTo(newQualifying.getPosition());
        assertThat(responseBody.getDriver()).extracting(
                        DriverDto::getDriverId,
                        DriverDto::getName,
                        DriverDto::getNationality,
                        DriverDto::getUrl
                )
                .containsExactlyInAnyOrder(5L, "Franco Colapinto", "Argentina", newQualifying.getDriverUrl());
        assertThat(responseBody.getConstructor()).extracting(
                        ConstructorDto::getConstructorId,
                        ConstructorDto::getName
                )
                .containsExactlyInAnyOrder(1L, newQualifying.getConstructorName());

        CircuitDto circuit = CircuitDto.builder()
                .circuitId(1L)
                .name("Tor Poznan")
                .country("Poland")
                .url("http://poznantor")
                .build();

        assertThat(responseBody.getRace()).extracting(
                        RaceDto::getRaceId,
                        RaceDto::getName,
                        RaceDto::getDate,
                        RaceDto::getCircuit
                )
                .containsExactlyInAnyOrder(1L, "GP Poznan", LocalDate.of(2025, 10, 12), circuit);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnConflictWhenCreatingQualifyingThatAlreadyExists() {
        QualifyingCreationDto newQualifying = QualifyingCreationDto.builder()
                .driverUrl("http://julsok1")
                .constructorName("ferrari")
                .position(5)
                .build();

        ResponseEntity<QualifyingDto> response = restTemplate
                .postForEntity("/api/races/1/qualifying",
                        newQualifying,
                        QualifyingDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenCreatingQualifyingWithDriverThatDoesntExist() {
        QualifyingCreationDto newQualifying = QualifyingCreationDto.builder()
                .driverUrl("http://johnson")
                .constructorName("ferrari")
                .position(5)
                .build();

        ResponseEntity<QualifyingDto> response = restTemplate
                .postForEntity("/api/races/1/qualifying",
                        newQualifying,
                        QualifyingDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenCreatingQualifyingWithConstructorThatDoesntExist() {
        QualifyingCreationDto newQualifying = QualifyingCreationDto.builder()
                .driverUrl("http://franco")
                .constructorName("maclaren")
                .position(5)
                .build();

        ResponseEntity<QualifyingDto> response = restTemplate
                .postForEntity("/api/races/1/qualifying",
                        newQualifying,
                        QualifyingDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // DELETE
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldHardDeleteRaceWhenRaceHasNoAnyResultsQualifying() {
        ResponseEntity<String> response = restTemplate.exchange("/api/races/3",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM races WHERE race_id = ?",
                Integer.class,
                3L
        );
        assertThat(count).isEqualTo(0);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldSoftDeleteRaceWhenRaceHasResultsQualifying() {
        ResponseEntity<String> response = restTemplate.exchange("/api/races/1",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM races WHERE race_id = ?",
                Boolean.class, 1L
        );
        assertThat(isDeleted).isTrue();
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingRace() {
        ResponseEntity<String> response = restTemplate.exchange("/api/races/100",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldHardDeleteResult() {
        ResponseEntity<String> response = restTemplate.exchange("/api/races/1/results/1",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM results WHERE result_id = ?",
                Integer.class, 1L
        );
        assertThat(count).isEqualTo(0);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingResult() {
        ResponseEntity<String> response = restTemplate.exchange("/api/1/races/results/100",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldHardDeleteQualifying() {
        ResponseEntity<String> response = restTemplate.exchange("/api/races/1/qualifying/1",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM qualifying WHERE qualify_id = ?",
                Integer.class, 1L
        );
        assertThat(count).isEqualTo(0);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingQualifying() {
        ResponseEntity<String> response = restTemplate.exchange("/api/1/races/results/100",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
