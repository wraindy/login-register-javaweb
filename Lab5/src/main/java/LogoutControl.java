import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * LogoutControl
 * 功能：
 *  注销已登录的用户
 *
 *  执行顺序：
 *  1、将该用户移出登录Map
 *  2、移除该用户Session
 *  3、清空该用户的cookie（JSESSIONID）
 *  4、重定向至主页index.html
 */
@WebServlet(urlPatterns = "/LogoutControl")
public class LogoutControl extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //获取用户Session
        HttpSession userSession = req.getSession();

        //1、将该用户退出登录Map
        LoginControl.loggedUserMap.remove(userSession.getAttribute("user").toString());

        //2、清除session会话
        userSession.invalidate();



        //3、浏览器会更新JSESSIONID（置空）
        Cookie cookie = new Cookie("JSESSIONID",null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);

        //以下方法等价于上述方法
//        Cookie[] cookies = req.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals("JSESSIONID")) {
//
//                    cookie.setValue("");
//                    cookie.setMaxAge(0);
//                    resp.addCookie(cookie);
//
//                }
//            }
//        }

        //注销后重定向至主页
        //4、注销后重定向回主页
        resp.sendRedirect("/index.html");
    }
}
