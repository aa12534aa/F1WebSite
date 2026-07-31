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

public class ConstructorsPage {

    private WebDriver driver;

    private WebDriverWait wait;

    @FindBy(xpath = "//input[@placeholder='Search by name...']")
    private WebElement searchInput;

    @FindBy(css = ".search-btn")
    private WebElement searchButton;

    @FindBy(id = "status-message")
    private WebElement statusMessage;

    @FindBy(xpath = "//tbody/tr")
    private List<WebElement> constructors;

    public ConstructorsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void enterSearchQuery(String constructorName) {
        searchInput.clear();
        searchInput.sendKeys(constructorName);
    }

    public void clickSearchButton() {
        searchButton.click();
        wait.until(ExpectedConditions.invisibilityOf(this.statusMessage));
    }

    public Integer getNumOfConstructors() {
        return constructors.size();
    }

    public String getFirstConstructor() {
        return constructors.get(0).findElements(By.tagName("td")).get(1).getText();
    }

    public String getWinsFromFirstRow() {
        return constructors.get(0).findElements(By.tagName("td")).get(2).getText();
    }
}
