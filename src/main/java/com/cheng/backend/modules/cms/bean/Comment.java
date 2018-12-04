/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.modules.cms.bean;

import com.cheng.backend.framework.common.BaseEntity;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 评论Entity
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Table(name = "cms_comment")
public class Comment extends BaseEntity<Comment> {

    private static final long serialVersionUID = 1L;
    private String categoryId;// 分类编号
    private String contentId;    // 归属分类内容的编号（Article.id、Photo.id、Download.id）
    private String title;    // 归属分类内容的标题（Article.title、Photo.title、Download.title）
    private String content; // 评论内容
    private String name;    // 评论姓名
    private String ip;        // 评论IP
    private Date createDate;// 评论时间
    private String auditUserId; // 审核人
    private Date auditDate;    // 审核时间
    private String delFlag;    // 删除标记删除标记（0：正常；1：删除；2：审核）

    public Comment() {
        super();
    }

    public Comment(String id) {
        this();
        this.id = id;
    }

    @NotNull
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @NotNull
    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    @Length(min = 1, max = 255)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Length(min = 1, max = 255)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Length(min = 1, max = 100)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId;
    }

    public Date getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(Date auditDate) {
        this.auditDate = auditDate;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @NotNull
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Length(min = 1, max = 1)
    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

}