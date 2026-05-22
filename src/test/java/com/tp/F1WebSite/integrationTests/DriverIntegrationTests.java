package com.tp.F1WebSite.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.tp.F1WebSite.CustomPageImpl;
import com.tp.F1WebSite.domain.dto.DriverDto;
import com.tp.F1WebSite.dto.driver.DriverAllInfoDto;
import com.tp.F1WebSite.dto.driver.DriverCircuitsWins;
import com.tp.F1WebSite.dto.driver.DriverRaceDto;
import com.tp.F1WebSite.dto.driver.DriverWinsRacesDto;
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
public class DriverIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private TestRestTemplate restTemplate;

    // GET
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnDriversPage() {
        ResponseEntity<CustomPageImpl<DriverWinsRacesDto>> response =
                restTemplate.exchange("/api/drivers",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<CustomPageImpl<DriverWinsRacesDto>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PageImpl<DriverWinsRacesDto> restPage = response.getBody();
        assertThat(restPage).isNotEmpty();
        assertThat(restPage.getContent()).hasSize(5);
        assertThat(restPage.getContent()).extracting(
                        DriverWinsRacesDto::getName,
                        DriverWinsRacesDto::getNumOfWins,
                        DriverWinsRacesDto::getNumOfRaces
                )
                .containsExactlyInAnyOrder(
                        tuple("Julian Sokołowski", 1L, 2L),
                        tuple("Max Verstappen", 0L, 2L),
                        tuple("Lewis Hamilton", 0L, 2L),
                        tuple("Charles Leclerc", 1L, 2L),
                        tuple("Franco Colapinto", 0L, 0L)
                );
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnDriverAllInfo() {
        ResponseEntity<DriverAllInfoDto> response =
                restTemplate.getForEntity("/api/drivers/1",
                        DriverAllInfoDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DriverAllInfoDto body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.getName()).isEqualTo("Julian Sokołowski");
        assertThat(body.getNumOfRaces()).isEqualTo(2L);
        assertThat(body.getNumOfPolePosition()).isEqualTo(1L);
        assertThat(body.getFirstPlaces()).isEqualTo(1L);
        assertThat(body.getSecondPlaces()).isEqualTo(1L);
        assertThat(body.getThirdPlaces()).isEqualTo(0L);
        assertThat(body.getGainedPoints()).isEqualTo(43.0);
        assertThat(body.getNationality()).isEqualTo("Poland");
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnDriverBestCircuitsList() {
        ResponseEntity<DriverAllInfoDto> response =
                restTemplate.getForEntity("/api/drivers/1",
                        DriverAllInfoDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DriverAllInfoDto responseBody = response.getBody();

        assertThat(responseBody).isNotNull();

        List<DriverCircuitsWins> responseBodyBestCircuits = responseBody.getBestCircuits();
        assertThat(responseBodyBestCircuits).hasSize(1);

        assertThat(responseBodyBestCircuits.getFirst().getCircuitName()).isEqualTo("Tor Poznan");
        assertThat(responseBodyBestCircuits.getFirst().getCountry()).isEqualTo("Poland");
        assertThat(responseBodyBestCircuits.getFirst().getNumOfWins()).isEqualTo(1L);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenDriverDoesNotExist() {
        ResponseEntity<DriverRaceDto> response =
                restTemplate.getForEntity("/api/drivers/100",
                        DriverRaceDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnDriverRacesPage() {
        ResponseEntity<CustomPageImpl<DriverRaceDto>> response =
                restTemplate.exchange("/api/drivers/1/races",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<CustomPageImpl<DriverRaceDto>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PageImpl<DriverRaceDto> responseBody = response.getBody();
        assertThat(responseBody).isNotEmpty();
        assertThat(responseBody.getContent()).hasSize(2);

        assertThat(responseBody.getContent()).anySatisfy(race -> {
            assertThat(race.getName()).isEqualTo("Julian Sokołowski");
            assertThat(race.getRaceName()).isEqualTo("GP Poznan");
            assertThat(race.getCountry()).isEqualTo("Poland");
            assertThat(race.getDate()).isEqualTo(LocalDate.of(2025, 10, 12));
            assertThat(race.getPosition()).isEqualTo(1);
            assertThat(race.getPoints()).isEqualTo(25.0);
            assertThat(race.getGrid()).isEqualTo(1);
            assertThat(race.getTeam()).isEqualTo("ferrari");
        });
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenRequestingRacesForNonExistingDriver() {
        ResponseEntity<DriverRaceDto> response =
                restTemplate.getForEntity("/api/drivers/100/races",
                        DriverRaceDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // POST
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldCreateDriver() {
        DriverDto newDriver = DriverDto.builder()
                .name("Carlito Sanzito")
                .nationality("Spain")
                .url("carlito.com")
                .build();

        ResponseEntity<DriverDto> response = restTemplate
                .postForEntity("/api/drivers",
                        newDriver,
                        DriverDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        DriverDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getDriverId()).isEqualTo(6L);
        assertThat(responseBody.getName()).isEqualTo(newDriver.getName());
        assertThat(responseBody.getNationality()).isEqualTo(newDriver.getNationality());
        assertThat(responseBody.getUrl()).isEqualTo(newDriver.getUrl());
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnConflictWhenCreatingDriverThatAlreadyExists() {
        DriverDto newDriver = DriverDto.builder()
                .name("Julian Sokołowski")
                .nationality("Poland")
                .url("http://julsok1")
                .build();

        ResponseEntity<DriverDto> response = restTemplate
                .postForEntity("/api/drivers",
                        newDriver,
                        DriverDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}