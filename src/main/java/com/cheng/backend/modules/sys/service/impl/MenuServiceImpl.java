/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.sys.service.impl;

import com.cheng.backend.framework.cache.JedisUtils;
import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.framework.util.Encodes;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.sys.bean.Menu;
import com.cheng.backend.modules.sys.mapper.MenuMapper;
import com.cheng.backend.modules.sys.service.MenuService;
import com.cheng.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统权限sevice实现类
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
@Service
public class MenuServiceImpl extends BaseServiceImpl<Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    /**
     * 根据角色ID获取权限
     *
     * @param roleId 角色ID
     * @return List
     */
    @Override
    public List<Menu> getMenuByRoleId(String roleId) {
        return menuMapper.selectMenuByRoleId(roleId);
    }

    /**
     * 根据用户ID获取权限
     *
     * @param userId 用户ID
     * @return List
     */
    @Override
    public List<Menu> getMenuByUserId(String userId) {
        return menuMapper.selectMenuByUserId(userId);
    }

    /**
     * 根据parentId获取权限
     *
     * @param parentId 父ID
     * @return List
     */
    @Override
    public List<Menu> getMenuByParentId(String parentId) {
        Menu menu = new Menu();
        menu.setParentId(parentId);
        return menuMapper.select(menu);
    }

    @Override
    @Transactional
    public void updateMenuSort(String[] ids, Integer[] sorts) throws Exception {
        for (int i = 0; i < ids.length; i++) {
            Menu menu = new Menu(ids[i]);
            menu.setSort(sorts[i]);
            super.update(menu);
        }
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
        // 清除日志相关缓存
        JedisUtils.delObject(LogServiceImpl.CACHE_PERMISSION_NAME_PATH_MAP);
    }

    /**
     * 保存权限信息
     *
     * @param object object
     * @return 保存的ID
     */
    @Override
    @Transactional
    public String save(Menu object) throws Exception {
        String oldParentIds = object.getParentIds();
        Menu parent = get(object.getParentId());
        if (parent != null) {
            object.setParentIds(parent.getParentIds() + parent.getId() + ",");
        } else {
            object.setParentIds(Menu.getRootId());
        }
        if (StringUtils.isNotEmpty(object.getId())) {
            object.preUpdate();
            super.update(object);
            // 更新子节点parentIds
            Example example = new Example(Menu.class);
            example.createCriteria().andLike("parentId", "%," + object.getId() + ",%");
            List<Menu> menuList = menuMapper.selectByExample(example);
            if (menuList != null && menuList.size() > 0) {
                for (Menu p : menuList) {
                    p.setParentIds(p.getParentIds().replace(oldParentIds, object.getParentIds()));
                    super.update(p);
                }
            }
        } else {
            object.setId(Encodes.uuid());
            object.preInsert();
            super.insert(object);
        }
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
        // 清除日志相关缓存
        JedisUtils.delObject(LogServiceImpl.CACHE_PERMISSION_NAME_PATH_MAP);
        return object.getId();
    }

    /**
     * 删除权限信息
     *
     * @param id 删除的ID
     */
    @Override
    @Transactional
    public int delete(String id) throws Exception {
        StringBuilder ids = new StringBuilder(id);
        //获取子节点集合
        List<Menu> childList = new ArrayList<>();
        getChildList(childList, UserUtils.getMenuList(), id);
        for (Menu sysMenu : childList) {
            ids.append(",").append(sysMenu.getId());
        }
        //删除该菜单及所有子菜单
        super.delete(ids.toString());
        //删除角色权限关联表
        menuMapper.deleteRoleMenuByMenuIds(ids.toString().split(","));
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
        // 清除日志相关缓存
        JedisUtils.delObject(LogServiceImpl.CACHE_PERMISSION_NAME_PATH_MAP);
        return 1;
    }

    /**
     * 获取某个父节点下面的所有子节点
     *
     * @param childList 用户保存子节点的集合
     * @param allList   总数据结合
     * @param parentId  父ID
     */
    private void getChildList(List<Menu> childList, List<Menu> allList, String parentId) {
        for (Menu object : allList) {
            if (parentId.equals(object.getParentId())) {
                getChildList(childList, allList, object.getId());
                childList.add(object);
            }
        }
    }

}
