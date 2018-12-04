/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.gen.service;


import com.cheng.backend.framework.common.BaseService;
import com.cheng.backend.modules.gen.bean.GenScheme;
import com.cheng.backend.modules.gen.bean.GenTable;

import java.util.List;

/**
 * 业务表Service
 *
 * @author fengcheng
 * @version 2016/7/28
 */
public interface GenTableService extends BaseService<GenTable> {

    List<GenTable> findTableListFormDb(GenTable genTable);

    boolean checkTableName(String name);

    GenTable getTableFormDb(GenTable genTable);

    String genCode(GenScheme genScheme) throws Exception;

}
