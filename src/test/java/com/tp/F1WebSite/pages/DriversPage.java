package com.tp.F1WebSite.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DriversPage {

    private WebDriver driver;

    private WebDriverWait wait;

    @FindBy(id = "search-input")
    private WebElement searchInput;

    @FindBy(css = ".search-btn")
    private WebElement searchButton;

    @FindBy(id = "status-message")
    private WebElement statusMessage;

    @FindBy(xpath = "//tbody[@id='all-drivers-body']/tr")
    private List<WebElement> resultsRows;

    public DriversPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void enterSearchQuery(String driverName) {
        searchInput.clear();
        searchInput.sendKeys(driverName);
    }

    public void clickSearchButton() {
        searchButton.click();
        wait.until(ExpectedConditions.invisibilityOf(statusMessage));
    }

    public Integer getNumberOfResults() {
        return resultsRows.size();
    }

    public String getDriverNameFromFirstRow() {
        return resultsRows.get(0).findElements(By.tagName("td")).get(1).getText();
    }

    public String getWinsFromFirstRow() {
        return resultsRows.get(0).findElements(By.tagName("td")).get(2).getText();
    }
}
