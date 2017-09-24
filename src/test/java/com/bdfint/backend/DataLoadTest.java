/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend;

import com.bdfint.backend.modules.sys.service.AreaService;
import com.bdfint.backend.modules.sys.service.TAreaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author fengcheng
 * @version 2017/9/23
 */
public class DataLoadTest extends BaseTest {

    @Autowired
    private AreaService areaService;
    @Autowired
    private TAreaService tAreaService;

    @Test
    public void testDynamicDatasource() throws Exception {
        areaService.updateData();
        tAreaService.updateData();
    }
}
