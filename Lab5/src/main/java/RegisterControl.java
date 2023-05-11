import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;


/**
 * RegisterControl类
 * 功能：
 *  管理用户的注册
 *
 *  执行顺序：
 *  1、检查验证码
 *  2、检查两次输入的密码是否一致（防御性编程）
 *  3、检查输入合法性（防御性编程）
 *  4、检查用户是否已存在
 *  5、注册信息放至数据库
 *  6、设置该用户登录并重定向回主页index.html
 *  注意：1、MySQL中，username是primary key，因此检测用户名是否重复就交给数据库判断
 *       2、用户注册成功后，会跳转主页并自动登录
 *       3、用户未登录且注册，则自动登录；用户已登录又注册，仅返回提示信息
 */
@WebServlet(urlPatterns = "/RegisterControl")
public class RegisterControl extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //获取注册者Session
        HttpSession visitorSession = req.getSession();
        //获取前端提交的注册表单内容
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String password2 = req.getParameter("password2");
        String code = req.getParameter("code");

        //1、检查验证码
        if(!code.equalsIgnoreCase(visitorSession.getAttribute("CAPTCHA").toString())){
            visitorSession.setAttribute("msgCode",GetMsg.incorrectCode);
            resp.sendRedirect("/register.html");
            return;
        }

        //移除验证码属性
        visitorSession.removeAttribute("CAPTCHA");

        //2、检查两次输入的密码是否一致
        if(!password.equals(password2)){
            visitorSession.setAttribute("msgCode",GetMsg.unconformity);
            resp.sendRedirect("/register.html");
            return;
        }

        //3、检查输入合法性
        if (!username.matches("[a-zA-Z0-9]{8,20}") || !password.matches(".{8,20}")) {
            visitorSession.setAttribute("msgCode", GetMsg.incorrectInput);
            resp.sendRedirect("/register.html");
            return;
        }

        //4、检查用户是否已存在
        //5、注册信息放至数据库
        User user = new User(username,password);
        try {
            if(DBAccess.addUser(user)){

                //若用户未登录，则自动登录
                if(visitorSession.getAttribute("user") == null){

                    LoginControl.loggedUserMap.put(username,user);
                    visitorSession.setAttribute("user",username);
                    visitorSession.removeAttribute("CAPTCHA");
                    resp.sendRedirect("/index.html");

                }else {
                    //否则发送msg提醒用户注销原登录账号再登录新注册账号
                    visitorSession.setAttribute("msgCode", GetMsg.succeededRegistered);
                    resp.sendRedirect("/register.html");
                }

            }else {
                visitorSession.setAttribute("msgCode", GetMsg.hasRegistered);
                resp.sendRedirect("/register.html");
                //return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
