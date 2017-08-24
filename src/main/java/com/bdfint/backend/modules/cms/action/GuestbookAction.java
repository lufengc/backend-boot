/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.cms.action;

import com.bdfint.backend.framework.common.BaseAction;
import com.bdfint.backend.framework.common.Param;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.cms.bean.Guestbook;
import com.bdfint.backend.modules.cms.service.GuestbookService;
import com.bdfint.backend.modules.sys.utils.DictUtils;
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
import java.util.Date;

/**
 * 留言
 *
 * @author lufengc
 * @date 2016-01-15 09:56:22
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/guestbook")
public class GuestbookAction extends BaseAction {

    @Autowired
    private GuestbookService guestbookService;

    /**
     * 数据绑定
     *
     * @param id ID
     * @return Guestbook
     */
    @ModelAttribute
    public Guestbook get(@RequestParam(required = false) String id) throws Exception {
        if (StringUtils.isNotEmpty(id)) {
            return guestbookService.get(id);
        } else {
            return new Guestbook();
        }
    }

    /**
     * 列表
     *
     * @param model    Model
     * @param object   object
     * @param request  request
     * @param response @return view
     */
    @RequestMapping(value = {"list", ""})
    @RequiresPermissions("cms:guestbook:view")
    public String list(Model model, Guestbook object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Example example = new Example(Guestbook.class);
        example.setOrderByClause("create_date desc");
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotEmpty(object.getContent())) {
            criteria.andLike("content", "%" + object.getContent() + "%");
        }
        if (StringUtils.isNotEmpty(object.getType())) {
            criteria.andEqualTo("type", object.getType());
        }
        if (StringUtils.isNotEmpty(object.getDelFlag())) {
            criteria.andEqualTo("delFlag", object.getDelFlag());
        }

        PageInfo<Guestbook> page = guestbookService.getPage(object, example);
        model.addAttribute("page", page);
        return "modules/cms/guestbookList";
    }

    /**
     * 编辑视图
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "form")
    @RequiresPermissions("cms:guestbook:edit")
    public String form(Model model, Guestbook object) throws Exception {
        return "modules/cms/guestbookForm";
    }

    /**
     * 保存
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "save")
    @RequiresPermissions("cms:guestbook:edit")
    protected String save(Model model, Guestbook object, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, object)) {
            return form(model, object);
        }
        if (object.getReUserId() == null) {
            object.setReUserId(UserUtils.getUserId());
            object.setReDate(new Date());
        }
        guestbookService.save(object);
        addMessage(redirectAttributes, DictUtils.getDictLabel(object.getDelFlag(), "cms_del_flag", "保存")
                + "留言'" + object.getName() + "'成功");
        return "redirect:" + adminPath + "/cms/guestbook/?repage&status=2";
    }

    /**
     * 删除
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "delete")
    @RequiresPermissions("cms:guestbook:edit")
    public String delete(Model model, Guestbook object, Param param, RedirectAttributes redirectAttributes) throws Exception {
        guestbookService.delete(object.getId());
        addMessage(redirectAttributes, "删除成功");
        return "redirect:" + adminPath + "/cms/guestbook/list?repage&status=2";
    }

}
