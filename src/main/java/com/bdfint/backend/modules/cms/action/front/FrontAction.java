/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.cms.action.front;

import com.bdfint.backend.framework.common.BaseEntity;
import com.bdfint.backend.framework.common.BaseFrontAction;
import com.bdfint.backend.framework.util.UserAgentUtils;
import com.bdfint.backend.modules.cms.bean.Article;
import com.bdfint.backend.modules.cms.bean.ArticleData;
import com.bdfint.backend.modules.cms.bean.Guestbook;
import com.bdfint.backend.modules.cms.service.ArticleService;
import com.bdfint.backend.modules.cms.service.GuestbookService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 网站Controller
 *
 * @author lufengcheng
 * @date 2016-01-15 09:56:22
 */
@Controller
@RequestMapping(value = "${frontPath}")
public class FrontAction extends BaseFrontAction {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private GuestbookService guestbookService;

    /**
     * 网站首页
     */
    @RequestMapping
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageInfo<Article> page = articleService.getPage(new Article(), new Example(Article.class));
        model.addAttribute("page", page);
        return "modules/cms/front/pages/index";
    }

    /**
     * 文章列表
     */
    @RequestMapping("article")
    public String article(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PageInfo<Article> page = articleService.getPage(new Article(), new Example(Article.class));
        model.addAttribute("page", page);
        return "modules/cms/front/pages/article-list";
    }

    /**
     * 文章详情
     */
    @RequestMapping("article/{id}")
    public String article(Model model, @PathVariable("id") String id) throws Exception {
        Article bean = articleService.get(id);
        if (bean == null || Objects.equals(bean.getDelFlag(), BaseEntity.DEL_FLAG_DELETE)) {
            return "error/404";
        }
        // 文章阅读次数+1
        bean.setHits(bean.getHits() + 1);
        articleService.updateArticle(bean);
        model.addAttribute("bean", bean);
        model.addAttribute("content", articleService.getArticleData(id).getContent());
        return "modules/cms/front/pages/article";
    }

    /**
     * 关于我
     */
    @RequestMapping("about")
    public String about(Model model) throws Exception {
        return "modules/cms/front/pages/about";
    }

    /**
     * 留言板首页
     */
    @RequestMapping(value = "guestbook", method = RequestMethod.GET)
    public String guestbook(Model model, Guestbook guestbook) throws Exception {
        PageInfo<Guestbook> page = guestbookService.getPage(guestbook, new Example(Guestbook.class));
        model.addAttribute("page", page);
        return "modules/cms/front/pages/guestbook";
    }

    /**
     * 留言板-保存留言信息
     */
    @RequestMapping(value = "guestbook", method = RequestMethod.POST)
    public String guestbookSave(Model model, Guestbook guestbook,
                                HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        guestbook.setIp(request.getRemoteAddr());
        guestbook.setDelFlag("0");
        guestbookService.save(guestbook);
        addMessage(redirectAttributes, "提交成功，谢谢！");
        return "redirect:" + frontPath + "/guestbook";
    }

    /**
     * data
     */
    @RequestMapping("data")
    public String data(Model model) throws Exception {
        return "modules/cms/front/pages/data";
    }

    /**
     * write page
     *
     * @return view
     */
    @RequestMapping(value = "write")
    public String write(Model model, String id, HttpServletRequest request) throws Exception {
        Article object;
        if (StringUtils.isNotEmpty(id)) {
            object = articleService.get(id);
        } else {
            object = new Article();
        }
        String content = "";
        if (StringUtils.isNotEmpty(object.getId())) {
            content = articleService.getArticleData(id).getContent();
        }
        boolean computer = UserAgentUtils.isComputer(request);
        model.addAttribute("computer", computer ? 1 : 0);
        model.addAttribute("cmsArticle", object);
        model.addAttribute("content", content);
        return "modules/cms/front/pages/write";
    }

    /**
     * save
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "/article/save")
    public String save(Model model, Article object, ArticleData articleData,
                       RedirectAttributes redirectAttributes) throws Exception {
        object.setCategoryId("心情随笔");
        String id = articleService.save(object, articleData);
        addMessage(redirectAttributes, "保存成功");
        return "redirect:" + frontPath + "/grz/article/" + id;
    }

    /**
     * 删除
     *
     * @return view
     */
    @RequestMapping(value = "/grz/article/delete/{id}")
    public String delete(@PathVariable("id") String id, RedirectAttributes redirectAttributes) throws Exception {
//        Article bean = new Article();
//        bean.setId(id);
//        bean.setDelFlag(BaseEntity.DEL_FLAG_DELETE);
//        articleService.updateArticle(bean);
        articleService.delete(id);
        addMessage(redirectAttributes, "删除成功");
        return "redirect:" + frontPath + "/grz?repage";
    }
}
