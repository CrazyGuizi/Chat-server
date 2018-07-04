package servlet;


import bean.BaseJSON;
import bean.User;
import com.alibaba.fastjson.JSON;
import dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        StringBuffer buffer = new StringBuffer();
        User user = UserDao.login(username, password);
        BaseJSON<User> json = new BaseJSON<>();

        if (user != null) {  // 登陆成功
            json.setCode(BaseJSON.CODE_OK); // 0
            json.setMsg("成功");
            json.setData(user);
        } else { // 账户密码出错
            json.setCode(BaseJSON.CODE_FAILED); // 1
            json.setMsg("账户或密码出错");
        }
        buffer.append(JSON.toJSONString(json));
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=UTF-8");
        System.out.println(buffer.toString());
        OutputStream out = resp.getOutputStream();
        out.write(buffer.toString().getBytes());
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
