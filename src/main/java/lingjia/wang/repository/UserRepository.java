package lingjia.wang.repository;

import lingjia.wang.base.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : RYAN0UP
 * @date : 2017/11/14
 * @version : 1.0
 */
public interface UserRepository extends JpaRepository<User,Long>{

    /**
     * 根据用户名和密码查询
     *
     * @param userName userName
     * @param userPass password
     * @return list
     */
    List<User> findByUserNameAndPassword(String userName, String userPass);

    /**
     * 根据邮箱和密码查询
     *
     * @param userEmail userEmail
     * @param userPass password
     * @return list
     */
    List<User> findByUserEmailAndPassword(String userEmail, String userPass);

    /**
     * 根据用户编号和密码查询
     *
     * @param userId userId
     * @param userPass userpass
     * @return User
     */
    User findByUserIdAndPassword(Long userId, String userPass);

    User findByUserName(String username);
}
