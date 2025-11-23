package tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.saucedemo.Core.ConfigReader;
import com.saucedemo.Core.DriverManager;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.util.Properties;

public class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);

    // config dari staging.properties / env lain
    protected static Properties config;

    // WebDriver yang dipakai di semua test class yang extend BaseTest
    protected WebDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void loadConfig() {
        // baca env dari -Penv=... (Gradle) atau -Denv=... (VM options), default = staging
        String env = System.getProperty("env");
        env = (env == null || env.isEmpty()) ? "staging" : env;

        config = ConfigReader.loadProperties(env);
        log.info("Loaded config env: {}", env);
    }

    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void setUp(@Optional("chrome") String browser) {
        // inisialisasi driver berdasarkan browser
        DriverManager.initDriver(browser);
        driver = DriverManager.getDriver();
        driver.manage().window().maximize();

        // buka baseUrl dari config (staging.properties)
        String baseUrl = config.getProperty("baseUrl", "https://www.saucedemo.com/");
        log.info("Open baseUrl: {}", baseUrl);
        driver.get(baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            log.info("Clearing session and cookies...");
            try {
                driver.manage().deleteAllCookies();
                // kalau cookie ini tidak ada, tidak apa-apa, Selenium akan ignore
                driver.manage().deleteCookieNamed("session-username");
                log.info("Session cleared successfully");
            } catch (Exception e) {
                log.warn("Error when clearing cookies: {}", e.getMessage());
            }
        }
        DriverManager.quitDriver();
        log.info("Driver quit");
    }

    // opsional: bisa dipanggil manual dari test kalau mau reset session di tengah test
    protected void clearSession() {
        if (driver != null) {
            log.info("Clearing session and cookies manually...");
            driver.manage().deleteAllCookies();
            driver.navigate().refresh();
            log.info("Session cleared and page refreshed");
        }
    }
}
