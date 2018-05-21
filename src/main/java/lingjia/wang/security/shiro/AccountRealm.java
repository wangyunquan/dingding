package lingjia.wang.security.shiro;


import lingjia.wang.base.domain.Authority;
import lingjia.wang.base.domain.Role;
import lingjia.wang.base.domain.User;
import lingjia.wang.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    public AccountRealm() {
        super(new AllowAllCredentialsMatcher());
        setAuthenticationTokenClass(UsernamePasswordToken.class);

        //TODO
        //FIXME: 暂时禁用Cache
        setCachingEnabled(false);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.fromRealm(getName()).iterator().next();
        if (username != null) {
            User user = userService.findByUsername(username);
            if (user != null) {
                SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

                for (Role roleinfo : user.getRoles()) {

                    // 添加基于Permission的权限信息
                    for (Authority authority : roleinfo.getAuthorities()) {
                        info.addStringPermission(authority.getAuthName());
                    }
                }
                return info;
            }
//                info.addRole(role.getKey());
//                for (Role r : user.getRoles()) {
//                    info.addRole(r.getName());
//                    ArrayList<String> ps = new ArrayList<String>();
//                    for(Permission p: r.getPermissions()){
//                        ps.add(p.getName());
//                    }
//
//                    info.addStringPermissions(ps);
//                }

        }
        return null;
    }



    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        AccountProfile profile = getAccount(userService, token); //TODO 登录加载相关信息

//        if(profile.getStatus() == Consts.STATUS_CLOSED){
//            throw new LockedAccountException(profile.getName());
//        }

        AccountAuthenticationInfo info = new AccountAuthenticationInfo(token.getPrincipal(), token.getCredentials(), getName());
        info.setProfile(profile);

        return info;
    }

    protected AccountProfile getAccount(UserService userService, AuthenticationToken token){
        UsernamePasswordToken upToken = (UsernamePasswordToken)token;
        return userService.login(upToken.getUsername());
    }

}

