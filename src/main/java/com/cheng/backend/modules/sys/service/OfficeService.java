/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.modules.sys.service;


import com.cheng.backend.framework.common.BaseService;
import com.cheng.backend.modules.sys.bean.Office;

import java.util.List;

/**
 * 机构Service
 *
 * @author fengcheng
 * @version 2016/7/28
 */
public interface OfficeService extends BaseService<Office> {


    /**
     * 获取结构列表
     *
     * @param isAll true：所有机构数据，false：只查询当前用户拥有的机构数据
     * @return List<Office>
     */
    List<Office> getList(Boolean isAll) throws Exception;

    /**
     * 根据用户ID获取机构列表
     *
     * @param userId 用户id
     * @return List<Office>
     */
    List<Office> getOfficeByUserId(String userId);

    /**
     * 根据parentId查询该机构下所有的子列表数据
     *
     * @param parentIds parentIds
     * @return List<Office>
     */
    List<Office> getByParentIdsLike(String parentIds);

    /**
     * 获取某个机构下的所有子机构
     *
     * @param parentId 父id
     * @return List<Office>
     */
    List<Office> getByParentId(String parentId) throws Exception;

}
