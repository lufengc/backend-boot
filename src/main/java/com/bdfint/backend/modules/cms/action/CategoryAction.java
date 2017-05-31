/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.cms.action;

import com.bdfint.backend.framework.common.BaseAction;
import com.bdfint.backend.framework.common.Param;
import com.bdfint.backend.framework.common.TreeEntity;
import com.bdfint.backend.modules.cms.bean.Article;
import com.bdfint.backend.modules.cms.bean.Category;
import com.bdfint.backend.modules.cms.bean.Site;
import com.bdfint.backend.modules.cms.service.CategoryService;
import com.bdfint.backend.modules.cms.service.FileTplService;
import com.bdfint.backend.modules.cms.service.SiteService;
import com.bdfint.backend.modules.cms.utils.TplUtils;
import com.bdfint.backend.modules.sys.bean.Office;
import com.bdfint.backend.modules.sys.service.OfficeService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 栏目Action
 *
 * @author lufengc
 * @date 2016-01-15 09:56:22
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/category")
public class CategoryAction extends BaseAction {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private FileTplService fileTplService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private OfficeService officeService;

    /**
     * 数据绑定
     *
     * @param id 主键
     * @return Category
     */
    @ModelAttribute
    public Category get(@RequestParam(required = false) String id) throws Exception {
        if (StringUtils.isNotEmpty(id)) {
            return categoryService.get(id);
        } else {
            return new Category();
        }
    }

    /**
     * 列表
     *
     * @param model    Model
     * @param object   object
     * @param request  request
     * @param response HttpServletResponse
     * @return view
     */
    @RequestMapping(value = {"list", ""})
    @RequiresPermissions("cms:category:view")
    public String list(Model model, Category object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Category> sourcelist = categoryService.getByUser(true, null);
        List<Category> list = Lists.newArrayList();
        sourcelist.sort(Comparator.comparing(TreeEntity::getSort));
        Category.sortList(list, sourcelist, "1");
        model.addAttribute("list", list);
        return "modules/cms/categoryList";
    }

    /**
     * 编辑视图
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "form")
    @RequiresPermissions("cms:category:view")
    public String form(Model model, Category object) throws Exception {
        if (object.getParentId() == null) {
            object.setParentId("1");
        }
        if (object.getId() == null) {
            List<Category> list = Lists.newArrayList();
            List<Category> sourcelist = categoryService.getList(new Category());
            Category.sortList(list, sourcelist, object.getParentId());
            if (list.size() > 0) {
                if ("0".equals(object.getParentId())) {
                    object.setSort(list.get(list.size() - 1).getSort() + 10000);
                } else {
                    object.setSort(list.get(list.size() - 1).getSort() + 10);
                }
            } else {
                object.setSort(categoryService.get(object.getParentId()).getSort() + 10);
            }
        }
        Category parent = categoryService.get(object.getParentId());
        if (parent != null) {
            object.setParentName(parent.getName());
        } else {
            object.setParentName("顶级栏目");
        }
        if (object.getOfficeId() != null) {
            Office office = officeService.get(object.getOfficeId());
            object.setOfficeName(office.getName());
        }
        model.addAttribute("cmsCategory", object);
        model.addAttribute("listViewList", getTplContent(Category.DEFAULT_TEMPLATE));
        model.addAttribute("category_DEFAULT_TEMPLATE", Category.DEFAULT_TEMPLATE);
        model.addAttribute("contentViewList", getTplContent(Article.DEFAULT_TEMPLATE));
        model.addAttribute("article_DEFAULT_TEMPLATE", Article.DEFAULT_TEMPLATE);
        return "modules/cms/categoryForm";
    }

    private List<String> getTplContent(String prefix) throws Exception {
        List<String> tplList = fileTplService.getNameListByPrefix(siteService.get(Site.getCurrentSiteId()).getSolutionPath());
        tplList = TplUtils.tplTrim(tplList, prefix, "");
        return tplList;
    }

    /**
     * 保存
     *
     * @param model              Model
     * @param object             object
     * @param redirectAttributes RedirectAttributes
     * @return ModelAndView
     */
    protected String save(Model model, Category object, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, object)) {
            return form(model, object);
        }
        categoryService.save(object);
        addMessage(redirectAttributes, "保存栏目成功");
        return "redirect:" + adminPath + "/cms/category/";
    }

    /**
     * 删除
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "delete")
    public String delete(Model model, Category object, Param param, RedirectAttributes redirectAttributes) throws Exception {
        if (Category.isRoot(object.getId())) {
            addMessage(redirectAttributes, "删除栏目失败, 不允许删除顶级栏目或编号为空");
        } else {
            categoryService.delete(object.getId());
            addMessage(redirectAttributes, "删除栏目成功");
        }
        return "redirect:" + adminPath + "/cms/category/";
    }

    /**
     * 批量修改栏目排序
     */
    @RequestMapping(value = "updateSort")
    @RequiresPermissions("sys:category:edit")
    public String updateSort(String[] ids, Integer[] sortIds, RedirectAttributes redirectAttributes) throws Exception {
        categoryService.updateSort(ids, sortIds);
        addMessage(redirectAttributes, "保存排序成功!");
        return "redirect:" + adminPath + "/sys/category/";
    }

    /**
     * 栏目树数据
     *
     * @param extId id
     * @return List
     */
    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(String module, @RequestParam(required = false) String extId) throws Exception {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Category> list = categoryService.getByUser(true, module);
        for (Category bean : list) {
            if (extId == null || (!extId.equals(bean.getId()) && !bean.getParentIds().contains("," + extId + ","))) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", bean.getId());
                map.put("pId", bean.getParentId() != null ? bean.getParentId() : 0);
                map.put("name", bean.getName());
                map.put("module", bean.getModule());
                mapList.add(map);
            }
        }
        return mapList;
    }

}
