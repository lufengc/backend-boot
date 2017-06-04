/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.bdfint.backend.modules.sys.action;

import com.bdfint.backend.framework.common.BaseAction;
import com.bdfint.backend.framework.common.Param;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.sys.bean.Menu;
import com.bdfint.backend.modules.sys.bean.Role;
import com.bdfint.backend.modules.sys.bean.User;
import com.bdfint.backend.modules.sys.service.MenuService;
import com.bdfint.backend.modules.sys.service.RoleService;
import com.bdfint.backend.modules.sys.service.UserService;
import com.bdfint.backend.modules.sys.utils.UserUtils;
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
 * 角色action
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/role")
public class RoleAction extends BaseAction {

    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;

    /**
     * 数据绑定
     *
     * @param id ID
     * @return Role
     */
    @ModelAttribute
    public Role get(@RequestParam(required = false) String id) throws Exception {
        Role sysRole;
        if (StringUtils.isNotEmpty(id)) {
            sysRole = roleService.get(id);
            StringBuilder menuIds = new StringBuilder();
            List<Menu> menuList = menuService.getMenuByRoleId(sysRole.getId());
            for (Menu sysMenu : menuList) {
                if (menuIds.length() == 0) {
                    menuIds.append(sysMenu.getId());
                } else {
                    menuIds.append(",").append(sysMenu.getId());
                }
            }
            sysRole.setMenuIds(menuIds.toString());
        } else {
            sysRole = new Role();
        }
        return sysRole;
    }

    /**
     * 角色列表
     *
     * @param model    Model
     * @param object   object
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return ModelAndView
     */
    @RequestMapping(value = {"list", ""})
    @RequiresPermissions("sys:role:view")
    public String list(Model model, Role object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.addAttribute("roleList", UserUtils.getRoleList());
        return "modules/sys/roleList";
    }

    /**
     * 角色编辑视图
     *
     * @param model  Model
     * @param object object
     * @return ModelAndView
     */
    @RequestMapping(value = "form")
    @RequiresPermissions("sys:role:view")
    public String form(Model model, Role object) throws Exception {
        return "modules/sys/roleForm";
    }

    /**
     * 保存角色信息
     *
     * @param model  Model
     * @param object object
     * @return ModelAndView
     */
    @RequestMapping(value = "save")
    @RequiresPermissions("sys:role:edit")
    public String save(Model model, Role object, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, object)) {
            return form(model, object);
        }
        roleService.save(object);
        addMessage(redirectAttributes, "保存角色'" + object.getName() + "'成功");
        return "redirect:" + adminPath + "/sys/role/list?repage";
    }

    /**
     * 删除角色信息
     *
     * @param model  Model
     * @param object object
     * @return ModelAndView
     */
    @RequestMapping(value = "delete")
    @RequiresPermissions("sys:role:edit")
    public String delete(Model model, Role object, Param param, RedirectAttributes redirectAttributes) throws Exception {
        roleService.delete(param.getIds());
        addMessage(redirectAttributes, "删除角色成功");
        return "redirect:" + adminPath + "/sys/role/list?repage";
    }

    /**
     * 授权设置页面
     *
     * @param role  Role
     * @param model Model
     * @return ModelAndView
     */
    @RequestMapping(value = "auth")
    @RequiresPermissions("sys:role:edit")
    public String auth(Role role, Model model) throws Exception {
        model.addAttribute("menuList", UserUtils.getMenuList());
        return "modules/sys/roleAuth";
    }

    /**
     * 角色分配页面
     *
     * @param role  Role
     * @param model Model
     * @return ModelAndView
     */
    @RequestMapping(value = "assign")
    @RequiresPermissions("sys:role:edit")
    public String assign(Role role, Model model) {
        List<User> userList = userService.getUserByRoleId(role.getId());
        model.addAttribute("userList", userList);
        return "modules/sys/roleAssign";
    }

    /**
     * 角色分配 -- 打开角色分配对话框
     *
     * @param model Model
     * @return ModelAndView
     */
    @RequestMapping(value = "selectUser")
    @RequiresPermissions("sys:role:edit")
    public String selectUser(User user, Model model) throws Exception {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        if (user.getOrderBy() != null) {
            example.setOrderByClause(user.getOrderBy());
        } else {
            example.setOrderByClause("create_date ASC");
        }

        if (user.getLoginName() != null) {
            criteria.andLike("loginName", "%" + user.getLoginName() + "%");
        }
        if (user.getName() != null) {
            criteria.andLike("name", "%" + user.getName() + "%");
        }
        PageInfo<User> page = userService.getPage(user, example);
        model.addAttribute("page", page);
        return "modules/sys/selectUser";
    }

    /**
     * 角色分配
     *
     * @param role               Role
     * @param ids                用户ids
     * @param redirectAttributes RedirectAttributes
     * @return ModelAndView
     */
    @RequestMapping(value = "assignRole")
    @RequiresPermissions("sys:role:edit")
    public String assignRole(Role role, String ids, RedirectAttributes redirectAttributes) {
        roleService.assignUserToRole(role, ids);
        addMessage(redirectAttributes, "已成功分配 " + ids.split(",").length + " 个用户");
        return "redirect:" + adminPath + "/sys/role/assign?id=" + role.getId();
    }

    /**
     * 角色分配 -- 从角色中移除用户
     *
     * @param userId             用户id
     * @param roleId             角色id
     * @param redirectAttributes RedirectAttributes
     * @return ModelAndView
     */
    @RequestMapping(value = "outRole")
    @RequiresPermissions("sys:role:edit")
    public String outRole(String userId, String roleId, RedirectAttributes redirectAttributes) throws Exception {
        User user = userService.get(userId);
        Role role = roleService.get(roleId);
        if (UserUtils.getUser().getId().equals(userId)) {
            addMessage(redirectAttributes, "无法从角色【" + role.getName() + "】中移除用户【" + user.getName() + "】自己！");
        } else {
            boolean flag = roleService.outUserInRole(role, user);
            if (!flag) {
                addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除失败！");
            } else {
                addMessage(redirectAttributes, "用户【" + user.getName() + "】从角色【" + role.getName() + "】中移除成功！");
            }
        }
        return "redirect:" + adminPath + "/sys/role/assign?id=" + role.getId();
    }
}
