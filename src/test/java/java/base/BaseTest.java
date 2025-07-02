package java.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;

    public void initializeDriver() {
       // System.setProperty("webdriver.chrome.driver", "/FRAND_Avanue_Project_Staging_Test/Configuration/chromedriver.exe");
    	WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    public void quitDriver() {
        if (driver != null) driver.quit();
    }
}
