/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.framework.security;

import com.bdfint.backend.framework.util.DateUtils;
import com.bdfint.backend.framework.util.Servlets;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.framework.util.UserAgentUtils;
import com.bdfint.backend.modules.sys.bean.User;
import com.bdfint.backend.modules.sys.service.LogService;
import com.bdfint.backend.modules.sys.service.UserService;
import com.bdfint.backend.modules.sys.utils.UserUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 表单验证（包含验证码）过滤类
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
@Service
public class FormAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {

    @Autowired
    private UserService userService;
    @Autowired
    private LogService logService;

    public static final String DEFAULT_CAPTCHA_PARAM = "validateCode";
    public static final String DEFAULT_MOBILE_PARAM = "mobileLogin";
    public static final String DEFAULT_MESSAGE_PARAM = "message";

    private String captchaParam = DEFAULT_CAPTCHA_PARAM;
    private String mobileLoginParam = DEFAULT_MOBILE_PARAM;
    private String messageParam = DEFAULT_MESSAGE_PARAM;

    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String username = getUsername(request);
        String password = getPassword(request);
        if (password == null) {
            password = "";
        }
        boolean rememberMe = isRememberMe(request);
        String host = StringUtils.getRemoteAddr((HttpServletRequest) request);
        String captcha = getCaptcha(request);
        boolean mobile = isMobileLogin(request);
        return new UsernamePasswordToken(username, password.toCharArray(), rememberMe, host, captcha, mobile);
    }

    public String getCaptchaParam() {
        return captchaParam;
    }

    protected String getCaptcha(ServletRequest request) {
        return WebUtils.getCleanParam(request, getCaptchaParam());
    }

    public String getMobileLoginParam() {
        return mobileLoginParam;
    }

    protected boolean isMobileLogin(ServletRequest request) {
        return WebUtils.isTrue(request, getMobileLoginParam());
    }

    public String getMessageParam() {
        return messageParam;
    }

    /**
     * 登录成功之后跳转URL
     */
    public String getSuccessUrl() {
        return super.getSuccessUrl();
    }

    /**
     * 登录成功调用事件
     */
    @Override
    protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
        SecurityRealm.Principal p = UserUtils.getPrincipal();
        if (p != null && !p.isMobileLogin()) {
            // 登录成功后，记录上次登录的时间和IP
            User user = UserUtils.getUser();
            if (user.getLoginDate() == null) {
                user.setLoginDate(new Date());
            }
            UserUtils.putCache("loginTime", DateUtils.formatDateTime(user.getLoginDate()));
            UserUtils.putCache("loginIp", user.getLoginIp());
            // 更新用户当前登录时间跟IP
            user.setLoginDate(new Date());
            user.setLoginIp(UserAgentUtils.getIpAddr(Servlets.getRequest()));
            userService.updateUserInfo(user);
            // 记录登录日志
            try {
                logService.save(Servlets.getRequest(), "系统登录");
            } catch (Exception e) {
                e.printStackTrace();
            }
            WebUtils.issueRedirect(request, response, getSuccessUrl(), null, true);
        } else {
            super.issueSuccessRedirect(request, response);
        }
    }

    /**
     * 登录失败调用事件
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token,
                                     AuthenticationException e, ServletRequest request, ServletResponse response) {
        String className = e.getClass().getName(), message = "";
        if (IncorrectCredentialsException.class.getName().equals(className)
                || UnknownAccountException.class.getName().equals(className)) {
            message = "用户或密码错误, 请重试.";
        } else if (e.getMessage() != null && StringUtils.startsWith(e.getMessage(), "msg:")) {
            message = StringUtils.replace(e.getMessage(), "msg:", "");
        } else {
            message = "系统出现点问题，请稍后再试！";
            e.printStackTrace(); // 输出到控制台
        }
        request.setAttribute(getFailureKeyAttribute(), className);
        request.setAttribute(getMessageParam(), message);
        return true;
    }

}