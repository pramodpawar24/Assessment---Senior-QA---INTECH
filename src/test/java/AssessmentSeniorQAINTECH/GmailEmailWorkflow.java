package AssessmentSeniorQAINTECH;
//Import statements
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class GmailEmailWorkflow {
 WebDriver driver;
 WebDriverWait wait;

 String[][] testData;

 public static void main(String[] args) throws Exception {
     GmailEmailWorkflow test = new GmailEmailWorkflow();
     test.setup();
     test.readTestData("testdata/EmailData.xlsx");
     test.login(test.testData[0][0], test.testData[0][1]);

     // Send 3 emails
     for (int i = 0; i < 3; i++) {
         test.sendEmail(test.testData[0][0], test.testData[i][2], test.testData[i][3]);
     }

     // Perform actions: reply, delete, forward
     test.replyToEmail(test.testData[0][2], test.testData[0][4]);
     test.deleteEmail(test.testData[1][2]);
     test.forwardEmail(test.testData[2][2], test.testData[2][5]);

     test.logout();
     test.login(test.testData[0][0], test.testData[0][1]);

     // Re-perform actions after login
     test.verifyDeletedEmail(test.testData[1][2]);
     test.forwardEmail(test.testData[2][2], test.testData[2][5]);
     test.replyToEmail(test.testData[0][2], test.testData[0][4]);

     test.logout();
     test.teardown();
 }

 public void setup() {
    WebDriverManager.chromedriver().setup();
     driver = new ChromeDriver();
     driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
    // wait = new WebDriverWait(driver, Duration.ofSeconds(30));
     driver.manage().window().maximize();
 }

 public void readTestData(String filePath) throws Exception {
     FileInputStream fis = new FileInputStream(new File(filePath));
     Workbook wb = new XSSFWorkbook(fis);
     Sheet sheet = wb.getSheetAt(0);
     testData = new String[sheet.getPhysicalNumberOfRows()][6];

     for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
         Row row = sheet.getRow(i);
         for (int j = 0; j < 6; j++) {
             testData[i][j] = row.getCell(j).getStringCellValue();
         }
     }
     wb.close();
 }

 public void login(String email, String password) {
     driver.get("https://mail.google.com/");
     driver.findElement(By.id("identifierId")).sendKeys(email);
     driver.findElement(By.id("identifierNext")).click();
     wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password"))).sendKeys(password);
     driver.findElement(By.id("passwordNext")).click();
     wait.until(ExpectedConditions.titleContains("Inbox"));
 }

 public void sendEmail(String to, String subject, String body) {
     wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".T-I.T-I-KE.L3"))).click();
     wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("to"))).sendKeys(to);
     driver.findElement(By.name("subjectbox")).sendKeys(subject);
     driver.findElement(By.cssSelector("div[aria-label='Message Body']")).sendKeys(body);
     driver.findElement(By.xpath("//div[text()='Send']")).click();
 }

 public void replyToEmail(String subject, String replyMsg) {
     searchEmailBySubject(subject);
     wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Reply']"))).click();
     wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[aria-label='Message Body']"))).sendKeys(replyMsg);
     driver.findElement(By.xpath("//div[text()='Send']")).click();
 }

 public void deleteEmail(String subject) {
     searchEmailBySubject(subject);
     WebElement moreBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@aria-label='More']")));
     moreBtn.click();
     driver.findElement(By.xpath("//div[text()='Delete this message']")).click();
 }

 public void forwardEmail(String subject, String forwardMsg) {
     searchEmailBySubject(subject);
     wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@aria-label='More']"))).click();
     driver.findElement(By.xpath("//div[text()='Forward']")).click();
     WebElement bodyBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[aria-label='Message Body']")));
     bodyBox.sendKeys(forwardMsg);
     driver.findElement(By.xpath("//div[text()='Send']")).click();
 }

 public void verifyDeletedEmail(String subject) {
     driver.findElement(By.name("q")).clear();
     driver.findElement(By.name("q")).sendKeys("in:trash subject:" + subject + Keys.ENTER);
     wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), 'Trash')]")));
     // Permanently delete manually or extend this method
 }

 public void logout() {
     WebElement profile = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[aria-label*='Google Account']")));
     profile.click();
     wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[text()='Sign out']"))).click();
 }

 public void searchEmailBySubject(String subject) {
     WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
     searchBox.clear();
     searchBox.sendKeys("subject:" + subject + Keys.ENTER);
     wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.Cp")));
     wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.Cp .zA"))).click();
 }

 public void teardown() {
     if (driver != null) {
         driver.quit();
     }
 }
} 
