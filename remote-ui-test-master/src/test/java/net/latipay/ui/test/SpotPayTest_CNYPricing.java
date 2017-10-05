package net.latipay.ui.test;

import java.text.SimpleDateFormat;
import java.util.Date;
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

//AUD 账户（lat_test02@163.com），SpotPay，Margin： Payer Fee!=0  ; AUD currency with profit sharing
//订单信息： AUD wallet：CNYPricing_01 ， Wechat gateway， 产生SpotPay 和rebate订单
//覆盖范围：SpotPay 成功登录->Account tab： 切换wallet -》 选择微信渠道，检查二维码页面 -》 新窗口模拟支付成功 
           // 返回查看支付成功后的返回页面 -> Merchant 端 transaction History 列表检查 (与数据库信息比对)  

//QR code 页面显示的Pay amount AUD/CNY 回测中没有计算，直接去的数据库中的值进行比对的

//TODO 二维码扫码页面的图片都未检查

public class SpotPayTest_CNYPricing {
	
	

	   @Test
	    public void PaidSuccess() throws Exception {
	        // 获取WebDriver
	        WebDriver chrome = WebDriverUtils.getWebDriver("chrome");
	        WebDriver chrome2 = WebDriverUtils.getWebDriver("chrome");
	        WebDriver chrome3 = WebDriverUtils.getWebDriver("chrome");
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
	            Select s1=new Select(chrome.findElement(By.id("wallet_id")));
	            s1.selectByVisibleText("CNYPricing_01"); 
	            WebElement amount_cur = chrome.findElements(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/form/div[2]/label/span")).get(0);
	            Assert.assertEquals("Transaction amount in CNY", amount_cur.getText());  
	  
	            WebElement amount = chrome.findElement(By.id("amount"));
	            amount.sendKeys("800");  //收800CNY 
	            WebElement refer = chrome.findElement(By.id("merchant_reference"));
	            String m_r = "Spotpay_refer_CNYPricing_PayerFee_withRebate";
	            refer.sendKeys(m_r);
	            WebElement next1 = chrome.findElement(By.className("bpbpHD"));
	            next1.click();
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);
	            
	            //payment method selection _ wechat  
	            WebElement payment_tab = chrome.findElements(By.className("cqTnUx")).get(0);
	            Assert.assertEquals("Payment method", payment_tab.getText());
	            WebElement wechat = chrome.findElement(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/form/div[2]/button"));  // 或者使用xpath定位
	            wechat.click();
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);
	            
	             //从数据库中读取相应的transaction 信息
	             Map<String, String> pending_w = DBUtils.query(DatabaseEnv.STAGING.name(), "SELECT * FROM `d_transaction_order` where customer_order_id = '" + m_r + "' and payment_method = 'wechat'  ORDER BY create_date DESC limit 1;");             
	             Assert.assertNotNull(pending_w);
	             String transactionId_w = pending_w.get("order_id");
	             String amount_aud = pending_w.get("amount").substring(0, pending_w.get("amount").length()-3);
	             String amount_cny = pending_w.get("amountCNY").substring(0, pending_w.get("amountCNY").length()-3);
            
	             
	            // QR scan page _wechat
	            WebElement w_qr_tab = chrome.findElements(By.className("cqTnUx")).get(0);
	            Assert.assertEquals("QR code to scan", w_qr_tab.getText());
	            WebElement w_aud = chrome.findElements(By.className("bDHqcA")).get(0); 
	            Assert.assertEquals("$"+amount_aud+" AUD", w_aud.getText());
	            WebElement w_pay_cny = chrome.findElements(By.className("dsFqHj")).get(0);  
	            Assert.assertEquals("CNY\n¥ "+amount_cny, w_pay_cny.getText());
      
	            	            
	 	           // 通过Latipay test platform 网址， 模拟支付成功
//	 	           WebDriver chrome3 = WebDriverUtils.getWebDriver("chrome");
	               WebDriverUtils.get(chrome3, "http://47.52.29.49:8080/data/");
		            WebElement transaction_id = chrome3.findElement(By.xpath("html/body/div[1]/div[2]/table/tbody/tr[3]/td[3]/table/tbody/tr/td[2]/input"));
		            transaction_id.sendKeys(transactionId_w);
		            WebElement paid_success = chrome3.findElement(By.xpath("html/body/div[1]/div[2]/table/tbody/tr[3]/td[4]/button"));
		            paid_success.click();     
		            WebDriverUtils.pageLoadWait(chrome3, 10);
		            Thread.sleep(10000);
		            WebElement success_check = chrome3.findElement(By.xpath(".//*[@id='result_3']"));
		            Assert.assertEquals("success", success_check.getText());     
		            
		            //验证API扫码页面支付成功后的返回页面是否正确 ——Confirmation page
		            WebDriverUtils.pageLoadWait(chrome, 10);
		            Thread.sleep(10000);
		            Assert.assertEquals("Latipay Spotpay", chrome.getTitle()); // 比较打开页面的Title是否正确
		            WebElement img = chrome.findElements(By.xpath("//img[@alt='confirmation']")).get(0);
//		            WebElement img = chrome.findElements(By.xpath(".//*[@id='root']/div/div[2]/div/div[1]/img")).get(0);
		            Assert.assertTrue(img.isDisplayed());
	                Assert.assertEquals("https://spotpay-staging.latipay.net/static/media/icon-confirm.c9d2af60.svg", img.getAttribute("src"));
      	            Assert.assertEquals("confirmation", img.getAttribute("alt"));
		            WebElement confirm_tab = chrome.findElements(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/h1/span")).get(0);
		            Assert.assertEquals("Confirmation", confirm_tab.getText());
		            WebElement paid_info = chrome.findElements(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/div/p[1]/span")).get(0); 	            
		            Assert.assertEquals("Your payment of $"+amount_aud+" AUD is now completed.", paid_info.getText());
		            WebElement note = chrome.findElements(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/div/p[2]/span")).get(0); 		 
		            Assert.assertEquals("The money will taken out of your account. To see when the recipient will receive your payment, please refer to your AliPay / WechatPay balance.", note.getText());
		            WebElement redo_butt = chrome.findElements(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/button")).get(0); 		            
		            Assert.assertEquals("DO IT AGAIN", redo_butt.getText()); 
		            redo_butt.click();
		            WebDriverUtils.pageLoadWait(chrome, 10);
		            Thread.sleep(10000);
		            //返回到Amount tab 页面
		            WebElement amount_tab2 = chrome.findElements(By.className("cqTnUx")).get(0);
		            Assert.assertEquals("Amount to be paid", amount_tab2.getText());
		            
		             //从数据库中读取相应的transaction 的状态， 并判断其状态是success
		             Map<String, String> succ_w = DBUtils.query(DatabaseEnv.STAGING.name(), "SELECT * FROM `d_transaction_order` where customer_order_id = '" + m_r + "' and type = 'SpotPay' and payment_method = 'wechat'  ORDER BY create_date DESC limit 1;");             
		             System.out.println("order info in DB"+succ_w);
		             String status = succ_w.get("status");
		             Assert.assertEquals("success", status); // 比较spotpay 订单的状态
		             String type = succ_w.get("type");
		             
		             //检查数据库有rebate订单产生
		             Map<String, String> rebate = DBUtils.query(DatabaseEnv.STAGING.name(), "SELECT * FROM `d_transaction_order` where customer_order_id = '" + m_r + "' and type = 'Rebate' and payment_method = 'wechat'  ORDER BY create_date DESC limit 1;");             
		             Assert.assertNotNull(rebate);
		             String type_r =rebate.get("type");
		             String transactionId_r =rebate.get("order_id");
		             String dbamount_r=rebate.get("amount");     
		             
		             
			           //切换到Merchant端平台， 登陆
			            WebDriverUtils.get(chrome2, "https://merchant-staging.latipay.net/login");
			            Assert.assertEquals("Latipay Merchant Portal", chrome2.getTitle()); // 比较打开页面的Title是否正确
			            WebElement m_email = chrome2.findElement(By.id("email"));
			            m_email.sendKeys(TestConstants.AUTOTEST_AUD02_EMAIL);
			            WebElement m_password = chrome2.findElement(By.id("password"));
			            m_password.sendKeys("1234567a");
			            WebElement m_login = chrome2.findElement(By.className("lat-login-submit"));
			            m_login.click();
			            WebDriverUtils.pageLoadWait(chrome2, 10);
			            Thread.sleep(10000);	             
	             
		             //比较rebate订单
		 	            Date date2 = new Date();
		 	            WebElement tranx_date2 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[1]/td[1]"));
		 	        	SimpleDateFormat sdf2 = new SimpleDateFormat("EEE, d MMM",Locale.ENGLISH );
		 	        	String date_s = sdf2.format(date2);
		 	            Assert.assertEquals(date_s, tranx_date2.getText());
		 	            
		 	            WebElement tranx_type2 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[1]/td[2]"));
		 	            Assert.assertEquals(type_r, tranx_type2.getText());  
		 	            
		 	            WebElement tranx_id2 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[1]/td[3]"));
		 	            Assert.assertEquals(transactionId_r, tranx_id2.getText());  
		 	            WebElement tranx_wal2 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[1]/td[4]"));
		 	            Assert.assertEquals("CNYPricing_01", tranx_wal2.getText());  
		 	            WebElement tranx_amount2 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[1]/td[5]"));
		 	            String amount_r = "$"+ dbamount_r.substring(0, dbamount_r.length()-3)+" AUD";
		 	            Assert.assertEquals(amount_r, tranx_amount2.getText());  
		             
			             //比较SpotPay transaction订单
//		 	            Date date3 = new Date();
		 	            WebElement tranx_date3 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[2]/td[1]"));
//		 	        	SimpleDateFormat sdf2 = new SimpleDateFormat("EEE, d MMM",Locale.ENGLISH );
//		 	        	String date_s = sdf.format(date);
		 	            Assert.assertEquals(date_s, tranx_date3.getText());
		 	            
		 	            WebElement tranx_type3 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[2]/td[2]"));
		 	            Assert.assertEquals(type, tranx_type3.getText());  
		 	            
		 	            WebElement tranx_id3 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[2]/td[3]"));
		 	            Assert.assertEquals(transactionId_w, tranx_id3.getText());  
		 	            WebElement tranx_wal3 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[2]/td[4]"));
		 	            Assert.assertEquals("CNYPricing_01", tranx_wal3.getText());  
		 	            WebElement tranx_amount3 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[2]/td[5]"));
		 	            Assert.assertEquals("$"+amount_aud+" AUD", tranx_amount3.getText());  
		 	            
		 	           //展开Processing 列表， 查看产生支付成功的订单已经不再processing 中显示了
		 	            WebElement show = chrome2.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[1]/div/a"));
		 	            show.click();	            
		 	            WebElement tranx_id0 = chrome2.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[3]"));
		 	            //路径不变， 重新定义了参数tranx_id0
		 	            Assert.assertNotEquals(transactionId_w, tranx_id0.getText());  

	            
	    } 
	       catch(Exception e) {
	    	e.printStackTrace();
	    }
	       finally {        
	            WebDriverUtils.returnWebDriver(chrome);
	            WebDriverUtils.returnWebDriver(chrome2);
	            WebDriverUtils.returnWebDriver(chrome3);
	    }

	    }
}
