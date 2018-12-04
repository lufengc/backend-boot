/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.framework.common;

/**
 * 数据Entity类
 *
 * @author lufengcheng
 * @version 2016-05-16
 */
public abstract class TreeEntity<T> extends DataEntity<T> {

    private static final long serialVersionUID = 1L;

    protected String parentId;    // 父级编号
    protected String parentIds; // 所有父级编号
    protected String name;    // 机构名称
    protected Integer sort;        // 排序

    public TreeEntity() {
        super();
    }

    public TreeEntity(String id) {
        super(id);
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

}
