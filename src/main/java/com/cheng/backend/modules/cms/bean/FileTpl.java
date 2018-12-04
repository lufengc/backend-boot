/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.cms.bean;

import com.cheng.backend.framework.common.BaseEntity;
import org.apache.commons.io.FileUtils;

import javax.persistence.Transient;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 文件模版
 *
 * @author fengcheng
 * @version 2016/7/28
 */
public class FileTpl extends BaseEntity<FileTpl> {

    @Transient
    private File file;

    // 应用的根目录
    @Transient
    private String root;

    public FileTpl(File file, String root) {
        this.file = file;
        this.root = root;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getName() {
        String ap = file.getAbsolutePath().substring(root.length());
        ap = ap.replace(File.separatorChar, '/');
        // 在resin里root的结尾是带'/'的，这样会导致getName返回的名称不以'/'开头。
        if (!ap.startsWith("/")) {
            ap = "/" + ap;
        }
        return ap;
    }

    public String getParent() {
        String ap = file.getParent().substring(root.length());
        ap = ap.replace(File.separatorChar, '/');
        // 在resin里root的结尾是带'/'的，这样会导致getName返回的名称不以'/'开头。
        if (!ap.startsWith("/")) {
            ap = "/" + ap;
        }
        return ap;
    }

    public String getPath() {
        String name = getName();
        return name.substring(0, name.lastIndexOf('/'));
    }

    public String getFilename() {
        return file.getName();
    }

    public String getSource() {
        if (file.isDirectory()) {
            return null;
        }
        try {
            return FileUtils.readFileToString(this.file, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getLastModified() {
        return file.lastModified();
    }

    public Date getLastModifiedDate() {
        return new Timestamp(getLastModified());
    }

    public long getLength() {
        return file.length();
    }

    public int getSize() {
        return (int) (getLength() / 1024) + 1;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }
}
