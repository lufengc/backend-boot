/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.modules.cms.bean;

import com.cheng.backend.framework.common.DataEntity;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import java.util.Date;

/**
 * 链接Entity
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Table(name = "cms_link")
public class Link extends DataEntity<Link> {

    private static final long serialVersionUID = 1L;
    private String categoryId;// 分类编号
    private String title;    // 链接名称
    private String color;    // 标题颜色（red：红色；green：绿色；blue：蓝色；yellow：黄色；orange：橙色）
    private String image;    // 链接图片
    private String href;    // 链接地址
    private Integer weight;    // 权重，越大越靠前
    private Date weightDate;// 权重期限，超过期限，将weight设置为0
    private String userId;        //关联用户

    public Link() {
        super();
        this.weight = 0;
    }

    public Link(String id) {
        this();
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Length(min = 1, max = 255)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Length(min = 0, max = 50)
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Date getWeightDate() {
        return weightDate;
    }

    public void setWeightDate(Date weightDate) {
        this.weightDate = weightDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}