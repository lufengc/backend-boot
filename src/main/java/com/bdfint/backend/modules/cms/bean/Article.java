/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.bdfint.backend.modules.cms.bean;

import com.bdfint.backend.framework.common.DataEntity;
import com.bdfint.backend.modules.cms.utils.CmsUtils;
import com.bdfint.backend.modules.sys.bean.User;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 文章Entity
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Table(name = "cms_article")
public class Article extends DataEntity<Article> {

    public static final String DEFAULT_TEMPLATE = "frontViewArticle";

    private static final long serialVersionUID = 1L;
    private String categoryId;// 分类编号
    private String title;    // 标题
    private String link;    // 外部链接
    private String color;    // 标题颜色（red：红色；green：绿色；blue：蓝色；yellow：黄色；orange：橙色）
    private String image;    // 文章图片
    private String keywords;// 关键字
    private String description;// 描述、摘要
    private Integer weight;    // 权重，越大越靠前
    private Date weightDate;// 权重期限，超过期限，将weight设置为0
    private Integer hits;    // 点击数
    private String posid;    // 推荐位，多选（1：首页焦点图；2：栏目页文章推荐；）
    private String customContentView;    // 自定义内容视图
    private String viewConfig;    // 视图参数


    @Transient
    private ArticleData articleData;    //文章副表
    @Transient
    private User user;
    @Transient
    private String categoryName;

    public Article() {
        super();
    }

    public Article(String id) {
        this();
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Length(min = 0, max = 255)
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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
        this.image = image;//CmsUtils.formatImageSrcToDb(image);
    }

    @Length(min = 0, max = 255)
    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Length(min = 0, max = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
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

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    @Length(min = 0, max = 10)
    public String getPosid() {
        return posid;
    }

    public void setPosid(String posid) {
        this.posid = posid;
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

    public ArticleData getArticleData() {
        return articleData;
    }

    public void setArticleData(ArticleData articleData) {
        this.articleData = articleData;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<String> getPosidList() {
        List<String> list = Lists.newArrayList();
        if (posid != null) {
            for (String s : StringUtils.split(posid, ",")) {
                list.add(s);
            }
        }
        return list;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPosidList(List<String> list) {
        posid = "," + StringUtils.join(list, ",") + ",";
    }

    public String getUrl() {
        return CmsUtils.getUrlDynamic(this);
    }

    public String getImageSrc() {
        return CmsUtils.formatImageSrcToWeb(this.image);
    }

}


