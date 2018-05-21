package lingjia.wang.web.controller.front;

import lingjia.wang.model.domain.Comment;
import lingjia.wang.model.domain.Gallery;
import lingjia.wang.model.domain.Post;
import lingjia.wang.model.dto.HaloConst;
import lingjia.wang.service.CommentService;
import lingjia.wang.service.GalleryService;
import lingjia.wang.service.PostService;
import lingjia.wang.web.controller.core.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author : RYAN0UP
 * @version : 1.0
 * @date : 2018/4/26
 */
@Controller
public class FrontPageController extends BaseController {

    @Autowired
    private GalleryService galleryService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    /**
     * 跳转到图库页面
     *
     * @return 模板路径/themes/{theme}/gallery
     */
    @GetMapping(value = "/gallery")
    public String gallery(Model model) {
        List<Gallery> galleries = galleryService.findAllGalleries();
        model.addAttribute("galleries", galleries);
        return this.render("gallery");
    }

    /**
     * 友情链接
     *
     * @return 模板路径/themes/{theme}/links
     */
    @GetMapping(value = "/links")
    public String links() {
        return this.render("links");
    }

    /**
     * 渲染自定义页面
     *
     * @param postUrl 页面路径
     * @param model   model
     * @return 模板路径/themes/{theme}/post
     */
    @GetMapping(value = "/p/{postUrl}")
    public String getPage(@PathVariable(value = "postUrl") String postUrl, Model model) {
        Post post = postService.findByPostUrl(postUrl, HaloConst.POST_TYPE_PAGE);

        Sort sort = new Sort(Sort.Direction.DESC,"commentDate");
        Pageable pageable = PageRequest.of(0,999,sort);
        Page<Comment> comments = commentService.findCommentsByPostAndCommentStatus(post,pageable,2);
        if(null==post){
            return this.renderNotFound();
        }
        model.addAttribute("comments",comments);
        model.addAttribute("post", post);
        post.setPostViews(post.getPostViews()+1);
        postService.saveByPost(post);
        return this.render("page");
    }
}
