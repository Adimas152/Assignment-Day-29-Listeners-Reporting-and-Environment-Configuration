
package tests;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ScreenshotListener extends TestListenerAdapter {

    @Override
    public void onTestFailure(ITestResult result) {
        Object testInstance = result.getInstance();

        try {
            WebDriver driver = null;

            if (testInstance instanceof BaseTest) {
                driver = ((BaseTest) testInstance).driver;
            }

            if (driver != null) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String fileName = "screenshots/" + result.getName() + "_" + System.currentTimeMillis() + ".png";

                Files.createDirectories(Paths.get("screenshots"));
                Files.write(Paths.get(fileName), screenshot);
            }
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot for test: " + result.getName());
        }
    }
}
