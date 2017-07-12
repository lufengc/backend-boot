/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.sys.action;

import com.bdfint.backend.framework.common.BaseAction;
import com.bdfint.backend.framework.common.Param;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.sys.bean.Dict;
import com.bdfint.backend.modules.sys.service.DictService;
import com.bdfint.backend.modules.sys.utils.DictUtils;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 字典Action
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Controller
@RequestMapping(value = "${adminPath}/sys/dict")
public class dictAction extends BaseAction {

    @Autowired
    private DictService dictService;

    /**
     * 数据绑定
     *
     * @param id id
     * @return Dict
     */
    @ModelAttribute
    protected Dict get(@RequestParam(required = false) String id) throws Exception {
        Dict sysDict;
        if (StringUtils.isNotEmpty(id)) {
            sysDict = dictService.get(id);
        } else {
            sysDict = new Dict();
        }
        return sysDict;
    }

    /**
     * 字典列表页
     *
     * @return view
     */
    @RequestMapping(value = {"list", ""})
    @RequiresPermissions("sys:dict:view")
    protected String list(Model model, Dict object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Set<String> typeList = new HashSet<>();
        for (Dict dict : dictService.getList(new Dict())) {
            typeList.add(dict.getType());
        }
        model.addAttribute("typeList", typeList);
        Example example = new Example(Dict.class);
        if (StringUtils.isNotEmpty(object.getOrderBy())) {
            example.setOrderByClause(object.getOrderBy());
        } else {
            example.setOrderByClause("create_date DESC");
        }
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(object.getDescription())) {
            criteria.andLike("description", "%" + object.getDescription() + "%");
        }
        if (StringUtils.isNotEmpty(object.getType())) {
            criteria.andEqualTo("type", object.getType());
        }
        PageInfo<Dict> page = dictService.getPage(object, example);
        model.addAttribute("page", page);
        return "modules/sys/dictList";
    }

    /**
     * 表单输入页面
     *
     * @return String
     */
    @RequestMapping(value = "form")
    @RequiresPermissions("sys:dict:view")
    protected String form(Model model, Dict dict) throws Exception {
        if (dict.getActionType() != null && dict.getActionType() == 2) {
            dict.setId(null);
            dict.setLabel(null);
            dict.setValue(null);
            List<Dict> dictList = DictUtils.getDictList(dict.getType());
            Integer maxSortId = dict.getSort();
            for (Dict bean : dictList) {
                if (bean.getSort() > maxSortId) {
                    maxSortId = bean.getSort();
                }
            }
            dict.setSort(maxSortId + 10);
        }
        return "modules/sys/dictForm";
    }

    /**
     * 保存
     *
     * @return String
     */
    @RequestMapping(value = "save")
    @RequiresPermissions("sys:dict:edit")
    protected String save(Model model, Dict object, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, object)) {
            return form(model, object);
        }
        dictService.save(object);
        addMessage(redirectAttributes, "保存字典成功");
        return "redirect:" + adminPath + "/sys/dict/list?repage";
    }

    /**
     * 删除
     *
     * @return String
     */
    @RequestMapping(value = "delete")
    @RequiresPermissions("sys:dict:edit")
    protected String delete(Model model, Dict object, Param param, RedirectAttributes redirectAttributes) throws Exception {
        dictService.delete(param.getIds());
        addMessage(redirectAttributes, "删除字典成功");
        return "redirect:" + adminPath + "/sys/dict/list?repage";
    }
}
