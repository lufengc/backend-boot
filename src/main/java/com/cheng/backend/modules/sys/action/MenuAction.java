/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.sys.action;

import com.cheng.backend.framework.common.BaseAction;
import com.cheng.backend.framework.common.Param;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.sys.bean.Menu;
import com.cheng.backend.modules.sys.bean.User;
import com.cheng.backend.modules.sys.service.MenuService;
import com.cheng.backend.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 菜单管理
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/menu")
public class MenuAction extends BaseAction {

    @Autowired
    private MenuService menuService;

    /**
     * 数据绑定
     *
     * @param id ID
     * @return Menu
     */
    @ModelAttribute
    public Menu get(@RequestParam(required = false) String id) throws Exception {
        if (StringUtils.isNotEmpty(id)) {
            return menuService.get(id);
        } else {
            return new Menu();
        }
    }

    /**
     * 权限列表页面
     *
     * @param model    Model
     * @param object   object
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = {"list", ""})
    @RequiresPermissions("sys:menu:view")
    public String list(Model model, Menu object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Menu> sourcelist;
        User user = UserUtils.getUser();
        if (user.isAdmin()) {
            sourcelist = menuService.getList(new Menu());
        } else {
            sourcelist = menuService.getMenuByUserId(user.getId());
        }
        List<Menu> list = Lists.newArrayList();
        Menu.sort(sourcelist);
        Menu.sortList(list, sourcelist, Menu.getRootId(), true);
        model.addAttribute("list", list);
        return "modules/sys/menuList";
    }

    /**
     * 权限编辑视图
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "form")
    @RequiresPermissions("sys:menu:view")
    public String form(Model model, Menu object) throws Exception {
        if (object.getParentId() == null) {
            object.setParentId(Menu.getRootId());
        }
        if (object.getId() == null) {
            List<Menu> list = Lists.newArrayList();
            List<Menu> sourcelist = menuService.getList(new Menu());
            Menu.sort(sourcelist);
            Menu.sortList(list, sourcelist, object.getParentId(), false);
            if (list.size() > 0) {
                if (Objects.equals(object.getParentId(), "1")) {
                    object.setSort(list.get(list.size() - 1).getSort() + 10000);
                } else {
                    object.setSort(list.get(list.size() - 1).getSort() + 10);
                }
            } else {
                object.setSort(menuService.get(object.getParentId()).getSort() + 10);
            }
        }
        Menu parent = menuService.get(object.getParentId());
        if (parent != null) {
            object.setParentName(parent.getName());
        } else {
            object.setParentName("功能菜单");
        }
        model.addAttribute("menu", object);
        return "modules/sys/menuForm";
    }

    /**
     * 保存
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "save")
    @RequiresPermissions("sys:menu:edit")
    public String save(Model model, Menu object, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, object)) {
            return form(model, object);
        }
        menuService.save(object);
        addMessage(redirectAttributes, "保存成功！");
        return "redirect:" + adminPath + "/sys/menu/";
    }

    /**
     * 删除
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "delete")
    @RequiresPermissions("sys:menu:edit")
    public String delete(Model model, Menu object, Param param, RedirectAttributes redirectAttributes) throws Exception {
        menuService.delete(object.getId());
        addMessage(redirectAttributes, "删除成功");
        return "redirect:" + adminPath + "/sys/menu/";
    }

    /**
     * 批量修改菜单排序
     */
    @RequestMapping(value = "updateSort")
    @RequiresPermissions("sys:menu:edit")
    public String updateSort(String[] ids, Integer[] sortIds, RedirectAttributes redirectAttributes) throws Exception {
        menuService.updateMenuSort(ids, sortIds);
        addMessage(redirectAttributes, "保存菜单排序成功!");
        return "redirect:" + adminPath + "/sys/menu/";
    }

    /**
     * 权限树数据
     *
     * @param extId      id
     * @param isShowHide 是否可见
     * @return List
     */
    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId,
                                              @RequestParam(required = false) Integer isShowHide) throws Exception {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Menu> list = UserUtils.getMenuList();
        for (Menu bean : list) {
            if (extId == null || (!extId.equals(bean.getId()) && !bean.getParentIds().contains("," + extId + ","))) {
                if (isShowHide != null && isShowHide == 0 && Objects.equals(bean.getIsShow(), "0")) {
                    continue;
                }
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", bean.getId());
                map.put("pId", bean.getParentId());
                map.put("name", bean.getName());
                mapList.add(map);
            }
        }
        return mapList;
    }


}
