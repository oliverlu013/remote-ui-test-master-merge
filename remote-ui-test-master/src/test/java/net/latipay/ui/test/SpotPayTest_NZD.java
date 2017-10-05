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

//AUD 账户（lat_test02@163.com），SpotPay，Margin： Payer Fee=0 （金额比较小）
//订单信息： NZDwallet：NZD_01 ， Alipay + Wechat 
//覆盖范围：Spotpay 成功登录->Account tab： 切换wallet -》 
             // 选择微信渠道，检查二维码页面 -》 回退-》 选择Alipay 渠道， 检查二维码页面 -》通过spotpay 页面的transaction history 跳转到Merchant portal end
             // Merchant 端 transaction processing 列表检查 (与数据库信息比对)   -- 未真正支付

// TODO 二维码扫码页面的图片都未检查

public class SpotPayTest_NZD {

	   @Test
	    public void testLoginSuccess() throws Exception {
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
	            Select s1=new Select(chrome.findElement(By.id("wallet_id")));
	            s1.selectByVisibleText("NZD_01"); 
	            WebElement amount_cur = chrome.findElements(By.xpath(".//*[@id='root']/div/div[2]/div/div[2]/form/div[2]/label/span")).get(0);
	            Assert.assertEquals("Transaction amount in NZD", amount_cur.getText());  
	  
	            WebElement amount = chrome.findElement(By.id("amount"));
	            amount.sendKeys("0.02");
	            WebElement refer = chrome.findElement(By.id("merchant_reference"));
	            String m_r="Spotpay_reference_NZD_NoPayerFee";
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
	            
	            // QR scan page _wechat
	            WebElement w_qr_tab = chrome.findElements(By.className("cqTnUx")).get(0);
	            Assert.assertEquals("QR code to scan", w_qr_tab.getText());
	            WebElement w_pay_aud = chrome.findElements(By.className("bDHqcA")).get(0);  
	            Assert.assertEquals("$0.02 NZD", w_pay_aud.getText());
	            WebElement w_pay_cny = chrome.findElements(By.className("dsFqHj")).get(0);  // 另外这块如何根据汇率实时计算？ ？？如果是比较大的金额， 汇率计算后会有误差
	            Assert.assertEquals("CNY\n¥ 0.09", w_pay_cny.getText());  //获取值是换行显示的 
	            
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
	            
	            // QR scan page _ Alipay
	            WebElement a_qr_tab = chrome.findElements(By.className("cqTnUx")).get(0);
	            Assert.assertEquals("QR code to scan", a_qr_tab.getText());
	            WebElement a_pay_aud = chrome.findElements(By.className("bDHqcA")).get(0); 
	            Assert.assertEquals("$0.02 NZD", a_pay_aud.getText());
	            WebElement a_pay_cny = chrome.findElements(By.className("dsFqHj")).get(0);  
	            Assert.assertEquals("CNY\n¥ 0.10", a_pay_cny.getText()); //获取值是换行显示的 
	            
	             //从数据库中读取相应的transaction 信息
	             Map<String, String> nzd_pending_w = DBUtils.query(DatabaseEnv.STAGING.name(), "SELECT * FROM `d_transaction_order` where customer_order_id = '" + m_r + "' and payment_method = 'wechat'  ORDER BY create_date DESC limit 1;");             
	             Assert.assertNotNull(nzd_pending_w);
	             String transactionId_w = nzd_pending_w.get("order_id");
	             
	             Map<String, String> nzd_pending_a = DBUtils.query(DatabaseEnv.STAGING.name(), "SELECT * FROM `d_transaction_order` where customer_order_id = '" + m_r + "' and payment_method = 'alipay'  ORDER BY create_date DESC limit 1;");             
	             Assert.assertNotNull(nzd_pending_a);
	             String transactionId_a = nzd_pending_a.get("order_id");
	             String type = nzd_pending_a.get("type");
	           
	           //切换到Merchant端平台， 登陆
	            WebElement tranx_hist = chrome.findElement(By.partialLinkText("Transaction history"));
	            tranx_hist.click();
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);
	            Assert.assertEquals("Latipay Merchant Portal", chrome.getTitle()); // 比较打开页面的Title是否正确
	            WebElement m_email = chrome.findElement(By.id("email"));
	            m_email.sendKeys(TestConstants.AUTOTEST_AUD02_EMAIL);
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
	            Assert.assertEquals("NZD_01", tranx_wal.getText());  
	            WebElement Tranx_Amount = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[5]"));
	            Assert.assertEquals("$0.02 NZD", Tranx_Amount.getText());  
	            
	            //检查第二行数据
	            WebElement tranx_date2 = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[1]"));
	            Assert.assertEquals(date_s, tranx_date2.getText());
	            WebElement tranx_type2 = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[2]"));
	            Assert.assertEquals(type, tranx_type2.getText());      
	            WebElement tranx_id2 = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[3]"));
	            Assert.assertEquals(transactionId_w, tranx_id2.getText());  
	            WebElement tranx_wal2 = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[4]"));
	            Assert.assertEquals("NZD_01", tranx_wal2.getText());  
	            WebElement Tranx_Amount2 = chrome.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[2]/td[5]"));
	            Assert.assertEquals("$0.02 NZD", Tranx_Amount2.getText());  
	            
	    } 
	       catch(Exception e) {
	    	e.printStackTrace();
	    }
	       finally {        
	            WebDriverUtils.returnWebDriver(chrome);
	    }

	    }
}
