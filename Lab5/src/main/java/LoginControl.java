import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * LoginControl类
 * 功能：
 *  控制用户的登录
 *
 *  执行顺序：
 *  1、检查验证码
 *  2、检查输入合法性（防御性编程）
 *  3、检查用户是否存在
 *  4、检查用户是否重复登录
 *  5、决定是否实现“记住我“功能（登录成功后）
 *  注意：1、”记住我“这个功能的实现方式是直接向前端发送用户名密码，十分不安全，实际业务不可采用此方法
 *       2、注意登录操作顺序
 */
@WebServlet(urlPatterns = "/LoginControl")
public class LoginControl extends HttpServlet {


    //Map用于保存已登录的用户信息
    public static Map<String,User> loggedUserMap = new HashMap<String, User>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.doPost(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //获取用户session
        HttpSession visitorSession = req.getSession();
        //获取前端提交的登录表单内容
        String username = req.getParameter("username");             //用户名
        String password = req.getParameter("password");             //密码
        String code = req.getParameter("code");                     //验证码
        Boolean btnState = (req.getParameter("rememberme") != null);  //是否开启“记住我”功能

        //1、检查验证码（因未知原因，要toString后才能和code比较）
        if(!code.equalsIgnoreCase(visitorSession.getAttribute("CAPTCHA").toString())){
            visitorSession.setAttribute("msgCode",GetMsg.incorrectCode);
            resp.sendRedirect("/login.html");
            return;
        }

        //移除验证码属性
        visitorSession.removeAttribute("CAPTCHA");

        //2、检查账号or密码的合法性（长度8-20，密码为任意字符/用户名必须字母或数字）
        if (!username.matches("[a-zA-Z0-9]{8,20}") || !password.matches(".{8,20}")) {
            visitorSession.setAttribute("msgCode", GetMsg.incorrectInput);
            resp.sendRedirect("/login.html");
            return;
        }
        //System.out.println("---账号密码输入合法性没问题");


        //3、检查用户是否存在(jdbc)
        try {
            if(DBAccess.queryUser(username,password)){

                //4、检查用户是否重复登录
                if(visitorSession.getAttribute("user") != null){
                    visitorSession.setAttribute("msgCode",GetMsg.hasLogged);
                    //System.out.println("用户"+username+"重复登录...");
                    resp.sendRedirect("/login.html");
                    return;
                }

                //登录成功的用户，session只有user属性
                visitorSession.setAttribute("user",username);
                visitorSession.removeAttribute("CAPTCHA");
                //visitorSession.removeAttribute("msgCode");


                //加入到已登录用户的map中
                loggedUserMap.put(username,new User(username,password));



                //5、设置用户“记住我”功能
                Cookie cookie = new Cookie("user", username);
                if(btnState){

                    // 设置cookie
                    cookie.setMaxAge(60*60); // 设置存活时间为1小时
                    resp.addCookie(cookie);
                    cookie = new Cookie("psw", password);
                    cookie.setMaxAge(60*60);
                    resp.addCookie(cookie);

                }else {

                    //让浏览器记住的账号密码都失效
                    cookie = new Cookie("user", "");
                    cookie.setMaxAge(0);
                    resp.addCookie(cookie);
                    cookie = new Cookie("psw", "");
                    cookie.setMaxAge(0);
                    resp.addCookie(cookie);
                }

                //最后重定向回主页
                resp.sendRedirect("/index.html");

            }else{

                //登录失败，发送msg
                visitorSession.setAttribute("msgCode",GetMsg.noUser);
                visitorSession.removeAttribute("CAPTCHA");
                resp.sendRedirect("/login.html");

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
