/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.sys.service.impl;

import com.bdfint.backend.framework.cache.JedisUtils;
import com.bdfint.backend.framework.common.BaseServiceImpl;
import com.bdfint.backend.framework.util.Encodes;
import com.bdfint.backend.framework.util.Exceptions;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.sys.bean.Log;
import com.bdfint.backend.modules.sys.bean.Menu;
import com.bdfint.backend.modules.sys.bean.User;
import com.bdfint.backend.modules.sys.mapper.LogMapper;
import com.bdfint.backend.modules.sys.mapper.MenuMapper;
import com.bdfint.backend.modules.sys.service.LogService;
import com.bdfint.backend.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 系统日志service实现类
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
@Service
public class LogServiceImpl extends BaseServiceImpl<Log> implements LogService {

    @Autowired
    private LogMapper logMapper;
    @Autowired
    private MenuMapper menuMapper;

    static final String CACHE_PERMISSION_NAME_PATH_MAP = "permissionNamePathMap";

    public String save(Log object) throws Exception {
        return null;
    }

    /**
     * 保存日志
     *
     * @param request HttpServletRequest
     * @param title   日志标题
     */
    @Override
    public void save(HttpServletRequest request, String title) throws Exception {
        save(request, null, null, title);
    }

    /**
     * 保存日志
     *
     * @param request HttpServletRequest
     * @param handler Object
     * @param ex      Exception
     * @param title   日志标题
     */
    @Override
    public void save(HttpServletRequest request, Object handler, Exception ex, String title) throws Exception {
        User user = UserUtils.getUser();
        if (user != null && user.getId() != null) {
            Log log = new Log();
            log.setId(Encodes.uuid());
            log.setTitle(title);
            log.setType(ex == null ? Log.TYPE_ACCESS : Log.TYPE_EXCEPTION);
            log.setRemoteAddr(StringUtils.getRemoteAddr(request));
            log.setUserAgent(request.getHeader("user-agent"));
            log.setRequestUri(request.getRequestURI());
            log.setParams(request.getParameterMap());
            log.setMethod(request.getMethod());
            // 异步保存日志
            new SaveLogThread(log, handler, ex).start();
        }
    }

    /**
     * 保存日志线程
     */
    private class SaveLogThread extends Thread {

        private Log log;
        private Object handler;
        private Exception ex;

        SaveLogThread(Log log, Object handler, Exception ex) {
            super(SaveLogThread.class.getSimpleName());
            this.log = log;
            this.handler = handler;
            this.ex = ex;
        }

        @Override
        public void run() {
            // 获取日志标题
            if (StringUtils.isBlank(log.getTitle())) {
                String permission = "";
                if (handler instanceof HandlerMethod) {
                    Method m = ((HandlerMethod) handler).getMethod();
                    RequiresPermissions rp = m.getAnnotation(RequiresPermissions.class);
                    permission = (rp != null ? StringUtils.join(rp.value(), ",") : "");
                }
                log.setTitle(getMenuNamePath(log.getRequestUri(), permission));
            }
            // 如果有异常，设置异常信息
            String exception = Exceptions.getStackTraceAsString(ex);
            if (exception != null) {
                exception = exception.replaceAll("'", "\"");
            }
            log.setException(exception);
            // 如果无标题并无异常日志，则不保存信息
            if (StringUtils.isBlank(log.getTitle()) && StringUtils.isBlank(log.getException())) {
                return;
            }
            // 保存日志信息
            try {
                log.setCreateBy(UserUtils.getUserId());
                log.setCreateDate(new Date());
                logMapper.insertSelective(log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取菜单名称路径（如：系统设置-机构用户-用户管理-编辑）
     *
     * @param requestUri 请求URL
     * @param permission 权限
     * @return 菜单路径
     */
    private String getMenuNamePath(String requestUri, String permission) {
        String href = StringUtils.substringAfter(requestUri, "");
        href = href.substring(1, href.length());
        @SuppressWarnings("unchecked")
        Map<String, String> permissionMap = (Map<String, String>) JedisUtils.getObject(CACHE_PERMISSION_NAME_PATH_MAP);
        if (permissionMap == null) {
            permissionMap = Maps.newHashMap();
            List<Menu> Menus = menuMapper.selectAll();
            for (Menu bean : Menus) {
                // 获取菜单名称路径（如：系统设置-机构用户-用户管理-编辑）
                String namePath = "";
                if (bean.getParentIds() != null) {
                    List<String> namePathList = Lists.newArrayList();
                    for (String id : StringUtils.split(bean.getParentIds(), ",")) {
                        for (Menu m : Menus) {
                            if (Objects.equals(m.getId(), id)) {
                                namePathList.add(m.getName());
                                break;
                            }
                        }
                    }
                    namePathList.add(bean.getName());
                    namePath = StringUtils.join(namePathList, "-");
                }
                // 设置菜单名称路径
                if (StringUtils.isNotBlank(bean.getHref())) {
                    permissionMap.put(bean.getHref(), namePath);
                } else if (StringUtils.isNotBlank(bean.getPermission())) {
                    for (String p : StringUtils.split(bean.getPermission())) {
                        permissionMap.put(p, namePath);
                    }
                }
            }
            JedisUtils.setObject(CACHE_PERMISSION_NAME_PATH_MAP, permissionMap, 0);
        }
        String MenuNamePath = permissionMap.get(href);
        if (MenuNamePath == null) {
            for (String p : StringUtils.split(permission)) {
                MenuNamePath = permissionMap.get(p);
                if (StringUtils.isNotBlank(MenuNamePath)) {
                    break;
                }
            }
            if (MenuNamePath == null) {
                return "";
            }
        }
        return MenuNamePath;
    }

    /**
     * 删除日志信息
     *
     * @param ids 删除的ID
     */
    @Override
    public int delete(String ids) throws Exception {
        return super.delete(ids);
    }

    /**
     * 清空所有日志信息
     */
    @Override
    public void empty() {
        Example example = new Example(Log.class);
        logMapper.deleteByExample(example);
    }
}
