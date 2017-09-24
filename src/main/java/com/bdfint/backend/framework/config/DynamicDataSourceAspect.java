/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.framework.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 切换数据源
 * Order(-10) 保证该AOP在@Transactional之前执行
 *
 * @author fengcheng
 * @version 2017/9/21
 */
@Aspect
@Order(-1)
@Component
public class DynamicDataSourceAspect {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    /**
     * 在方法执行之前进行执行：
     * 在方法上使用注解@annotation(targetDataSource)： 会拦截注解targetDataSource的方法，否则不拦截;
     */
    @Before("@annotation(targetDataSource)")
    public void changeDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        //获取当前的指定的数据源;
        String dsId = targetDataSource.value();
        //如果不在注入的所有的数据源范围之内，那么输出警告信息，系统自动使用默认的数据源。
        if (!DynamicDataSource.containsDataSource(dsId)) {
            logger.error("数据源[{}]不存在，使用默认数据源 > {}", targetDataSource.value() + point.getSignature());
        } else {
            logger.debug("Use DataSource : {} > {}", targetDataSource.value(), point.getSignature());
            //找到的话，那么设置到动态数据源上下文中。
            DynamicDataSource.setDataSourceType(targetDataSource.value());
        }
    }


    /**
     * 方法执行完毕之后，销毁当前数据源信息，进行垃圾回收。
     *
     * @param point            JoinPoint
     * @param targetDataSource TargetDataSource
     */
    @After("@annotation(targetDataSource)")
    public void restoreDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        logger.debug("Revert DataSource : {} > {}", targetDataSource.value(), point.getSignature());
        DynamicDataSource.clearDataSourceType();
    }
}
