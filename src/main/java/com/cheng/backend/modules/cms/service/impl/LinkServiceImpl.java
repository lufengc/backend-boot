/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.modules.cms.service.impl;

import com.cheng.backend.framework.cache.JedisUtils;
import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.cms.bean.Link;
import com.cheng.backend.modules.cms.mapper.LinkMapper;
import com.cheng.backend.modules.cms.service.LinkService;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * 链接Service
 *
 * @author fengcheng
 * @version 2016-09-12
 */
@Service
@Transactional(readOnly = true)
public class LinkServiceImpl extends BaseServiceImpl<Link> implements LinkService {

    @Autowired
    private LinkMapper linkMapper;

    @Override
    public PageInfo<Link> getPage(Link object, Example example) throws Exception {
        // 更新过期的权重，间隔为“6”个小时
        Date updateExpiredWeightDate = (Date) JedisUtils.getObject("updateExpiredWeightDateByLink");
        if (updateExpiredWeightDate == null || updateExpiredWeightDate.getTime() < new Date().getTime()) {
            Link link = new Link();
            link.setWeight(0);
            Example example1 = new Example(Link.class);
            example1.createCriteria().andGreaterThan("weight", 0)
                    .andLessThan("weightDate", new Date());
            linkMapper.updateByExampleSelective(link, example1);
            JedisUtils.setObject("updateExpiredWeightDateByLink", DateUtils.addHours(new Date(), 6), 0);
        }

        return super.getPage(object, example);
    }

    @Override
    public String save(Link object) throws Exception {
        return null;
    }

    @Override
    public int delete(String ids) throws Exception {
        super.delete(ids);
        return 1;
    }

    /**
     * 通过编号获取内容标题
     */
    @Override
    public List<Object[]> findByIds(String ids) throws Exception {
        List<Link> cmsLinks = Lists.newArrayList();
        for (String id : ids.split(",")) {
            cmsLinks.add(super.get(id));
        }
        List<Object[]> list = Lists.newArrayList();
        for (Link e : cmsLinks) {
            list.add(new Object[]{e.getId(), StringUtils.abbr(e.getTitle(), 50)});
        }
        return list;
    }

}
