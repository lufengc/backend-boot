/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.modules.cms.service.impl;

import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.cms.bean.Category;
import com.cheng.backend.modules.cms.bean.Site;
import com.cheng.backend.modules.cms.mapper.CategoryMapper;
import com.cheng.backend.modules.cms.service.CategoryService;
import com.cheng.backend.modules.cms.utils.CmsUtils;
import com.cheng.backend.modules.sys.service.OfficeService;
import com.cheng.backend.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * 栏目Service实现类
 *
 * @author fengcheng
 * @version 2016-09-12
 */
@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category> implements CategoryService {

    public static final String CACHE_CATEGORY_LIST = "categoryList";

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private OfficeService officeService;

    @Override
    @SuppressWarnings("unchecked")
    public List<Category> getByUser(boolean isCurrentSite, String module) throws Exception {
        List<Category> list = (List<Category>) UserUtils.getCache(CACHE_CATEGORY_LIST);
        if (list == null) {
            list = getList(new Category());
            for (Category next : list) {
                next.setOfficeName(officeService.get(next.getOfficeId()).getName());
            }
            if (list.size() > 0) {
                UserUtils.putCache(CACHE_CATEGORY_LIST, list);
            }
        }

        if (isCurrentSite) {
            List<Category> categoryList = Lists.newArrayList();
            for (Category e : list) {
                if (Category.isRoot(e.getId()) || (e.getSiteId() != null && e.getSiteId().equals(Site.getCurrentSiteId()))) {
                    if (StringUtils.isNotEmpty(module)) {
                        if (module.equals(e.getModule()) || "".equals(e.getModule())) {
                            categoryList.add(e);
                        }
                    } else {
                        categoryList.add(e);
                    }
                }
            }
            return categoryList;
        }
        return list;
    }

    @Override
    public List<Category> getByParentId(String parentId, String siteId) {
        Category category = new Category();
        category.setParentId(parentId);
        category.setSiteId(siteId);
        return categoryMapper.select(category);
    }

    @Override
    public List<Category> getByParentIdsLike(String parentIds) {
        Example example = new Example(Category.class);
        example.createCriteria().andLike("parentIds", "%," + parentIds + ",%");
        return categoryMapper.selectByExample(example);
    }

    @Override
    public String save(Category category) throws Exception {
        category.setSiteId(Site.getCurrentSiteId());
        if (StringUtils.isNotBlank(category.getViewConfig())) {
            category.setViewConfig(StringEscapeUtils.unescapeHtml4(category.getViewConfig()));
        }
        if (StringUtils.isNotEmpty(category.getId())) {
            category.preUpdate();
            super.update(category);
        } else {
            category.preInsert();
            super.insert(category);
        }
        UserUtils.removeCache(CACHE_CATEGORY_LIST);
        CmsUtils.removeCache("mainNavList_" + category.getSiteId());
        return category.getId();
    }

    @Override
    public int delete(String id) throws Exception {
        StringBuilder ids = new StringBuilder(id);
        //获取子节点集合
        List<Category> childList = new ArrayList<>();
        Category category = CmsUtils.getCategory(id);
        getChildList(childList, CmsUtils.getCategoryList(category.getSiteId(), null, -1, null), id);
        for (Category object : childList) {
            ids.append(",").append(object.getId());
        }

        super.delete(ids.toString());
        UserUtils.removeCache(CACHE_CATEGORY_LIST);
        CmsUtils.removeCache("mainNavList_" + category.getSiteId());
        return 1;
    }

    /**
     * 获取某个父节点下面的所有子节点
     *
     * @param childList 用户保存子节点的集合
     * @param allList   总数据结合
     * @param parentId  父ID
     */
    private void getChildList(List<Category> childList, List<Category> allList, String parentId) {
        for (Category object : allList) {
            if (parentId.equals(object.getParentId())) {
                getChildList(childList, allList, object.getId());
                childList.add(object);
            }
        }
    }

    /**
     * 通过编号获取栏目列表
     */
    @Override
    public List<Category> getByIds(String ids) throws Exception {
        List<Category> categoryList = Lists.newArrayList();
        for (String id : ids.split(",")) {
            categoryList.add(get(id));
        }
        return categoryList;
    }

    @Override
    public void updateSort(String[] ids, Integer[] sorts) throws Exception {
        for (int i = 0; i < ids.length; i++) {
            Category category = new Category();
            category.setId(ids[i]);
            category.setSort(sorts[i]);
            super.update(category);
        }
        UserUtils.removeCache(CACHE_CATEGORY_LIST);
        CmsUtils.removeCacheLike("mainNavList_");
    }

}
