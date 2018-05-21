package lingjia.wang.security.shiro;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;

public class AuthenticationUtils {

    public static AuthenticationToken createToken(String username, String password) {
        return new UsernamePasswordToken(username, password);
    }
    /**
     * 获取登录信息
     *
     * @return
     */
    public static AccountSubject getSubject(){
        return (AccountSubject) SecurityUtils.getSubject();
    }

    public static  void putProfile(AccountProfile profile) {

        SecurityUtils.getSubject().getSession(true).setAttribute("profile", profile);
    }
}
