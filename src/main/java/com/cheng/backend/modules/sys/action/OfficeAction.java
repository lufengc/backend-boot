/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.modules.sys.action;

import com.cheng.backend.framework.common.BaseAction;
import com.cheng.backend.framework.common.Global;
import com.cheng.backend.framework.common.Param;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.sys.bean.Area;
import com.cheng.backend.modules.sys.bean.Office;
import com.cheng.backend.modules.sys.bean.User;
import com.cheng.backend.modules.sys.service.AreaService;
import com.cheng.backend.modules.sys.service.OfficeService;
import com.cheng.backend.modules.sys.utils.DictUtils;
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
 * 机构Controller
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/office")
public class OfficeAction extends BaseAction {

    @Autowired
    private OfficeService officeService;
    @Autowired
    private AreaService areaService;

    @ModelAttribute
    public Office get(@RequestParam(required = false) String id) throws Exception {
        if (StringUtils.isNotBlank(id)) {
            return officeService.get(id);
        } else {
            return new Office();
        }
    }

    /**
     * 机构首页
     *
     * @return String
     */
    @RequestMapping(value = {""})
    @RequiresPermissions("sys:office:view")
    public String index() {
        return "modules/sys/officeIndex";
    }

    /**
     * 机构列表
     *
     * @param model    Model
     * @param office   Office
     * @param request  HttpServletRequest
     * @param response HttpServletRequest
     * @return String
     */
    @RequestMapping(value = "list")
    @RequiresPermissions("sys:office:view")
    public String list(Model model, Office office, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (office == null || StringUtils.isEmpty(office.getParentIds()) || StringUtils.isEmpty(office.getId())) {
            model.addAttribute("list", officeService.getList(false));
        } else {
            model.addAttribute("list", officeService.getByParentId(office.getId()));
        }
        return "modules/sys/officeList";
    }

    /**
     * 机构表单
     *
     * @param model  Model
     * @param office Office
     * @return String
     */
    @RequestMapping(value = "form")
    @RequiresPermissions("sys:office:view")
    public String form(Model model, Office office) throws Exception {
        User user = UserUtils.getUser();
        if (office.getParentId() == null) {
            office.setParentId(user.getOfficeId());
        }
        office.setParentId(office.getParentId());
        if (office.getAreaId() == null) {
            office.setAreaId(officeService.get(user.getOfficeId()).getAreaId());
        }
        // 自动获取排序号
        if (StringUtils.isEmpty(office.getId()) && StringUtils.isNotEmpty(office.getParentId())) {
            int size = 0;
            List<Office> list = officeService.getList(true);
            for (Office e : list) {
                if (e.getParentId() != null && e.getParentId().equals(office.getParentId())) {
                    size++;
                }
            }
            office.setCode(officeService.get(office.getParentId()).getCode()
                    + StringUtils.leftPad(String.valueOf(size > 0 ? size + 1 : 1), 3, "0"));
        }

        if (office.getParentId() != null) {
            Office parent = officeService.get(office.getParentId());
            if (parent != null) {
                office.setParentName(parent.getName());
            }
        }
        if (office.getId() != null) {
            Area area = areaService.get(office.getAreaId());
            if (area != null) {
                office.setAreaName(area.getName());
            }
            User primaryPerson = UserUtils.get(office.getPrimaryPerson());
            if (primaryPerson != null) {
                office.setPrimaryPersonName(primaryPerson.getName());
            }
            User deputyPerson = UserUtils.get(office.getDeputyPerson());
            if (deputyPerson != null) {
                office.setDeputyPersonName(deputyPerson.getName());
            }
        }

        model.addAttribute("sysOffice", office);
        return "modules/sys/officeForm";
    }

    /**
     * 保存
     *
     * @param model              Model
     * @param office             Office
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @RequestMapping(value = "save")
    @RequiresPermissions("sys:office:edit")
    public String save(Model model, Office office, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, office)) {
            return form(model, office);
        }
        officeService.save(office);

        if (office.getChildDeptList() != null) {
            Office childOffice;
            for (String id : office.getChildDeptList()) {
                childOffice = new Office();
                childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
                childOffice.setParentId(office.getId());
                childOffice.setAreaId(office.getAreaId());
                childOffice.setType("2");
                childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade()) + 1));
                childOffice.setUseable(Global.YES);
                officeService.save(childOffice);
            }
        }
        addMessage(redirectAttributes, "保存机构'" + office.getName() + "'成功");
        return "redirect:" + adminPath + "/sys/office/list";
    }

    /**
     * @param model              Model
     * @param office             Office
     * @param param              参数接收器
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @RequestMapping(value = "delete")
    @RequiresPermissions("sys:office:edit")
    public String delete(Model model, Office office, Param param, RedirectAttributes redirectAttributes) throws Exception {
        officeService.delete(office.getId());
        addMessage(redirectAttributes, "删除机构成功");
        return "redirect:" + adminPath + "/sys/office/list";
    }

    /**
     * 获取机构JSON数据。
     *
     * @param extId 排除的ID
     * @param type  类型（1：公司；2：部门/小组/其它：3：用户）
     * @param grade 显示级别
     * @return Json
     */
    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId,
                                              @RequestParam(required = false) String type,
                                              @RequestParam(required = false) Long grade,
                                              @RequestParam(required = false) Boolean isAll) throws Exception {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Office> list = officeService.getList(isAll);
        for (Office e : list) {
            if ((StringUtils.isBlank(extId) || (!extId.equals(e.getId()) && !e.getParentIds().contains("," + extId + ",")))
                    && (type == null || ((!Objects.equals(type, "1") || type.equals(e.getType()))))
                    && (grade == null || (Integer.parseInt(e.getGrade()) <= grade.intValue()))
                    && Global.YES.equals(e.getUseable())) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("pIds", e.getParentIds());
                map.put("name", e.getName());
                if (type != null && Objects.equals(type, "3")) {
                    map.put("isParent", true);
                }
                mapList.add(map);
            }
        }
        return mapList;
    }
}
