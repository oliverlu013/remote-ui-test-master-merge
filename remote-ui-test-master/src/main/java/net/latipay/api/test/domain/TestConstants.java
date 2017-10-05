package net.latipay.api.test.domain;

import okhttp3.MediaType;

/**
 * @author jasonlu 7:46:18 PM
 */
public class TestConstants {

    public static final String    API_BASE                = "https://api-staging.latipay.net";
    public static final String    TRADER_API_BASE         = "https://api-trader-staging.latipay.net";
    public static final String    OPEN_API                = "http://api.latipay.co.nz";

    public static final MediaType JSON_TYPE               = MediaType.parse("application/json;charset=UTF-8");
    public static final MediaType XML_TYPE                = MediaType.parse("application/xml;charset=UTF-8");
    public static final MediaType HTML_TYPE               = MediaType.parse("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
    public static final String    DEFAULT_PAYMENT_GATEWAY = "0,1,2,3,4";
    public static final String    AG                      = "HmacSHA256";
    public static final Integer   S_CODE                  = Integer.valueOf(0);
    public static final String    S_MSG                   = "SUCCESS";
    public static final String    LINE_S                  = System.getProperty("line.separator");

    public static final String    DB_IP                   = "rm-3ns34nih6q6b21i48o.mysql.rds.aliyuncs.com";
    public static final String    DB_SCH                  = "latipay20";
    public static final String    DB_DR                   = "com.mysql.cj.jdbc.Driver";
    public static final String    DB_USER                 = "latipay";
    public static final String    DB_PWD                  = "Aflkewfjoi678fads675";

    public static final String    apipay_return_url       = "https://www.google.co.nz";
    public static final String    spotpay_return_url      = "https://spotpay-staging.latipay.net/confirmation";
    public static final String    staticpay_return_url    = "https://spotpay-staging.latipay.net/static_qr_confirmation";

}
