/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.cms.service;

import com.cheng.backend.framework.common.BaseService;
import com.cheng.backend.modules.cms.bean.Article;
import com.cheng.backend.modules.cms.bean.Category;

import java.util.List;
import java.util.Map;

/**
 * 统计Service
 *
 * @author fengcheng
 * @version 2016-09-12
 */
public interface StatsService extends BaseService<Article> {

    List<Category> article(Map<String, Object> paramMap);
}
