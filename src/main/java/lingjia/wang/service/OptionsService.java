package lingjia.wang.service;

import lingjia.wang.model.domain.Options;

import java.util.Map;

/**
 * @author : RYAN0UP
 * @version : 1.0
 * @date : 2017/11/14
 */
public interface OptionsService {

    /**
     * 保存单个设置选项
     *
     * @param key   key
     * @param value value
     */
    void saveOption(String key, String value);

    /**
     * 保存多个设置选项
     *
     * @param options options
     */
    void saveOptions(Map<String, String> options);

    /**
     * 移除设置选项
     *
     * @param options options
     */
    void removeOption(Options options);

    /**
     * 获取所有设置选项
     *
     * @return map
     */
    Map<String, String> findAllOptions();

    /**
     * 根据key查询单个设置
     *
     * @param key key
     * @return String
     */
    String findOneOption(String key);
}
