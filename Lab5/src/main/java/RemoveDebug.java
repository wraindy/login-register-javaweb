import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *  RemoveDebug类
 *  功能：
 *  用于前端页面调试，清除Session和“记住我”功能的Cookie
 *  方法：
 *  浏览器输入localhost:8080/r，可以看到服务器的反馈html页面
 *  域名和端口号可以自行修改，服务器部署本机因此用localhost；tomcat默认端口号8080没有修改，因此还是8080
 */
@WebServlet(urlPatterns = "/r")
public class RemoveDebug extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //1、移除登录Map
        if(req.getSession().getAttribute("user")!=null)
            LoginControl.loggedUserMap.remove(req.getSession().getAttribute("user").toString());

        //2、服务器对应的session失效
        req.getSession().invalidate();

        //3、让浏览器全部cookie失效
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        cookie = new Cookie("user", "");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        cookie = new Cookie("psw", "");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);

        //System.out.println("debug:已清除该用户的session和cookie...  ");

        //特别提醒，先设置响应内容的格式再获取打印流，否则中文乱码
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write("<h1>这是来自服务器的调试信息...</h1>");
        writer.write("<h3>已清除该用户的session和cookie...</h3>");
        writer.close();
    }
}
