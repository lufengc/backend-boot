<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>栏目管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 3});
		});
    	function updateSort() {
			loading('正在提交，请稍等...');
	    	$("#listForm").attr("action", "${ctx}/cms/category/updateSort");
	    	$("#listForm").submit();
    	}
		function refresh(){//刷新
			window.location="${ctx}/cms/category/list";
		}
	</script>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content">
    <div class="ibox">
        <div class="ibox-title">
            <h5>栏目列表 </h5>
            <div class="ibox-tools"></div>
        </div>
        <div class="ibox-content">
            <sys:message content="${message}"/>
            <!-- 工具栏 -->
            <div class="row">
                <div class="col-sm-12">
                    <div class="pull-left">
                        <shiro:hasPermission name="cms:category:edit">
                            <table:addRow url="${ctx}/cms/category/form" title="菜单"/><!-- 增加按钮 -->
                            <button id="btnSubmit" class="btn btn-white btn-sm" data-toggle="tooltip" data-placement="left"
                                    onclick="updateSort()"><i class="fa fa-save"></i> 保存排序</button>
                        </shiro:hasPermission>
                        <button class="btn btn-white btn-sm" data-toggle="tooltip" data-placement="left"
                                onclick="refresh()"><i class="glyphicon glyphicon-repeat"></i> 刷新</button>
                    </div>
                </div>
            </div>

            <!-- 数据表 -->
            <form id="listForm" method="post">
                <table id="treeTable" class="table">
                    <thead>
                    <tr>
                        <th>栏目名称</th>
                        <th>归属机构</th>
                        <th>栏目模型</th>
                        <th>排序</th>
                        <th title="是否在导航中显示该栏目">导航菜单</th>
                        <th title="是否在分类页中显示该栏目的文章列表">栏目列表</th>
                        <th>展现方式</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${list}" var="tpl">
                        <tr id="${tpl.id}" pId="${tpl.parentId ne '1'?tpl.parentId:'0'}">
                            <td><a href="#" onclick="openDialogView('查看菜单', '${ctx}/cms/category/form?id=${tpl.id}')">${tpl.name}</a></td>
                            <td>${tpl.officeName}</td>
                            <td>${fns:getDictLabel(tpl.module, 'cms_module', '公共模型')}</td>
                            <td style="text-align:center;">
                                <shiro:hasPermission name="cms:category:edit">
                                    <input type="hidden" name="ids" value="${tpl.id}"/>
                                    <input type="text" name="sorts" value="${tpl.sort}" class="form-control" style="width:50px;margin:0;padding:0;"/>
                                </shiro:hasPermission>
                                <shiro:lacksPermission name="cms:category:edit">
                                    ${tpl.sort}
                                </shiro:lacksPermission>
                            </td>
                            <td>${fns:getDictLabel(tpl.inMenu, 'show_hide', '隐藏')}</td>
                            <td>${fns:getDictLabel(tpl.inList, 'show_hide', '隐藏')}</td>
                            <td>${fns:getDictLabel(tpl.showModes, 'cms_show_modes', '默认展现方式')}</td>
                            <td>
                                <shiro:hasPermission name="cms:category:edit">
                                    <a href="#" onclick="openDialog('修改', '${ctx}/cms/category/form?id=${tpl.id}')">修改</a>
                                    <a href="#" onclick="return confirmx('要删除该栏目及所有子栏目项吗？', '${ctx}/cms/category/delete?id=${tpl.id}')">删除</a>
                                    <a href="#" onclick="openDialog('添加下级栏目', '${ctx}/cms/category/form?parentId=${tpl.id}')">添加下级栏目</a>
                                </shiro:hasPermission>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </form>
        </div>
    </div>
</div>
</body>
</html>