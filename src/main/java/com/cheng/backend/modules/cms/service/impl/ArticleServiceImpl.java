/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.cms.service.impl;

import com.cheng.backend.framework.cache.JedisUtils;
import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.cms.bean.Article;
import com.cheng.backend.modules.cms.bean.ArticleData;
import com.cheng.backend.modules.cms.bean.Link;
import com.cheng.backend.modules.cms.mapper.ArticleDataMapper;
import com.cheng.backend.modules.cms.mapper.ArticleMapper;
import com.cheng.backend.modules.cms.service.ArticleService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * 文章Service实现类
 *
 * @author fengcheng
 * @version 2016-09-12
 */
@Service
public class ArticleServiceImpl extends BaseServiceImpl<Article> implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleDataMapper articleDataMapper;

    /**
     * 更新过期的权重，间隔时间为6个小时
     */
    @Override
    public void updateWeight() {
        // 更新过期的权重，间隔为“6”个小时
        Date updateExpiredWeightDate = (Date) JedisUtils.getObject("updateExpiredWeightDateByArticle");
        if (updateExpiredWeightDate == null || updateExpiredWeightDate.getTime() < new Date().getTime()) {
            Article article = new Article();
            article.setWeight(0);
            Example example = new Example(Link.class);
            example.createCriteria().andGreaterThan("weight", 0)
                    .andLessThan("weightDate", new Date());
            articleMapper.updateByExampleSelective(article, example);
            JedisUtils.setObject("updateExpiredWeightDateByArticle", DateUtils.addHours(new Date(), 6), 0);
        }
    }

    /**
     * 保存
     *
     * @param object object
     */
    @Override
    public String save(Article object) throws Exception {
        if (StringUtils.isNotBlank(object.getViewConfig())) {
            object.setViewConfig(StringEscapeUtils.unescapeHtml4(object.getViewConfig()));
        }
        if (StringUtils.isNotEmpty(object.getId())) {
            object.preUpdate();
            articleMapper.updateByPrimaryKeySelective(object);
            object.getArticleData().setId(object.getId());
            articleDataMapper.updateByPrimaryKeySelective(object.getArticleData());
        } else {
            object.preInsert();
            articleMapper.insertSelective(object);
            object.getArticleData().setId(object.getId());
            articleDataMapper.insertSelective(object.getArticleData());
        }
        return object.getId();
    }

    /**
     * 获取文章内容
     *
     * @param id 文章内容ID
     * @return 文章内容
     */
    @Override
    public ArticleData getArticleData(String id) {
        return articleDataMapper.selectByPrimaryKey(new ArticleData(id));
    }

    /**
     * 更新文章信息（点击数）
     *
     * @param object 文章列表对象
     */
    @Override
    public void updateArticle(Article object) throws Exception {
        super.update(object);
    }

    /**
     * 删除
     *
     * @param ids 删除的ID
     * @throws Exception
     */
    @Override
    public int delete(String ids) throws Exception {
        for (String id : ids.split(",")) {
            articleMapper.deleteByPrimaryKey(id);
            articleDataMapper.deleteByPrimaryKey(id);
        }
        return 1;
    }

    /**
     * 通过编号获取内容标题
     *
     * @return new Object[]{栏目Id,文章Id,文章标题}
     */
    @Override
    public List<Object[]> getByIds(String ids) throws Exception {
        List<Article> articles = Lists.newArrayList();
        for (String id : ids.split(",")) {
            articles.add(super.get(id));
        }
        List<Object[]> list = Lists.newArrayList();
        for (Article e : articles) {
            list.add(new Object[]{e.getCategoryId(), e.getId(), StringUtils.abbr(e.getTitle(), 50)});
        }
        return list;
    }

}
