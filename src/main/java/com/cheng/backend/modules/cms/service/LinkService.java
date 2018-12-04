/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.cms.service;


import com.cheng.backend.framework.common.BaseService;
import com.cheng.backend.modules.cms.bean.Link;

import java.util.List;

/**
 * 链接Service
 *
 * @author fengcheng
 * @version 2016-09-12
 */
public interface LinkService extends BaseService<Link> {


    List<Object[]> findByIds(String ids) throws Exception;
}
