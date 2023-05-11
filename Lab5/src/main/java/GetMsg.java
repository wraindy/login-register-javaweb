import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * GetMsg类
 * 功能：
 * 登录、注册界面需要结果反馈，msg就是结果反馈
 */
@WebServlet(urlPatterns = "/GetMsg")
public class GetMsg extends HttpServlet {

    /**
     *  MSG消息类型
     *  incorrectCode---验证码错误
     *  incorrectInput---用户名/密码输入不合法（正则表达式）
     *  noUser---用户不存在
     *  notLogged---用户未登录
     *  hasLogged---用户已登录
     *  unconformity---两次密码不一致
     *  hasRegistered---用户重复注册
     *  succeededRegistered---用户已登录且注册新账号成功
     *
     *  具体内容请看doPost()方法
     */
    public static int incorrectCode = 0;
    public static int incorrectInput = 1;
    public static int noUser = 2;
    public static int notLogged = 3;
    public static int hasLogged = 4;
    public static int unconformity = 5;
    public static int hasRegistered = 6;

    public static int succeededRegistered = 7;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    /**
     *
     * 注意；
     *  只有通过登录和注册控制类才能获取msg，因此设置 if (req.getSession().getAttribute("msgCode") != null)判断
     *  避免其他非法请求访问
     * */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        HttpSession visitorSession = req.getSession();

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = resp.getWriter();

        if (req.getSession().getAttribute("msgCode") != null) {
            if (visitorSession.getAttribute("msgCode").equals(GetMsg.incorrectCode)) {
                writer.write("验证码错误，请重试！");
            } else if (visitorSession.getAttribute("msgCode").equals(GetMsg.incorrectInput)) {
                writer.write("账号或密码输入不合法，请重试！");
            } else if (visitorSession.getAttribute("msgCode").equals(GetMsg.noUser)) {
                writer.write("用户不存在，请重试！");
            } else if (visitorSession.getAttribute("msgCode").equals(GetMsg.notLogged)) {
                writer.write("您还未登录，请登录！");
            } else if (visitorSession.getAttribute("msgCode").equals(GetMsg.hasLogged)) {
                writer.write("您已登录，请注销后再试！");
            }else if (visitorSession.getAttribute("msgCode").equals(GetMsg.unconformity)) {
                writer.write("两次输入密码不一致，请重试！");
            }else if (visitorSession.getAttribute("msgCode").equals(GetMsg.hasRegistered)) {
                writer.write("用户名已存在，请重试！");
            }else if (visitorSession.getAttribute("msgCode").equals(GetMsg.succeededRegistered)) {
                writer.write("注册成功，请注销已登录的账号后登录新账号！");
            } else {
                writer.write("");
            }
        } else {
            writer.write("");
        }
        writer.close();

        //每次发送错误提醒后就移除msgCode属性
        visitorSession.removeAttribute("msgCode");
    }
}
