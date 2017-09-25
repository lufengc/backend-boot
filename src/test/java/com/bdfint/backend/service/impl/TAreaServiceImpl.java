/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.service.impl;

import com.bdfint.backend.framework.config.TargetDataSource;
import com.bdfint.backend.modules.sys.bean.Area;
import com.bdfint.backend.modules.sys.bean.TArea;
import com.bdfint.backend.modules.sys.mapper.AreaMapper;
import com.bdfint.backend.modules.sys.mapper.TAreaMapper;
import com.bdfint.backend.service.TAreaService;
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
public class TAreaServiceImpl implements TAreaService {

    @Autowired
    private TAreaMapper tAreaMapper;
    @Autowired
    private AreaMapper AreaMapper;

    @Override
    @Transactional
    public void updateData() throws Exception {
        Area tArea = new Area();
        tArea.setId("1");
        tArea.setName("中国3");
        AreaMapper.updateByPrimaryKeySelective(tArea);
    }

    @Override
    @Transactional
    @TargetDataSource("ds1")
    public void updateDatads() throws Exception {
        TArea tArea = new TArea();
        tArea.setId("1");
        tArea.setName("中国ds3");
        tAreaMapper.updateByPrimaryKeySelective(tArea);
    }

}
