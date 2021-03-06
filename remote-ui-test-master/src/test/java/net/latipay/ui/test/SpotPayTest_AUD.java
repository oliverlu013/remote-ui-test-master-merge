package net.latipay.ui.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import net.latipay.api.test.domain.DatabaseEnv;
import net.latipay.ui.test.common.DBUtils;
import net.latipay.ui.test.common.WebDriverUtils;
import net.latipay.ui.test.domain.TestConstants;

//NZD 账户（lat_test01@163.com），SpotPay，Margin： MerchantFee， margin默认值
//订单信息： AUDwallet：AUD_01 ， Alipay + Wechat 
//覆盖范围：Spotpay 成功登录->Account tab： 默认选项，默认设置，wallet所有选项不包含disabled wallet -》 
               // 选择微信渠道，检查二维码页面 -》 回退-》 选择Alipay 渠道， 检查二维码页面 -》通过spotpay 页面的transaction history 跳转到Merchant portal end
               // Merchant 端 transaction processing 列表检查 (与数据库信息比对)   -- 未真正支付

//TODO 二维码扫码页面的图片都未检查
//TODO 流程中的图片在回放是显示不出来

public class SpotPayTest_AUD {

	   @Test
	    public void unpaidSpotPayTranxCreation() throws Exception {
	        // 获取WebDriver
	        WebDriver chrome = WebDriverUtils.getWebDriver("chrome");
	       try {
	        //login SpotPay success
	            WebDriverUtils.get(chrome, "https://spotpay-staging.latipay.net/login");
	            WebElement email = chrome.findElement(By.id("email"));
	            email.sendKeys(TestConstants.AUTOTEST_NZD02_EMAIL);
	            WebElement password = chrome.findElement(By.id("password"));
	            password.sendKeys("1234567a");
	            WebElement login = chrome.findElement(By.xpath("//button[@type='submit']")); 
	            login.click();
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);

	            
	            //Amount tab setting -> 检查默认显示信息，检查wallet下拉菜单不包含disabled wallet
	            WebElement amount_tab = chrome.findElements(By.className("cqTnUx")).get(0);
	            Assert.assertEquals("Amount to be paid", amount_tab.getText());
	            WebElement img_1 = chrome.findElements(By.xpath("//img[@alt='amount']")).get(0);
//	            Assert.assertTrue(img_1.isDisplayed());
                Assert.assertEquals("https://spotpay-staging.latipay.net/static/media/icon-amount.e25ffbad.svg", img_1.getAttribute("src"));
	            Select s1=new Select(chrome.findElement(By.id("wallet_id")));
	            Assert.assertEquals("initAccount", s1.getFirstSelectedOption().getText());//检查默认选项检查
	            List<WebElement> allOptions = s1.getOptions();	            
	            for(WebElement option : allOptions)  //遍历列表下面的所有选项, 并判断没有显示disabled的wallet
	            {
	            	System.out.println(option.getText());
	            	Assert.assertNotEquals("DisabledAUD_02", option.getText());	            
	            }
	            s1.selectByVisibleText("AUD_01");  //切换选项
	            WebElement amount_cur = chrome.findElements(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/form/div[2]/label/span")).get(0);
	            Assert.assertEquals("Transaction amount in AUD", amount_cur.getText());  
	  
	            WebElement amount = chrome.findElement(By.id("amount"));
	            Assert.assertEquals("Amount", amount.getAttribute("placeholder"));
	            amount.sendKeys("100");
	            WebElement refer = chrome.findElement(By.id("merchant_reference"));
	            Assert.assertEquals("Reference", refer.getAttribute("placeholder"));
	            String m_r = "Spotpay_reference_AUD_MFee";
	            refer.sendKeys(m_r);
	            WebElement next1 = chrome.findElement(By.xpath("//button[@type='submit']")); 
	            Assert.assertEquals("NEXT", next1.getText());
	            next1.click();
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);

	            
	            //payment method selection _ wechat  
	            WebElement payment_tab = chrome.findElements(By.className("cqTnUx")).get(0);
	            Assert.assertEquals("Payment method", payment_tab.getText());
	            WebElement img_2 = chrome.findElements(By.xpath("//img[@alt='method']")).get(0);
//	            Assert.assertTrue(img_2.isDisplayed());
                Assert.assertEquals("https://spotpay-staging.latipay.net/static/media/icon-method.c78d4431.svg", img_2.getAttribute("src"));
	            WebElement wechat = chrome.findElement(By.xpath("(//button[@type='submit'])[2]")); 
//	            Assert.assertEquals("https://spotpay-staging.latipay.net/static/media/icon-wechat.85920dde.svg", wechat.getAttribute("src"));
	            // 为什么有时候跑出来的结果是null 啊？？？
//	            Assert.assertEquals("wechat", wechat.getAttribute("alt"));
	            wechat.click();
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);
           
	             //从数据库中读取相应的transaction _wechat 信息
	             Map<String, String> pending_w = DBUtils.query(DatabaseEnv.STAGING.name(), "SELECT * FROM `d_transaction_order` where customer_order_id = '" + m_r + "' and payment_method = 'wechat'  ORDER BY create_date DESC limit 1;");             
	             Assert.assertNotNull(pending_w);
	             String transactionId_w = pending_w.get("order_id");
	             String amount_cny = pending_w.get("amountCNY").substring(0, pending_w.get("amountCNY").length()-3);
            
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
                WebElement w_pay_cny = chrome.findElements(By.className("dsFqHj")).get(0); 
	            Assert.assertEquals("CNY\n¥ "+amount_cny, w_pay_cny.getText()); 

