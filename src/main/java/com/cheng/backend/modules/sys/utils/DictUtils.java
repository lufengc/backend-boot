/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.sys.utils;

import com.alibaba.fastjson.JSON;
import com.cheng.backend.framework.cache.JedisUtils;
import com.cheng.backend.framework.common.SpringContextHolder;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.sys.bean.Dict;
import com.cheng.backend.modules.sys.service.DictService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 字典工具类
 *
 * @author fengcheng
 * @version 2016/7/28
 */
public class DictUtils {

    private static DictService dictService = SpringContextHolder.getBean(DictService.class);

    public static final String CACHE_DICT_MAP = "dictMap";

    /**
     * 根据多个字典值和字典名获取对应的标签
     *
     * @param value        字典名对应的值
     * @param type         字典名
     * @param defaultValue 默认值
     * @return 字典标签
     */
    public static String getDictLabel(String value, String type, String defaultValue) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(value)) {
            for (Dict dict : getDictList(type)) {
                if (type.equals(dict.getType()) && value.equals(dict.getValue())) {
                    return dict.getLabel();
                }
            }
        }
        return defaultValue;
    }

    /**
     * 根据多个字典值和字典名获取对应的标签（多个以逗号分隔）
     *
     * @param values       字典名对应的值（多个以逗号分隔）
     * @param type         字典名
     * @param defaultValue 默认值
     * @return 字典标签
     */
    public static String getDictLabels(String values, String type, String defaultValue) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(values)) {
            List<String> valueList = Lists.newArrayList();
            for (String value : StringUtils.split(values, ",")) {
                valueList.add(getDictLabel(value, type, defaultValue));
            }
            return StringUtils.join(valueList, ",");
        }
        return defaultValue;
    }

    /**
     * 根据某个字典label和字典类型获取值
     *
     * @param label        字典名对应的标签
     * @param type         字典名
     * @param defaultLabel 默认标签
     * @return 字典值
     */
    public static String getDictValue(String label, String type, String defaultLabel) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(label)) {
            for (Dict dict : getDictList(type)) {
                if (type.equals(dict.getType()) && label.equals(dict.getLabel())) {
                    return dict.getValue();
                }
            }
        }
        return defaultLabel;
    }

    /**
     * 根据字典名获取字典列表
     *
     * @param type 字典名 为空查询所有
     * @return List
     */
    public static List<Dict> getDictList(String type) {
        List<Dict> dictList = null;
        Map<String, List<Dict>> dictMap = getDictMap();
        if (dictMap != null) {
            if (type != null) {
                dictList = dictMap.get(type);
            } else {
                dictList = new ArrayList<>();
                for (List<Dict> sysDicts : dictMap.values()) {
                    dictList.addAll(sysDicts);
                }
            }
        }
        if (dictList == null) {
            dictList = Lists.newArrayList();
        }
        return dictList;
    }

    /**
     * 返回字典列表（JSON）
     *
     * @param type 字典名
     * @return json
     */
    public static String getDictListJson(String type) {
        return JSON.toJSONString(getDictList(type));
    }

    /**
     * 获取字典MAP
     *
     * @return Map
     */
    public static Map<String, List<Dict>> getDictMap() {
        try {
            List<Dict> dictList;
            @SuppressWarnings("unchecked")
            Map<String, List<Dict>> dictMap = (Map<String, List<Dict>>) JedisUtils.getObject(CACHE_DICT_MAP);
            if (dictMap == null) {
                dictMap = Maps.newHashMap();
                for (Dict dict : dictService.getList(new Dict())) {
                    dictList = dictMap.get(dict.getType());
                    if (dictList != null) {
                        dictList.add(dict);
                    } else {
                        dictMap.put(dict.getType(), Lists.newArrayList(dict));
                    }
                }
                JedisUtils.setObject(CACHE_DICT_MAP, dictMap, 0);
            }
            return dictMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回字典列表（JSON）
     *
     * @return json
     */
    public static String getDictMapJson() {
        return JSON.toJSONString(getDictMap());
    }
}
