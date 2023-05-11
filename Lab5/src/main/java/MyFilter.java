import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * MyFilter类
 *  功能：
*   1、阻止未登录用户访问受保护资源
 *  2、为所有访问者设置Session
 */
@WebFilter(urlPatterns = "/*")
public class MyFilter implements Filter {

    //受保护资源url清单，后续可直接添加到列表即可
    private static final String[] protectedURLList = {"/test.html","/DBAccess","/User","/LogoutControl"};

    public void init(FilterConfig filterConfig) {
        System.out.println("---LoginFilter已启动...");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取访问者Session
        HttpSession visitorSession = request.getSession();

        //获取用户请求的资源
        String visitURI = request.getRequestURI();
        //检查访问者是否正在访问受保护资源
        boolean isProtected = false;
        for (String url : protectedURLList) {
            if (visitURI.endsWith(url)) {
                isProtected = true;
                break;
            }
        }

        //当访问者未登录且访问受保护的资源时，将被设置msg且重定向至登录界面
        //一定要用==，因为未登录用户没有user属性
        if (isProtected&&visitorSession.getAttribute("user") == null) {

            visitorSession.setAttribute("msgCode",GetMsg.notLogged);
            //System.out.println("用户未登录且访问受保护的资源");
            response.sendRedirect("/login.html");

        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }
}
