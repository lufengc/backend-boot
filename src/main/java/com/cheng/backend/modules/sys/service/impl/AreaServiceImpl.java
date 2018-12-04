/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.sys.service.impl;

import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.framework.util.Encodes;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.sys.bean.Area;
import com.cheng.backend.modules.sys.bean.Menu;
import com.cheng.backend.modules.sys.mapper.AreaMapper;
import com.cheng.backend.modules.sys.service.AreaService;
import com.cheng.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 区域Service
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Service
public class AreaServiceImpl extends BaseServiceImpl<Area> implements AreaService {

    @Autowired
    private AreaMapper areaMapper;

    /**
     * 保存或更新操作
     *
     * @param object Object
     * @return 主键ID
     */
    @Override
    @Transactional
    public String save(Area object) throws Exception {
        String oldParentIds = object.getParentIds();
        Area parent = get(object.getParentId());
        if (parent != null) {
            object.setParentIds(parent.getParentIds() + parent.getId() + ",");
        } else {
            object.setParentIds(Menu.getRootId());
        }
        if (StringUtils.isNotEmpty(object.getId())) {
            object.preUpdate();
            super.update(object);
            // 更新子节点parentIds
            Example example = new Example(Area.class);
            example.createCriteria().andLike("parentId", ",object.getId(),");
            List<Area> list = areaMapper.selectByExample(example);
            if (list != null && list.size() > 0) {
                for (Area p : list) {
                    p.setParentIds(p.getParentIds().replace(oldParentIds, object.getParentIds()));
                    super.update(object);
                }
            }
        } else {
            List<Area> areaList = UserUtils.getAreaList();
            int sort = 0;
            for (Area area : areaList) {
                if (area.getSort() > sort) {
                    sort = area.getSort();
                }
            }
            object.setSort(sort);
            object.setId(Encodes.uuid());
            object.preInsert();
            super.insert(object);
        }
        UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
        return object.getId();
    }

    /**
     * 删除操作
     *
     * @param ids 删除的ID
     * @return String
     */
    @Override
    @Transactional
    public int delete(String ids) throws Exception {
        super.delete(ids);
        UserUtils.removeCache(UserUtils.CACHE_AREA_LIST);
        return 1;
    }

    /**
     * 根据parentId查询子节点列表
     *
     * @param id parentId
     * @return 子节点集合
     */
    @Override
    public List<Area> findChildList(String id) {
        List<Area> childList = new ArrayList<>();
        List<Area> areaList = UserUtils.getAreaList();
        for (Area area : areaList) {
            if (Objects.equals(area.getParentId(), id)) {
                if (findChildList(area.getId()).size() > 0) {
                    area.setHasChild(true);
                }
                childList.add(area);
            }
        }
        return childList;
    }
}
