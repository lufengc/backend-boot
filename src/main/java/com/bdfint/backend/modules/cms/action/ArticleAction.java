/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.cms.action;

import com.bdfint.backend.framework.common.BaseAction;
import com.bdfint.backend.framework.common.Param;
import com.bdfint.backend.framework.mapper.JsonMapper;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.cms.bean.Article;
import com.bdfint.backend.modules.cms.bean.ArticleData;
import com.bdfint.backend.modules.cms.bean.Category;
import com.bdfint.backend.modules.cms.bean.Site;
import com.bdfint.backend.modules.cms.service.ArticleService;
import com.bdfint.backend.modules.cms.service.CategoryService;
import com.bdfint.backend.modules.cms.service.FileTplService;
import com.bdfint.backend.modules.cms.service.SiteService;
import com.bdfint.backend.modules.cms.utils.CmsUtils;
import com.bdfint.backend.modules.cms.utils.TplUtils;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 网站Controller
 *
 * @author lufengcheng
 * @date 2016-01-15 09:56:22
 */
@Controller
@RequestMapping(value = "${adminPath}/cms/article")
public class ArticleAction extends BaseAction {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private FileTplService fileTplService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 数据绑定
     *
     * @param id ID
     * @return Article
     */
    @ModelAttribute
    public Article get(@RequestParam(required = false) String id) throws Exception {
        Article article;
        if (StringUtils.isNotEmpty(id)) {
            article = articleService.get(id);
            article.setCategoryName(categoryService.get(article.getCategoryId()).getName());
        } else {
            article = new Article();
        }
        return article;
    }

    @RequestMapping(value = "")
    @RequiresPermissions("cms:article:view")
    public String index() {
        return "modules/cms/articleIndex";
    }

    @RequestMapping(value = "none")
    @RequiresPermissions("cms:article:view")
    public String none() {
        return "modules/cms/articleNone";
    }

    /**
     * 列表
     *
     * @param model    Model
     * @param object   object
     * @param request  request
     * @param response @return view
     */
    @RequestMapping(value = "list")
    @RequiresPermissions("cms:article:view")
    public String list(Model model, Article object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        articleService.updateWeight();
        String conditions = "del_flag <> 1";
        //根据当前组织机构ID查询数据
        if (StringUtils.isNotEmpty(object.getCategoryId())) {
            String ids = object.getCategoryId();
            List<Category> categories = categoryService.getByParentIdsLike(object.getCategoryId());
            if (categories != null && categories.size() > 0) {
                for (Category categorie : categories) {
                    ids += "," + categorie.getId();
                }
            }
            conditions += " AND category_id in(" + StringUtils.idsToString(ids) + ")";
        }

        if (StringUtils.isNotEmpty(object.getTitle())) {
            conditions += " AND title LIKE '%" + object.getTitle() + "%'";
        }
        if (StringUtils.isNotEmpty(object.getKeywords())) {
            conditions += " AND keywords LIKE '%" + object.getKeywords() + "%'";
        }
        Example example = new Example(Article.class);
        example.createCriteria().andCondition(conditions);
        PageInfo<Article> page = articleService.getPage(object, example);
        List<Article> list = page.getList();
        for (Article article : list) {
            article.setCategoryName(categoryService.get(article.getCategoryId()).getName());
        }
        page.setList(list);
        model.addAttribute("page", page);
        return "modules/cms/articleList";
    }

    /**
     * 编辑视图
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "form")
    @RequiresPermissions("cms:article:edit")
    public String form(Model model, Article object) throws Exception {
        ArticleData articleData = null;
        if (StringUtils.isNotEmpty(object.getId())) {
            articleData = articleService.getArticleData(object.getId());
        } else {
            object.setDelFlag("2");
        }
        if (articleData == null) {
            articleData = new ArticleData();
            articleData.setAllowComment("1");
        }
        object.setArticleData(articleData);
        model.addAttribute("contentViewList", getTplContent());
        model.addAttribute("article_DEFAULT_TEMPLATE", Article.DEFAULT_TEMPLATE);
        model.addAttribute("article", object);
        CmsUtils.addViewConfigAttribute(model, CmsUtils.getCategory(object.getCategoryId()));
        return "modules/cms/articleForm";
    }

    private List<String> getTplContent() throws Exception {
        List<String> tplList = fileTplService.getNameListByPrefix(siteService.get(Site.getCurrentSiteId()).getSolutionPath());
        tplList = TplUtils.tplTrim(tplList, Article.DEFAULT_TEMPLATE, "");
        return tplList;
    }

    /**
     * 保存
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "save")
    @RequiresPermissions("cms:article:edit")
    public String save(Model model, Article object, RedirectAttributes redirectAttributes) throws Exception {
        if (!beanValidator(model, object)) {
            return form(model, object);
        }
        articleService.save(object);
        addMessage(redirectAttributes, "保存成功");
        return "redirect:" + adminPath + "/cms/article/list?repage";
    }

    /**
     * 删除
     *
     * @param model  Model
     * @param object object
     * @return view
     */
    @RequestMapping(value = "delete")
    @RequiresPermissions("cms:article:edit")
    public String delete(Model model, Article object, Param param, RedirectAttributes redirectAttributes) throws Exception {
        articleService.delete(param.getIds());
        addMessage(redirectAttributes, "删除成功");
        return "redirect:" + adminPath + "/cms/article/list?repage";
    }

    /**
     * 文章选择列表
     */
    @RequestMapping(value = "selectList")
    @RequiresPermissions("cms:article:view")
    public String selectList(Article article, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        list(model, article, request, response);
        return "modules/cms/articleSelectList";
    }

    /**
     * 通过编号获取文章标题
     */
    @ResponseBody
    @RequestMapping(value = "findByIds")
    @RequiresPermissions("cms:article:view")
    public String findByIds(String ids) throws Exception {
        List<Object[]> list = articleService.getByIds(ids);
        return JsonMapper.nonDefaultMapper().toJson(list);
    }

}
