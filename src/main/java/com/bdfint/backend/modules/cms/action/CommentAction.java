/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.cms.action;

import com.bdfint.backend.framework.common.BaseAction;
import com.bdfint.backend.framework.common.Param;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.cms.bean.Comment;
import com.bdfint.backend.modules.cms.service.CommentService;
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
 * 评论
 *
 * @author lufengc
 * @date 2016-01-15 09:56:22
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/comment")
public class CommentAction extends BaseAction {

    @Autowired
    private CommentService commentService;

    /**
     * 数据绑定
     *
     * @param id ID
     * @return Comment
     */
    @ModelAttribute
    public Comment get(@RequestParam(required = false) String id) throws Exception {
        Comment cmsComment;
        if (StringUtils.isNotEmpty(id)) {
            cmsComment = commentService.get(id);
        } else {
            cmsComment = new Comment();
        }
        return cmsComment;
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
    @RequiresPermissions("cms:comment:view")
    public String list(Model model, Comment object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Example example = new Example(Comment.class);
        example.setOrderByClause("create_date desc");

        Example.Criteria criteria = example.createCriteria();
        if (object.getTitle() != null) {
            criteria.andLike("title", "%" + object.getTitle() + "%");
        }
        if (object.getDelFlag() != null) {
            criteria.andEqualTo("delFlag", object.getDelFlag());
        }
        PageInfo<Comment> page = commentService.getPage(object, example);
        model.addAttribute("page", page);
        return "modules/cms/commentList";
    }

    /**
     * 编辑视图
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "form")
    @RequiresPermissions("cms:comment:edit")
    public String form(Model model, Comment object) throws Exception {
        return "";
    }

    /**
     * 保存
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "save")
    @RequiresPermissions("cms:comment:edit")
    protected String save(Model model, Comment object, RedirectAttributes redirectAttributes) throws Exception {
        if (beanValidator(redirectAttributes, object)) {
            if (object.getAuditUserId() == null) {
                object.setAuditUserId(UserUtils.getUserId());
                object.setAuditDate(new Date());
            }
            commentService.save(object);
            addMessage(redirectAttributes, DictUtils.getDictLabel(object.getDelFlag(), "cms_status", "保存")
                    + "评论'" + StringUtils.abbr(StringUtils.replaceHtml(object.getContent()), 50) + "'成功");
        }
        addMessage(redirectAttributes, "保存成功");
        return "redirect:" + adminPath + "/cms/comment/list?repage";
    }

    /**
     * 删除
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "delete")
    @RequiresPermissions("cms:comment:edit")
    public String delete(Model model, Comment object, Param param, RedirectAttributes redirectAttributes) throws Exception {
        commentService.delete(object.getId());
        addMessage(redirectAttributes, "删除成功");
        return "redirect:" + adminPath + "/cms/comment/list?repage";
    }

}
