package tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Listeners;
import com.saucedemo.Pages.LoginPage;
import com.saucedemo.Utils.TestUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners({ScreenshotListener.class})
public class LoginTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(LoginTest.class);

    // =========================
    // 1. Hard Assert – Positive Flow
    // =========================
    @Test(description = "Valid login dengan hard assert, user diarahkan ke halaman inventory")
    public void testValidLoginHardAssert() {
        LoginPage loginPage = new LoginPage(driver);

        // Ambil kredensial dari file properties
        String username = config.getProperty("standardUser", "standard_user");
        String password = config.getProperty("password", "secret_sauce");

        log.info("[HardAssert] START testValidLoginHardAssert");
        log.info("[HardAssert] Step 1 - Login dengan user: {}", username);

        loginPage.login(username, password);

        String currentUrl = driver.getCurrentUrl();
        String title      = driver.getTitle();

        log.info("[HardAssert] Step 2 - Setelah login, URL   : {}", currentUrl);
        log.info("[HardAssert] Step 3 - Setelah login, Title : {}", title);
        log.info("[HardAssert] Step 4 - Eksekusi HARD ASSERT");

        // Assert 1: redirect ke inventory
        Assert.assertTrue(
                currentUrl.contains("inventory.html"),
                "URL seharusnya mengandung 'inventory.html' setelah login sukses."
        );
        log.info("[HardAssert] Assert 1 OK - Redirect ke inventory.html");

        // Assert 2: title sesuai
        Assert.assertEquals(
                title,
                "Swag Labs",
                "Title halaman setelah login sukses seharusnya 'Swag Labs'."
        );
        log.info("[HardAssert] Assert 2 OK - Title halaman sesuai 'Swag Labs'");

        log.info("[HardAssert] END testValidLoginHardAssert (semua assert PASS)");
    }

    // =========================
    // 2. Soft Assert – Negative Flow (locked_out_user)
    // =========================
    @Test(description = "Invalid login locked_out_user dengan soft assert.")
    public void testLockedUserLoginSoftAssert() {
        log.info("[SoftAssert] START testLockedUserLoginSoftAssert");

        SoftAssert softAssert = new SoftAssert();
        LoginPage loginPage = new LoginPage(driver);

        String lockedUser = config.getProperty("lockedUser", "locked_out_user");
        String password   = config.getProperty("password", "secret_sauce");
        String expectedLockedMessage = config.getProperty(
                "lockedOutMessage",
                "Sorry, this user has been locked out."
        );

        log.info("[SoftAssert] Step 1 - Login dengan locked user: {}", lockedUser);
        loginPage.login(lockedUser, password);

        String currentUrl   = driver.getCurrentUrl();
        String errorMessage = loginPage.getFailedLoginErrorMessage();

        log.info("[SoftAssert] Step 2 - URL setelah login gagal : {}", currentUrl);
        log.info("[SoftAssert] Step 3 - Error message (actual) : {}", errorMessage);
        log.info("[SoftAssert] Step 4 - Mulai SOFT ASSERT");

        // 1. Tetap di domain saucedemo
        softAssert.assertTrue(
                currentUrl.contains("saucedemo.com"),
                "User seharusnya tetap di halaman saucedemo setelah login gagal."
        );
        log.info("[SoftAssert] Soft Assert 1 OK - User masih di situs saucedemo");

        // 2. Pesan error sesuai expectation
        softAssert.assertTrue(
                loginPage.isErrorMessageDisplayed(expectedLockedMessage),
                "Pesan error tidak sesuai. Expected mengandung: " + expectedLockedMessage
        );
        log.info("[SoftAssert] Soft Assert 2 OK - Pesan error mengandung teks yang diharapkan");

        // 3. Panjang message cukup informatif
        softAssert.assertTrue(
                errorMessage.length() > 10,
                "Panjang pesan error seharusnya lebih dari 10 karakter."
        );
        log.info("[SoftAssert] Soft Assert 3 OK - Panjang pesan error > 10 karakter");

        log.info("[SoftAssert] Step 5 - softAssert.assertAll()");
        softAssert.assertAll();

        log.info("[SoftAssert] END testLockedUserLoginSoftAssert (semua soft assert PASS)");
    }

    // =========================
    // 3. Data Driven Test (Excel)
    // =========================
    @DataProvider(name = "loginCredentials")
    public Object[][] loginCredentials() {
        log.info("[DataProvider] Load data login dari Excel: Data/login-data-test.xlsx (sheet: login-tests)");
        return TestUtils.getTestData(
                "src/test/resources/Data/login-data-test.xlsx",
                "login-tests"
        );
    }

    @Test(
            dataProvider = "loginCredentials",
            description = "Data driven login test menggunakan kombinasi username, password, expectedType, dan expectedMessage."
    )
    public void testLoginDataDriven(
            String username,
            String password,
            String expectedType,
            String expectedMessage
    ) {
        log.info("[DataDriven] === START Case ===");
        log.info("[DataDriven] Input data: username='{}', password='{}', expectedType='{}', expectedMessage='{}'",
                username, password, expectedType, expectedMessage);

        LoginPage loginPage = new LoginPage(driver);
        log.info("[DataDriven] Step 1 - Eksekusi login()");
        loginPage.login(username, password);

        String currentUrl = driver.getCurrentUrl();
        String title      = driver.getTitle();

        log.info("[DataDriven] Step 2 - URL   setelah login : {}", currentUrl);
        log.info("[DataDriven] Step 3 - Title setelah login : {}", title);

        // Tentukan deskripsi skenario untuk log END
        String scenarioDescription;

        switch (expectedType.toLowerCase()) {
            case "success" -> {
                log.info("[DataDriven] Expectation: SUCCESS (harus masuk ke inventory)");

                Assert.assertTrue(
                        currentUrl.contains("inventory.html"),
                        "Expected SUCCESS, tapi tidak redirect ke inventory. Data: " + username + "/" + password
                );
                log.info("[DataDriven] Assert SUCCESS 1 OK - Redirect ke inventory.html");

                Assert.assertEquals(
                        title,
                        "Swag Labs",
                        "Title seharusnya 'Swag Labs' untuk login sukses."
                );
                log.info("[DataDriven] Assert SUCCESS 2 OK - Title halaman 'Swag Labs'");

                scenarioDescription = "success login (username & password valid)";
            }
            case "locked" -> {
                log.info("[DataDriven] Expectation: FAILED case (locked)");

                Assert.assertTrue(
                        loginPage.isErrorMessageDisplayed(expectedMessage),
                        "Pesan error tidak sesuai. Expected: " + expectedMessage +
                                " | Actual: " + loginPage.getFailedLoginErrorMessage()
                );
                log.info("[DataDriven] Assert FAILED 1 OK - Pesan error sesuai expectation");

                Assert.assertTrue(
                        currentUrl.contains("saucedemo.com"),
                        "Untuk case gagal, user seharusnya tetap di halaman login."
                );
                log.info("[DataDriven] Assert FAILED 2 OK - User tetap di halaman login");

                scenarioDescription = "locked user (" + username + ")";
            }
            case "invalid" -> {
                log.info("[DataDriven] Expectation: FAILED case (invalid)");

                Assert.assertTrue(
                        loginPage.isErrorMessageDisplayed(expectedMessage),
                        "Pesan error tidak sesuai. Expected: " + expectedMessage +
                                " | Actual: " + loginPage.getFailedLoginErrorMessage()
                );
                log.info("[DataDriven] Assert FAILED 1 OK - Pesan error sesuai expectation");

                Assert.assertTrue(
                        currentUrl.contains("saucedemo.com"),
                        "Untuk case gagal, user seharusnya tetap di halaman login."
                );
                log.info("[DataDriven] Assert FAILED 2 OK - User tetap di halaman login");

                // Detail skenario khusus invalid
                if (username.isEmpty() && password.isEmpty()) {
                    scenarioDescription = "invalid - username & password kosong";
                } else if (username.isEmpty()) {
                    scenarioDescription = "invalid - username kosong, password diisi";
                } else if (password.isEmpty()) {
                    scenarioDescription = "invalid - username diisi, password kosong";
                } else if ("wrong_user".equals(username)) {
                    scenarioDescription = "invalid - username salah, password benar";
                } else if ("standard_user".equals(username)) {
                    scenarioDescription = "invalid - username benar, password salah";
                } else {
                    scenarioDescription = "invalid - kombinasi kredensial tidak valid";
                }
            }
            default -> {
                log.error("[DataDriven] expectedType tidak dikenali: {}", expectedType);
                Assert.fail("expectedType tidak dikenali: " + expectedType);
                scenarioDescription = "unknown scenario";
            }
        }

        log.info(
                "[DataDriven] === END Case: {} (username='{}', password='{}', expectedType='{}') ===\n",
                scenarioDescription,
                username,
                password,
                expectedType
        );
    }
}
