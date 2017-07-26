/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.sys.service;

import com.bdfint.backend.framework.common.BaseService;
import com.bdfint.backend.modules.sys.bean.Menu;

import java.util.List;

/**
 * 系统权限sevice
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
public interface MenuService extends BaseService<Menu> {

    /**
     * 保存或更新操作
     *
     * @param object Object
     */
    String save(Menu object) throws Exception;

    /**
     * 根据角色ID获取权限
     *
     * @param roleId 角色ID
     * @return List
     */
    List<Menu> getMenuByRoleId(String roleId);

    /**
     * 根据用户ID获取权限
     *
     * @param userId 用户ID
     * @return List
     */
    List<Menu> getMenuByUserId(String userId);

    /**
     * 根据parentId获取权限
     *
     * @param parentId 父ID
     * @return List
     */
    List<Menu> getMenuByParentId(String parentId);

    /**
     * 更新排序
     *
     * @param ids   Integer[]
     * @param sorts Integer[]
     */
    void updateMenuSort(String[] ids, Integer[] sorts) throws Exception;
}
