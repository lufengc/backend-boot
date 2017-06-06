/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.cms.service;

import com.bdfint.backend.framework.common.BaseService;
import com.bdfint.backend.modules.cms.bean.Article;
import com.bdfint.backend.modules.cms.bean.ArticleData;

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
