/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.gen.bean;

import com.cheng.backend.framework.common.DataEntity;
import com.cheng.backend.framework.util.StringUtils;
import com.google.common.collect.Lists;

import javax.persistence.Transient;
import java.util.List;

/**
 * 业务表Entity
 *
 * @author fengcheng
 * @version 2016/7/28
 */
public class GenTable extends DataEntity<GenTable> {

    private static final long serialVersionUID = 1L;
    private String name;    // 名称
    private String comments;        // 描述
    private String className;        // 实体类名称
    private String parentTable;        // 关联父表
    private String parentTableFk;        // 关联父表外键

    @Transient
    private List<GenTableColumn> columnList = Lists.newArrayList();    // 表列
    @Transient
    private List<String> pkList; // 当前表主键列表

    public GenTable() {
        super();
    }

    public GenTable(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getParentTable() {
        return parentTable;
    }

    public void setParentTable(String parentTable) {
        this.parentTable = parentTable;
    }

    public String getParentTableFk() {
        return parentTableFk;
    }

    public void setParentTableFk(String parentTableFk) {
        this.parentTableFk = parentTableFk;
    }

    public List<GenTableColumn> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<GenTableColumn> columnList) {
        this.columnList = columnList;
    }

    public List<String> getPkList() {
        return pkList;
    }

    public void setPkList(List<String> pkList) {
        this.pkList = pkList;
    }

    /**
     * 获取列名和说明
     *
     * @return
     */
    public String getNameAndComments() {
        return getName() + (comments == null ? "" : "  :  " + comments);
    }

    /**
     * 获取导入依赖包字符串
     *
     * @return
     */
    public List<String> getImportList() {
        List<String> importList = Lists.newArrayList(); // 引用列表
        for (GenTableColumn column : getColumnList()) {
            if (column.getIsNotBaseField() || ("1".equals(column.getIsQuery()) && "between".equals(column.getQueryType())
                    && ("createTime".equals(column.getSimpleJavaField()) || "updateTime".equals(column.getSimpleJavaField())))) {
                // 导入类型依赖包， 如果类型中包含“.”，则需要导入引用。
                if (StringUtils.indexOf(column.getJavaType(), ".") != -1 && !importList.contains(column.getJavaType())) {
                    importList.add(column.getJavaType());
                }
            }
            /*if (column.getIsNotBaseField()) {
                // 导入JSR303、Json等依赖包
                for (String ann : column.getAnnotationList()) {
                    if (!importList.contains(StringUtils.substringBeforeLast(ann, "("))) {
                        importList.add(StringUtils.substringBeforeLast(ann, "("));
                    }
                }
            }*/
        }
        return importList;
    }

}


