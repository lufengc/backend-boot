/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.modules.cms.bean;

import com.cheng.backend.framework.common.TreeEntity;
import com.cheng.backend.modules.cms.utils.CmsUtils;
import com.google.common.collect.Lists;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * 栏目Entity
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Table(name = "cms_category")
public class Category extends TreeEntity<Category> {

    public static final String DEFAULT_TEMPLATE = "frontList";

    private static final long serialVersionUID = 1L;
    private String siteId;        // 归属站点
    private String officeId;    // 归属部门
    private String module;    // 栏目模型（article：文章；picture：图片；download：下载；link：链接；special：专题）
    private String image;    // 栏目图片
    private String href;    // 链接
    private String target;    // 目标（ _blank、_self、_parent、_top）
    private String description;    // 描述，填写有助于搜索引擎优化
    private String keywords;    // 关键字，填写有助于搜索引擎优化
    private String inMenu;        // 是否在导航中显示（1：显示；0：不显示）
    private String inList;        // 是否在分类页中显示列表（1：显示；0：不显示）
    private String showModes;    // 展现方式（0:有子栏目显示栏目列表，无子栏目显示内容列表;1：首栏目内容列表；2：栏目第一条内容）
    private String allowComment;// 是否允许评论
    private String isAudit;    // 是否需要审核
    private String customListView;        // 自定义列表视图
    private String customContentView;    // 自定义内容视图
    private String viewConfig;    // 视图参数

    @Transient
    private String cnt;//信息量
    @Transient
    private String hits;//点击量

    @Transient
    private String officeName;
    @Transient
    private String parentName;
    @Transient
    private List<Category> childList = Lists.newArrayList();    // 拥有子分类列表

    public Category() {
        super();
        this.delFlag = DEL_FLAG_NORMAL;
    }

    public Category(String id) {
        this();
        this.id = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    @Length(min = 0, max = 20)
    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Length(min = 0, max = 255)
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Length(min = 0, max = 255)
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

    @Length(min = 0, max = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Length(min = 0, max = 255)
    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Length(min = 1, max = 1)
    public String getInMenu() {
        return inMenu;
    }

    public void setInMenu(String inMenu) {
        this.inMenu = inMenu;
    }

    @Length(min = 1, max = 1)
    public String getInList() {
        return inList;
    }

    public void setInList(String inList) {
        this.inList = inList;
    }

    @Length(min = 1, max = 1)
    public String getShowModes() {
        return showModes;
    }

    public void setShowModes(String showModes) {
        this.showModes = showModes;
    }

    @Length(min = 1, max = 1)
    public String getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(String allowComment) {
        this.allowComment = allowComment;
    }

    @Length(min = 1, max = 1)
    public String getIsAudit() {
        return isAudit;
    }

    public void setIsAudit(String isAudit) {
        this.isAudit = isAudit;
    }

    public String getCustomListView() {
        return customListView;
    }

    public void setCustomListView(String customListView) {
        this.customListView = customListView;
    }

    public String getCustomContentView() {
        return customContentView;
    }

    public void setCustomContentView(String customContentView) {
        this.customContentView = customContentView;
    }

    public String getViewConfig() {
        return viewConfig;
    }

    public void setViewConfig(String viewConfig) {
        this.viewConfig = viewConfig;
    }

    public List<Category> getChildList() {
        return childList;
    }

    public void setChildList(List<Category> childList) {
        this.childList = childList;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public static void sortList(List<Category> list, List<Category> sourcelist, String parentId) {
        for (int i = 0; i < sourcelist.size(); i++) {
            Category e = sourcelist.get(i);
            if (e.getParentId() != null && e.getParentId().equals(parentId)) {
                list.add(e);
                // 判断是否还有子节点, 有则继续获取子节点
                for (int j = 0; j < sourcelist.size(); j++) {
                    Category child = sourcelist.get(j);
                    if (child.getParentId() != null && child.getParentId().equals(e.getId())) {
                        sortList(list, sourcelist, e.getId());
                        break;
                    }
                }
            }
        }
    }

    public String getIds() {
        return (this.getParentIds() != null ? this.getParentIds().replaceAll(",", " ") : "")
                + (this.getId() != null ? this.getId() : "");
    }

    public boolean isRoot() {
        return isRoot(this.id);
    }

    public static boolean isRoot(String id) {
        return id != null && id.equals("1");
    }

    public String getUrl() {
        return CmsUtils.getUrlDynamic(this);
    }
}