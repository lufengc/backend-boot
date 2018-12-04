/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.cms.service.impl;

import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.framework.util.Encodes;
import com.cheng.backend.modules.cms.bean.Guestbook;
import com.cheng.backend.modules.cms.service.GuestbookService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 留言板service实现类
 *
 * @author fengcheng
 * @date 2016-01-15 09:56:22
 */
@Service
public class GuestbookServiceImpl extends BaseServiceImpl<Guestbook> implements GuestbookService {

    @Override
    public String save(Guestbook object) throws Exception {
        if (StringUtils.isNotEmpty(object.getId())) {
            super.update(object);
        } else {
            object.setId(Encodes.uuid());
            super.insert(object);
        }
        return object.getId();
    }

}
