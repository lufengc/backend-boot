/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.sys.service;


import com.bdfint.backend.framework.common.BaseService;
import com.bdfint.backend.modules.sys.bean.User;

import java.util.List;

/**
 * 用户service实现类
 *
 * @author lufengcheng
 * @version 2016-01-13 09:41:50
 */
public interface UserService extends BaseService<User> {


    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return User
     */
    User getUserByLoginName(String username);

    /**
     * 根据用户姓名查询
     *
     * @param name 姓名
     * @return User
     */
    List<User> getUserByName(String name);

    /**
     * 根据角色ID查询用户列表
     *
     * @param roleId roleId
     * @return List<User>
     */
    List<User> getUserByRoleId(String roleId);

    /**
     * 更新当前用户信息
     *
     * @param currentUser 当前用户
     */
    void updateUserInfo(User currentUser) throws Exception;

    /**
     * 根据机构ID查询用户列表
     *
     * @param officeId officeId
     * @return List<User>
     */
    List<User> getUserByOfficeId(String officeId);

    /**
     * 更新用户状态
     *
     * @param ids  用户ids
     * @param user User
     */
    int updateStatus(String ids, User user) throws Exception;

    /**
     * 初始化密码
     *
     * @param ids      用户ids
     * @param password 密码
     */
    int initPassword(String ids, String password) throws Exception;


    void dataLoad();
}
