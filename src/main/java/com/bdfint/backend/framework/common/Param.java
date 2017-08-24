/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.framework.common;

/**
 * 公共参数接收器
 *
 * @author lufengcheng
 * @version 2016-01-15 09:56:22
 */
public class Param extends BaseEntity<Param> {

    private String ids;

    private String checkedIds;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getCheckedIds() {
        return checkedIds;
    }

    public void setCheckedIds(String checkedIds) {
        this.checkedIds = checkedIds;
    }
}
