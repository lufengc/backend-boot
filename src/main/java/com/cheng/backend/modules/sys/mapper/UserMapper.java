/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.sys.mapper;

import com.cheng.backend.framework.common.CommonMapper;
import com.cheng.backend.modules.sys.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author fengcheng
 * @version 2017/2/28
 */
@Mapper
@Repository
public interface UserMapper extends CommonMapper<User> {

    List<User> selectUserByRoleId(String roleId);

    void deleteUserRoleByUserId(String userId);

    void insertUserRole(User user);
}
