import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *  GetCAPTCHA类
*   验证码-Completely Automated Public Turing test to tell Computers and Humans Apart简称 CAPTCHA
 *  功能：
 *  实现验证码的分发
 *  注意：
 *  1、可在codeArray自定义添加验证码字符
*/
@WebServlet(urlPatterns = "/GetCAPTCHA")
public class GetCAPTCHA extends HttpServlet {

    //验证码图片的宽高
    static int width = 120;
    static int height = 40;

    //自定义验证码内容
    String[] codeArray = {"0","1","2","3","4","5","6","7","8","9"};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //创建验证码图片类
        BufferedImage codesImg = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        //获取画笔
        Graphics pen = codesImg.getGraphics();

        //画出验证码图的背景
        pen.setColor(Color.blue);
        pen.fillRect(0, 0, width, height);

        //重置画笔，准备话验证码图的字符
        pen.setColor(Color.YELLOW);
        pen.setFont(new Font("宋体",Font.BOLD,30));

        //StringBuffer类可变，追加内容上比String快，因此用codeString保存验证码字符串
        StringBuffer codeString = new StringBuffer();

        //4是验证码内的字符个数
        for (int i = 0; i < 4; i++) {

            //随机产生验证码的单个字符
            int index = (int) (Math.random() * codeArray.length);

            //画出该字符
            pen.drawString(codeArray[index],i*20+10,30);

            //将该字符追加到验证码字符串中
            codeString.append(codeArray[index]);
        }

        //用session保存当前会话的验证码答案
        req.getSession().setAttribute("CAPTCHA",codeString);
        //System.out.println("---GetCAPTCHA产生的验证码答案---"+codeString);

        //重置画笔，设置干扰线
        pen.setColor(Color.red);
        for (int i = 0; i < 20; i++) {
            pen.drawLine((int)(Math.random()*width), (int)(Math.random()*height), (int)(Math.random()*width), (int)(Math.random()*height));
        }

        //返回验证码给前端
        ImageIO.write(codesImg, "jpeg", resp.getOutputStream());
    }
}
