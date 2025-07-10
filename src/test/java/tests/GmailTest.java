package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.ExcelUtil;

import java.util.Map;

public class GmailTest {
    static WebDriver driver;
    static Map<String, String> data;

    public static void main(String[] args) throws InterruptedException {
        // Load test data from Excel
        data = ExcelUtil.readTestData("testdata.xlsx");

        // Launch browser
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        // Login to Gmail
        login();

        // Send 3 emails to self
        for (int i = 1; i <= 3; i++) {
            sendEmail(data.get("subject" + i), data.get("body" + i));
        }

        // Perform reply, delete, forward actions
        replyToEmail(data.get("subject1"), data.get("replyMessage"));
        deleteEmail(data.get("subject2"));
        forwardEmail(data.get("subject3"), data.get("forwardMessage"));

        // Logout and re-login
        logout();
        login();

        // Verify and perform additional actions
        deleteFromTrash(data.get("subject2"));
        forwardEmail(data.get("subject3"), data.get("forwardMessage"));
        replyToEmail(data.get("subject1"), data.get("replyMessage"));

        // Final logout
        logout();
        driver.quit();
    }

    public static void login() throws InterruptedException {
        driver.get("https://mail.google.com");
        driver.findElement(By.id("identifierId")).sendKeys(data.get("email"), Keys.ENTER);
        Thread.sleep(2000);
        driver.findElement(By.name("password")).sendKeys(data.get("password"), Keys.ENTER);
        Thread.sleep(5000);
    }

    public static void logout() throws InterruptedException {
        driver.get("https://mail.google.com/mail/logout");
        Thread.sleep(3000);
    }

    public static void sendEmail(String subject, String body) throws InterruptedException {
        driver.findElement(By.cssSelector(".T-I.T-I-KE.L3")).click();
        Thread.sleep(2000);
        driver.findElement(By.name("to")).sendKeys(data.get("email"));
        driver.findElement(By.name("subjectbox")).sendKeys(subject);
        driver.findElement(By.cssSelector("div[aria-label='Message Body']")).sendKeys(body);
        driver.findElement(By.xpath("//div[text()='Send']")).click();
        Thread.sleep(3000);
    }

    public static void searchAndOpenEmail(String subject) throws InterruptedException {
        WebElement searchBox = driver.findElement(By.name("q"));
        searchBox.clear();
        searchBox.sendKeys("subject:" + subject + Keys.ENTER);
        Thread.sleep(3000);
        driver.findElement(By.cssSelector(".zA")).click();
        Thread.sleep(3000);
    }

    public static void replyToEmail(String subject, String replyText) throws InterruptedException {
        searchAndOpenEmail(subject);
        driver.findElement(By.xpath("//span[text()='Reply']")).click();
        Thread.sleep(2000);
        driver.findElement(By.cssSelector("div[aria-label='Message Body']")).sendKeys(replyText);
        driver.findElement(By.xpath("//div[text()='Send']")).click();
        Thread.sleep(3000);
    }

    public static void forwardEmail(String subject, String forwardText) throws InterruptedException {
        searchAndOpenEmail(subject);
        driver.findElement(By.xpath("//span[text()='Forward']")).click();
        Thread.sleep(2000);
        driver.findElement(By.name("to")).sendKeys(data.get("email"));
        driver.findElement(By.cssSelector("div[aria-label='Message Body']")).sendKeys(forwardText);
        driver.findElement(By.xpath("//div[text()='Send']")).click();
        Thread.sleep(3000);
    }

    public static void deleteEmail(String subject) throws InterruptedException {
        searchAndOpenEmail(subject);
        driver.findElement(By.xpath("//div[@aria-label='Delete']")).click();
        Thread.sleep(2000);
    }

    public static void deleteFromTrash(String subject) throws InterruptedException {
        driver.get("https://mail.google.com/mail/u/0/#trash");
        Thread.sleep(3000);
        searchAndOpenEmail(subject);
        driver.findElement(By.xpath("//div[@aria-label='Delete forever']")).click();
        Thread.sleep(2000);
    }
}
