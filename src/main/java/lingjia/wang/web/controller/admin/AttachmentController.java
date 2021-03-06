package lingjia.wang.web.controller.admin;

import lingjia.wang.model.domain.Attachment;
import lingjia.wang.model.domain.Logs;
import lingjia.wang.model.dto.HaloConst;
import lingjia.wang.model.dto.LogsRecord;
import lingjia.wang.service.AttachmentService;
import lingjia.wang.service.LogsService;
import lingjia.wang.utils.HaloUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : RYAN0UP
 * @date : 2017/12/19
 * @version : 1.0
 * description: 附件
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private LogsService logsService;

    /**
     * 刷新HaloConst
     */
    private void updateConst() {
        HaloConst.ATTACHMENTS.clear();
        HaloConst.ATTACHMENTS = attachmentService.findAllAttachments();
    }

    /**
     * 获取upload的所有图片资源并渲染页面
     *
     * @param model model
     * @return 模板路径admin/admin_attachment
     */
    @GetMapping
    public String attachments(Model model,
                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                              @RequestParam(value = "size", defaultValue = "18") Integer size) {
        Sort sort = new Sort(Sort.Direction.DESC, "attachId");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Attachment> attachments = attachmentService.findAllAttachments(pageable);
        model.addAttribute("attachments", attachments);

        return "admin/admin_attachment";
    }

    /**
     * 跳转选择附件页面
     *
     * @param model model
     * @param page  page 当前页码
     * @return 模板路径admin/widget/_attachment-select
     */
    @GetMapping(value = "/select")
    public String selectAttachment(Model model,
                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                   @RequestParam(value = "id",defaultValue = "none") String id,
                                   @RequestParam(value = "type",defaultValue = "normal") String type) {
        Sort sort = new Sort(Sort.Direction.DESC, "attachId");
        Pageable pageable = PageRequest.of(page, 18, sort);
        Page<Attachment> attachments = attachmentService.findAllAttachments(pageable);
        model.addAttribute("attachments", attachments);
        model.addAttribute("id", id);
        if(StringUtils.equals(type,"post")){
            return "admin/widget/_attachment-select-post";
        }
        return "admin/widget/_attachment-select";
    }

    /**
     * 上传附件
     *
     * @param file    file
     * @param request request
     * @return Map<String   ,   Object></>
     */
    @PostMapping(value = "/upload", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file,
                                      HttpServletRequest request) {
        return uploadAttachment(file, request);
    }

    /**
     * editor.md上传图片
     *
     * @param file    file
     * @param request request
     * @return Map<String   ,   Object></>
     */
    @PostMapping(value = "/upload/editor", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public Map<String, Object> editorUpload(@RequestParam("editormd-image-file") MultipartFile file,
                                            HttpServletRequest request) {
        return uploadAttachment(file, request);
    }


    /**
     * 上传图片
     *
     * @param file    file
     * @param request request
     * @return Map<String   ,   Object></>
     */
    public Map<String, Object> uploadAttachment(MultipartFile file, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (!file.isEmpty()) {
            try {
                //程序根路径，也就是/resources
                File basePath = new File(ResourceUtils.getURL("classpath:").getPath());
                //upload的路径
                StringBuffer sbMedia = new StringBuffer("upload/");
                //获取当前年月以创建目录，如果没有该目录则创建
                sbMedia.append(HaloUtils.YEAR).append("/").append(HaloUtils.MONTH).append("/");
                File mediaPath = new File(basePath.getAbsolutePath(), sbMedia.toString());
                if (!mediaPath.exists()) {
                    mediaPath.mkdirs();
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String nameWithOutSuffix = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.')).replaceAll(" ","_").replaceAll(",","")+dateFormat.format(new Date())+new Random().nextInt(1000);
                String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);
                String fileName = nameWithOutSuffix+"."+fileSuffix;
                file.transferTo(new File(mediaPath.getAbsoluteFile(), fileName));

                //保存在数据库
                Attachment attachment = new Attachment();
                attachment.setAttachName(fileName);
                attachment.setAttachPath(new StringBuffer("/upload/").append(HaloUtils.YEAR).append("/").append(HaloUtils.MONTH).append("/").append(fileName).toString());
                //判断图片大小，如果长宽都小于500，则保存原始图片路径
                BufferedImage sourceImg = ImageIO.read(new FileInputStream(mediaPath.getPath() + "/" + fileName));
                if (sourceImg.getWidth() < 500 || sourceImg.getHeight() < 500) {
                    attachment.setAttachSmallPath(new StringBuffer("/upload/").append(HaloUtils.YEAR).append("/").append(HaloUtils.MONTH).append("/").append(fileName).toString());
                } else {
                    attachment.setAttachSmallPath(new StringBuffer("/upload/").append(HaloUtils.YEAR).append("/").append(HaloUtils.MONTH).append("/").append(nameWithOutSuffix).append("_small.").append(fileSuffix).toString());
                    //剪裁图片
                    HaloUtils.cutCenterImage(new StringBuffer(mediaPath.getAbsolutePath()).append("/").append(fileName).toString(), new StringBuffer(mediaPath.getAbsolutePath()).append("/").append(nameWithOutSuffix).append("_small.").append(fileSuffix).toString(), 500, 500, fileSuffix);
                }

                attachment.setAttachType(file.getContentType());
                attachment.setAttachSuffix(new StringBuffer(".").append(fileSuffix).toString());
                attachment.setAttachCreated(new Date());
                attachmentService.saveByAttachment(attachment);

                updateConst();
                log.info("上传文件[" + fileName + "]到[" + mediaPath.getAbsolutePath() + "]成功");
                logsService.saveByLogs(
                        new Logs(LogsRecord.UPLOAD_FILE, fileName, HaloUtils.getIpAddr(request), new Date())
                );

                result.put("success", 1);
                result.put("message", "上传成功！");
                result.put("url", attachment.getAttachPath());
            } catch (Exception e) {
                log.error("未知错误：{0}", e.getMessage());
                result.put("success", 0);
                result.put("message", "上传失败！");
            }
        } else {
            log.error("文件不能为空");
        }
        return result;
    }

    /**
     * 处理获取附件详情的请求
     *
     * @param model    model
     * @param attachId 附件编号
     * @return 模板路径admin/widget/_attachment-detail
     */
    @GetMapping(value = "/attachment")
    public String attachmentDetail(Model model, @PathParam("attachId") Long attachId) {
        Optional<Attachment> attachment = attachmentService.findByAttachId(attachId);
        model.addAttribute("attachment", attachment.get());

        //设置选项
        model.addAttribute("options", HaloConst.OPTIONS);
        return "admin/widget/_attachment-detail";
    }

    /**
     * 移除附件的请求
     *
     * @param attachId 附件编号
     * @param request  request
     * @return true：移除附件成功，false：移除附件失败
     */
    @GetMapping(value = "/remove")
    @ResponseBody
    public boolean removeAttachment(@PathParam("attachId") Long attachId,
                                    HttpServletRequest request) {
        Optional<Attachment> attachment = attachmentService.findByAttachId(attachId);
        String delFileName = attachment.get().getAttachName();
        String delSmallFileName = delFileName.substring(0, delFileName.lastIndexOf('.')) + "_small" + attachment.get().getAttachSuffix();
        try {
            //删除数据库中的内容
            attachmentService.removeByAttachId(attachId);
            //刷新HaloConst变量
            updateConst();
            //删除文件
            File basePath = new File(ResourceUtils.getURL("classpath:").getPath());
            File mediaPath = new File(basePath.getAbsolutePath(), attachment.get().getAttachPath().substring(0, attachment.get().getAttachPath().lastIndexOf('/')));
            File delFile = new File(new StringBuffer(mediaPath.getAbsolutePath()).append("/").append(delFileName).toString());
            File delSmallFile = new File(new StringBuffer(mediaPath.getAbsolutePath()).append("/").append(delSmallFileName).toString());

            BufferedImage sourceImg = ImageIO.read(new FileInputStream(delFile));
            if (sourceImg.getWidth() > 500 && sourceImg.getHeight() > 500) {
                if (delSmallFile.exists()) {
                    if (delSmallFile.delete()) {
                        updateConst();
                    }
                }
            }
            if (delFile.exists() && delFile.isFile()) {
                if (delFile.delete()) {
                    updateConst();
                    log.info("删除文件[" + delFileName + "]成功！");
                    logsService.saveByLogs(
                            new Logs(LogsRecord.REMOVE_FILE, delFileName, HaloUtils.getIpAddr(request), new Date())
                    );
                } else {
                    log.error("删除附件[" + delFileName + "]失败！");
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("删除附件[" + delFileName + "]失败！:", e.getMessage());
            return false;
        }
        return true;
    }
}
