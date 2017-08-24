/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.sys.utils;

import com.bdfint.backend.framework.cache.JedisUtils;
import com.bdfint.backend.framework.common.SpringContextHolder;
import com.bdfint.backend.framework.security.SecurityRealm;
import com.bdfint.backend.modules.sys.bean.*;
import com.bdfint.backend.modules.sys.service.*;
import com.google.common.collect.Lists;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户工具类
 *
 * @author fengcheng
 * @version 2016/7/28
 */
public class UserUtils {

    private static UserService userService = SpringContextHolder.getBean(UserService.class);
    private static RoleService roleService = SpringContextHolder.getBean(RoleService.class);
    private static MenuService menuService = SpringContextHolder.getBean(MenuService.class);
    private static AreaService areaService = SpringContextHolder.getBean(AreaService.class);
    private static OfficeService officeService = SpringContextHolder.getBean(OfficeService.class);

    public static final String USER_CACHE_ = "userCache_";
    public static final String USER_CACHE_ID_ = "id_";
    public static final String USER_CACHE_LOGIN_NAME_ = "loginName_";

    public static final String CACHE_ROLE_LIST = "roleList";
    public static final String CACHE_MENU_LIST = "menuList";
    public static final String CACHE_AREA_LIST = "areaList";
    public static final String CACHE_OFFICE_LIST = "officeList";
    public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";


    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 取不到返回null
     */
    public static User get(String id) {
        User user = (User) JedisUtils.getObject(USER_CACHE_ + USER_CACHE_ID_ + id);
        if (user == null) {
            try {
                user = userService.get(id);
                if (user != null) {
                    user.setRoleList(roleService.getRoleByUserId(id));
                    JedisUtils.setObject(USER_CACHE_ + USER_CACHE_ID_ + id, user, 0);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return user;
    }

    /**
     * 根据登录名获取用户
     *
     * @param loginName 登录名
     * @return 取不到返回null
     */
    public static User getUserByLoginName(String loginName) {
        User user = (User) JedisUtils.getObject(USER_CACHE_ + USER_CACHE_LOGIN_NAME_ + loginName);
        if (user == null) {
            user = userService.getUserByLoginName(loginName);
            if (user == null) {
                return null;
            }
            user.setRoleList(roleService.getRoleByUserId(user.getId()));
            JedisUtils.setObject(USER_CACHE_ + USER_CACHE_LOGIN_NAME_ + loginName, user, 0);
        }
        return user;
    }

    /**
     * 获取当前用户
     *
     * @return 取不到返回 new User()
     */
    public static User getUser() {
        SecurityRealm.Principal principal = getPrincipal();
        if (principal != null) {
            User user = get(principal.getId());
            if (user != null) {
                return user;
            }
            return new User();
        }
        return new User();
    }

    /**
     * 获取当前用户角色列表
     *
     * @return 角色列表
     */
    public static List<Role> getRoleList() {
        @SuppressWarnings("unchecked")
        List<Role> roleList = (List<Role>) getCache(CACHE_ROLE_LIST);
        try {
            if (roleList == null) {
                User user = getUser();
                if (user.isAdmin()) {
                    roleList = roleService.getList(new Role());
                } else {
                    roleList = roleService.getRoleByUserId(getUserId());
                }
                putCache(CACHE_ROLE_LIST, roleList);
            }
        } catch (Exception e) {
            roleList = new ArrayList<>();
        }
        return roleList;
    }

    /**
     * 获取当前用户授权菜单
     *
     * @return 权限菜单列表
     */
    public static List<Menu> getMenuList() {
        @SuppressWarnings("unchecked")
        List<Menu> menuList = (List<Menu>) getCache(CACHE_MENU_LIST);
        try {
            if (menuList == null) {
                User user = getUser();
                if (user.isAdmin()) {
                    Menu Menu = new Menu();
                    menuList = menuService.getList(Menu);
                } else {
                    menuList = menuService.getMenuByUserId(getUserId());
                }
                putCache(CACHE_MENU_LIST, menuList);
            }
        } catch (Exception e) {
            menuList = new ArrayList<>();
        }
        List<Menu> list = Lists.newArrayList();
        Menu.sort(menuList);
        Menu.sortList(list, menuList, Menu.getRootId(), true);
        return list;
    }

    /**
     * 获取当前用户授权的区域
     *
     * @return
     */
    public static List<Area> getAreaList() {
        @SuppressWarnings("unchecked")
        List<Area> areaList = (List<Area>) getCache(CACHE_AREA_LIST);
        try {
            if (areaList == null) {
                areaList = areaService.getList(new Area());
                putCache(CACHE_AREA_LIST, areaList);
            }
        } catch (Exception e) {
            areaList = new ArrayList<>();
        }
        return areaList;
    }

    /**
     * 获取当前用户有权限访问的部门
     */
    public static List<Office> getOfficeList() {
        @SuppressWarnings("unchecked")
        List<Office> officeList = (List<Office>) getCache(CACHE_OFFICE_LIST);
        try {
            if (officeList == null) {
                User user = getUser();
                if (user.isAdmin()) {
                    officeList = officeService.getList(new Office());
                } else {
                    officeList = officeService.getOfficeByUserId(getUserId());
                }
                putCache(CACHE_OFFICE_LIST, officeList);
            }
        } catch (Exception e) {
            officeList = new ArrayList<>();
        }
        return officeList;
    }

    /**
     * 获取授权主要对象
     */
    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * 获取当前登录者ID
     *
     * @return 取不到返回 new User()
     */
    public static String getUserId() {
        SecurityRealm.Principal principal = getPrincipal();
        return principal.getId();
    }

    /**
     * 获取当前登录者对象
     */
    public static SecurityRealm.Principal getPrincipal() {
        Subject subject = SecurityUtils.getSubject();
        return (SecurityRealm.Principal) subject.getPrincipal();
    }

    /**
     * 获取当前用户session
     *
     * @return 当前用户session
     */
    public static Session getSession() {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession(false);
        if (session == null) {
            session = subject.getSession();
        }
        return session;
    }

    // ============== User Cache ==============

    /**
     * 清除当前用户缓存
     */
    public static void clearCache() {
        removeCache(CACHE_ROLE_LIST);
        removeCache(CACHE_MENU_LIST);
        removeCache(CACHE_AREA_LIST);
        removeCache(CACHE_OFFICE_LIST);
        removeCache(CACHE_OFFICE_ALL_LIST);
    }

    /**
     * 清除指定用户缓存
     *
     * @param user 当前用户
     */
    public static void clearCache(User user) {
        JedisUtils.delObject(USER_CACHE_ + USER_CACHE_ID_ + user.getId());
        JedisUtils.delObject(USER_CACHE_ + USER_CACHE_LOGIN_NAME_ + user.getLoginName());
        JedisUtils.delObject(USER_CACHE_ + USER_CACHE_LOGIN_NAME_ + user.getOldLoginName());
    }


    public static Object getCache(String key) {
        return getCache(key, null);
    }

    public static Object getCache(String key, Object defaultValue) {
        Object obj = getSession().getAttribute(key);
        return obj == null ? defaultValue : obj;
    }

    public static void putCache(String key, Object value) {
        getSession().setAttribute(key, value);
    }

    public static void removeCache(String key) {
        getSession().removeAttribute(key);
    }


}

