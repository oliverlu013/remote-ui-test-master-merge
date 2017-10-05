package net.latipay.ui.test.common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.latipay.api.test.common.DatabasePool;
import net.latipay.api.test.domain.DatabaseEnv;
import net.latipay.api.test.domain.DatabaseType;
import net.latipay.api.test.domain.TestConstants;

/**
 * @author jasonlu 9:52:17 AM
 */
public class DBUtils {

    private static String DB_ERROR_FORMAT = " dbUrl=%s | username=%s | password=%s";
    static {
        DBUtils.regist(DatabaseEnv.STAGING.name(), DatabaseType.MYSQL, TestConstants.DB_IP, "3306", TestConstants.DB_SCH, TestConstants.DB_USER, TestConstants.DB_PWD);
    }

    /**
     * 注册数据库，返回注册数据库Key，对该数据库连接操作需要使用该Key作为入参
     * 
     * @param DatabaseType.mysql
     * @param 数据库地址
     * @param 数据库端口
     * @param 数据库名称
     * @param 用户名
     * @param 密码
     * @return 注册数据库Key
     */
    public static void regist(String key, DatabaseType type, String host, String port, String database, String username, String password) {
        if (type == null || StringUtils.isBlank(host) || StringUtils.isBlank(port) || StringUtils.isBlank(database) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            StringBuilder sb = new StringBuilder();
            sb.append(host);
            sb.append(":");
            sb.append(port);
            sb.append("/");
            sb.append(database);
            throw new IllegalArgumentException("数据库入参出错：" + String.format(DB_ERROR_FORMAT, sb.toString(), username, password));
        }
        StringBuilder dbUrl = new StringBuilder();
        switch (type) {
            case MYSQL:
                dbUrl.append("jdbc:mysql://");
                dbUrl.append(host);
                dbUrl.append(":");
                dbUrl.append(port);
                dbUrl.append("/");
                dbUrl.append(database);
                break;
        }
        registerConnection(key, dbUrl.toString(), username, password);
    }

    /**
     * 注册数据库，返回注册数据库Key，对该数据库连接操作需要使用该Key作为入参
     * 
     * @param 数据库连接串
     * @param 用户名
     * @param 密码
     * @return
     */
    public static void registerConnection(String key, String dbUrl, String username, String password) {
        if (StringUtils.isBlank(dbUrl) || StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("数据库入参出错：" + String.format(DB_ERROR_FORMAT, dbUrl, username, password));
        }
        DatabasePool.registerConnection(key, dbUrl, username, password);
    }

    /**
     * 查询单条记录，结果强制转换成Map<String,String>
     * 
     * @param 注册数据库Key
     * @param 查询SQL
     * @return Map<String, String>
     */
    public static Map<String, String> query(String dbKey, String sql) {
        Connection conn = null;
        if (dbKey == null || StringUtils.isBlank(sql) || (conn = DatabasePool.getConnection(dbKey)) == null) {
            throw new IllegalArgumentException();
        }
        Statement statement = null;
        try {
            Map<String, String> record = new HashMap<String, String>();
            statement = conn.createStatement();
            statement.setMaxRows(1);
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    record.put(metaData.getColumnLabel(i), resultSet.getString(metaData.getColumnLabel(i)));
                }
            }
            return record;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 查询多条记录，结果强制转换成Map<String,String>
     * 
     * @param 注册数据库Key
     * @param 查询SQL
     * @return List<Map<String, String>>
     */
    public static List<Map<String, String>> queryList(String dbKey, String sql) {
        Connection conn = null;
        if (dbKey == null || StringUtils.isBlank(sql) || (conn = DatabasePool.getConnection(dbKey)) == null) {
            throw new IllegalArgumentException();
        }
        Statement statement = null;
        try {
            List<Map<String, String>> results = new ArrayList<Map<String, String>>();
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Map<String, String> record = new HashMap<String, String>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    record.put(metaData.getColumnName(i), resultSet.getString(metaData.getColumnName(i)));
                }
                results.add(record);
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 查询单条记录
     * 
     * @param 注册数据库Key
     * @param 查询SQL
     * @return Map<String, Object>
     */
    public static Map<String, Object> queryObject(String dbKey, String sql) {
        Connection conn = null;
        if (dbKey == null || StringUtils.isBlank(sql) || (conn = DatabasePool.getConnection(dbKey)) == null) {
            throw new IllegalArgumentException();
        }
        Statement statement = null;
        try {
            Map<String, Object> record = new HashMap<String, Object>();
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            statement.setMaxRows(1);
            if (resultSet.next()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    record.put(metaData.getColumnLabel(i), resultSet.getObject(metaData.getColumnLabel(i)));
                }
            }
            return record;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 查询多条记录
     * 
     * @param 注册数据库Key
     * @param 查询SQL
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> queryObjectList(String dbKey, String sql) {
        Connection conn = null;
        if (dbKey == null || StringUtils.isBlank(sql) || (conn = DatabasePool.getConnection(dbKey)) == null) {
            throw new IllegalArgumentException();
        }
        Statement statement = null;
        try {
            List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Map<String, Object> record = new HashMap<String, Object>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    record.put(metaData.getColumnName(i), resultSet.getObject(metaData.getColumnName(i)));
                }
                results.add(record);
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 执行SQL
     * 
     * @param 注册数据库Key
     * @param 执行SQL
     * @return boolean 执行结果
     */
    public static boolean executeSql(String dbKey, String sql) {
        Connection conn = null;
        if (dbKey == null || StringUtils.isBlank(sql) || (conn = DatabasePool.getConnection(dbKey)) == null) {
            throw new IllegalArgumentException();
        }
        Statement statement = null;
        try {
            statement = conn.createStatement();
            boolean result = statement.execute(sql);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 更新SQL
     * 
     * @param 注册数据库Key
     * @param 更新SQL
     * @return 更新数量
     */
    public static int executeUpdate(String dbKey, String sql) {
        Connection conn = null;
        if (dbKey == null || StringUtils.isBlank(sql) || (conn = DatabasePool.getConnection(dbKey)) == null) {
            throw new IllegalArgumentException();
        }
        Statement statement = null;
        try {
            statement = conn.createStatement();
            int updated = statement.executeUpdate(sql);
            return updated;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 插入SQL
     * 
     * @param 注册数据库Key
     * @param 插入SQL
     * @return 插入数量
     */
    public static int executeInsert(String dbKey, String sql) {
        return executeUpdate(dbKey, sql);
    }
}
