package com.tp.F1WebSite.tests.PagesTests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tp.F1WebSite.pages.DriversPage;
import com.tp.F1WebSite.security.JwtGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DriverSearchTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @LocalServerPort
    private int port;

    @Autowired
    private JwtGenerator jwtGenerator;

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.get("http://localhost:" + port);

        String token = generateTestToken("USER");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.localStorage.setItem('token', '" + token + "');");

        driver.navigate().refresh();
        driver.get("http://localhost:" + port + "/drivers");
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void shouldFindDriverByName() {
        DriversPage driversPage = new DriversPage(driver);

        driversPage.enterSearchQuery("hamilton");
        driversPage.clickSearchButton();

        assertEquals(1, driversPage.getNumberOfResults());
        assertEquals("Lewis Hamilton", driversPage.getDriverNameFromFirstRow());
        assertEquals("0", driversPage.getWinsFromFirstRow());
    }

    @Sql(scripts = "/testData/PrepareData.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/testData/CleanData.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void shouldFindTwoDriversByName() {
        DriversPage driversPage = new DriversPage(driver);

        driversPage.enterSearchQuery("m");
        driversPage.clickSearchButton();

        assertEquals(2, driversPage.getNumberOfResults());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private String generateTestToken(String role) {
        var authority = new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role);
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "jan.kowalski@f1.pl", "password", java.util.List.of(authority)
        );
        return jwtGenerator.generateToken(authentication);
    }
}
