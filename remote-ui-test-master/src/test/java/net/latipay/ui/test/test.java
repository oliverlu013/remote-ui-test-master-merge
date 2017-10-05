package net.latipay.ui.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.gargoylesoftware.htmlunit.javascript.host.Set;

import net.latipay.api.test.domain.DatabaseEnv;
import net.latipay.ui.test.common.DBUtils;
import net.latipay.ui.test.common.WebDriverUtils;
import net.latipay.ui.test.domain.TestConstants;

public class test {
	

    @Test
    public void testLoginSuccess() throws Exception {
    	
        WebDriver chrome1 = WebDriverUtils.getWebDriver("chrome");
        WebDriverUtils.get(chrome1, "http://www.baidu.com");
 
        WebDriver chrome2 = WebDriverUtils.getWebDriver("chrome");
        WebDriverUtils.get(chrome2, "http://www.cnblogs.com/lingling99/p/5750168.html");
    
        WebDriver chrome3 = WebDriverUtils.getWebDriver("chrome");
        WebDriverUtils.get(chrome3, "http://47.52.29.49:8080/data/");

        String currentWindow = chrome3.getWindowHandle();
        List<String> it = new ArrayList<String>(chrome3.getWindowHandles()); // 将set集合存入list对象
                
        chrome3.switchTo().window(it.get(0)); 
        Thread.sleep(1000);
         String url1=chrome1.getCurrentUrl(); 
         System.out.println(url1);

         chrome3.switchTo().window(it.get(1)); 
         Thread.sleep(1000);
         String url2=chrome2.getCurrentUrl(); 
         System.out.println(url2);
         
         chrome3.switchTo().window(currentWindow);
         chrome3.switchTo().window(it.get(2));
         Thread.sleep(1000);
         String url3=chrome3.getCurrentUrl(); 
         System.out.println(url3);

         chrome1.close();
         chrome2.close();
         chrome3.close();
    
}
}