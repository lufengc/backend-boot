/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.sys.utils;

import com.cheng.backend.framework.common.Global;
import com.cheng.backend.framework.util.DateUtils;
import com.cheng.backend.framework.util.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * @author fengcheng
 * @version 2017/5/23
 */
public class UploadUtils {

    private static final String ALLOW_TYPE_IMG = "jpg|gif|jpeg|png|bmp";

    public static String uploadImage(MultipartFile upload, int uploadType) {
        String fileName = "";
        String realPath = "";
        try {
            if (upload != null) {
                String uploadFileName = upload.getOriginalFilename();
                String filetype = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
                // 文件类型限制
                boolean allowed = ALLOW_TYPE_IMG.contains(filetype);
                if (allowed) {
                    String path = Global.getFileUploadPath();
                    if (uploadType == 2) { //编辑器上传
                        realPath += "editor/";
                    }
                    realPath += "userid_" + UserUtils.getPrincipal() + "/images/"
                            + DateUtils.formatDate(new Date()) + "/";
                    fileName = UUID.randomUUID().toString().replace("-", "") + "." + filetype;
                    FileUtils.createDirectory(path + realPath);
                    upload.transferTo(new File(path + realPath + fileName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return realPath + fileName;
    }
}
