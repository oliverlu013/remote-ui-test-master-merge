package net.latipay.api.test.common;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author jasonlu 9:46:51 AM
 */
public class DatabasePool {

    public static Map<String, Connection> pool = new HashMap<String, Connection>();
    static {
        try {
            Driver mysqlDriver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(mysqlDriver);
        } catch (SQLException e) {
            throw new RuntimeException("数据库驱动注册失败");
        }
    }

    public static void registerConnection(String key, String dbUrl, String username, String password) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(dbUrl) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) throw new RuntimeException("数据库注册入参错误");
        if (!pool.containsKey(key)) {
            try {
                Connection conn = DriverManager.getConnection(dbUrl, username, password);
                pool.put(key, conn);
            } catch (SQLException e) {
                throw new RuntimeException("数据库注册失败：" + key);
            }
        }
    }

    public static Connection getConnection(String key) {
        if (StringUtils.isBlank(key)) return null;
        return pool.get(key);
    }

    public static Connection getConnecttion(String key, String dbUrl, String username, String password) {
        if (pool.containsKey(key)) {
            return pool.get(key);
        }
        try {
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            pool.put(key, conn);
            return conn;
        } catch (SQLException e) {
            return null;
        }
    }

}
