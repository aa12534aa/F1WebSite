package com.tp.F1WebSite.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.tp.F1WebSite.CustomPageImpl;
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

    @Sql("/testData/PrepareData.sql")
    @Test
    void shouldReturnCircuitsPage() {
        ResponseEntity<CustomPageImpl<CircuitRacesDto>> response =
                restTemplate.exchange("/api/circuits",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<CustomPageImpl<CircuitRacesDto>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PageImpl<CircuitRacesDto> responseBody = response.getBody();
        assertThat(responseBody).isNotEmpty();
        assertThat(responseBody.getContent()).hasSize(2);
        assertThat(responseBody.getContent()).extracting("name")
                .containsExactlyInAnyOrder("Tor Poznan", "Circuit de Barcelona");
        assertThat(responseBody.getContent()).extracting("country")
                .containsExactlyInAnyOrder("Poland", "Spain");
        assertThat(responseBody.getContent()).extracting("numOfRaces")
                .containsExactlyInAnyOrder(1L, 1L);
    }
}
