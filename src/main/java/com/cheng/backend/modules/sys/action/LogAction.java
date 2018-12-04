/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.sys.action;

import com.cheng.backend.framework.common.BaseAction;
import com.cheng.backend.framework.common.Param;
import com.cheng.backend.framework.util.DateUtils;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.sys.bean.Log;
import com.cheng.backend.modules.sys.bean.User;
import com.cheng.backend.modules.sys.service.LogService;
import com.cheng.backend.modules.sys.service.UserService;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 日志action
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/log")
public class LogAction extends BaseAction {

    @Autowired
    private LogService logService;
    @Autowired
    private UserService userService;

    @ModelAttribute
    public Log get(@RequestParam(required = false) String id) throws Exception {
        if (StringUtils.isNotEmpty(id)) {
            return logService.get(id);
        } else {
            return new Log();
        }
    }

    /**
     * 列表页面
     *
     * @param model    model
     * @param object   object
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return String
     */
    @RequestMapping(value = {"list", ""})
    @RequiresPermissions("sys:log:view")
    public String list(Model model, Log object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Example example = new Example(Log.class, false);
        Example.Criteria criteria = example.createCriteria();
        example.orderBy("createDate").desc();
        String conditions = "1 = 1";

        //用户名称查询条件
        String createName = object.getCreateName();
        if (StringUtils.isNotEmpty(createName)) {
            List<User> userList = userService.getUserByName(createName);
            if (userList != null && userList.size() > 0) {
                String ids = "";
                for (User user : userList) {
                    if (StringUtils.isEmpty(ids)) {
                        ids += user.getId();
                    } else {
                        ids += user.getId() + ",";
                    }
                }
                conditions = " and create_by in(" + ids + ")";
            } else {
                return "modules/sys/logList";
            }
        }

        //时间范围查询条件
        String createTimeRange = object.getCreateTimeRange();
        if (StringUtils.isNotEmpty(createTimeRange)) {
            String[] split = createTimeRange.split(" - ");
            String start = DateUtils.formatDate(DateUtils.parseDate(split[0])) + " 00:00:00";
            String end = DateUtils.formatDate(DateUtils.parseDate(split[1])) + " 23:59:59";
            conditions += " and create_date >= '" + start + "' and create_date <= '" + end + "'";
        }
        if (StringUtils.isNotEmpty(object.getType())) {
            conditions += " and type = '2'";
        }
        if (StringUtils.isNotEmpty(object.getRequestUri())) {
            conditions += " and request_uri = '" + object.getRequestUri() + "'";
        }


        if (StringUtils.isNotEmpty(conditions)) {
            criteria.andCondition(conditions);
        }
        PageInfo<Log> page = logService.getPage(object, example);
        model.addAttribute("page", page);
        return "modules/sys/logList";
    }

    /**
     * 删除
     *
     * @param model  model
     * @param object object
     * @return String
     */
    @RequestMapping(value = "delete")
    @RequiresPermissions("sys:log:edit")
    public String delete(Model model, Log object, Param param, RedirectAttributes redirectAttributes) throws Exception {
        logService.delete(param.getIds());
        addMessage(redirectAttributes, "删除成功");
        return "redirect:" + adminPath + "/sys/log/list?repage";
    }

    /**
     * 清空
     */
    @RequestMapping(value = "empty")
    @RequiresPermissions("sys:log:edit")
    public String empty(RedirectAttributes redirectAttributes) {
        logService.empty();
        addMessage(redirectAttributes, "清空日志成功");
        return "redirect:" + adminPath + "/sys/log/?repage";
    }

}
