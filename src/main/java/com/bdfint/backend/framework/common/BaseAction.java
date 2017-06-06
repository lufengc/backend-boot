/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.framework.common;

import com.bdfint.backend.framework.mapper.JsonMapper;
import com.bdfint.backend.framework.util.BeanValidators;
import com.bdfint.backend.framework.util.DateUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * BaseAction
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
public abstract class BaseAction {

    /**
     * 日志的记录
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 管理基础路径
     */
    @Value("${adminPath}")
    protected String adminPath;

    /**
     * 前端基础路径
     */
    @Value("${frontPath}")
    protected String frontPath;

//    /**
//     * 数据模型驱动
//     *
//     * @param id ID
//     * @return T
//     */
//    protected abstract T get(@RequestParam(required = false) String id) throws Exception;
//
//    /**
//     * 显示entity的列表页面
//     *
//     * @param model    Model
//     * @param object   object
//     * @param request  HttpServletRequest
//     * @param response HttpServletResponse
//     * @return String
//     */
//    protected abstract String list(Model model, T object, HttpServletRequest request, HttpServletResponse response) throws Exception;
//
//    /**
//     * 显示新增或修改entity页面
//     *
//     * @param model  Model
//     * @param object object
//     * @return String
//     */
//    protected abstract String form(Model model, T object) throws Exception;
//
//    /**
//     * 新增或修改entity
//     *
//     * @param model  Model
//     * @param object object
//     * @return String
//     */
//    protected abstract String save(Model model, T object, RedirectAttributes redirectAttributes) throws Exception;
//
//    /**
//     * 删除entity
//     *
//     * @param model  Model
//     * @param object object
//     * @param param  参数接收器
//     * @return String
//     */
//    protected abstract String delete(Model model, T object, Param param, RedirectAttributes redirectAttributes) throws Exception;

    /**
     * 验证Bean实例对象
     */
    @Autowired
    protected Validator validator;

    /**
     * 服务端参数有效性验证
     *
     * @param object 验证的实体对象
     * @param groups 验证组
     * @return 验证成功：返回true；严重失败：将错误信息添加到 message 中
     */
    protected boolean beanValidator(Model model, Object object, Class<?>... groups) {
        try {
            BeanValidators.validateWithException(validator, object, groups);
        } catch (ConstraintViolationException ex) {
            List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
            list.add(0, "数据验证失败：");
            addMessage(model, list.toArray(new String[]{}));
            return false;
        }
        return true;
    }

    /**
     * 服务端参数有效性验证
     *
     * @param object 验证的实体对象
     * @param groups 验证组
     * @return 验证成功：返回true；严重失败：将错误信息添加到 flash message 中
     */
    protected boolean beanValidator(RedirectAttributes redirectAttributes, Object object, Class<?>... groups) {
        try {
            BeanValidators.validateWithException(validator, object, groups);
        } catch (ConstraintViolationException ex) {
            List<String> list = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
            list.add(0, "数据验证失败：");
            addMessage(redirectAttributes, list.toArray(new String[]{}));
            return false;
        }
        return true;
    }

    /**
     * 服务端参数有效性验证
     * 验证成功：继续执行；验证失败：抛出异常跳转400页面。
     *
     * @param object 验证的实体对象
     * @param groups 验证组，不传入此参数时，同@Valid注解验证
     */
    protected void beanValidator(Object object, Class<?>... groups) {
        BeanValidators.validateWithException(validator, object, groups);
    }

    /**
     * 添加Model消息
     *
     * @param model    Model
     * @param messages 要添加的消息
     */
    protected void addMessage(Model model, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        model.addAttribute("message", sb.toString());
    }

    /**
     * 添加flash消息
     *
     * @param redirectAttributes RedirectAttributes
     * @param messages           要添加的flash消息
     */
    protected void addMessage(RedirectAttributes redirectAttributes, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        redirectAttributes.addFlashAttribute("message", sb.toString());
    }

    /**
     * 客户端返回JSON字符串
     *
     * @param response HttpServletResponse
     * @param object   Object
     * @return String
     */
    protected String renderString(HttpServletResponse response, Object object) {
        return renderString(response, JsonMapper.toJsonString(object));
    }

    /**
     * 客户端返回字符串
     *
     * @param response HttpServletResponse
     * @param string   String
     * @return String
     */
    private String renderString(HttpServletResponse response, String string) {
        try {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 参数绑定异常
     *
     * @return ModelAndView
     */
    @ExceptionHandler({BindException.class, ConstraintViolationException.class, ValidationException.class, NumberFormatException.class})
    public String bindException() {
        return "error/400";
    }

    /**
     * 授权登录异常
     *
     * @return ModelAndView
     */
    @ExceptionHandler({AuthenticationException.class, UnauthorizedException.class})
    public String authenticationException() {
        return "error/403";
    }

    /**
     * 初始化数据绑定
     * 1. 将所有传递进来的String进行HTML编码，防止XSS攻击
     * 2. 将字段中Date类型转换为String类型
     * 2016-01-28 17:22:04注释掉，富文本编辑器被转码
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
//        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
//            @Override
//            public void setAsText(String text) {
//                if (text != null) {
//                    if (Objects.equals(text.trim(), "")) {
//                        setValue(null);
//                    } else {
//                        setValue(StringEscapeUtils.escapeHtml4(text.trim()));
//                    }
//                }
//            }
//
//            @Override
//            public String getAsText() {
//                Object value = getValue();
//                return value != null ? value.toString() : null;
//            }
//        });
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }

            @Override
            public String getAsText() {
                Object value = getValue();
                return value != null ? DateUtils.formatDateTime((Date) value) : "";
            }
        });
    }

}
