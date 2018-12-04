/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.cms.mapper;

import com.cheng.backend.framework.common.CommonMapper;
import com.cheng.backend.modules.cms.bean.ArticleData;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author fengcheng
 * @version 2017/2/28
 */
@Mapper
@Repository
public interface ArticleDataMapper extends CommonMapper<ArticleData> {

}
