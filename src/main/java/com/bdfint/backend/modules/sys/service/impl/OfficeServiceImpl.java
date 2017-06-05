/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.sys.service.impl;

import com.bdfint.backend.framework.common.BaseServiceImpl;
import com.bdfint.backend.framework.util.Encodes;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.sys.bean.Menu;
import com.bdfint.backend.modules.sys.bean.Office;
import com.bdfint.backend.modules.sys.mapper.OfficeMapper;
import com.bdfint.backend.modules.sys.service.OfficeService;
import com.bdfint.backend.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

/**
 * 机构Service
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Service
public class OfficeServiceImpl extends BaseServiceImpl<Office> implements OfficeService {

    @Autowired
    private OfficeMapper officeMapper;

    /**
     * 获取结构列表
     *
     * @param isAll true：所有机构数据，false：只查询当前用户拥有的机构数据
     * @return List<Office>
     */
    @Override
    public List<Office> getList(Boolean isAll) throws Exception {
        if (isAll != null && isAll) {
            return getList(new Office());
        } else {
            return UserUtils.getOfficeList();
        }
    }

    /**
     * 根据用户ID获取机构列表
     *
     * @param userId 用户id
     * @return List<Office>
     */
    @Override
    public List<Office> getOfficeByUserId(String userId) {
        return officeMapper.getOfficeByUserId(userId);
    }

    /**
     * 根据parentId查询该机构下所有的子列表数据
     *
     * @param parentIds parentIds
     * @return List<Office>
     */
    @Override
    public List<Office> getByParentIdsLike(String parentIds) {
        Example example = new Example(Office.class);
        example.createCriteria().andLike("parentIds", "%," + parentIds + ",%");
        return officeMapper.selectByExample(example);
    }

    /**
     * 获取某个机构下的所有子机构
     *
     * @param parentId 父id
     * @return List<Office>
     */
    @Override
    public List<Office> getByParentId(String parentId) throws Exception {
        List<Office> childList = new ArrayList<>();
        childList.add(get(parentId));
        getChildList(childList, UserUtils.getOfficeList(), parentId);
        return childList;
    }

    /**
     * 获取某个父节点下面的所有子节点
     *
     * @param childList 用户保存子节点的集合
     * @param allList   总数据结合
     * @param parentId  父ID
     */
    private void getChildList(List<Office> childList, List<Office> allList, String parentId) {
        for (Office object : allList) {
            if (parentId.equals(object.getParentId())) {
                getChildList(childList, allList, object.getId());
                childList.add(object);
            }
        }
    }

    /**
     * 保存
     *
     * @param object Office
     * @return 主键id
     */
    @Override
    @Transactional
    public String save(Office object) throws Exception {
        String oldParentIds = object.getParentIds();
        Office parent = get(object.getParentId());
        if (parent != null) {
            object.setParentIds(parent.getParentIds() + parent.getId() + ",");
        } else {
            object.setParentIds(Menu.getRootId());
        }
        if (StringUtils.isNotEmpty(object.getId())) {
            object.preUpdate();
            super.update(object);
            // 更新子节点parentIds
            Example example = new Example(Office.class);
            example.createCriteria().andLike("parentId", "," + object.getId() + ",");
            List<Office> officeList = officeMapper.selectByExample(example);
            if (officeList != null && officeList.size() > 0) {
                for (Office p : officeList) {
                    p.setParentIds(p.getParentIds().replace(oldParentIds, object.getParentIds()));
                    super.update(p);
                }
            }
        } else {
            object.setId(Encodes.uuid());
            object.preInsert();
            super.insert(object);
        }
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_ALL_LIST);
        return object.getId();
    }

    /**
     * 删除
     *
     * @param id 需要删除的主键id
     * @return 处理的结果数量
     */
    @Override
    @Transactional
    public int delete(String id) throws Exception {
        StringBuilder ids = new StringBuilder(id);
        //获取子节点集合
        List<Office> childList = new ArrayList<>();
        getChildList(childList, UserUtils.getOfficeList(), id);
        for (Office office : childList) {
            ids.append(",").append(office.getId());
        }
        //删除该机构及所有子机构项
        super.delete(ids.toString());
        //删除角色机构关联表
        officeMapper.deleteRoleOfficeByOfficeIds(ids.toString().split(","));

        UserUtils.removeCache(UserUtils.CACHE_OFFICE_LIST);
        UserUtils.removeCache(UserUtils.CACHE_OFFICE_ALL_LIST);
        return 1;
    }

}
