/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author fengcheng
 * @version 2017/3/13
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BaseTest {
    Logger logger = LoggerFactory.getLogger(this.getClass());
}
