/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend;

import com.bdfint.backend.modules.sys.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author fengcheng
 * @version 2017/9/23
 */
public class DataLoadTest extends BaseTest {

    @Autowired
    private UserService userService;

    @Test
    public void dataLoad() {
        userService.dataLoad();
    }
}
