/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.modules.cms.service.impl;

import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.modules.cms.bean.Comment;
import com.cheng.backend.modules.cms.service.CommentService;
import org.springframework.stereotype.Service;

/**
 * 评论Service
 *
 * @author fengcheng
 * @version 2016-09-12
 */
@Service
public class CommentServiceImpl extends BaseServiceImpl<Comment> implements CommentService {

    @Override
    public String save(Comment object) throws Exception {
        return null;
    }
}
