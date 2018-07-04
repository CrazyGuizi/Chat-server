package util;

import java.sql.*;

public class DBUtil {

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/chat?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT&&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static DBUtil single = null;

    private DBUtil() {}
    public static DBUtil getInstance() {
        if (single == null) {
            synchronized (DBUtil.class) {
                if (single == null) {
                    single = new DBUtil();
                }
            }
        }
        return single;
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // 增删改
    public <T> int executeUpdate(String sql, T[] ts) {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        int result = 0;
        try {
            if (conn != null) {
                ps = conn.prepareStatement(sql);
            }

            if (ps != null) {
                if (ts != null) {
                    for (int i = 0; i < ts.length; i++) {
                        ps.setObject(i + 1, ts[i]);
                    }
                }
            }
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(conn, ps);

        }
        return result;
    }

    // 关闭资源1
    private void close(Connection conn, PreparedStatement ps) {
        try {
            if (conn!= null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查找
    public <T> ResultSet query(String sql, T[] ts) {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = conn.prepareStatement(sql);
            if (ps != null) {
                if (ts != null) {
                    for (int i = 0; i < ts.length; i++) {
                        ps.setObject(i + 1, ts[i]);
                    }
                }
            }
            resultSet = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

}
