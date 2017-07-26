/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.framework.exception;

/**
 * 自定义异常基类
 *
 * @author lufengc
 * @version 2016/1/21 19:29
 */
public abstract class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Class<? extends BaseException> subClass;

    public Class<? extends BaseException> getSubClass() {
        return subClass;
    }

    public void setSubClass(Class<? extends BaseException> subClass) {
        this.subClass = subClass;
    }

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    protected BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
