/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.sys.action;

import com.bdfint.backend.framework.common.BaseAction;
import com.bdfint.backend.framework.common.Param;
import com.bdfint.backend.framework.util.*;
import com.bdfint.backend.framework.util.excel.ExportExcel;
import com.bdfint.backend.framework.util.excel.ImportExcel;
import com.bdfint.backend.modules.sys.bean.Office;
import com.bdfint.backend.modules.sys.bean.User;
import com.bdfint.backend.modules.sys.service.OfficeService;
import com.bdfint.backend.modules.sys.service.RoleService;
import com.bdfint.backend.modules.sys.service.UserService;
import com.bdfint.backend.modules.sys.utils.UploadUtils;
import com.bdfint.backend.modules.sys.utils.UserUtils;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;

/**
 * 用户管理
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserAction extends BaseAction {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private OfficeService officeService;

    /**
     * 数据绑定
     *
     * @param id ID
     * @return User
     */
    @ModelAttribute
    public User get(@RequestParam(required = false) String id) throws Exception {
        User User;
        if (StringUtils.isNotEmpty(id)) {
            User = userService.get(id);
            if (User != null) {
                User.setRoleList(roleService.getRoleByUserId(id));
                Office office = officeService.get(User.getOfficeId());
                User.setOfficeName(office.getName());
                Office company = officeService.get(User.getCompanyId());
                User.setCompanyName(company.getName());
            } else {
                User = new User();
            }
        } else {
            User = new User();
        }
        return User;
    }

    /**
     * 用户首页
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "index")
    @RequiresPermissions("sys:user:view")
    public String index() {
        return "modules/sys/userIndex";
    }

    /**
     * 用户列表
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "list")
    @RequiresPermissions("sys:user:view")
    public String list(Model model, User object, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        if (object.getOrderBy() != null) {
            example.setOrderByClause(object.getOrderBy());
        } else {
            example.setOrderByClause("create_date DESC");
        }

        if (object.getLoginName() != null) {
            criteria.andLike("loginName", "%" + object.getLoginName() + "%");
        }
        if (object.getName() != null) {
            criteria.andLike("name", "%" + object.getName() + "%");
        }
        if (object.getMobile() != null) {
            criteria.andLike("mobile", "%" + object.getMobile() + "%");
        }
        //根据当前组织机构ID查询数据
        List<String> ids = Lists.newArrayList();
        if (StringUtils.isNotEmpty(object.getOfficeId())) {
            ids.add(object.getOfficeId());
            List<Office> offices = officeService.getByParentIdsLike(object.getOfficeId());
            if (offices != null && offices.size() > 0) {
                for (Office office : offices) {
                    ids.add(office.getId());
                }
            }
        }
        if (ids.size() > 0) {
            criteria.andIn("officeId", ids);
        }
        PageInfo<User> page = userService.getPage(object, example);
        model.addAttribute("page", page);
        return "modules/sys/userList";
    }

    /**
     * 表单输入页面
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "form")
    @RequiresPermissions("sys:user:view")
    public String form(Model model, User user) throws Exception {
        return "modules/sys/userForm";
    }

    /**
     * 保存
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "save")
    @RequiresPermissions("sys:user:edit")
    public String save(Model model, User user, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, user)) {
            return form(model, user);
        }
        userService.save(user);
        addMessage(redirectAttributes, "保存用户'" + user.getName() + "'成功");
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 更新状态
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "updateStatus")
    @RequiresPermissions("sys:user:edit")
    public String updateStatus(User user, Param param, RedirectAttributes redirectAttributes) throws Exception {
        userService.updateStatus(param.getIds(), user);
        addMessage(redirectAttributes, "操作成功");
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 密码初始化
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "initPassword")
    @RequiresPermissions("sys:user:edit")
    public String initPassword(User user, Param param, RedirectAttributes redirectAttributes) throws Exception {
        userService.initPassword(param.getIds(), user.getPassword());
        addMessage(redirectAttributes, "操作成功");
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 删除
     *
     * @return ModelAndView
     */
    @RequestMapping(value = "delete")
    @RequiresPermissions("sys:user:edit")
    public String delete(Model model, User user, Param param, RedirectAttributes redirectAttributes) throws Exception {
        userService.delete(param.getIds());
        addMessage(redirectAttributes, "删除用户成功");
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 验证用户名是否有效
     *
     * @param oldLoginName 旧用户名
     * @param loginName    用户名
     * @return String
     */
    @ResponseBody
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "checkUsername")
    public String checkLoginName(String oldLoginName, String loginName) {
        if (loginName != null && loginName.equals(oldLoginName)) {
            return "true";
        } else if (loginName != null && userService.getUserByLoginName(loginName) == null) {
            return "true";
        }
        return "false";
    }

    /**
     * 导出用户数据
     *
     * @param user               User
     * @param response           HttpServletResponse
     * @param redirectAttributes RedirectAttributes
     * @return ModelAndView
     */
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public String exportFile(User user, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "用户数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            PageInfo<User> page = userService.getPage(user, new Example(User.class));
            new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出用户失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 导入用户数据
     *
     * @param file               MultipartFile
     * @param redirectAttributes RedirectAttributes
     * @return ModelAndView
     */
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public String importFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcel importExcel = new ImportExcel(file, 1, 0);
            List<User> list = importExcel.getDataList(User.class);
            for (User user : list) {
                try {
                    if ("true".equals(checkLoginName("", user.getLoginName()))) {
                        user.setPassword(Encodes.encryptPassword("123456"));
                        BeanValidators.validateWithException(validator, user);
                        userService.save(user);
                        successNum++;
                    } else {
                        failureMsg.append("<br/>登录名 ").append(user.getLoginName()).append(" 已存在; ");
                        failureNum++;
                    }
                } catch (ConstraintViolationException ex) {
                    failureMsg.append("<br/>登录名 ").append(user.getLoginName()).append(" 导入失败：");
                    List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
                    for (String message : messageList) {
                        failureMsg.append(message).append("; ");
                        failureNum++;
                    }
                } catch (Exception ex) {
                    failureMsg.append("<br/>登录名 ").append(user.getLoginName()).append(" 导入失败：").append(ex.getMessage());
                }
            }
            if (failureNum > 0) {
                failureMsg.insert(0, "，失败 " + failureNum + " 条用户，导入信息如下：");
            }
            addMessage(redirectAttributes, "已成功导入 " + successNum + " 条用户" + failureMsg);
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入用户失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 下载导入用户数据模板
     *
     * @param response           HttpServletResponse
     * @param redirectAttributes RedirectAttributes
     * @return ModelAndView
     */
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "用户数据导入模板.xlsx";
            List<User> list = Lists.newArrayList();
            list.add(UserUtils.getUser());
            new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 用户头像显示编辑保存
     *
     * @param user  User
     * @param model Model
     * @return ModelAndView
     */
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "imageEdit")
    public String imageEdit(User user, Model model) throws Exception {
        User currentUser = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getName())) {
            if (user.getPhoto() != null) {
                currentUser.setPhoto(user.getPhoto());
            }
            userService.updateUserInfo(currentUser);
            model.addAttribute("message", "保存用户信息成功");
            return "modules/sys/userInfo";
        }
        model.addAttribute("user", currentUser);
        return "modules/sys/userImageEdit";
    }

    /**
     * 用户头像显示编辑保存
     *
     * @return ModelAndView
     */
    @RequiresPermissions("sys:user:edit")
    @RequestMapping(value = "imageUpload")
    public String imageUpload(HttpServletRequest request, MultipartFile file) throws Exception {
        User currentUser = UserUtils.getUser();
        String fileName = UploadUtils.uploadImage(file, 1);
        // 判断文件是否为空
        if (StringUtils.isNotEmpty(fileName)) {
            currentUser.setPhoto(fileName);
            userService.updateUserInfo(currentUser);
        }
        return "modules/sys/userImageEdit";
    }

    /**
     * 当前用户信息显示
     *
     * @param model Model
     * @return ModelAndView
     */
    @RequestMapping(value = "info")
    @RequiresPermissions("sys:user:view")
    public String info(Model model) {
        User currentUser = UserUtils.getUser();
        model.addAttribute("user", currentUser);
        return "modules/sys/userInfo";
    }

    /**
     * 获取用户树结构
     *
     * @param officeId officeId
     */
    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String officeId) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<User> list = userService.getUserByOfficeId(officeId);
        if (list != null && list.size() > 0) {
            for (User e : list) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", "u_" + e.getId());
                map.put("pId", officeId);
                map.put("name", StringUtils.replace(e.getName(), " ", ""));
                mapList.add(map);
            }
        }
        return mapList;
    }

}
