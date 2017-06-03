<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>机构管理</title>
    <meta name="decorator" content="default"/>
    <%@ include file="/WEB-INF/views/include/treetable.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {
            var tpl = $("#treeTableTpl").html().replace(/(\/\/<!\-\-)|(\/\/\-\->)/g, "");
            var data = ${fns:toJson(list)};
            var rootId = "${not empty office.parentId ? office.parentId : '0'}";
            addRow("#treeTableList", tpl, data, rootId, true);
            $("#treeTable").treeTable({expandLevel: 5});
        });
        function addRow(list, tpl, data, pid, root) {
            for (var i = 0; i < data.length; i++) {
                var row = data[i];
                if ((${fns:jsGetVal('row.parentId')}) == pid) {
                    $(list).append(Mustache.render(tpl, {
                        dict: {
                            type: getDictLabel(${fns:toJson(fns:getDictList('sys_office_type'))}, row.type)
                        }, pid: (root ? 0 : pid), row: row
                    }));
                    addRow(list, tpl, data, row.id);
                }
            }
        }
        function refresh() {//刷新或者排序，页码不清零
            window.location = "${ctx}/sys/office/list";
        }
    </script>
</head>
<body>
<sys:message content="${message}"/>
<!-- 工具栏 -->
<div class="row">
    <div class="col-sm-12">
        <div class="pull-left">
            <shiro:hasPermission name="sys:office:edit">
                <table:addRow url="${ctx}/sys/office/form?parentId=${office.id}" title="机构" target="officeContent"/><!-- 增加按钮 -->
            </shiro:hasPermission>
            <button class="btn btn-white btn-sm" data-toggle="tooltip" data-placement="left" onclick="refresh()" title="刷新">
                <i class="glyphicon glyphicon-repeat"></i> 刷新
            </button>
        </div>
    </div>
</div>
<table id="treeTable" class="table table-bordered table-hover">
    <thead>
    <tr>
        <th>机构名称</th>
        <th>归属区域</th>
        <th>机构编码</th>
        <th>机构类型</th>
        <th>备注</th>
        <shiro:hasPermission name="sys:office:edit">
            <th>操作</th>
        </shiro:hasPermission>
    </tr>
    </thead>
    <tbody id="treeTableList"></tbody>
</table>
<script type="text/template" id="treeTableTpl">
    <tr id="{{row.id}}" pId="{{pid}}">
        <td><a href="#" onclick="openDialogView('查看机构', '${ctx}/sys/office/form?id={{row.id}}')">{{row.name}}</a></td>
        <td>{{row.areaId}}</td>
        <td>{{row.code}}</td>
        <td>{{dict.type}}</td>
        <td>{{row.description}}</td>
        <shiro:hasPermission name="sys:office:edit">
        <td>
            <a href="#"
               onclick="openDialog('修改机构', '${ctx}/sys/office/form?id={{row.id}}', '800px', '600px', 'officeContent')"
               class="btn btn-link btn-xs"><i class="fa fa-edit"></i> 修改</a>
            <a href="${ctx}/sys/office/delete?id={{row.id}}" onclick="return confirmx('要删除该机构及所有子机构项吗？', this.href)"
               class="btn btn-link btn-xs"><i class="fa fa-trash"></i> 删除</a>
            <a href="#"
               onclick="openDialog('添加下级机构', '${ctx}/sys/office/form?parentId={{row.id}}', '800px', '600px', 'officeContent')"
               class="btn  btn-link btn-xs"><i class="fa fa-plus"></i> 添加下级机构</a>
        </td>
        </shiro:hasPermission>
    </tr>
</script>
</body>
</html>