	            WebElement w_qr_code = chrome.findElements(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/div[2]/canvas")).get(0);
	            Assert.assertEquals("canvas", w_qr_code.getTagName()); // 这种canvas 类型的二维码到底如何比较判断？ TODO
	            // 或者截取二维码，然后转化成对应的URL如何保存
	            WebElement w_img = chrome.findElements(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/div[2]/div/img")).get(0);
	            System.out.println(w_img.toString());	            //Selenium 中如何判断 利用base64编码的图片数据的对错
	            
	            WebElement pre_s2 = chrome.findElement(By.partialLinkText("Previous Step"));
	            pre_s2.click();    //回退到前一页
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);
	            
	            //重现选择渠道 payment method selection _ Alipay  
	            payment_tab = chrome.findElements(By.className("cqTnUx")).get(0);
	            Assert.assertEquals("Payment method", payment_tab.getText());
	            WebElement alipay = chrome.findElement(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/form/div[1]/button"));
	            alipay.click();
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);
	            
	            //从数据库中读取相应的transaction Alipay 信息
	             Map<String, String> pending_a = DBUtils.query(DatabaseEnv.STAGING.name(), "SELECT * FROM `d_transaction_order` where customer_order_id = '" + m_r + "' and payment_method = 'alipay'  ORDER BY create_date DESC limit 1;");             
	             Assert.assertNotNull(pending_a);
	             String transactionId_a = pending_a.get("order_id");
	             String amount_aud_a = pending_a.get("amount").substring(0, pending_a.get("amount").length()-3);
	             String amount_cny_a = pending_a.get("amountCNY").substring(0, pending_a.get("amountCNY").length()-3);
	             String type = pending_a.get("type");
           
	            // QR scan page _ Alipay
	            WebElement a_qr_tab = chrome.findElements(By.className("cqTnUx")).get(0);
	            Assert.assertEquals("QR code to scan", a_qr_tab.getText());
	            WebElement a_pay_aud = chrome.findElements(By.className("bDHqcA")).get(0); 
	            Assert.assertEquals("$"+amount_aud_a+" AUD", a_pay_aud.getText());
	            WebElement a_pay_cny = chrome.findElements(By.className("dsFqHj")).get(0);  
	            Assert.assertEquals("CNY\n¥ "+amount_cny_a, a_pay_cny.getText()); //获取值是换行显示的 
	            
	           
	           //切换到Merchant端平台， 登陆
	            WebElement tranx_hist = chrome.findElement(By.partialLinkText("Transaction history"));
	            tranx_hist.click();
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);
	            Assert.assertEquals("Latipay Merchant Portal", chrome.getTitle()); // 比较打开页面的Title是否正确
	            WebElement m_email = chrome.findElement(By.id("email"));
	            m_email.sendKeys(TestConstants.AUTOTEST_NZD02_EMAIL);
	            WebElement m_password = chrome.findElement(By.id("password"));
	            m_password.sendKeys("1234567a");
	            WebElement m_login = chrome.findElement(By.className("lat-login-submit"));
	            m_login.click();
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);
	            
	            //展开Processing 列表， 查看产生的新订单 (应该是两条)
	            WebElement show = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[1]/div/a"));
	            show.click();
	            
	            Date date = new Date();
	            WebElement tranx_date = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
	        	SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM",Locale.ENGLISH );
	        	String date_s = sdf.format(date);
	            Assert.assertEquals(date_s, tranx_date.getText());	    
	            
	            WebElement tranx_type = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[2]"));
	            Assert.assertEquals(type, tranx_type.getText());  	            
	            WebElement tranx_id = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[3]"));
	            Assert.assertEquals(transactionId_a, tranx_id.getText());  
	            WebElement tranx_wal = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[4]"));
	            Assert.assertEquals("AUD_01", tranx_wal.getText());  
	            WebElement Tranx_Amount = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[5]"));
	            Assert.assertEquals("$"+amount_aud_a+" AUD", Tranx_Amount.getText());  
	            
	            //检查第二行数据
	            WebElement tranx_date2 = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[1]"));
	            Assert.assertEquals(date_s, tranx_date2.getText());
	            WebElement tranx_type2 = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[2]"));
	            Assert.assertEquals(type, tranx_type2.getText());     
	            WebElement tranx_id2 = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[3]"));
	            Assert.assertEquals(transactionId_w, tranx_id2.getText());  
	            WebElement tranx_wal2 = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[4]"));
	            Assert.assertEquals("AUD_01", tranx_wal2.getText());  
	            WebElement Tranx_Amount2 = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[5]"));
	            Assert.assertEquals("$"+amount_aud_a+" AUD", Tranx_Amount2.getText());  
	            
	    } 
	       catch(Exception e) {
	    	e.printStackTrace();
	    }
	       finally {        
	            WebDriverUtils.returnWebDriver(chrome);
	    }

	    }
}
