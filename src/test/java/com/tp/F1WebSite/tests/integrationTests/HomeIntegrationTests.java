package com.tp.F1WebSite.tests.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.tp.F1WebSite.dto.home.BestDriversConstructors;
import com.tp.F1WebSite.dto.constructor.ConstructorWinsRacesDto;
import com.tp.F1WebSite.dto.driver.DriverWinsRacesDto;
import com.tp.F1WebSite.security.JwtGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
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

    @Autowired
    private JwtGenerator jwtGenerator;

    private HttpEntity<Void> getHeadersForRole(String role) {
        var authority = new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role);
        Authentication authentication;
        if (role.equals("USER")) {
            authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    "jan.kowalski@f1.pl", "password", java.util.List.of(authority)
            );
        } else {
            authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    "employee@f1.pl", "password", java.util.List.of(authority)
            );
        }

        String token = jwtGenerator.generateToken(authentication);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }

    private <T> HttpEntity<T> getPostEntityWithRole(T body, String role) {
        HttpEntity<Void> headersEntity = getHeadersForRole(role);
        return new HttpEntity<>(body, headersEntity.getHeaders());
    }

    // GET
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnBestDriversList() {
        ResponseEntity<BestDriversConstructors> response =
                restTemplate.exchange("/api/home",
                        HttpMethod.GET,
                        getHeadersForRole("USER"),
                        BestDriversConstructors.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        BestDriversConstructors responseBody = response.getBody();

        assertThat(responseBody).isNotNull();
        List<DriverWinsRacesDto> responseBodyDrivers = responseBody.getBestDrivers();

        assertThat(responseBodyDrivers).isNotEmpty();
        assertThat(responseBodyDrivers).hasSize(5);
        assertThat(responseBodyDrivers).extracting(
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
    void shouldReturnBestConstructorsList() {
        ResponseEntity<BestDriversConstructors> response =
                restTemplate.exchange("/api/home",
                        HttpMethod.GET,
                        getHeadersForRole("USER"),
                        BestDriversConstructors.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        BestDriversConstructors responseBody = response.getBody();

        assertThat(responseBody).isNotNull();
        List<ConstructorWinsRacesDto> responseBodyConstructors = responseBody.getBestConstructors();

        assertThat(responseBodyConstructors).isNotEmpty();
        assertThat(responseBodyConstructors).hasSize(3);
        assertThat(responseBodyConstructors).extracting(
                        ConstructorWinsRacesDto::getName,
                        ConstructorWinsRacesDto::getNumOfWins,
                        ConstructorWinsRacesDto::getNumOfRaces
                )
                .containsExactlyInAnyOrder(
                        tuple("ferrari", 2L, 4L),
                        tuple("Mercedes", 0L, 4L),
                        tuple("Mclaren", 0L, 0L)
                );
    }
}
