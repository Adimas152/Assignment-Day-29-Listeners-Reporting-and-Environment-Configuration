package com.saucedemo.Pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.saucedemo.Core.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Properties;

public class LoginPage extends BasePage {

    private static final Logger log = LogManager.getLogger(LoginPage.class);

    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorAlert;

    public LoginPage(WebDriver driver) {
        super(driver);
    }
//    public void login(String username, String password) {
//        log.info("Logging in with username: {}", username);
//        usernameInput.sendKeys(username);
//        passwordInput.sendKeys(password);
//        loginButton.click();
//    }

    public void login(String username, String password) {
        log.info("Logging in with username: {}", username);
        usernameInput.clear();
        passwordInput.clear();
        if (username != null) {
            usernameInput.sendKeys(username);
        }
        if (password != null) {
            passwordInput.sendKeys(password);
        }
        loginButton.click();
    }

    public String getFailedLoginErrorMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(errorAlert));
        return errorAlert.getText().trim();
    }

//    public boolean isErrorMessageDisplayed(Properties config) {
//        String expectedErrorMessage = config.getProperty("errorMessage");
//        String actualErrorMessage = this.getFailedLoginErrorMessage();
//        return actualErrorMessage.contains(expectedErrorMessage);
//    }

    public boolean isErrorMessageDisplayed(String expectedErrorMessage) {
        String actualErrorMessage = this.getFailedLoginErrorMessage();
        return actualErrorMessage.contains(expectedErrorMessage);
    }
}
