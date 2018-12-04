/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.cms.service.impl;

import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.modules.cms.bean.FileTpl;
import com.cheng.backend.modules.cms.service.FileTplService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件模版Service实现类
 *
 * @author fengcheng
 * @version 2016-09-12
 */
@Service
public class FileTplServiceImpl extends BaseServiceImpl<FileTpl> implements FileTplService {

    @Autowired
    private ServletContext context;

    @Override
    public List<String> getNameListByPrefix(String path) {
        List<FileTpl> list = getListByPath(path, false);
        List<String> result = new ArrayList<>(list.size());
        for (FileTpl tpl : list) {
            result.add(tpl.getName());
        }
        return result;
    }

    @Override
    public List<FileTpl> getListByPath(String path, boolean directory) {
        File f = new File(context.getRealPath(path));
        if (f.exists()) {
            File[] files = f.listFiles();
            if (files != null) {
                List<FileTpl> list = new ArrayList<>();
                for (File file : files) {
                    if (file.isFile() || directory)
                        list.add(new FileTpl(file, context.getRealPath("")));
                }
                return list;
            } else {
                return new ArrayList<>(0);
            }
        } else {
            return new ArrayList<>(0);
        }
    }

    @Override
    public List<FileTpl> getListForEdit(String path) {
        List<FileTpl> list = getListByPath(path, true);
        List<FileTpl> result = new ArrayList<>();
        result.add(new FileTpl(new File(context.getRealPath(path)), context.getRealPath("")));
        getAllDirectory(result, list);
        return result;
    }

    @Override
    public void getAllDirectory(List<FileTpl> result, List<FileTpl> list) {
        for (FileTpl tpl : list) {
            result.add(tpl);
            if (tpl.isDirectory()) {
                getAllDirectory(result, getListByPath(tpl.getName(), true));
            }
        }
    }

    @Override
    public FileTpl getFileTpl(String name) {
        File f = new File(context.getRealPath(name));
        if (f.exists()) {
            return new FileTpl(f, "");
        } else {
            return null;
        }
    }

    @Override
    public String save(FileTpl object) throws Exception {
        return null;
    }
}
