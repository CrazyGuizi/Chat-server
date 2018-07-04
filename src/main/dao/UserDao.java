package dao;

import bean.User;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import util.DBUtil;

import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public static UserDao single = null;

    private UserDao() {}

    public static UserDao getInstance() {
        if (single == null) {
            synchronized (UserDao.class) {
                if (single == null) {
                    single = new UserDao();
                }
            }
        }
        return single;
    }

    /**
     *  添加用户
     * @param name 昵称
     * @param username 账户
     * @param password 密码
     * @param about 个人说明
     * @return 0为添加用户失败，1为添加成功，-1为用户名已存在
     */
    public static int register(String name, String username, String password, String about) {
        DBUtil dbUtil = DBUtil.getInstance();
        String sql = "INSERT INTO user(name,username,password,about) values(?,?,?,?)";
        User user = findUserByUsername(username); // 查看账户是否存在
        int result;
        if (user == null) {
            result = dbUtil.executeUpdate(sql, new String[] {name, username, password, about});
        } else {
            result = -1;
        }
        return result;
    }

    /**
     * 更改个人信息
     * @param id 用户id
     * @param about 用户更改的信息
     * @return 成功返回1，否则返回0
     */
    public static int changeAbout(int id, String about) {
        DBUtil dbUtil = DBUtil.getInstance();
        String sql = "UPDATE user SET about=? where id=?";
        int result = dbUtil.executeUpdate(sql, new String[] {about, String.valueOf(id)});
        return result == 1 ? 1 : 0;
    }

    /**
     * 登录，验证账号密码
     * @param username
     * @param password
     * @return 如果用户存在，则返回这个用户，否则返回null
     */
    public static User login(String username, String password) {
        DBUtil dbUtil = DBUtil.getInstance();
        String sql = "SELECT * from user where username=? and password=?";
        ResultSet rs = dbUtil.query(sql, new String[] {username, password});
        User user = null;
        try {
            while (rs.next()) {
                user = new User(rs.getInt("id"),
                rs.getString("name"),
                username,
                password,
                rs.getString("about")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return user;
    }

    /**
     * 根据账户获取用户
     * @param username
     * @return
     */
    public static User findUserByUsername(String username) {
        DBUtil dbUtil = DBUtil.getInstance();
        String sql = "SELECT * from user where username=?";
        ResultSet rs = dbUtil.query(sql, new String[] {username});
        User user = null;
        try {
            while (rs.next()) {
                user = new User(rs.getInt("id"),
                        rs.getString("name"),
                        username,
                        rs.getString("password"),
                        rs.getString("about")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return user;
    }

    /**
     * 获取所有用户
     * @return
     */
    public static List<User> findAllUser() {
        DBUtil dbUtil = DBUtil.getInstance();
        String sql = "SELECT * from user";
        ResultSet rs = dbUtil.query(sql, null);
        List<User> users = new ArrayList<>();
        try {
            while (rs.next()) {
                users.add(new User(rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("about")
                ));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return users;
    }


    public static void main(String[] args) {
        StringBuffer buffer = new StringBuffer();
        List<User> users = UserDao.findAllUser();
        if (users != null) {
            for (User u : users) {
                buffer.append(JSON.toJSONString(u)).toString();
            }
        }
        System.out.println(buffer.toString());

    }
}
