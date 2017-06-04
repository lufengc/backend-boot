/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.framework.security;

import com.bdfint.backend.framework.security.shiro.session.SessionDAO;
import com.bdfint.backend.framework.servlet.ValidateCodeServlet;
import com.bdfint.backend.framework.util.Encodes;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.sys.action.LoginAction;
import com.bdfint.backend.modules.sys.bean.Menu;
import com.bdfint.backend.modules.sys.bean.Role;
import com.bdfint.backend.modules.sys.bean.User;
import com.bdfint.backend.modules.sys.utils.UserUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 用户身份验证,授权 Realm 组件
 *
 * @author lufengcheng
 * @version 2015-6-16
 **/
@Service
public class SecurityRealm extends AuthorizingRealm {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SessionDAO sessionDao;

    /**
     * 登录验证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

        int activeSessionSize = sessionDao.getActiveSessions(false).size();
        if (logger.isDebugEnabled()) {
            logger.debug("login submit, active session size: {}, username: {}", activeSessionSize, token.getUsername());
        }

        // 校验登录验证码
        if (LoginAction.isValidateCodeLogin(token.getUsername(), false, false)) {
            Session session = UserUtils.getSession();
            String code = null;
            if (session != null) {
                code = (String) session.getAttribute(ValidateCodeServlet.VALIDATE_CODE);
            }
            if (token.getCaptcha() == null || !token.getCaptcha().toUpperCase().equals(code)) {
                throw new AuthenticationException("msg:验证码错误, 请重试.");
            }
        }
        // 校验用户名密码
        User user = UserUtils.getUserByLoginName(token.getUsername());
        if (user != null) {
            if (Objects.equals(user.getDelFlag(), User.DEL_FLAG_DELETE)) {
                throw new AuthenticationException("msg:该帐号不存在.");
            }
            if (Objects.equals(user.getDelFlag(), User.DEL_FLAG_AUDIT)) {
                throw new AuthenticationException("msg:该帐号还未审核通过，请耐心等待或联系管理员.");
            }
            if (Objects.equals(user.getDelFlag(), User.DEL_FLAG_LOCK)) {
                throw new AuthenticationException("msg:该帐号已被锁定，请联系管理员.");
            }
            byte[] salt = Encodes.decodeHex(user.getPassword().substring(0, 16));
            return new SimpleAuthenticationInfo(new Principal(user, token.isMobileLogin()),
                    user.getPassword().substring(16), ByteSource.Util.bytes(salt), getName());
        } else {
            throw new AuthenticationException("msg:该帐号不存在.");
        }
    }

    /**
     * 权限检查
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Principal principal = (Principal) getAvailablePrincipal(principals);
        final User user = UserUtils.getUserByLoginName(principal.getLoginName());
        if (user != null) {
            SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
            // 添加基于Permission的权限信息
            List<Menu> list = UserUtils.getMenuList();
            for (Menu menu : list) {
                if (StringUtils.isNotBlank(menu.getPermission())) {
                    for (String sign : StringUtils.split(menu.getPermission(), ",")) {
                        authorizationInfo.addStringPermission(sign);
                    }
                }
            }
            // 添加用户权限
            authorizationInfo.addStringPermission("user");
            // 添加用户角色信息
            for (Role role : user.getRoleList()) {
                authorizationInfo.addRole(role.getEnname());
            }
            return authorizationInfo;
        } else {
            return null;
        }
    }

    @Override
    protected void checkPermission(Permission permission, AuthorizationInfo info) {
        authorizationValidate(permission);
        super.checkPermission(permission, info);
    }

    @Override
    protected boolean[] isPermitted(List<Permission> permissions, AuthorizationInfo info) {
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
                authorizationValidate(permission);
            }
        }
        return super.isPermitted(permissions, info);
    }

    @Override
    public boolean isPermitted(PrincipalCollection principals, Permission permission) {
        authorizationValidate(permission);
        return super.isPermitted(principals, permission);
    }

    @Override
    protected boolean isPermittedAll(Collection<Permission> permissions, AuthorizationInfo info) {
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
                authorizationValidate(permission);
            }
        }
        return super.isPermittedAll(permissions, info);
    }

    /**
     * 授权验证方法
     *
     */
    private void authorizationValidate(Permission permission) {
        // 模块授权预留接口
    }

    /**
     * 设定密码校验的Hash算法与迭代次数
     */
    @PostConstruct
    public void initCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(Encodes.HASH_ALGORITHM);
        matcher.setHashIterations(Encodes.HASH_INTERATIONS);
        setCredentialsMatcher(matcher);
    }

    /**
     * 清空用户关联权限认证，待下次使用时重新加载
     */
    /*public void clearCachedAuthorizationInfo(Principal principal) {
        SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}*/

    /**
     * 清空所有关联认证
     * @Deprecated 不需要清空，授权缓存保存到session中
     */
    /*@Deprecated
	public void clearAllCachedAuthorizationInfo() {
		Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
		if (cache != null) {
			for (Object key : cache.keys()) {
				cache.remove(key);
			}
		}
	}*/

    /**
     * 授权用户信息
     */
    public static class Principal implements Serializable {

        private static final long serialVersionUID = 1L;

        private String id; // 编号
        private String loginName; // 登录名
        private String name; // 姓名
        private boolean mobileLogin; // 是否手机登录

        public Principal(User user, boolean mobileLogin) {
            this.id = user.getId();
            this.loginName = user.getLoginName();
            this.name = user.getName();
            this.mobileLogin = mobileLogin;
        }

        public String getId() {
            return id;
        }

        public String getLoginName() {
            return loginName;
        }

        public String getName() {
            return name;
        }

        public boolean isMobileLogin() {
            return mobileLogin;
        }

        /**
         * 获取SESSIONID
         */
        public String getSessionid() {
            try {
                return (String) UserUtils.getSession().getId();
            } catch (Exception e) {
                return "";
            }
        }

        @Override
        public String toString() {
            return id;
        }

    }

}
