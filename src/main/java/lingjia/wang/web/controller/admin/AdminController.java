package lingjia.wang.web.controller.admin;

import lingjia.wang.model.domain.Comment;
import lingjia.wang.model.domain.Logs;
import lingjia.wang.model.domain.Post;
import lingjia.wang.base.domain.User;
import lingjia.wang.model.dto.HaloConst;
import lingjia.wang.model.dto.LogsRecord;
import lingjia.wang.service.CommentService;
import lingjia.wang.service.LogsService;
import lingjia.wang.service.PostService;
import lingjia.wang.service.UserService;
import lingjia.wang.utils.HaloUtils;
import lingjia.wang.web.controller.core.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : RYAN0UP
 * @date : 2017/12/5
 * @version : 1.0
 * description: 后台首页控制器
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogsService logsService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CommentService commentService;

    /**
     * 请求后台页面
     *
     * @param model   model
     * @param session session
     * @return 模板路径admin/admin_index
     */
    @GetMapping(value = {"", "/index"})
    public String index(Model model, HttpSession session) {
        //查询文章条数
        Integer postCount = postService.findAllPosts(HaloConst.POST_TYPE_POST).size();
        model.addAttribute("postCount", postCount);

        //查询评论的条数
        Integer commentCount = commentService.findAllComments().size();
        model.addAttribute("commentCount", commentCount);

        //查询最新的文章
        List<Post> postsLatest = postService.findPostLatest();
        model.addAttribute("postTopFive", postsLatest);

        //查询最新的日志
        List<Logs> logsLatest = logsService.findLogsLatest();
        model.addAttribute("logs", logsLatest);

        //查询最新的评论
        List<Comment> comments = commentService.findCommentsLatest();
        model.addAttribute("comments", comments);

        model.addAttribute("mediaCount", HaloConst.ATTACHMENTS.size());
        return "admin/admin_index";
    }




    /**
     * 退出登录 销毁session
     *
     * @param session session
     * @return 重定向到/admin/login
     */
    @GetMapping(value = "/logOut")
    public String logOut(HttpSession session) {
        User user = (User) session.getAttribute(HaloConst.USER_SESSION_KEY);
        logsService.saveByLogs(new Logs(LogsRecord.LOGOUT, user.getUserName(), HaloUtils.getIpAddr(request), new Date()));
        session.invalidate();
        log.info("用户[" + user.getUserName() + "]退出登录");
        return "redirect:/admin/login";
    }

    /**
     * 查看所有日志
     *
     * @param model model model
     * @param page  page 当前页码
     * @param size  size 每页条数
     * @return 模板路径admin/widget/_logs-all
     */
    @GetMapping(value = "/logs")
    public String logs(Model model,
                       @RequestParam(value = "page", defaultValue = "0") Integer page,
                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "logId");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Logs> logs = logsService.findAllLogs(pageable);
        model.addAttribute("logs", logs);
        return "admin/widget/_logs-all";
    }

    /**
     * 清除所有日志
     *
     * @return 重定向到/admin
     */
    @GetMapping(value = "/logs/clear")
    public String logsClear() {
        try {
            logsService.removeAllLogs();
        } catch (Exception e) {
            log.error("未知错误：" + e.getMessage());
        }
        return "redirect:/admin";
    }

    /**
     * 不可描述的页面
     *
     * @return 模板路径admin/admin_halo
     */
    @GetMapping(value = "/halo")
    public String halo() {
        return "admin/admin_halo";
    }
}
