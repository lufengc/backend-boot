/*
 * Copyright (c) 2017. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.bdfint.backend.modules.gen.utils;

import com.bdfint.backend.framework.common.Global;
import com.bdfint.backend.framework.mapper.JaxbMapper;
import com.bdfint.backend.framework.util.DateUtils;
import com.bdfint.backend.framework.util.FileUtils;
import com.bdfint.backend.framework.util.FreeMarkers;
import com.bdfint.backend.framework.util.StringUtils;
import com.bdfint.backend.modules.gen.bean.*;
import com.bdfint.backend.modules.sys.utils.UserUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * 代码生成工具类
 *
 * @author fengcheng
 * @version 2016/7/28
 */
public class GenUtils {

    private static Logger logger = LoggerFactory.getLogger(GenUtils.class);

    /**
     * 初始化列属性字段
     *
     * @param genTable GenTable
     */
    public static void initColumnField(GenTable genTable) {
        for (GenTableColumn column : genTable.getColumnList()) {

            // 如果是不是新增列，则跳过。
            if (StringUtils.isNotBlank(column.getId())) {
                continue;
            }

            // 设置字段说明
            if (StringUtils.isBlank(column.getComments())) {
                column.setComments(column.getName());
            }

            // 设置java类型
            if (StringUtils.startsWithIgnoreCase(column.getJdbcType(), "CHAR")
                    || StringUtils.startsWithIgnoreCase(column.getJdbcType(), "VARCHAR")
                    || StringUtils.startsWithIgnoreCase(column.getJdbcType(), "NARCHAR")) {
                column.setJavaType("String");
            } else if (StringUtils.startsWithIgnoreCase(column.getJdbcType(), "DATETIME")
                    || StringUtils.startsWithIgnoreCase(column.getJdbcType(), "DATE")
                    || StringUtils.startsWithIgnoreCase(column.getJdbcType(), "TIMESTAMP")) {
                column.setJavaType("java.util.Date");
                column.setShowType("dateselect");
            } else if (StringUtils.startsWithIgnoreCase(column.getJdbcType(), "BIGINT")
                    || StringUtils.startsWithIgnoreCase(column.getJdbcType(), "NUMBER")
                    || StringUtils.startsWithIgnoreCase(column.getJdbcType(), "INT")) {
                // 如果是浮点型
                String[] ss = StringUtils.split(StringUtils.substringBetween(column.getJdbcType(), "(", ")"), ",");
                if (ss != null && ss.length == 2 && Integer.parseInt(ss[1]) > 0) {
                    column.setJavaType("Double");
                }
                // 如果是整形
                else if (ss != null && ss.length == 1 && Integer.parseInt(ss[0]) <= 11) {
                    column.setJavaType("Integer");
                }
                // 长整形
                else {
                    column.setJavaType("Long");
                }
            }

            // 设置java字段名
            column.setJavaField(StringUtils.toCamelCase(column.getName()));

            // 是否是主键
            column.setIsPk(genTable.getPkList().contains(column.getName()) ? "1" : "0");

            // 插入字段
            column.setIsInsert("1");

            // 编辑字段
            if (!StringUtils.equalsIgnoreCase(column.getName(), "id")
                    && !StringUtils.equalsIgnoreCase(column.getName(), "create_by")
                    && !StringUtils.equalsIgnoreCase(column.getName(), "create_date")
                    && !StringUtils.equalsIgnoreCase(column.getName(), "update_by")
                    && !StringUtils.equalsIgnoreCase(column.getName(), "update_date")
                    && !StringUtils.equalsIgnoreCase(column.getName(), "del_flag")) {

                column.setIsEdit("1");
            }

            // 列表字段
            if (!StringUtils.equalsIgnoreCase(column.getName(), "id")
                    && !StringUtils.equalsIgnoreCase(column.getName(), "update_by")
                    && !StringUtils.equalsIgnoreCase(column.getName(), "update_date")
                    && !StringUtils.equalsIgnoreCase(column.getName(), "del_flag")
                    && !StringUtils.equalsIgnoreCase(column.getName(), "remarks")) {
                column.setIsList("1");
            }

            // 查询字段
            if (StringUtils.equalsIgnoreCase(column.getName(), "name")
                    || StringUtils.equalsIgnoreCase(column.getName(), "title")) {
                column.setIsQuery("1");
            }

            // 查询字段类型
            if (StringUtils.equalsIgnoreCase(column.getName(), "name")
                    || StringUtils.equalsIgnoreCase(column.getName(), "title")) {
                column.setQueryType("like");
            }

            // 设置特定类型和字段名

            // 用户
            if (StringUtils.startsWithIgnoreCase(column.getName(), "user_id")) {
                column.setJavaField(column.getJavaField().replaceAll("Id", ".id|name"));
                column.setShowType("userselect");
            }
            // 部门
            else if (StringUtils.startsWithIgnoreCase(column.getName(), "office_id")) {
                column.setJavaField(column.getJavaField().replaceAll("Id", ".id|name"));
                column.setShowType("officeselect");
            }
            // 区域
            else if (StringUtils.startsWithIgnoreCase(column.getName(), "area_id")) {
                column.setJavaField(column.getJavaField().replaceAll("Id", ".id|name"));
                column.setShowType("areaselect");
            }
            // 创建者、更新者
            else if (StringUtils.startsWithIgnoreCase(column.getName(), "create_by")
                    || StringUtils.startsWithIgnoreCase(column.getName(), "update_by")) {
                column.setJavaField(column.getJavaField() + ".id");
                column.setShowType("userselect");
            }
            // 创建时间、更新时间
            else if (StringUtils.startsWithIgnoreCase(column.getName(), "create_date")
                    || StringUtils.startsWithIgnoreCase(column.getName(), "update_date")) {
                column.setShowType("dateselect");
            }
            // 备注、内容
            else if (StringUtils.equalsIgnoreCase(column.getName(), "remarks")
                    || StringUtils.equalsIgnoreCase(column.getName(), "content")) {
                column.setShowType("textarea");
            }
            // 父级ID
            else if (StringUtils.equalsIgnoreCase(column.getName(), "parent_id")) {
                column.setJavaField("parent.id|name");
                column.setShowType("treeselect");
            }
            // 所有父级ID
            else if (StringUtils.equalsIgnoreCase(column.getName(), "parent_ids")) {
                column.setQueryType("like");
            }
            // 状态
            else if (StringUtils.equalsIgnoreCase(column.getName(), "del_flag")) {
                column.setShowType("radiobox");
                column.setDictType("del_flag");
            }
        }
    }

