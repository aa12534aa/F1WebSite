package com.tp.F1WebSite.tests.integrationTests;

import static org.assertj.core.api.Assertions.assertThat;

import com.tp.F1WebSite.domain.dto.CircuitDto;
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

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTests {

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

//    // Authorization
//    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    @Test
//    void shouldReturn401ForUnauthorizedUser() {
//        ResponseEntity<String> response = restTemplate
//                .exchange(
//                    "/api/circuits",
//                    HttpMethod.GET,
//                    null,
//                    String.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
//    }

    // Authentication
    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void shouldReturn403ForUnauthenticatedUser() {
        CircuitDto newCircuit = CircuitDto.builder()
                .name("Hungaroring")
                .country("Hungary")
                .url("http://hungaroring")
                .build();

        ResponseEntity<CircuitDto> response = restTemplate
                .exchange("/api/circuits",
                        HttpMethod.POST,
                        getPostEntityWithRole(newCircuit, "USER"),
                        CircuitDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}