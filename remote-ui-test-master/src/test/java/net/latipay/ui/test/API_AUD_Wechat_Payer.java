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

import net.latipay.api.test.domain.DatabaseEnv;
import net.latipay.ui.test.common.DBUtils;
import net.latipay.ui.test.common.RandomUtils;
import net.latipay.ui.test.common.WebDriverUtils;
import net.latipay.ui.test.domain.TestConstants;

//账户信息： AUD账户（lat_test02@163.com）, 个人， margin： Payer fee（默认值）， AUD币种的对应Profit Sharing值 
//订单信息：AUD currency， initAccount钱包
//覆盖范围： Wechat渠道API订单创建 -> 二维码页面显示检查-> Merchant 端 transaction processing 列表检查 (与数据库信息比对)
           //    -> 模拟支付成功， 检查二维码页面的返回页面 -> Merchant端 transaction history 列表页面增加两条记录 Online+Rebate (与数据库信息比对)
           //    -> Merchant端 transaction Processing 列表原来的Online type的记录不在显示了
public class API_AUD_Wechat_Payer {
	
	 @Test
	    public void apiCreate() throws Exception {
		 

	        // 获取WebDriver
	        WebDriver chrome = WebDriverUtils.getWebDriver("chrome");
	        WebDriver chrome2 = WebDriverUtils.getWebDriver("chrome");
	        WebDriver chrome3 = WebDriverUtils.getWebDriver("chrome");
	        //Payme
			 try {
	        //Payment method selection and api parameter configuration
	            WebDriverUtils.get(chrome, "https://pay-staging.latipay.net/api-console");
	             WebElement gw_wechat = chrome.findElement(By.xpath(".//*[@id='__next']/div/div/div[1]/div/row/button[1]")); 
	             gw_wechat.click();
	             WebDriverUtils.pageLoadWait(chrome, 10);
	             Thread.sleep(10000);
	             WebElement payment_method = chrome.findElements(By.name("payment_method")).get(0);
	             Assert.assertEquals("wechat", payment_method.getAttribute("value"));
	             
	             // configuration setting
	             WebElement api_key = chrome.findElements(By.name("api_key")).get(0);
	             api_key.clear();
	             api_key.sendKeys(TestConstants.AUTOTEST_AUD02_KEY);
	             WebElement wallet_id = chrome.findElements(By.name("wallet_id")).get(0);
	             wallet_id.clear();
	             wallet_id.sendKeys(TestConstants.AUTOTEST_AUD02_WID_initAccount);
	             WebElement amount = chrome.findElements(By.name("amount")).get(0);
	             amount.clear();
	             amount.sendKeys("100");
	             WebElement user_id = chrome.findElements(By.name("user_id")).get(0);
	             user_id.clear();
	             user_id.sendKeys(TestConstants.AUTOTEST_AUD02_UID);
	             WebElement merchant_reference = chrome.findElements(By.name("merchant_reference")).get(0);
	             merchant_reference.clear();
	             String m_r = RandomUtils.randomString(5) + RandomUtils.randomNum(5) + RandomUtils.randomString(5) + RandomUtils.randomStringNum(5); 
	             System.out.println(m_r);
	             merchant_reference.sendKeys(m_r);
	             WebElement return_url = chrome.findElements(By.name("return_url")).get(0);
	             return_url.clear();
	             return_url.sendKeys("https://merchant-staging.latipay.net/login");
	             
	             WebElement product_name = chrome.findElements(By.name("product_name")).get(0);
	             product_name.clear();
	             product_name.sendKeys("Product: API_AUD_initAccount_Wechat_Payerfee");
           
	             
	             WebElement submit = chrome.findElement(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/div[1]/form/button")); 
	             submit.click();
	             WebDriverUtils.pageLoadWait(chrome, 10);
	             Thread.sleep(10000);
	             
            
	             //QR code 页面扫码检查
	             WebElement wallet_name = chrome.findElements(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/h5")).get(0);
                 Assert.assertEquals("initAccount", wallet_name.getText());
	 
	             WebElement pay_amount = chrome.findElements(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/h2")).get(0);
	             // 这里需要读取trader端该商户Margin设置， 然后在计算？？ 接口中这部分是否已经cover？
	             //另外如果是从数据库中取值，也是只有金额， 缺少货币符号等其他信息, 是否需要拼接检查
                 Assert.assertEquals("$ 102.50AUD", pay_amount.getText());
                 
	             WebElement massage = chrome.findElements(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/sup")).get(0);
	             // 这里需要读取trader端该商户Margin设置？？ 接口中这部分是否已经cover？
                 Assert.assertEquals("Latipay将收取2.50%的手续费", massage.getText());
                 
	             WebElement QRCode = chrome.findElements(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/div[2]/div/canvas")).get(0);
                 Assert.assertNotNull(QRCode);
                 // ???: 比较二维码信息， 目前的二维码没有看到对应的image URL
                 
	             WebElement note = chrome.findElements(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/div[2]/p")).get(0);
                 Assert.assertEquals("请使用手机微信扫描二维码，截图无效", note.getText());
                                                   
                 //检查订单信息的table
	             WebElement product_info = chrome.findElements(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/div[4]/div/table/tbody/tr[1]/td[1]")).get(0);
                 Assert.assertEquals("产品信息", product_info.getText());                 
	             WebElement product_value = chrome.findElements(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/div[4]/div/table/tbody/tr[1]/td[2]")).get(0);
                 Assert.assertEquals("Product: API_AUD_initAccount_Wechat_Payerfee", product_value.getText());
                 
	             WebElement order_info = chrome.findElements(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/div[4]/div/table/tbody/tr[2]/td[1]")).get(0);
                 Assert.assertEquals("订单编号", order_info.getText());     
	             WebElement order_value = chrome.findElements(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/div[4]/div/table/tbody/tr[2]/td[2]")).get(0);
                 Assert.assertEquals(m_r, order_value.getText());
                 
	             WebElement rate_info = chrome.findElements(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/div[4]/div/table/tbody/tr[3]/td[1]")).get(0);
                 Assert.assertEquals("实时汇率", rate_info.getText());   
	             WebElement rate_value = chrome.findElements(By.xpath(".//*[@id='__next']/div/div/div[1]/div/div/div[4]/div/table/tbody/tr[3]/td[2]")).get(0);
//	             System.out.println(rate_value.getText());
	             //数据库查询实时汇率， 然后和显示的汇率进行比较
	 	        Map<String, String> rate = DBUtils.query(DatabaseEnv.STAGING.name(),
		        		"SELECT * FROM `d_fx_rate` where fx_rate_name = 'AUDCNY' and source_type = 'wechat' and sub_type = 'online'");
	 	        String rate_string = rate.get("rate").substring(0, 7);
//		        System.out.println(rate_string);	             
                 Assert.assertEquals(rate_string, rate_value.getText());
                 
	             //从数据库中读取相应的transaction 信息
                 System.out .println(m_r);
	             Map<String, String> order = DBUtils.query(DatabaseEnv.STAGING.name(),
	            		 "SELECT * FROM `d_transaction_order` where customer_order_id = '"+m_r+"';");             
	             System.out.println(order);
	             String transactionId = order.get("order_id");
	             System.out.println(transactionId);            
	             String type = order.get("type");
	             System.out.println(type);

  	           //切换到Merchant端平台， 登陆
 //               WebDriver chrome2 = WebDriverUtils.getWebDriver("chrome");
                WebDriverUtils.get(chrome2, "https://merchant-staging.latipay.net/login");
 	            WebElement m_email = chrome2.findElement(By.id("email"));
 	            m_email.sendKeys(TestConstants.AUTOTEST_AUD02_EMAIL);
 	            WebElement m_password = chrome2.findElement(By.id("password"));
 	            m_password.sendKeys("1234567a");
 	            WebElement m_login = chrome2.findElement(By.className("lat-login-submit"));
 	            m_login.click();
 	            WebDriverUtils.pageLoadWait(chrome2, 10);
 	            Thread.sleep(10000);
 	            
 	            //展开Processing 列表， 查看产生的新订单 , 并于数据库中读取的值作比较
 	            WebElement show = chrome2.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[1]/div/a"));
 	            show.click();
 	            
 	            Date date = new Date();
 	            WebElement tranx_date = chrome2.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[1]"));
 	        	SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM",Locale.ENGLISH );
 	        	String date_s = sdf.format(date);
 	            Assert.assertEquals(date_s, tranx_date.getText());
 	            
 	            WebElement tranx_type = chrome2.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[2]"));
 	            Assert.assertEquals(type, tranx_type.getText());  
 	            
 	            WebElement tranx_id = chrome2.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[3]"));
 	            Assert.assertEquals(transactionId, tranx_id.getText());  
 	            WebElement tranx_wal = chrome2.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[4]"));
 	            Assert.assertEquals("initAccount", tranx_wal.getText());  
 	            WebElement tranx_amount = chrome2.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[5]"));
 	            Assert.assertEquals("$100.00 AUD", tranx_amount.getText());  
 	            
 	           // 通过Latipay test platform 网址， 模拟支付成功
// 	           WebDriver chrome3 = WebDriverUtils.getWebDriver("chrome");
               WebDriverUtils.get(chrome3, "http://47.52.29.49:8080/data/");
	            WebElement transaction_id = chrome3.findElement(By.xpath("html/body/div[1]/div[2]/table/tbody/tr[3]/td[3]/table/tbody/tr/td[2]/input"));
	            transaction_id.sendKeys(transactionId);
	            WebElement paid_success = chrome3.findElement(By.xpath("html/body/div[1]/div[2]/table/tbody/tr[3]/td[4]/button"));
	            paid_success.click();     
	            WebDriverUtils.pageLoadWait(chrome3, 10);
	            Thread.sleep(10000);
	            WebElement success_check = chrome3.findElement(By.xpath(".//*[@id='result_3']"));
	            Assert.assertEquals("success", success_check.getText());      
	            
	            //验证API扫码页面支付成功后的返回页面是否正确
	            WebDriverUtils.pageLoadWait(chrome, 10);
	            Thread.sleep(10000);
	            Assert.assertEquals("Latipay Merchant Portal", chrome.getTitle()); // 比较打开页面的Title是否正确
	            
	             //从数据库中读取相应的transaction 的状态， 并判断其状态是success
	             Map<String, String> apiorder_success = DBUtils.query(DatabaseEnv.STAGING.name(), "SELECT * FROM `d_transaction_order` where customer_order_id = '" + m_r + "' and type = 'Online';");             
	             Assert.assertNotNull(apiorder_success);
	             String status = apiorder_success.get("status");
	             Assert.assertEquals("success", status); // 比较API 订单的状态
	             
	             //检查数据库有rebate订单产生
	             Map<String, String> rebate = DBUtils.query(DatabaseEnv.STAGING.name(), "SELECT * FROM `d_transaction_order` where customer_order_id = '" + m_r + "' and type = 'Rebate';");             
	             Assert.assertNotNull(rebate);
	             String type_r =rebate.get("type");
	             String transactionId_r =rebate.get("order_id");
	             String dbamount_r=rebate.get("amount");                        
	             
	             //比较Merchant 端-> transaction History 列表更新
	             chrome2.navigate().refresh();
	             
	             //比较rebate订单
//	 	            Date date2 = new Date();
	 	            WebElement tranx_date2 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[1]/td[1]"));
//	 	        	SimpleDateFormat sdf2 = new SimpleDateFormat("EEE, d MMM",Locale.ENGLISH );
//	 	        	String date_s = sdf.format(date);
	 	            Assert.assertEquals(date_s, tranx_date2.getText());
	 	            
	 	            WebElement tranx_type2 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[1]/td[2]"));
	 	            Assert.assertEquals(type_r, tranx_type2.getText());  
	 	            
	 	            WebElement tranx_id2 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[1]/td[3]"));
	 	            Assert.assertEquals(transactionId_r, tranx_id2.getText());  
	 	            WebElement tranx_wal2 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[1]/td[4]"));
	 	            Assert.assertEquals("initAccount", tranx_wal2.getText());  
	 	            WebElement tranx_amount2 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[1]/td[5]"));
	 	            String amount_r = "$"+ dbamount_r.substring(0, dbamount_r.length()-3)+" AUD";
	 	            System.out.println(amount_r);
	 	            Assert.assertEquals(amount_r, tranx_amount2.getText());  
	             
		             //比较API transaction订单
//	 	            Date date3 = new Date();
	 	            WebElement tranx_date3 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[2]/td[1]"));
//	 	        	SimpleDateFormat sdf2 = new SimpleDateFormat("EEE, d MMM",Locale.ENGLISH );
//	 	        	String date_s = sdf.format(date);
	 	            Assert.assertEquals(date_s, tranx_date3.getText());
	 	            
	 	            WebElement tranx_type3 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[2]/td[2]"));
	 	            Assert.assertEquals(type, tranx_type3.getText());  
	 	            
	 	            WebElement tranx_id3 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[2]/td[3]"));
	 	            Assert.assertEquals(transactionId, tranx_id3.getText());  
	 	            WebElement tranx_wal3 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[2]/td[4]"));
	 	            Assert.assertEquals("initAccount", tranx_wal3.getText());  
	 	            WebElement tranx_amount3 = chrome2.findElement(By.xpath(".//*[@id='lat-page-history']/div[2]/table/tbody/tr[2]/td[5]"));
	 	            Assert.assertEquals("$100.00 AUD", tranx_amount3.getText());  
	 	            
	 	           //展开Processing 列表， 查看产生支付成功的订单已经不再processing 中显示了
	 	            WebElement show2 = chrome2.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[1]/div/a"));
	 	            show2.click();	            
	 	            WebElement tranx_id0 = chrome2.findElement(By.xpath(".//*[@id='root']/div/span/div/div[2]/div/div[2]/div[2]/table/tbody/tr[1]/td[3]"));
	 	            //路径不变， 重新定义了参数tranx_id0
	 	            Assert.assertNotEquals(transactionId, tranx_id0.getText());  
	 	            
		 }catch (Exception e) {
		    	e.printStackTrace();
		}
			 finally {
	            WebDriverUtils.returnWebDriver(chrome);
	            WebDriverUtils.returnWebDriver(chrome2);
	            WebDriverUtils.returnWebDriver(chrome3);
		}
             
 }

}
