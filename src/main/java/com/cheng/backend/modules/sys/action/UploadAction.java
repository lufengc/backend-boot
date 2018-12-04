/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.sys.action;

import com.cheng.backend.framework.common.Global;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.sys.utils.UploadUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件上传action
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Controller
@RequestMapping(value = "${adminPath}")
public class UploadAction {

    /**
     * 上传图片
     *
     * @param upload 上传文件
     * @return json
     */
    @ResponseBody
    @RequestMapping("/uploadImage")
    public String uploadImage(@RequestParam(value = "upload", required = false) MultipartFile upload) {
        return UploadUtils.uploadImage(upload, 1);
    }

    /**
     * 编辑器上传图片
     *
     * @param file 上传文件
     */
    @RequestMapping("/editorUpload")
    public void editorUpload(MultipartFile file, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String fileName = UploadUtils.uploadImage(file, 2);
        if (StringUtils.isNotEmpty(fileName)) {
            // 返回图片的URL地址
            response.getWriter().write(request.getContextPath() + Global.getFileAccessPath() + fileName);
        }
    }

}