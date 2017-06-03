<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>字典管理</title>
    <meta name="decorator" content="default"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content">
    <div class="ibox">
        <div class="ibox-title">
            <h5>字典列表</h5>
            <div class="ibox-tools"></div>
        </div>
        <div class="ibox-content">
            <sys:message content="${message}"/>
            <!-- 查询条件 -->
            <div class="row">
                <div class="col-sm-12">
                    <form:form id="searchForm" modelAttribute="dict" action="${ctx}/sys/dict/" method="post" class="form-inline">
                        <input id="pageNum" name="pageNum" type="hidden" value="${page.pageNum}"/>
                        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
                        <table:sortColumn id="orderBy" name="orderBy" value="${dict.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
                        <div class="form-group">
                            <span>名称：</span>
                            <form:input path="description" htmlEscape="false" maxlength="50" class="form-control"/>
                            <span>英文名称：</span>
                            <form:select id="type" path="type" class="form-control m-b">
                                <form:option value="" label=""/>
                                <form:options items="${typeList}" htmlEscape="false"/>
                            </form:select>
                            <button  class="btn btn-primary btn-outline btn-sm " onclick="searchAll()" ><i class="fa fa-search"></i> 查询</button>
                            <button  class="btn btn-primary btn-outline btn-sm " onclick="resetAll()" ><i class="fa fa-refresh"></i> 重置</button>
                        </div>
                    </form:form>
                    <br/>
                </div>
            </div>

            <!-- 工具栏 -->
            <div class="row">
                <div class="col-sm-12">
                    <div class="pull-left">
                        <shiro:hasPermission name="sys:user:edit">
                            <table:addRow url="${ctx}/sys/dict/form" title="字典" width="800px" height="620px"/><!-- 增加按钮 -->
                            <table:delRow url="${ctx}/sys/dict/delete" id="contentTable"/><!-- 删除按钮 -->
                        </shiro:hasPermission>
                        <button class="btn btn-white btn-sm " data-toggle="tooltip" data-placement="left" onclick="sortOrRefresh()" title="刷新"><i class="glyphicon glyphicon-repeat"></i> 刷新</button>
                    </div>
                </div>
            </div>

            <!-- 数据表 -->
            <table:check id="contentTable"/>
            <table id="contentTable" class="table table-bordered table-hover">
                <thead>
                <tr>
                    <th><input type="checkbox" class="i-checks"></th>
                    <th class="sort-column description">字典名称</th>
                    <th class="sort-column type">字典英文名</th>
                    <th>类型名</th>
                    <th class="sort-column value">类型值</th>
                    <th class="sort-column sort">排序</th>
                    <shiro:hasPermission name="sys:dict:edit">
                        <th>操作</th>
                    </shiro:hasPermission>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${page.list}" var="bean">
                    <tr>
                        <td><input type="checkbox" id="${bean.id}" class="i-checks"></td>
                        <td>${bean.description}</td>
                        <td>${bean.type}</td>
                        <td><a href="#" onclick="openDialogView('查看字典', '${ctx}/sys/dict/form?id=${bean.id}','800px', '500px')">${bean.label}</a></td>
                        <td>${bean.value}</td>
                        <td>${bean.sort}</td>
                        <shiro:hasPermission name="sys:dict:edit">
                        <td>
                            <a href="#"
                               onclick="openDialog('修改字典', '${ctx}/sys/dict/form?id=${bean.id}','800px', '500px')"
                               class="btn btn-link btn-xs"><i class="fa fa-edit"></i> 修改
                            </a>
                            <a href="#"
                               onclick="return confirmx('确认要删除该字典吗？', '${ctx}/sys/dict/delete?ids=${bean.id}&type=${bean.type}')"
                               class="btn btn-link btn-xs"><i class="fa fa-trash"></i> 删除
                            </a>
                            <a href="#" onclick="openDialog('添加键值', '${ctx}/sys/dict/form?id=${bean.id}&actionType=2','800px', '500px')"
                               class="btn btn-link btn-xs"><i class="fa fa-plus"></i> 添加键值
                            </a>
                        </td>
                        </shiro:hasPermission>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            <table:page page="${page}"/>
        </div>
    </div>
</div>

</body>
</html>
