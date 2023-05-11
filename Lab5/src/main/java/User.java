/**
 *  User类
 *  功能：
 *  用于将已登录的用户信息放到LoginControl的Map
 */
public class User {
    private String username;
    private String password;

    public User(String username,String password){
        this.username = username;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
