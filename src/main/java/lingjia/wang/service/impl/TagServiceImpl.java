package lingjia.wang.service.impl;

import lingjia.wang.model.domain.Tag;
import lingjia.wang.repository.TagRepository;
import lingjia.wang.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author : RYAN0UP
 * @version : 1.0
 * @date : 2018/1/12
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    /**
     * 新增/修改标签
     *
     * @param tag tag
     * @return Tag
     */
    @Override
    public Tag saveByTag(Tag tag) {
        return tagRepository.save(tag);
    }

    /**
     * 根据编号移除标签
     *
     * @param tagId tagId
     * @return Tag
     */
    @Override
    public Tag removeByTagId(Long tagId) {
        Optional<Tag> tag = findByTagId(tagId);
        tagRepository.delete(tag.get());
        return tag.get();
    }

    /**
     * 获取所有标签
     *
     * @return list
     */
    @Override
    public List<Tag> findAllTags() {
        return tagRepository.findAll();
    }

    /**
     * 根据编号查询标签
     *
     * @param tagId tagId
     * @return Link
     */
    @Override
    public Optional<Tag> findByTagId(Long tagId) {
        return tagRepository.findById(tagId);
    }

    /**
     * 根据标签路径查询
     *
     * @param tagUrl tagUrl
     * @return tag
     */
    @Override
    public Tag findByTagUrl(String tagUrl) {
        return tagRepository.findTagByTagUrl(tagUrl);
    }

    /**
     * 根据标签名称查询
     *
     * @param tagName tagName
     * @return tag
     */
    @Override
    public Tag findTagByTagName(String tagName) {
        return tagRepository.findTagByTagName(tagName);
    }

    /**
     * 转换标签字符串为实体集合
     *
     * @param tagList tagList
     * @return list
     */
    @Override
    public List<Tag> strListToTagList(String tagList) {
        String[] tags = tagList.split(",");
        List<Tag> tagsList = new ArrayList<>();
        for (String tag : tags) {
            Tag t = findTagByTagName(tag);
            Tag nt = null;
            if (null != t) {
                tagsList.add(t);
            } else {
                nt = new Tag();
                nt.setTagName(tag);
                nt.setTagUrl(tag);
                tagsList.add(saveByTag(nt));
            }
        }
        return tagsList;
    }
}
