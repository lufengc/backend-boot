/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.framework.exception;

/**
 * 自定义公共异常类
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
public class CommonException extends BaseException {

    private static final long serialVersionUID = 1L;

    {
        this.setSubClass(this.getClass());
    }

    public CommonException() {
        super();
    }

    public CommonException(String message) {
        super(message);
    }

    public CommonException(Throwable cause) {
        super(cause);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }
}
