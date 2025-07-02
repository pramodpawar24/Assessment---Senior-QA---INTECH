package java.tests;

import base.BaseTest;
import utils.ExcelUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.annotations.*;

import java.util.Map;

public class GmailWorkflowTest extends BaseTest {
    Map<String, String> data;

    @BeforeClass
    public void setup() {
        data = ExcelUtil.readTestData("testdata.xlsx");
        initializeDriver();
    }

    @Test
    public void testGmailEmailWorkflow() throws InterruptedException {
        login(data.get("email"), data.get("password"));

        for (int i = 1; i <= 3; i++) {
            composeAndSendEmail(data.get("email"), data.get("subject" + i), data.get("body" + i));
        }

        replyToEmail(data.get("subject1"), data.get("replyMessage"));
        deleteEmail(data.get("subject2"));
        forwardEmail(data.get("subject3"), data.get("forwardMessage"));

        logout();
        login(data.get("email"), data.get("password"));

        // Additional actions like verifying Trash, re-forward, and re-reply
        logout();
    }

    public void login(String email, String password) throws InterruptedException {
        driver.get("https://mail.google.com");
        driver.findElement(By.id("identifierId")).sendKeys(email + Keys.ENTER);
        Thread.sleep(2000);
        driver.findElement(By.name("password")).sendKeys(password + Keys.ENTER);
        Thread.sleep(5000);
    }

    public void logout() throws InterruptedException {
        driver.get("https://mail.google.com/mail/logout");
        Thread.sleep(2000);
    }

    public void composeAndSendEmail(String to, String subject, String body) throws InterruptedException {
        driver.findElement(By.cssSelector(".T-I.T-I-KE.L3")).click(); // Compose button
        Thread.sleep(2000);
        driver.findElement(By.name("to")).sendKeys(to);
        driver.findElement(By.name("subjectbox")).sendKeys(subject);
        driver.findElement(By.cssSelector("div[aria-label='Message Body']")).sendKeys(body);
        driver.findElement(By.xpath("//div[text()='Send']")).click();
        Thread.sleep(3000);
    }

    public void replyToEmail(String subject, String replyText) throws InterruptedException {
        searchEmail(subject);
        driver.findElement(By.xpath("//span[text()='Reply']")).click();
        Thread.sleep(2000);
        driver.findElement(By.cssSelector("div[aria-label='Message Body']")).sendKeys(replyText);
        driver.findElement(By.xpath("//div[text()='Send']")).click();
        Thread.sleep(3000);
    }

    public void forwardEmail(String subject, String forwardText) throws InterruptedException {
        searchEmail(subject);
        driver.findElement(By.xpath("//span[text()='Forward']")).click();
        Thread.sleep(2000);
        driver.findElement(By.name("to")).sendKeys(data.get("email"));
        driver.findElement(By.cssSelector("div[aria-label='Message Body']")).sendKeys(forwardText);
        driver.findElement(By.xpath("//div[text()='Send']")).click();
        Thread.sleep(3000);
    }

    public void deleteEmail(String subject) throws InterruptedException {
        searchEmail(subject);
        driver.findElement(By.xpath("//div[@aria-label='Delete']")).click();
        Thread.sleep(2000);
    }

    public void searchEmail(String subject) throws InterruptedException {
        driver.findElement(By.name("q")).clear();
        driver.findElement(By.name("q")).sendKeys("subject:" + subject + Keys.ENTER);
        Thread.sleep(3000);
        driver.findElement(By.cssSelector(".zA")).click(); // Open first matching email
        Thread.sleep(3000);
    }

    @AfterClass
    public void tearDown() {
        quitDriver();
    }
}
