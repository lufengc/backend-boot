/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.sys.service.impl;

import com.cheng.backend.framework.cache.JedisUtils;
import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.sys.bean.Dict;
import com.cheng.backend.modules.sys.service.DictService;
import com.cheng.backend.modules.sys.utils.DictUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 字典表service实现类
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Service
public class DictServiceImpl extends BaseServiceImpl<Dict> implements DictService {

    /**
     * 保存
     *
     * @param object Dict
     * @return ID
     */
    @Override
    @Transactional
    public String save(Dict object) throws Exception {
        String id = object.getId();
        if (StringUtils.isNotEmpty(object.getId())) {
            object.preUpdate();
            super.update(object);
        } else {
            object.preInsert();
            super.insert(object);
            id = object.getId();
        }
        JedisUtils.delObject(DictUtils.CACHE_DICT_MAP);
        return id;
    }

    /**
     * 删除
     *
     * @param ids 删除的ID
     * @return int
     */
    @Override
    @Transactional
    public int delete(String ids) throws Exception {
        int delete = super.delete(ids);
        JedisUtils.delObject(DictUtils.CACHE_DICT_MAP);
        return delete;
    }

}
