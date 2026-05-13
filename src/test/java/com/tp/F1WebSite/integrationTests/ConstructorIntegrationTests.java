package com.tp.F1WebSite.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.tp.F1WebSite.CustomPageImpl;
import com.tp.F1WebSite.dto.ConstructorAllInfoDto;
import com.tp.F1WebSite.dto.ConstructorRaceDto;
import com.tp.F1WebSite.dto.ConstructorWinsRacesDto;
import com.tp.F1WebSite.repositories.ConstructorRepository;
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

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConstructorIntegrationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ConstructorRepository constructorRepository;

    @Sql("/testData/PrepareData.sql")
    @Test
    void shouldReturnConstructorsPage() {
        ResponseEntity<CustomPageImpl<ConstructorWinsRacesDto>> response =
                restTemplate.exchange("/api/constructors",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<CustomPageImpl<ConstructorWinsRacesDto>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PageImpl<ConstructorWinsRacesDto> responseBody = response.getBody();
        assertThat(responseBody).isNotEmpty();
        assertThat(responseBody.getContent()).hasSize(2);
        assertThat(responseBody.getContent()).extracting("name")
                .containsExactlyInAnyOrder("ferrari", "Mercedes");
        assertThat(responseBody.getContent()).extracting("numOfWins")
                .containsExactlyInAnyOrder(2L, 0L);
        assertThat(responseBody.getContent()).extracting("numOfRaces")
                .containsExactlyInAnyOrder(4L, 4L);
    }

    @Sql("/testData/PrepareData.sql")
    @Test
    void shouldReturnConstructorAllInfo() {
        ResponseEntity<ConstructorAllInfoDto> response =
                restTemplate.getForEntity("/api/constructors/1",
                        ConstructorAllInfoDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ConstructorAllInfoDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getName()).isEqualTo("ferrari");
        assertThat(responseBody.getFirstPlaces()).isEqualTo(2L);
        assertThat(responseBody.getSecondPlaces()).isEqualTo(1L);
        assertThat(responseBody.getThirdPlaces()).isEqualTo(0L);
        assertThat(responseBody.getGainedPoints()).isEqualTo(80.0);
        assertThat(responseBody.getNumOfRaces()).isEqualTo(4L);
    }

    @Sql("/testData/PrepareData.sql")
    @Test
    void shouldReturnConstructorRacesPage() {
        ResponseEntity<CustomPageImpl<ConstructorRaceDto>> response =
                restTemplate.exchange("/api/constructors/1/races",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<CustomPageImpl<ConstructorRaceDto>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PageImpl<ConstructorRaceDto> responseBody = response.getBody();
        assertThat(responseBody).isNotEmpty();
        assertThat(responseBody.getContent()).hasSize(4);
        assertThat(responseBody.getContent()).anySatisfy(race -> {
            assertThat(race.getName()).isEqualTo("ferrari");
            assertThat(race.getDriverName()).isEqualTo("Julian Sokołowski");
            assertThat(race.getGrid()).isEqualTo(1L);
            assertThat(race.getPosition()).isEqualTo(1L);
            assertThat(race.getPoints()).isEqualTo(25.0);
            assertThat(race.getDate()).isEqualTo(LocalDate.of(2025, 10, 12));
            assertThat(race.getRaceName()).isEqualTo("GP Poznan");
            assertThat(race.getCountry()).isEqualTo("Poland");
        });
    }
}
