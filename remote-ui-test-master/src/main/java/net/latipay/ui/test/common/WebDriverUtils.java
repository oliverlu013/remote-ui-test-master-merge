package net.latipay.ui.test.common;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author jasonlu 7:54:42 PM
 */
public class WebDriverUtils {

    public static WebDriver getWebDriver(String browser) {
        WebDriver webDriver = null;
        switch (browser) {
            case "chrome":
                DesiredCapabilities chrome = DesiredCapabilities.chrome();
                ChromeOptions options = new ChromeOptions();
                chrome.setCapability(ChromeOptions.CAPABILITY, options);
                webDriver = new ChromeDriver(chrome);
                break;
            case "ie":
                DesiredCapabilities ie = DesiredCapabilities.internetExplorer();
                webDriver = new InternetExplorerDriver(ie);
                break;
            default:
                throw new RuntimeException("unsupported browser : " + browser);
        }
        webDriver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        webDriver.manage().timeouts().setScriptTimeout(120, TimeUnit.SECONDS);
        return webDriver;
    }

    public static void get(WebDriver webDriver, String url) {
        webDriver.switchTo().window(webDriver.getWindowHandle());
        webDriver.manage().window().maximize();
        webDriver.get(url);
        pageLoadWait(webDriver, 10);
    }

    public static void returnWebDriver(WebDriver driver) {
        driver.quit();
    }

    public static void pageLoadWait(WebDriver driver, int seconds) {
        new WebDriverWait(driver, seconds).until(pageOnReady());
    }

    public static ExpectedCondition<Boolean> pageOnReady() {
        return new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver driver) {
                String state = (String) executeJs(driver, "return document.readyState");
                return "complete".equals(state);
            }
        };
    }

    public static Object executeJs(WebDriver driver, String jsCode) {
        return ((JavascriptExecutor) driver).executeScript(jsCode);
    }

}
