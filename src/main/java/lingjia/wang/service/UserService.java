package lingjia.wang.service;

import lingjia.wang.base.domain.User;
import lingjia.wang.security.shiro.AccountProfile;

import java.util.Date;
import java.util.List;

/**
 * @author : RYAN0UP
 * @version : 1.0
 * @date : 2017/11/14
 */
public interface UserService {

    /**
     * 保存个人资料
     *
     * @param user user
     */
    void saveByUser(User user);

    /**
     * 根据用户名和密码查询，用于登录
     *
     * @param userName userName
     * @param userPass password
     * @return User
     */
    List<User> userLoginByName(String userName, String userPass);

    /**
     * 根据邮箱和密码查询，用户登录
     *
     * @param userEmail userEmail
     * @param userPass  password
     * @return list
     */
    List<User> userLoginByEmail(String userEmail, String userPass);

    /**
     * 查询所有用户
     *
     * @return list
     */
    User findUser();

    /**
     * 根据用户编号和密码查询
     *
     * @param userId   userid
     * @param userPass userpass
     * @return user
     */
    User findByUserIdAndUserPass(Long userId, String userPass);

    /**
     * 修改禁用状态
     *
     * @param enable enable
     */
    void updateUserLoginEnable(String enable);

    /**
     * 修改最后登录时间
     *
     * @param lastDate lastDate
     */
    User updateUserLoginLast(Date lastDate);

    /**
     * 增加登录错误次数
     *
     *
     */
    Integer updateUserLoginError();

    /**
     * 修改用户的状态为正常
     */
    User updateUserNormal();

    User findByUsername(String username);

    AccountProfile login(String username);

}
