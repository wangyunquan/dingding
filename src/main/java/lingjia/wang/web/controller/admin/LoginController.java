package lingjia.wang.web.controller.admin;

import lingjia.wang.base.domain.User;
import lingjia.wang.model.domain.Logs;
import lingjia.wang.model.dto.HaloConst;
import lingjia.wang.model.dto.LogsRecord;
import lingjia.wang.security.shiro.AccountProfile;
import lingjia.wang.service.LogsService;
import lingjia.wang.service.UserService;
import lingjia.wang.utils.HaloUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller

public class LoginController  {
Logger log=LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private LogsService logsService;
    /**
     * 处理跳转到登录页的请求
     *
     * @param session session
     * @return 模板路径admin/admin_login
     */
    @GetMapping(value = "/login")
    public String login(HttpSession session) {
//        AccountProfile user = (AccountProfile) session.getAttribute(HaloConst.USER_SESSION_KEY);
//        //如果session存在，跳转到后台首页
//        if (null != user) {
//            return "redirect:/admin";
//        }
        return "admin/admin_login";
    }



    /**
     * 验证登录信息
     *
     * @param loginName 登录名：邮箱／用户名
     * @param loginPwd  loginPwd 密码
     * @param session   session session
     * @return String 登录状态
     */
    @PostMapping(value = "/getLogin")
    @ResponseBody
    public String getLogin(@ModelAttribute("loginName") String loginName,
                           @ModelAttribute("loginPwd") String loginPwd,

                           HttpSession session, HttpServletRequest request) {
        String status = "false";
        try {
//            User aUser = userService.findUser();
//            User user = null;
//            if (StringUtils.equals(aUser.getLoginEnable(), "false")) {
//                status = "disable";
//            } else {
            //验证是否是邮箱登录
            Pattern patternEmail = Pattern.compile("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");
            Matcher matcher = patternEmail.matcher(loginName);
            if (matcher.find()) {
                // TODO    user = userService.userLoginByEmail(loginName, HaloUtils.getMD5(loginPwd)).get(0);
            } else {
                AuthenticationToken token = new UsernamePasswordToken(loginName, loginPwd);
                ((UsernamePasswordToken) token).setRememberMe(true);
                try {
                    SecurityUtils.getSubject().login(token);
                    Subject subject=    SecurityUtils.getSubject();
                    //  session.setAttribute(HaloConst.USER_SESSION_KEY, user);
                    //重置用户的登录状态为正常
                    userService.updateUserNormal();
                    userService.updateUserLoginLast(new Date());
                    logsService.saveByLogs(new Logs(LogsRecord.LOGIN, LogsRecord.LOGIN_SUCCESS, HaloUtils.getIpAddr(request), new Date()));
                    status = "true";
                } catch (AuthenticationException e) {
                    if (e instanceof UnknownAccountException) {
                        status="false";
                        log.info("用户不存在" );
                        log.error("登录失败！：{0}", e.getMessage());
                    } else if (e instanceof LockedAccountException) {
                        log.info("用户被禁用");
                        log.error("登录失败！：{0}", e.getMessage());
                    } else {
                        log.info("用户认证失败");
                        log.error("登录失败！：{0}", e.getMessage());
                    }
                }
            }


            //  }
        } catch (Exception e) {
            Integer errorCount = userService.updateUserLoginError();
            if (errorCount >= 5) {
                userService.updateUserLoginEnable("false");
            }
            userService.updateUserLoginLast(new Date());
            logsService.saveByLogs(new Logs(LogsRecord.LOGIN, LogsRecord.LOGIN_ERROR + "[" + loginName + "," + loginPwd + "]", HaloUtils.getIpAddr(request), new Date()));
            log.error("登录失败！：{0}", e.getMessage());
        }
        return status;
    }
}
