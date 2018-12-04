/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.cms.service;

import com.cheng.backend.framework.common.BaseService;
import com.cheng.backend.modules.cms.bean.Article;
import com.cheng.backend.modules.cms.bean.ArticleData;

import java.util.List;

/**
 * 文章service
 *
 * @author fengcheng
 * @version 2016-09-12
 */
public interface ArticleService extends BaseService<Article> {

    ArticleData getArticleData(String id);

    void updateArticle(Article object) throws Exception;

    List<Object[]> getByIds(String ids) throws Exception;

    void updateWeight();
}
