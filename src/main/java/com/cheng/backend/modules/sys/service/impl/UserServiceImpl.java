/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.sys.service.impl;

import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.framework.util.Encodes;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.sys.bean.User;
import com.cheng.backend.modules.sys.mapper.UserMapper;
import com.cheng.backend.modules.sys.service.UserService;
import com.cheng.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户service实现类
 *
 * @author lufengcheng
 * @version 2016-01-15 09:53:27
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据登录名查询
     *
     * @param loginName 用户名
     * @return User
     */
    @Override
    public User getUserByLoginName(String loginName) {
        User user = new User();
        user.setDelFlag(null);
        user.setLoginName(loginName);
        return userMapper.selectOne(user);
    }

    @Override
    public List<User> getUserByName(String name) {
        User user = new User();
        user.setName(name);
        return userMapper.select(user);
    }

    /**
     * 根据角色ID获取用户信息
     *
     * @param roleId 角色ID
     * @return List<User>
     */
    @Override
    public List<User> getUserByRoleId(String roleId) {
        return userMapper.selectUserByRoleId(roleId);
    }

    /**
     * 更新当前用户信息
     *
     * @param currentUser 当前用户
     */
    @Override
    @Transactional
    public void updateUserInfo(User currentUser) throws Exception {
        super.update(currentUser);
        UserUtils.clearCache();
        UserUtils.clearCache(currentUser);
    }

    /**
     * 根据机构ID获取用户信息
     *
     * @param officeId 机构ID
     * @return List<User>
     */
    @Override
    public List<User> getUserByOfficeId(String officeId) {
        User user = new User();
        user.setOfficeId(officeId);
        return userMapper.select(user);
    }

    /**
     * 更新用户状态
     */
    @Override
    @Transactional
    public int updateStatus(String ids, User user) throws Exception {
        String[] split = ids.split(",");
        for (String id : split) {
            user.setId(id);
            super.update(user);
            user.setLoginName(get(id).getLoginName());
            // 清除用户缓存
            UserUtils.clearCache(user);
        }
        return ids.length();
    }

    /**
     * 密码初始化
     *
     * @param ids      需要初始化的id
     * @param password 需要初始化的密码
     */
    @Override
    @Transactional
    public int initPassword(String ids, String password) throws Exception {
        String entryptPassword = Encodes.encryptPassword(password);
        User user;
        String[] split = ids.split(",");
        for (String id : split) {
            user = new User();
            user.setId(id);
            user.setPassword(entryptPassword);
            super.update(user);
            user.setLoginName(get(id).getLoginName());
            // 清除用户缓存
            UserUtils.clearCache(user);
        }
        return ids.length();
    }

    /**
     * 保存用户信息
     *
     * @param object User
     */
    @Override
    @Transactional
    public String save(User object) throws Exception {
        if (StringUtils.isNoneEmpty(object.getId())) {
            // 如果新密码为空，则不更换密码
            if (StringUtils.isNotBlank(object.getPassword())) {
                object.setPassword(Encodes.encryptPassword(object.getPassword()));
            } else {
                object.setPassword(null);
            }
            object.preUpdate();
            super.update(object);
            userMapper.deleteUserRoleByUserId(object.getId());
        } else {
            if (StringUtils.isNotBlank(object.getPassword())) {
                object.setPassword(Encodes.encryptPassword(object.getPassword()));
            } else {
                object.setPassword(Encodes.encryptPassword("123456"));
            }
            object.preInsert();
            super.insert(object);
        }
        //更新用户角色关联
        userMapper.insertUserRole(object);
        // 清除当前用户缓存
        if (object.getLoginName().equals(UserUtils.getUser().getLoginName())) {
            UserUtils.clearCache();
        }
        // 清除用户缓存
        UserUtils.clearCache(object);
        return object.getId();
    }

    /**
     * 删除用户信息（逻辑删除）
     *
     * @param ids 要删除的ID
     */
    @Override
    @Transactional
    public int delete(String ids) throws Exception {
        User user;
        String[] split = ids.split(",");
        for (String id : split) {
            user = new User();
            user.setId(id);
            user.setDelFlag(User.DEL_FLAG_DELETE);
            super.update(user);
            user.setLoginName(get(id).getLoginName());
            // 清除用户缓存
            UserUtils.clearCache(user);
        }
        return ids.length();
    }
}
