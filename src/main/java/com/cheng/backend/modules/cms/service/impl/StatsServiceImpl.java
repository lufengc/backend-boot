/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.modules.cms.service.impl;


import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.modules.cms.bean.Article;
import com.cheng.backend.modules.cms.bean.Category;
import com.cheng.backend.modules.cms.bean.Site;
import com.cheng.backend.modules.cms.service.StatsService;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 统计Service
 *
 * @author fengcheng
 * @version 2016-09-12
 */
@Service
public class StatsServiceImpl extends BaseServiceImpl<Article> implements StatsService {


    @Override
    public List<Category> article(Map<String, Object> paramMap) {
        Category category = new Category();
        category.setSiteId(Site.getCurrentSiteId());


        return Lists.newArrayList();
    }

    @Override
    public String save(Article object) throws Exception {
        return null;
    }
}
