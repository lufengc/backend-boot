/*
 * Copyright (c) 2018. <a href="http://www.lufengc.com">lufengc</a> All rights reserved.
 */

package com.cheng.backend.modules.gen.service.impl;

import com.cheng.backend.framework.common.BaseServiceImpl;
import com.cheng.backend.framework.util.Encodes;
import com.cheng.backend.framework.util.StringUtils;
import com.cheng.backend.modules.gen.bean.*;
import com.cheng.backend.modules.gen.mapper.GenTableColumnMapper;
import com.cheng.backend.modules.gen.mapper.GenTableMapper;
import com.cheng.backend.modules.gen.service.GenTableService;
import com.cheng.backend.modules.gen.utils.GenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 业务表Service实现类
 *
 * @author fengcheng
 * @version 2016/7/28
 */
@Service
public class GenTableServiceImpl extends BaseServiceImpl<GenTable> implements GenTableService {

	@Autowired
	private GenTableMapper genTableMapper;
	@Autowired
	private GenTableColumnMapper genTableColumnMapper;

	@Override
	public GenTable get(Object id) throws Exception {
		GenTable genTable = super.get(id);
		Example example = new Example(GenTableColumn.class);
		example.orderBy("sort").asc();
		example.createCriteria().andEqualTo("genTableId", genTable.getId());
		List<GenTableColumn> columnList = genTableColumnMapper.selectByExample(example);
		genTable.setColumnList(columnList);
		return genTable;
	}

	@Override
	@Transactional
	public String save(GenTable object) throws Exception {
		if (StringUtils.isBlank(object.getId())) {
			object.setId(Encodes.uuid());
			super.insert(object);
		} else {
			super.update(object);
		}
		// 保存列
		for (GenTableColumn column : object.getColumnList()) {
			column.setGenTable(object);
			if (StringUtils.isBlank(column.getId())) {
				column.setId(Encodes.uuid());
				column.setGenTableId(object.getId());
				genTableColumnMapper.insertSelective(column);
			} else {
				genTableColumnMapper.updateByPrimaryKeySelective(column);
			}
		}
		return object.getId();
	}

	@Override
	@Transactional
	public int delete(String ids) throws Exception {
		super.delete(ids);
		Example example = new Example(GenTableColumn.class);
		example.createCriteria().andIn("genTableId", Arrays.asList(ids.split(",")));
		genTableColumnMapper.deleteByExample(example);
		return 1;
	}

	/**
	 * 获取物理数据表列表
	 *
	 * @param genTable GenTable
	 * @return List<GenTable>
	 */
	public List<GenTable> findTableListFormDb(GenTable genTable) {
		return genTableMapper.findTableList(genTable.getName() != null ? genTable.getName().toUpperCase() : null);
	}

	/**
	 * 验证表名是否可用
	 *
	 * @param tableName 表名
	 * @return boolean 如果已存在，则返回false
	 */
	public boolean checkTableName(String tableName) {
		if (StringUtils.isBlank(tableName)) {
			return true;
		}
		GenTable genTable = new GenTable();
		genTable.setName(tableName);
		List<GenTable> list = genTableMapper.select(genTable);
		return !(list != null && list.size() > 0);
	}

	/**
	 * 获取物理数据表列表
	 *
	 * @param genTable GenTable
	 * @return GenTable
	 */
	public GenTable getTableFormDb(GenTable genTable) {
		// 如果有表名，则获取物理表
		if (StringUtils.isNotBlank(genTable.getName())) {
			List<GenTable> list = findTableListFormDb(genTable);
			if (list.size() > 0) { // 表已存在
				// 如果是新增，初始化表属性
				if (StringUtils.isBlank(genTable.getId())) {
					genTable = list.get(0);
					// 设置字段说明
					if (StringUtils.isBlank(genTable.getComments())) {
						genTable.setComments(genTable.getName());
					}
					genTable.setClassName(StringUtils.toCapitalizeCamelCase(genTable.getName()));
				}
				// 添加新列
				List<GenTableColumn> columnList = genTableMapper.findTableColumnList(genTable.getName().toUpperCase());
				// 同步已存在的表的最新列数据到genTableColumn中
				// 添加表中已存在 数据中不存在的列
				for (GenTableColumn column : columnList) {
					boolean b = false;
					for (GenTableColumn e : genTable.getColumnList()) {
						if (e.getName().equals(column.getName())) {
							b = true;
						}
					}
					if (!b) {
						genTable.getColumnList().add(column);
					}
				}
				// 删除表中不存在 数据中存在的列
				for (GenTableColumn e : genTable.getColumnList()) {
					boolean b = false;
					for (GenTableColumn column : columnList) {
						if (column.getName().equals(e.getName())) {
							b = true;
						}
					}
					if (!b) {
						e.setDelFlag(GenTableColumn.DEL_FLAG_DELETE);
					}
				}
				// 获取主键
				genTable.setPkList(genTableMapper.findTablePK(genTable.getName().toUpperCase()));
				// 初始化列属性字段
				GenUtils.initColumnField(genTable);
			}
		}
		return genTable;
	}

	/**
	 * 生成代码
	 *
	 * @param genScheme GenScheme
	 * @return String
	 */
	public String genCode(GenScheme genScheme) throws Exception {
		StringBuilder result = new StringBuilder();
		// 获取所有代码模板
		GenConfig config = GenUtils.getConfig();
		// 获取模板列表
		List<GenTemplate> templateList = GenUtils.getTemplateList(config, genScheme.getCategory(), false);
		// 生成主表模板代码
		Map<String, Object> model = GenUtils.getDataModel(genScheme);
		for (GenTemplate tpl : templateList) {
			result.append(GenUtils.generateToFile(tpl, model, genScheme.getReplaceFile()));
		}
		return result.toString();
	}

}
