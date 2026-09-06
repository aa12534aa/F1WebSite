package com.tp.F1WebSite.tests.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.tp.F1WebSite.CustomPageImpl;
import com.tp.F1WebSite.domain.dto.ConstructorDto;
import com.tp.F1WebSite.dto.constructor.ConstructorAllInfoDto;
import com.tp.F1WebSite.dto.constructor.ConstructorRaceDto;
import com.tp.F1WebSite.dto.constructor.ConstructorWinsRacesDto;
import com.tp.F1WebSite.security.JwtGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
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
    private JdbcTemplate jdbcTemplate;

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
    void shouldReturnConstructorsPage() {
        ResponseEntity<CustomPageImpl<ConstructorWinsRacesDto>> response =
                restTemplate.exchange("/api/constructors",
                        HttpMethod.GET,
                        getHeadersForRole("USER"),
                        new ParameterizedTypeReference<CustomPageImpl<ConstructorWinsRacesDto>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PageImpl<ConstructorWinsRacesDto> responseBody = response.getBody();
        assertThat(responseBody).isNotEmpty();
        assertThat(responseBody.getContent()).hasSize(3);
        assertThat(responseBody.getContent()).extracting(
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

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnConstructorAllInfo() {
        ResponseEntity<ConstructorAllInfoDto> response =
                restTemplate.exchange("/api/constructors/1",
                        HttpMethod.GET,
                        getHeadersForRole("USER"),
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

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenConstructorDoesNotExist() {
        ResponseEntity<ConstructorDto> response =
                restTemplate.exchange("/api/constructors/100",
                        HttpMethod.GET,
                        getHeadersForRole("USER"),
                        ConstructorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnConstructorRacesPage() {
        ResponseEntity<CustomPageImpl<ConstructorRaceDto>> response =
                restTemplate.exchange("/api/constructors/1/races",
                        HttpMethod.GET,
                        getHeadersForRole("USER"),
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

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenRequestingRacesForNonExistingConstructor() {
        ResponseEntity<ConstructorDto> response =
                restTemplate.exchange("/api/constructors/100/races",
                        HttpMethod.GET,
                        getHeadersForRole("USER"),
                        ConstructorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // POST
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldCreateConstructor() {
        ConstructorDto newConstructor = ConstructorDto.builder()
                .name("BMW")
                .build();

        ResponseEntity<ConstructorDto> response = restTemplate
                .exchange("/api/constructors",
                        HttpMethod.POST,
                        getPostEntityWithRole(newConstructor, "EMPLOYEE"),
                        ConstructorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ConstructorDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getConstructorId()).isEqualTo(4L);
        assertThat(responseBody.getName()).isEqualTo(newConstructor.getName());
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnConflictWhenCreatingConstructorThatAlreadyExists() {
        ConstructorDto newConstructor = ConstructorDto.builder()
                .name("ferrari")
                .build();

        ResponseEntity<ConstructorDto> response = restTemplate
                .exchange("/api/constructors",
                        HttpMethod.POST,
                        getPostEntityWithRole(newConstructor, "EMPLOYEE"),
                        ConstructorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnBadRequestWhenCreatingConstructorWithBlankName() {
        ConstructorDto newConstructor = ConstructorDto.builder()
                .name("   ")
                .build();

        ResponseEntity<String> response = restTemplate
                .exchange("/api/constructors",
                        HttpMethod.POST,
                        getPostEntityWithRole(newConstructor, "EMPLOYEE"),
                        String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // DELETE
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldHardDeleteConstructorWhenConstructorHasNoAnyResultsQualifying() {
        ResponseEntity<String> response = restTemplate.exchange("/api/constructors/3",
                HttpMethod.DELETE,
                getHeadersForRole("EMPLOYEE"),
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM constructors WHERE constructor_id = ?",
                Integer.class,
                3L
        );
        assertThat(count).isEqualTo(0);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldSoftDeleteConstructorWhenConstructorHasResultsQualifying() {
        ResponseEntity<String> response = restTemplate.exchange("/api/constructors/1",
                HttpMethod.DELETE,
                getHeadersForRole("EMPLOYEE"),
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Boolean isDeleted = jdbcTemplate.queryForObject(
                "SELECT is_deleted FROM constructors WHERE constructor_id = ?",
                Boolean.class, 1L
        );
        assertThat(isDeleted).isTrue();
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingConstructor() {
        ResponseEntity<String> response = restTemplate.exchange("/api/constructors/100",
                HttpMethod.DELETE,
                getHeadersForRole("EMPLOYEE"),
                new ParameterizedTypeReference<String>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // PUT
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldUpdateConstructor() {
        ConstructorDto updatedConstructor = ConstructorDto.builder()
                .name("Alpine")
                .build();

        ResponseEntity<ConstructorDto> response = restTemplate.exchange("/api/constructors/1",
                HttpMethod.PUT,
                getPostEntityWithRole(updatedConstructor, "EMPLOYEE"),
                ConstructorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ConstructorDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getConstructorId()).isEqualTo(1L);
        assertThat(responseBody.getName()).isEqualTo(updatedConstructor.getName());
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnNotFoundWhenUpdatingNotExistingConstructor() {
        ConstructorDto updatedConstructor = ConstructorDto.builder()
                .name("Alpine")
                .build();

        ResponseEntity<ConstructorDto> response = restTemplate.exchange("/api/constructors/100",
                HttpMethod.PUT,
                getPostEntityWithRole(updatedConstructor, "EMPLOYEE"),
                ConstructorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturnConflictWhenUpdatingConstructorNameEqualToSomeoneElse() {
        ConstructorDto updatedConstructor = ConstructorDto.builder()
                .name("Mercedes")
                .build();

        ResponseEntity<ConstructorDto> response = restTemplate.exchange("/api/constructors/1",
                HttpMethod.PUT,
                getPostEntityWithRole(updatedConstructor, "EMPLOYEE"),
                ConstructorDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
