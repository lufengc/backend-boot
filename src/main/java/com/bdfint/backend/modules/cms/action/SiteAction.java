/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.cms.action;

import com.bdfint.backend.framework.common.BaseAction;
import com.bdfint.backend.framework.common.Param;
import com.bdfint.backend.framework.util.CookieUtils;
import com.bdfint.backend.modules.cms.bean.Site;
import com.bdfint.backend.modules.cms.service.SiteService;
import com.bdfint.backend.modules.sys.utils.UserUtils;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
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

/**
 * 站点
 *
 * @author lufengcheng
 * @date 2016-01-15 09:56:22
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/site")
public class SiteAction extends BaseAction {

    @Autowired
    private SiteService siteService;

    /**
     * 数据绑定
     *
     * @param id ID
     * @return Site
     */
    @ModelAttribute
    public Site get(@RequestParam(required = false) String id) throws Exception {
        Site cmsSite;
        if (StringUtils.isNotEmpty(id)) {
            cmsSite = siteService.get(id);
        } else {
            cmsSite = new Site();
        }
        return cmsSite;
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
    @RequiresPermissions("cms:site:view")
    public String list(Model model, Site object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Example example = new Example(Site.class);
        Example.Criteria criteria = example.createCriteria();
        if (object.getName() != null) {
            criteria.andLike("name", "%" + object.getName() + "%");
        }
        PageInfo<Site> page = siteService.getPage(object, example);
        model.addAttribute("page", page);
        return "modules/cms/siteList";
    }

    /**
     * 编辑视图
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "form")
    @RequiresPermissions("cms:site:edit")
    public String form(Model model, Site object) throws Exception {
        return "modules/cms/siteForm";
    }

    /**
     * 保存
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "save")
    @RequiresPermissions("cms:site:edit")
    protected String save(Model model, Site object, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, object)) {
            return form(model, object);
        }
        siteService.save(object);
        addMessage(redirectAttributes, "保存成功");
        return "redirect:" + adminPath + "/cms/site/list?repage";
    }

    /**
     * 删除
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "delete")
    @RequiresPermissions("cms:site:edit")
    public String delete(Model model, Site object, Param param, RedirectAttributes redirectAttributes) throws Exception {
        if (Site.isDefault(object.getId())) {
            addMessage(redirectAttributes, "删除站点失败, 不允许删除默认站点");
        } else {
            siteService.delete(object.getId());
            addMessage(redirectAttributes, "删除成功");
        }
        return "redirect:" + adminPath + "/cms/site/list?repage";
    }

    /**
     * 选择站点
     *
     * @param id 站点id
     */
    @RequestMapping(value = "select")
    @RequiresPermissions("cms:site:edit")
    public String select(String id, boolean flag, HttpServletResponse response) {
        if (id != null) {
            UserUtils.putCache("siteId", id);
            // 保存到Cookie中，下次登录后自动切换到该站点
            CookieUtils.setCookie(response, "siteId", id);
        }
        if (flag) {
            return "redirect:" + adminPath;
        }
        return "redirect:" + adminPath + "/cms/site/list?repage";
    }
}
