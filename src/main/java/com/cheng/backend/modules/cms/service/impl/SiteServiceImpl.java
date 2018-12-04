/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */
package com.cheng.backend.modules.cms.service.impl;

import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.cms.bean.Site;
import com.cheng.backend.modules.cms.service.SiteService;
import com.cheng.backend.modules.cms.utils.CmsUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;

/**
 * 站点Service
 *
 * @author fengcheng
 * @version 2016-09-12
 */
@Service
public class SiteServiceImpl extends BaseServiceImpl<Site> implements SiteService {

    @Override
    public String save(Site site) throws Exception {
        if (site.getCopyright() != null) {
            site.setCopyright(StringEscapeUtils.unescapeHtml4(site.getCopyright()));
        }
        if (StringUtils.isNotEmpty(site.getId())) {
            site.preUpdate();
            super.update(site);
        } else {
            site.preInsert();
            super.insert(site);
        }
        CmsUtils.removeCache("site_" + site.getId());
        CmsUtils.removeCache("siteList");
        return site.getId();
    }

    @Override
    public int delete(String ids) throws Exception {
        super.delete(ids);
        CmsUtils.removeCache("site_" + ids);
        CmsUtils.removeCache("siteList");
        return 1;
    }

}
