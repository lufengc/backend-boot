/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.gen.action;

import com.bdfint.backend.framework.common.BaseAction;
import com.bdfint.backend.framework.common.Param;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.gen.bean.GenScheme;
import com.bdfint.backend.modules.gen.bean.GenTable;
import com.bdfint.backend.modules.gen.service.GenTableService;
import com.bdfint.backend.modules.gen.utils.GenUtils;
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
 * 业务表Action
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Controller
@RequestMapping(value = "${adminPath}/gen/genTable")
public class GenTableAction extends BaseAction {

    @Autowired
    private GenTableService genTableService;

    @ModelAttribute
    public GenTable get(@RequestParam(required = false) String id) throws Exception {
        if (StringUtils.isNotEmpty(id)) {
            return genTableService.get(id);
        } else {
            return new GenTable();
        }
    }

    @RequestMapping(value = {"list", ""})
    public String list(Model model, GenTable genTable, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageInfo<GenTable> page = genTableService.getPage(genTable, new Example(GenTable.class));
        model.addAttribute("page", page);
        return "modules/gen/genTableList";
    }

    @RequestMapping(value = "form")
    public String form(Model model, GenTable genTable) {
        // 获取物理表列表
        List<GenTable> tableList = genTableService.findTableListFormDb(genTable);
        model.addAttribute("tableList", tableList);
        // 验证表是否存在
        if (StringUtils.isBlank(genTable.getId()) && !genTableService.checkTableName(genTable.getName())) {
            addMessage(model, "下一步失败！" + genTable.getName() + " 表已经添加！");
            genTable.setName("");
        } else { // 获取物理表字段
            genTable = genTableService.getTableFormDb(genTable);
        }
        model.addAttribute("genTable", genTable);
        model.addAttribute("config", GenUtils.getConfig());
        return "modules/gen/genTableForm";
    }

    @RequestMapping(value = "save")
    @RequiresPermissions("gen:genTable:edit")
    public String save(Model model, GenTable genTable, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, genTable)) {
            return form(model, genTable);
        }
        // 验证表是否已经存在
        if (StringUtils.isBlank(genTable.getId()) && !genTableService.checkTableName(genTable.getName())) {
            addMessage(model, "保存失败！" + genTable.getName() + " 表已经存在！");
            genTable.setName("");
            return form(model, genTable);
        }
        genTableService.save(genTable);
        addMessage(redirectAttributes, "保存业务表'" + genTable.getName() + "'成功");
        return "redirect:" + adminPath + "/gen/genTable/list?repage";
    }

    @RequestMapping(value = "delete")
    @RequiresPermissions("gen:genTable:edit")
    public String delete(Model model, GenTable genTable, Param param, RedirectAttributes redirectAttributes) throws Exception {
        genTableService.delete(param.getIds());
        addMessage(redirectAttributes, "删除业务表成功");
        return "redirect:" + adminPath + "/gen/genTable/list?repage";
    }

    /**
     * 生成代码表单页
     *
     * @param model     Model
     * @param genScheme GenScheme
     * @return ModelAndView
     */
    @RequestMapping(value = "genCodeForm")
    @RequiresPermissions("gen:genTable:edit")
    public String genCodeForm(Model model, GenScheme genScheme) throws Exception {
        GenTable genTable = get(genScheme.getId());
        genScheme.setId(genTable.getId());
        genScheme.setName(genTable.getName());
        model.addAttribute("genScheme", genScheme);
        model.addAttribute("config", GenUtils.getConfig());
        return "modules/gen/genCodeForm";
    }

    /**
     * 生成代码
     *
     * @param model              Model
     * @param genScheme          GenScheme
     * @param redirectAttributes RedirectAttributes
     * @return 管理列表页
     */
    @RequestMapping(value = "genCode")
    @RequiresPermissions("gen:genTable:edit")
    public String genCode(Model model, GenScheme genScheme, GenTable genTable, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, genScheme)) {
            return genCodeForm(model, genScheme);
        }
        genScheme.setGenTable(genTable);
        String result = genTableService.genCode(genScheme);
        addMessage(redirectAttributes, "'" + genScheme.getName() + "'数据表生成代码成功<br/>" + result);
        return "redirect:" + adminPath + "/gen/genTable/list?repage";
    }
}