    /**
     * 获取模板路径
     */
    public static String getTemplatePath() {
        try {
            File file = new DefaultResourceLoader().getResource("").getFile();
            if (file != null) {
                return file.getAbsolutePath() + File.separator + StringUtils.replaceEach(GenUtils.class.getName(),
                        new String[]{"util." + GenUtils.class.getSimpleName(), "."}, new String[]{"templates", File.separator});
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }

        return "";
    }

    /**
     * XML文件转换为对象
     *
     * @param fileName 文件名
     * @param clazz    Class<?>
     */
    @SuppressWarnings("unchecked")
    public static <T> T fileToObject(String fileName, Class<?> clazz) {
        try {
            String pathName = "/templates/modules/gen/" + fileName;
            logger.debug("File to object: {}", pathName);
            Resource resource = new ClassPathResource(pathName);
            InputStream is = resource.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line).append("\r\n");
            }
            is.close();
            br.close();
            logger.debug("Read file content: {}", sb.toString());
            return (T) JaxbMapper.fromXml(sb.toString(), clazz);
        } catch (IOException e) {
            logger.warn("Error file convert: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取代码生成配置对象
     */
    public static GenConfig getConfig() {
        return fileToObject("config.xml", GenConfig.class);
    }

    /**
     * 根据分类获取模板列表
     *
     * @param config       GenConfig
     * @param category     分类
     * @param isChildTable 是否是子表
     */
    public static List<GenTemplate> getTemplateList(GenConfig config, String category, boolean isChildTable) {
        List<GenTemplate> templateList = Lists.newArrayList();
        if (config != null && config.getCategoryList() != null && category != null) {
            for (GenCategory e : config.getCategoryList()) {
                if (category.equals(e.getValue())) {
                    List<String> list = e.getTemplate();
                    if (list != null) {
                        for (String s : list) {
                            if (StringUtils.startsWith(s, GenCategory.CATEGORY_REF)) {
                                templateList.addAll(getTemplateList(config, StringUtils.replace(s, GenCategory.CATEGORY_REF, ""), false));
                            } else {
                                GenTemplate template = fileToObject(s, GenTemplate.class);
                                if (template != null) {
                                    templateList.add(template);
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return templateList;
    }

    /**
     * 获取数据模型
     *
     * @param genScheme GenScheme
     */
    public static Map<String, Object> getDataModel(GenScheme genScheme) {
        Map<String, Object> model = Maps.newHashMap();

        model.put("packageName", StringUtils.lowerCase(genScheme.getPackageName()));
        model.put("lastPackageName", StringUtils.substringAfterLast((String) model.get("packageName"), "."));
        model.put("moduleName", StringUtils.lowerCase(genScheme.getModuleName()));
        model.put("subModuleName", StringUtils.lowerCase(genScheme.getSubModuleName()));
        model.put("className", StringUtils.uncapitalize(genScheme.getGenTable().getClassName()));
        model.put("ClassName", StringUtils.capitalize(genScheme.getGenTable().getClassName()));

        model.put("functionName", genScheme.getFunctionName());
        model.put("functionNameSimple", genScheme.getFunctionNameSimple());
        model.put("functionAuthor", StringUtils.isNotBlank(genScheme.getFunctionAuthor()) ? genScheme.getFunctionAuthor() : UserUtils.getUser().getName());
        model.put("functionVersion", DateUtils.getDate());

        model.put("urlPrefix", model.get("moduleName") + (StringUtils.isNotBlank(genScheme.getSubModuleName())
                ? "/" + StringUtils.lowerCase(genScheme.getSubModuleName()) : "") + "/" + model.get("className"));
        model.put("viewPrefix", //StringUtils.substringAfterLast(model.get("packageName"),".")+"/"+
                model.get("urlPrefix"));
        model.put("permissionPrefix", model.get("moduleName") + (StringUtils.isNotBlank(genScheme.getSubModuleName())
                ? ":" + StringUtils.lowerCase(genScheme.getSubModuleName()) : "") + ":" + model.get("className"));

        model.put("dbType", "mysql");

        model.put("table", genScheme.getGenTable());

        return model;
    }

    /**
     * 生成到文件
     *
     * @param tpl           GenTemplate
     * @param model         Map<String, Object>
     * @param isReplaceFile isReplaceFile
     */
    public static String generateToFile(GenTemplate tpl, Map<String, Object> model, boolean isReplaceFile) {
        // 获取生成文件
        String fileName = Global.getProjectPath() + File.separator
                + StringUtils.replaceEach(FreeMarkers.renderString(tpl.getFilePath() + "/", model),
                new String[]{"//", "/", "."}, new String[]{File.separator, File.separator, File.separator})
                + FreeMarkers.renderString(tpl.getFileName(), model);
        logger.debug(" fileName === " + fileName);

        // 获取生成文件内容
        String content = FreeMarkers.renderString(StringUtils.trimToEmpty(tpl.getContent()), model);
        logger.debug(" content === \r\n" + content);

        // 如果选择替换文件，则删除原文件
        if (isReplaceFile) {
            FileUtils.deleteFile(fileName);
        }

        // 创建并写入文件
        if (FileUtils.createFile(fileName)) {
            FileUtils.writeToFile(fileName, content, true);
            logger.debug(" file create === " + fileName);
            return "生成成功：" + fileName + "<br/>";
        } else {
            logger.debug(" file extents === " + fileName);
            return "文件已存在：" + fileName + "<br/>";
        }
    }

}
