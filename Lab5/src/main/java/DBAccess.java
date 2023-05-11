import javax.servlet.annotation.WebServlet;
import java.sql.*;

/**
 *  DBAccess类
 *  功能：
 *  1、查询用户信息（用于登录）
 *  2、添加用户信息（用于注册）
 * */
@WebServlet(loadOnStartup = 0)
public class DBAccess {

    // 以下为连接数据库必要的信息
    private static final String dbusername; // 表示数据库访问账号的用户名
    private static final String dbpassword; // 表示数据库访问账号的密码
    private static final String dburl; // 表示数据库访问url

    // 以下参数表示登录、注册的结果
    private static final Boolean loginSucceed; // 表示登录成功
    private static final Boolean loginFailed; // 表示登录失败
    private static final Boolean registerSucceed; // 表示注册成功
    private static final Boolean registerFailed; // 表示注册失败


    static {

        dbusername = "root";
        dbpassword = "00000000";
        dburl = "jdbc:mysql://localhost:3306/lab5?characterEncoding=utf-8&serverTimezone=UTC";
        loginSucceed = true;
        loginFailed = false;
        registerSucceed = true;
        registerFailed = false;

        try {
            //注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("-----DBAccess初始化完毕");
    }



    /**
     * checkLogin方法
     * 功能：查询用户信息
     * @param vname 访客输入的用户名
     * @param vpassword 访客输入的密码
     */
    public static Boolean queryUser (String vname,String vpassword) throws SQLException {

        //System.out.println("---MySQL正在查询用户...");

        //1、建立数据库连接
        Connection connection = DriverManager.getConnection(dburl,dbusername,dbpassword);

        //2、DBMS对sql语句进行预编译，随后传值，最后执行查询操作并返回结果集
        String sql = "select * from users where username = ? and password = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,vname);
        preparedStatement.setString(2,vpassword);
        ResultSet resultSet = preparedStatement.executeQuery();

        //3、获取查询结果
        Boolean queryResult = resultSet.next();

        //4、释放相关资源（先创建的后释放）
        if(resultSet != null)
            resultSet.close();
        if(preparedStatement != null)
            preparedStatement.close();
        if(connection != null)
            connection.close();

        //System.out.println("---收到登录请求");
        //System.out.println("---vname="+vname+" | vpassword="+vpassword);

        //5、返回查询结果
        if(queryResult){
            //System.out.println("---登录成功!该用户是"+vname);
            return loginSucceed;
        }else {
            //System.out.println("---失败！该用户不存在");
            return loginFailed;
        }

    }


    /**
     * addUser方法
     * 功能：添加用户信息
     * @param user 新用户信息
     */
    public static Boolean addUser(User user) throws SQLException {

        System.out.println("---MySQL正在添加用户...");

        //1、建立数据库连接
        Connection connection = DriverManager.getConnection(dburl,dbusername,dbpassword);

        //2、关闭事务自动提交
        connection.setAutoCommit(false);

        PreparedStatement preparedStatement = null;
        int affectedRows = 0;

        //3、DBMS对sql语句进行预编译，随后传值，最后执行并获取受影响的行数
        try{
            String sql = "insert into users values (?,?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,user.getUsername());
            preparedStatement.setString(2,user.getPassword());
            affectedRows = preparedStatement.executeUpdate();
            connection.commit();

        }catch (Exception e){
            //4、用异常机制捕获错误，回滚事务
            connection.rollback();

        }finally {
            //5、关闭资源
            if(preparedStatement != null)
                preparedStatement.close();
            if(connection != null)
                connection.close();
        }

        System.out.println("---MySQL添加用户完毕...");
        //6、返回执行结果
        return (affectedRows == 1 ? registerSucceed : registerFailed);
    }
}
