/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.bdfint.backend.modules.sys.action;

import com.bdfint.backend.framework.common.BaseAction;
import com.bdfint.backend.framework.common.Param;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.sys.bean.Area;
import com.bdfint.backend.modules.sys.service.AreaService;
import com.bdfint.backend.modules.sys.utils.UserUtils;
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

/**
 * 区域Action
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/area")
public class AreaAction extends BaseAction {

    @Autowired
    private AreaService areaService;


    @ModelAttribute
    protected Area get(@RequestParam(required = false) String id) throws Exception {
        if (StringUtils.isNotEmpty(id)) {
            return areaService.get(id);
        } else {
            return new Area();
        }
    }

    @RequestMapping(value = {"list", ""})
    @RequiresPermissions("sys:area:view")
    protected String list(Model model, Area object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.addAttribute("list", UserUtils.getAreaList());
        return "modules/sys/areaList";
    }

    @RequestMapping(value = "form")
    @RequiresPermissions("sys:area:view")
    protected String form(Model model, Area area) throws Exception {
        Area parent = areaService.get(area.getParentId());
        if (parent != null) {
            area.setParentName(parent.getName());
        }
        model.addAttribute("area", area);
        return "modules/sys/areaForm";
    }

    @RequestMapping(value = "save")
    @RequiresPermissions("sys:area:edit")
    protected String save(Model model, Area area, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, area)) {
            return form(model, area);
        }
        areaService.save(area);
        addMessage(redirectAttributes, "保存区域'" + area.getName() + "'成功");
        return "redirect:" + adminPath + "/sys/area/";
    }

    @RequestMapping(value = "delete")
    @RequiresPermissions("sys:area:edit")
    protected String delete(Model model, Area object, Param param, RedirectAttributes redirectAttributes) throws Exception {
        areaService.delete(param.getIds());
        addMessage(redirectAttributes, "删除区域成功");
        return "redirect:" + adminPath + "/sys/area/";
    }

    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        try {
            List<Area> list = UserUtils.getAreaList();
            for (Area e : list) {
                if (StringUtils.isBlank(extId)
                        || (!extId.equals(e.getId()) && !e.getParentIds().contains("," + extId + ","))) {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("id", e.getId());
                    map.put("pId", e.getParentId());
                    map.put("name", e.getName());
                    mapList.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapList;
    }
}
