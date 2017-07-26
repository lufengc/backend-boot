/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.sys.service.impl;

import com.bdfint.backend.framework.common.BaseServiceImpl;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.sys.bean.Role;
import com.bdfint.backend.modules.sys.bean.User;
import com.bdfint.backend.modules.sys.mapper.RoleMapper;
import com.bdfint.backend.modules.sys.service.RoleService;
import com.bdfint.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统角色service实现类
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 根据用户ID获取角色列表
     *
     * @param userId 用户ID
     * @return List
     */
    @Override
    public List<Role> getRoleByUserId(String userId) {
        return roleMapper.selectRoleByUserId(userId);
    }

    /**
     * 给角色分配用户
     *
     * @param role Role
     * @param ids  要分配的用户ids
     */
    @Override
    @Transactional
    public void assignUserToRole(Role role, String ids) {
        roleMapper.assignUserToRole(role.getId(), ids.split(","));
    }

    /**
     * 移除角色中的用户
     *
     * @param role Role
     * @param user SysUser
     * @return boolean
     */
    @Override
    @Transactional
    public boolean outUserInRole(Role role, User user) {
        // 删除用户角色关联
        roleMapper.outUserInRole(role.getId(), user.getId());
        return true;
    }

    /**
     * 保存角色信息
     *
     * @param object object
     * @return 保存的ID
     */
    @Override
    @Transactional
    public String save(Role object) throws Exception {
        if (object.getId() != null) {
            object.preUpdate();
            super.update(object);
            //删除角色权限关联表
            roleMapper.deleteRoleMenuByRoleId(object.getId());
        } else {
            object.preInsert();
            super.insert(object);
        }
        if (StringUtils.isNotEmpty(object.getMenuIds())) {
            //保存角色权限关联表
            roleMapper.insertRoleMenu(object.getId(), object.getMenuIds().split(","));
        }
        // 清除用户角色缓存
        UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
        return object.getId();
    }

    /**
     * 删除角色
     *
     * @param ids 删除的ID
     */
    @Override
    @Transactional
    public int delete(String ids) throws Exception {
        //删除角色表
        super.delete(ids);
        //删除用户角色关联表
        roleMapper.deleteUserRoleByRoleId(ids);
        //删除角色权限关联表
        for (String roleId : ids.split(",")) {
            roleMapper.deleteRoleMenuByRoleId(roleId);
        }
        // 清除用户角色缓存
        UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
        return 1;
    }


}
