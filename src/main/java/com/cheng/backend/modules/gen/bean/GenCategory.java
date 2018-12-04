/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.gen.bean;


import com.cheng.backend.modules.sys.bean.Dict;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * 生成方案Entity
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@XmlRootElement(name = "category")
public class GenCategory extends Dict {

    private static final long serialVersionUID = 1L;
    private List<String> template;            // 主表模板

    public static String CATEGORY_REF = "category-ref:";

    public GenCategory() {
        super();
    }

    @XmlElement(name = "template")
    public List<String> getTemplate() {
        return template;
    }

    public void setTemplate(List<String> template) {
        this.template = template;
    }

}


