package lingjia.wang.service.impl;

import lingjia.wang.base.domain.Authority;
import lingjia.wang.base.domain.Role;
import lingjia.wang.base.domain.User;
import lingjia.wang.repository.UserRepository;
import lingjia.wang.security.shiro.AccountProfile;
import lingjia.wang.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : RYAN0UP
 * @version : 1.0
 * @date : 2017/11/14
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 保存个人资料
     *
     * @param user user
     */
    @Override
    public void saveByUser(User user) {
        userRepository.save(user);
    }

    /**
     * 根据用户名和密码查询
     *
     * @param userName userName
     * @param userPass password
     * @return user
     */
    @Override
    public List<User> userLoginByName(String userName, String userPass) {
        return userRepository.findByUserNameAndPassword(userName, userPass);
    }

    /**
     * 根据邮箱和密码查询，用户登录
     *
     * @param userEmail userEmail
     * @param userPass  password
     * @return list
     */
    @Override
    public List<User> userLoginByEmail(String userEmail, String userPass) {
        return userRepository.findByUserEmailAndPassword(userEmail, userPass);
    }

    /**
     * 查询所有用户
     *
     * @return list
     */
    @Override
    public User findUser() {
        List<User> users = userRepository.findAll();
        if (users != null && users.size() > 0) {
            return users.get(0);
        } else {
            return new User();
        }
    }

    /**
     * 验证修改密码时，密码是否正确
     *
     * @param userId   userId
     * @param userPass password
     * @return User
     */
    @Override
    public User findByUserIdAndUserPass(Long userId, String userPass) {
        return userRepository.findByUserIdAndPassword(userId, userPass);
    }

    /**
     * 修改禁用状态
     *
     * @param enable enable
     */
    @Override
    public void updateUserLoginEnable(String enable) {
        User user = this.findUser();
        user.setLoginEnable(enable);
        userRepository.save(user);
    }

    /**
     * 修改最后登录时间
     *
     * @param lastDate lastDate
     */
    @Override
    public User updateUserLoginLast(Date lastDate) {
        User user = this.findUser();
        user.setLoginLast(lastDate);
        userRepository.save(user);
        return user;
    }

    /**
     * 修改登录错误次数
     *
     *
     */
    @Override
    public Integer updateUserLoginError() {
        User user = this.findUser();
        user.setLoginError((user.getLoginError() == null ? 0 : user.getLoginError()) + 1);
        userRepository.save(user);
        return user.getLoginError();
    }

    /**
     * 修改用户的状态为正常
     */
    @Override
    public User updateUserNormal() {
        User user = this.findUser();
        user.setLoginEnable("true");
        user.setLoginError(0);
        user.setLoginLast(new Date());
        userRepository.save(user);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    @Override
    public AccountProfile login(String username) {
        User userinfo=  this.findByUsername(username);
        userinfo.setLoginLast(new Date());
        userRepository.save(userinfo);

        AccountProfile accountProfile=new AccountProfile();
        int acp=userinfo.getUserEmail()==null?0:1;
        accountProfile.setActiveEmail(acp);
        List<Authority> authorities=new ArrayList<>();
        for(Role roleinfo :userinfo.getRoles())
        {
            authorities.addAll(roleinfo.getAuthorities());
        }
        accountProfile.setAuthoritys(authorities);
        accountProfile.setId(userinfo.getUserId());
        accountProfile.setName(userinfo.getUserDisplayName());
        accountProfile.setUsername(userinfo.getUserName());
        accountProfile.setAvatar(userinfo.getUserAvatar());
       // accountProfile.setStatus(userinfo.get);

        return accountProfile;
    }
}
