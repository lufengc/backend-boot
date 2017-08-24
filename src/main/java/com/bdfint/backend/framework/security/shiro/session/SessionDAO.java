/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.framework.security.shiro.session;

import org.apache.shiro.session.Session;

import java.util.Collection;

/**
 * SessionDao自定义
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
public interface SessionDAO extends org.apache.shiro.session.mgt.eis.SessionDAO {

    /**
     * 获取活动会话
     *
     * @param includeLeave 是否包括离线（最后访问时间大于3分钟为离线会话）
     * @return Collection<Session>
     */
    Collection<Session> getActiveSessions(boolean includeLeave);

    /**
     * 获取活动会话
     *
     * @param includeLeave  是否包括离线（最后访问时间大于3分钟为离线会话）
     * @param principal     根据登录者对象获取活动会话
     * @param filterSession 不为空，则过滤掉（不包含）这个会话。
     * @return Collection<Session>
     */
    Collection<Session> getActiveSessions(boolean includeLeave, Object principal, Session filterSession);

}
