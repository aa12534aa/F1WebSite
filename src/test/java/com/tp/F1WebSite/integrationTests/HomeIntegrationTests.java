package com.tp.F1WebSite.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Sql("/testData/PrepareData.sql")
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
        assertThat(responseBodyDrivers).extracting("name")
                .containsExactlyInAnyOrder("Julian Sokołowski", "Max Verstappen", "Lewis Hamilton", "Charles Leclerc");
        assertThat(responseBodyDrivers).extracting("numOfWins")
                .containsExactlyInAnyOrder(1L, 1L, 0L, 0L);
        assertThat(responseBodyDrivers).extracting("numOfRaces")
                .containsExactlyInAnyOrder(2L, 2L, 2L, 2L);
    }

    @Sql("/testData/PrepareData.sql")
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
        assertThat(responseBodyConstructors).extracting("name")
                .containsExactlyInAnyOrder("ferrari", "Mercedes");
        assertThat(responseBodyConstructors).extracting("numOfWins")
                .containsExactlyInAnyOrder(2L, 0L);
        assertThat(responseBodyConstructors).extracting("numOfRaces")
                .containsExactlyInAnyOrder(4L, 4L);
    }
}
