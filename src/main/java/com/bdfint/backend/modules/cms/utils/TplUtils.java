/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.cms.utils;


import com.bdfint.backend.framework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengcheng
 * @version 2016-09-12
 */
public class TplUtils {
    /**
     * 去除模板前缀
     *
     * @param list     模板列表
     * @param prefix   模板前缀  例如“frontViewArticle”
     * @param include  需包含的模板  例如“/WEB-INF/views/modules/cms/front/themes/jeesite/articleSelectList.jsp”
     * @param excludes 需去除的模板  例如“frontViewArticle”
     */
    public static List<String> tplTrim(List<String> list, String prefix, String include, String... excludes) {
        List<String> result = new ArrayList<String>();
        if (!StringUtils.isBlank(include) && !list.contains(include)) {
            if (!tplContain(excludes, include)) {
                int start = include.lastIndexOf("/");
                int end = include.lastIndexOf(".");
                if (start == -1 || end == -1) {
                    throw new RuntimeException("include not contain '/' or '.':" + include);
                }
                result.add(include.substring(start + 1, end));
            }
        }
        for (String t : list) {
            if (!tplContain(excludes, t)) {
                int start = t.lastIndexOf("/");
                int end = t.lastIndexOf(".");
                if (start == -1 || end == -1) {
                    throw new RuntimeException("name not contain '/' or '.':" + t);
                }
                t = t.substring(start + 1, end);
                if (t.contains(prefix)) {
                    result.add(t);
                }
            }
        }
        return result;
    }

    /**
     * 检查tpl是否存在于excludes里面。
     *
     * @param excludes 需去除的模板  例如“frontViewArticle”
     * @param tpl      模版
     */
    private static boolean tplContain(String[] excludes, String tpl) {
        int start = tpl.lastIndexOf("/");
        int end = tpl.lastIndexOf(".");
        if (start == -1 || end == -1) {
            throw new RuntimeException("tpl not contain '/' or '.':" + tpl);
        }
        String name = tpl.substring(start + 1, end);
        for (String e : excludes) {
            if (e.equals(name)) {
                return true;
            }
        }
        return false;
    }
}