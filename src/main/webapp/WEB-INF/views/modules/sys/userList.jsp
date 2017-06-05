<%@ page language="java" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
    <title>用户管理</title>
    <meta name="decorator" content="default"/>
    <script>

        function isCheck(tableId, status) {
            var str="";
            var ids="";
            var error = 0;
            $("#" + tableId).find("tbody tr td input.i-checks:checkbox").each(function(){
                if(true === $(this).is(':checked')){
                    var statusId = $(this).attr("statusid");
                    if(status === 1 && statusId ==="0"){
                        error = -1;
                        return;
                    }else if(status === 3 && statusId === "3"){
                        error = -2;
                        return;
                    }
                    str+=$(this).attr("id")+",";
                }
            });
            if(error === -1){
                top.layer.alert('请选择已锁定的账户进行解锁!', {icon: 0, title:'警告'});
                return "0";
            }else if(error === -2) {
                top.layer.alert('请选择未锁定的账户进行锁定!', {icon: 0, title:'警告'});
                return "0";
            }
            if(str.substr(str.length-1)=== ','){
                ids = str.substr(0,str.length-1);
            }
            if(ids === ""){
                top.layer.alert('请至少选择一条数据!', {icon: 0, title:'警告'});
                return "0";
            }else{
                return ids;
            }
        }

        function lock(){
            var ids = isCheck("contentTable", 3);
            if(ids !== "0"){
                top.layer.confirm('确认要锁定吗?', {icon: 3, title:'系统提示'}, function(index){
                    window.location = "${ctx}/sys/user/updateStatus?delFlag=3&ids="+ids;
                    top.layer.close(index);
                });
            }
        }

        function unlock(){
            var ids = isCheck("contentTable", 1);
            if(ids !== "0"){
                top.layer.confirm('确认要解锁吗?', {icon: 3, title:'系统提示'}, function(index){
                    window.location = "${ctx}/sys/user/updateStatus?delFlag=0&ids="+ids;
                    top.layer.close(index);
                });
            }
        }

        function initPassword() {
            var ids = isCheck("contentTable");
            if(ids !== "0"){
                top.layer.confirm('确认要初始化密码吗?', {icon: 3, title:'系统提示'}, function(index){
                    window.location = "${ctx}/sys/user/initPassword?password=123456&ids="+ids;
                    top.layer.close(index);
                });
            }
        }
    </script>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content">
    <div class="ibox">
        <div class="ibox-title">
            <h5>用户列表</h5>
            <div class="ibox-tools"></div>
        </div>
        <div class="ibox-content">
            <sys:message content="${message}"/>

            <!-- 查询条件 -->
            <div class="row">
                <div class="col-sm-12">
                    <form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/user/list" method="post" class="form-inline">
                        <input id="pageNum" name="pageNum" type="hidden" value="${page.pageNum}"/>
                        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
                        <table:sortColumn id="orderBy" name="orderBy" value="${user.orderBy}" callback="sortOrRefresh();"/><!-- 支持排序 -->
                        <div class="form-group">
                            <span>登录名：<form:input path="loginName" htmlEscape="false" maxlength="50" class="form-control"/></span>

                            <span>姓名：<form:input path="name" htmlEscape="false" maxlength="50" class="form-control"/></span>

                            <span>手机号：<form:input path="mobile" htmlEscape="false" maxlength="50" class="form-control"/></span>

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
                            <table:addRow url="${ctx}/sys/user/form" title="用户" width="800px" height="650px" target="userContent"/><!-- 增加按钮 -->
                            <table:delRow url="${ctx}/sys/user/delete" id="contentTable"/><!-- 删除按钮 -->
                            <%--<table:importExcel url="${ctx}/sys/user/import"/>--%><!-- 导入按钮 -->
                            <table:exportExcel url="${ctx}/sys/user/export"/><!-- 导出按钮 -->
                            <button class="btn btn-white btn-sm" onclick="lock()" data-toggle="tooltip" data-placement="top">
                                <i class="fa fa-trash-o"> 锁定</i>
                            </button>
                            <button class="btn btn-white btn-sm" onclick="unlock()" data-toggle="tooltip" data-placement="top">
                                <i class="fa fa-trash-o"> 解锁</i>
                            </button>
                            <button class="btn btn-white btn-sm" onclick="initPassword()" data-toggle="tooltip" data-placement="top">
                                <i class="fa fa-trash-o"> 密码初始化</i>
                            </button>
                        </shiro:hasPermission>
                        <button class="btn btn-white btn-sm" data-toggle="tooltip" data-placement="left" onclick="sortOrRefresh()" title="刷新">
                            <i class="glyphicon glyphicon-repeat"></i> 刷新
                        </button>
                    </div>
                </div>
            </div>

            <!-- 数据表 -->
            <table:check id="contentTable"/>
            <table id="contentTable" class="table table-bordered table-hover">
                <thead>
                    <tr>
                        <th><input type="checkbox" class="i-checks"></th>
                        <th class="sort-column login_name">用户名</th>
                        <th class="sort-column name">姓名</th>
                        <th class="sort-column mobile">手机号</th>
                        <th class="sort-column email">邮箱</th>
                        <th class="sort-column login_flag">用户状态</th>
                        <th class="sort-column user_type">用户类型</th>
                        <shiro:hasPermission name="sys:user:edit"><th>操作</th></shiro:hasPermission>
                    </tr>
                </thead>
                <tbody>
                <c:forEach items="${page.list}" var="bean">
                    <tr>
                        <td><input type="checkbox" id="${bean.id}" class="i-checks" statusid="${bean.loginFlag}"></td>
                        <td><a href="#" onclick="openDialogView('查看', '${ctx}/sys/user/form?id=${bean.id}','800px', '650px')">
                            ${bean.loginName}</a>
                        </td>
                        <td>${bean.name}</td>
                        <td>${bean.mobile}</td>
                        <td>${bean.email}</td>
                        <td>${fns:getDictLabel(bean.delFlag, "user_delFlag", "")}</td>
                        <td>${fns:getDictLabel(bean.userType, "sys_user_type", "")}</td>
                        <shiro:hasPermission name="sys:user:edit">
                        <td>
                            <a href="#"
                               onclick="openDialog('修改', '${ctx}/sys/user/form?id=${bean.id}','800px', '650px', 'userContent')"
                               class="btn btn-link btn-xs"><i class="fa fa-edit"></i> 修改
                            </a>
                            <a href=""
                               onclick="return confirmx('确认要删除该用户吗？', '${ctx}/sys/user/delete?ids=${bean.id}')"
                               class="btn btn-link btn-xs"><i class="fa fa-trash"></i> 删除
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

