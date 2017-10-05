package net.latipay.ui.test;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import net.latipay.ui.test.common.WebDriverUtils;
import net.latipay.ui.test.domain.TestConstants;

public class test2 {
	

	   @Test
	    public void PaidSuccess() throws Exception {
	        // 获取WebDriver
	        WebDriver chrome = WebDriverUtils.getWebDriver("chrome");
	       try {
	        //login SpotPay success
	            WebDriverUtils.get(chrome, "https://spotpay-staging.latipay.net/login");
	            WebElement email = chrome.findElement(By.id("email"));
	            email.sendKeys(TestConstants.AUTOTEST_AUD02_EMAIL);
	            WebElement password = chrome.findElement(By.id("password"));
	            password.sendKeys("1234567a");
	             WebElement login = chrome.findElement(By.xpath("//button[@type='submit']")); 
	            login.click();
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);

	            
	            //Amount tab setting
	            WebElement amount_tab = chrome.findElements(By.className("cqTnUx")).get(0);
	            Assert.assertEquals("Amount to be paid", amount_tab.getText());
	            WebElement img_1 = chrome.findElements(By.xpath("//img[@alt='amount']")).get(0);
//	            Assert.assertTrue(img_1.isDisplayed());
                Assert.assertEquals("https://spotpay-staging.latipay.net/static/media/icon-amount.e25ffbad.svg", img_1.getAttribute("src"));
	            Select s1=new Select(chrome.findElement(By.id("wallet_id")));
	            s1.selectByVisibleText("CNYPricing_01"); 
	            WebElement amount_cur = chrome.findElements(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/form/div[2]/label/span")).get(0);
	            Assert.assertEquals("Transaction amount in CNY", amount_cur.getText());  
	  
	            WebElement amount = chrome.findElement(By.id("amount"));
	            Assert.assertEquals("Amount", amount.getAttribute("placeholder"));
	            amount.sendKeys("800");  //收800CNY 
	            WebElement refer = chrome.findElement(By.id("merchant_reference"));
	            Assert.assertEquals("Reference", refer.getAttribute("placeholder"));
	            String m_r = "Spotpay_refer_CNYPricing_PayerFee_withRebate";
	            refer.sendKeys(m_r);
	            WebElement next1 = chrome.findElement(By.xpath("//button[@type='submit']")); 
	            Assert.assertEquals("NEXT", next1.getText());
	            next1.click();
	            
	            //payment method selection _ wechat  
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);
	            WebElement payment_tab = chrome.findElements(By.className("cqTnUx")).get(0);
	            Assert.assertEquals("Payment method", payment_tab.getText());
	            WebElement img_2 = chrome.findElements(By.xpath("//img[@alt='method']")).get(0);
//	            Assert.assertTrue(img_2.isDisplayed());
                Assert.assertEquals("https://spotpay-staging.latipay.net/static/media/icon-method.c78d4431.svg", img_2.getAttribute("src"));
	            WebElement wechat = chrome.findElement(By.xpath("xpath=(//button[@type='submit'])[2]")); 
	            Assert.assertEquals("https://spotpay-staging.latipay.net/static/media/icon-wechat.85920dde.svg", wechat.getAttribute("src"));
	            Assert.assertEquals("wechat", wechat.getAttribute("alt"));
	            wechat.click();
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);
	            
	            // QR scan page _wechat	                       
	            WebElement w_qr_tab = chrome.findElements(By.className("cqTnUx")).get(0);
	            Assert.assertEquals("QR code to scan", w_qr_tab.getText());
	            WebElement img_3 = chrome.findElements(By.xpath("//img[@alt='method']")).get(0);
//	            Assert.assertTrue(img_3.isDisplayed());
                Assert.assertEquals("https://spotpay-staging.latipay.net/static/media/icon-qr.1d39e670.svg", img_3.getAttribute("src"));
	            WebElement w_total_amount = chrome.findElements(By.className("dwtRyO")).get(0);
	            Assert.assertEquals("Total amount to be paid", w_total_amount.getText());
	            WebElement w_pay_aud = chrome.findElements(By.className("bDHqcA")).get(0);  
	            Assert.assertEquals("$100.00 AUD", w_pay_aud.getText());
	            
	       }catch (Exception e) {
			// TODO: handle exception
		}
	   }
}
