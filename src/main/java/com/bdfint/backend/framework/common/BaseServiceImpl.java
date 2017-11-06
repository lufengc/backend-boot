/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.framework.common;

import com.bdfint.backend.framework.util.Encodes;
import com.bdfint.backend.framework.util.Reflections;
import com.bdfint.backend.framework.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * service基类实现类
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
public abstract class BaseServiceImpl<T extends BaseEntity<T>> implements BaseService<T> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected Mapper<T> mapper;

    /**
     * 业务数据插入或更新操作
     * 复杂操作由业务类实现
     *
     * @param object T
     */
    @Override
    public String save(T object) throws Exception {
        String id = object.getId();
        if (StringUtils.isNotEmpty(object.getId())) {
            update(object);
        } else {
            object.setId(Encodes.uuid());
            insert(object);
            id = object.getId();
        }
        return id;
    }

    /**
     * 插入
     *
     * @param object T
     */
    @Override
    public int insert(T object) throws Exception {
        return mapper.insertSelective(object);
    }

    /**
     * 更新
     *
     * @param object T
     */
    @Override
    public void update(T object) throws Exception {
        mapper.updateByPrimaryKeySelective(object);
    }

    /**
     * 删除
     *
     * @param ids 删除的ID
     * @return 删除数量
     */
    @Override
    public int delete(String ids) throws Exception {
        String[] split = ids.split(",");
        for (String s : split) {
            mapper.deleteByPrimaryKey(s);
        }
        return split.length;
    }

    /**
     * 根据ID查询
     *
     * @param id 对象id
     * @return Object
     */
    public T get(Object id) throws Exception {
        T entity = mapper.selectByPrimaryKey(id);
        if (entity == null) {
            @SuppressWarnings("unchecked")
            Class<T> entityClass = Reflections.getClassGenricType(getClass());
            entity = entityClass.newInstance();
        }
        return entity;
    }

    /**
     * 查询列表,
     * 参数为空对象，则查询所有，如：getList(new SysUser())
     *
     * @param object 要查询的对象
     * @return List<T>
     */
    public List<T> getList(T object) throws Exception {
        List<T> list = mapper.select(object);
        if (list == null) {
            list = Lists.newArrayList();
        }
        return list;
    }

    /**
     * 分页查询
     *
     * @param object  分页对象
     * @param example Example
     * @return Page<T>
     */
    @Override
    public PageInfo<T> getPage(T object, Example example) throws Exception {
        if (example.getOredCriteria().size() > 0) {
            example.getOredCriteria().get(0).andNotEqualTo("delFlag", BaseEntity.DEL_FLAG_DELETE);
        } else {
            example.createCriteria().andNotEqualTo("delFlag", BaseEntity.DEL_FLAG_DELETE);
        }
        int pageSize = object.getPageSize() == 0 ? Global.PAGE_SIZE : object.getPageSize();
        if (pageSize != -1) {
            PageHelper.startPage(object.getPageNum(), pageSize);
        }
        List<T> list = mapper.selectByExample(example);
        PageInfo<T> page = new PageInfo<>(list);
        page.setPageNum(object.getPageNum());
        page.setPageSize(pageSize);
        return page;
    }
}
