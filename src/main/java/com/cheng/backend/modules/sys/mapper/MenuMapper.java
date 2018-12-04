/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.sys.mapper;

import com.cheng.backend.framework.common.CommonMapper;
import com.cheng.backend.modules.sys.bean.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author fengcheng
 * @version 2017/2/28
 */
@Mapper
@Repository
public interface MenuMapper extends CommonMapper<Menu> {

    List<Menu> selectMenuByUserId(String userId);

    List<Menu> selectMenuByRoleId(String roleId);

    void deleteRoleMenuByMenuIds(@Param("array") String[] ids);
}
