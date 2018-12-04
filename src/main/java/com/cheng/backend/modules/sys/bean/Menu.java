/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.modules.sys.bean;

import com.cheng.backend.framework.common.DataEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * 菜单Entity
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Table(name = "sys_menu")
public class Menu extends DataEntity<Menu> {

    private static final long serialVersionUID = 1L;
    private String parentId;    // 父级菜单
    private String parentIds; // 所有父级编号
    private String name;    // 名称
    private String href;    // 链接
    private String target;    // 目标（ mainFrame、_blank、_self、_parent、_top）
    private String icon;    // 图标
    private Integer sort;    // 排序
    private String isShow;    // 是否在菜单中显示（1：显示；0：不显示）
    private String permission; // 权限标识

    @Transient
    private String parentName;

    public Menu() {
        super();
    }

    public Menu(String id) {
        super(id);
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Length(min = 1, max = 2000)
    public String getParentIds() {
        return parentIds;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    @Length(min = 1, max = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Length(min = 0, max = 2000)
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @Length(min = 0, max = 20)
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Length(min = 0, max = 100)
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @NotNull
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Length(min = 1, max = 1)
    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    @Length(min = 0, max = 200)
    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @JsonIgnore
    public static void sort(List<Menu> sourcelist) {
        sourcelist.sort((o1, o2) -> {
            // 按sortId升序排序
            if (o1.getSort() > o2.getSort()) {
                return 1;
            } else if (Objects.equals(o1.getSort(), o2.getSort())) {
                return 0;
            } else {
                return -1;
            }
        });
    }

    @JsonIgnore
    public static void sortList(List<Menu> list, List<Menu> sourcelist, String parentId, boolean cascade) {
        for (Menu e : sourcelist) {
            if (Objects.equals(e.getParentId(), parentId)) {
                list.add(e);
                if (cascade) {
                    // 判断是否还有子节点, 有则继续获取子节点
                    for (Menu child : sourcelist) {
                        if (child.getParentId().equals(e.getId())) {
                            sortList(list, sourcelist, e.getId(), true);
                            break;
                        }
                    }
                }
            }
        }
    }

    @JsonIgnore
    public static String getRootId() {
        return "0";
    }

    @Override
    public String toString() {
        return name;
    }
}