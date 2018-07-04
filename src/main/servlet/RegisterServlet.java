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

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String name = req.getParameter("name");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String about = req.getParameter("about");

        StringBuffer buffer = new StringBuffer();
        int result = UserDao.register(name, username, password, about);
        BaseJSON<User> json = new BaseJSON<>();

        if (result == 0) { // 注册失败
            json.setCode(BaseJSON.CODE_EXCEPTION);
            json.setMsg("注册失败");
        } else if (result == -1) { // 用户已存在
            json.setCode(BaseJSON.CODE_FAILED);
            json.setMsg("用户已存在");
        } else { // 注册成功
            json.setCode(BaseJSON.CODE_OK);
            json.setMsg("成功");
            User user = UserDao.findUserByUsername(username);
            if (user != null) {
                json.setData(user);
            }
        }
        buffer.append(JSON.toJSONString(json));
        resp.setContentType("text/html;charset=UTF-8");
        OutputStream out = resp.getOutputStream();
        out.write(buffer.toString().getBytes());
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
