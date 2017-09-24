/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.sys.service.impl;

import com.bdfint.backend.framework.common.BaseServiceImpl;
import com.bdfint.backend.framework.config.TargetDataSource;
import com.bdfint.backend.modules.sys.bean.TArea;
import com.bdfint.backend.modules.sys.mapper.TAreaMapper;
import com.bdfint.backend.modules.sys.service.TAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 区域Service
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Service
public class TAreaServiceImpl extends BaseServiceImpl<TArea> implements TAreaService {

    @Autowired
    private TAreaMapper tAreaMapper;

    @Override
    @Transactional
    @TargetDataSource("ds1")
    public void updateData() throws Exception {
        TArea tArea = new TArea();
        tArea.setId("1");
        tArea.setName("中国ds1");
        tAreaMapper.updateByPrimaryKeySelective(tArea);
    }

    @Override
    public String save(TArea object) throws Exception {
        return null;
    }
}
