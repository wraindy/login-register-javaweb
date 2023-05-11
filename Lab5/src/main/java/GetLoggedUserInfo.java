import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
/**
 *  GetLoggedUserInfo类
 *  功能：
 *  给前端获取已登录的用户名
 * */
@WebServlet(urlPatterns = "/GetLoggedUserInfo")
public class GetLoggedUserInfo extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String name = "";
        try {
            //未登录的访客是没有user属性的，用异常捕获机制处理
            name = req.getSession().getAttribute("user").toString();
        } catch (Exception e) {
            return;
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = resp.getWriter();

        writer.write(name);
        writer.close();
    }
};

