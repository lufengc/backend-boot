<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="renderer" content="webkit">
    <meta name="keywords" content="首页">
    <meta name="description" content="首页">
    <title>后台管理系统</title>
    <%@ include file="/WEB-INF/views/include/head-ins.jsp"%>
    <script src="${ctxStatic}/static/app/js/index-ins.js"></script>
</head>

<body class="fixed-sidebar full-height-layout gray-bg" style="min-width: 1024px">
    <div id="wrapper">
        <!--左侧导航开始-->
        <nav class="navbar-default navbar-static-side" role="navigation">
            <div class="nav-close"><i class="fa fa-times-circle"></i></div>
            <div class="sidebar-collapse">
                <ul class="nav" id="side-menu">
                    <li class="nav-header">
                        <div class="dropdown profile-element">
                            <span><img alt="image" class="img-circle" style="height:64px;width:64px;"
                                       src="<c:if test="${empty fns:getUser().photo}">${ctxStatic}/static/app/image/default.jpg</c:if>
                                       <c:if test="${not empty fns:getUser().photo}">${fns:getFileAccessPath()}/${fns:getUser().photo}</c:if>" /></span>
                            <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                                <span class="clear">
                                    <span class="block m-t-xs"><strong class="font-bold">${fns:getUser().loginName}</strong></span>
                                    <span class="text-muted text-xs block">${fns:getUser().roleNames}<b class="caret"></b></span>
                                </span>
                            </a>
                            <ul class="dropdown-menu animated flipInX m-t-xs">
                                <li><a class="J_menuItem" href="${ctx}/sys/user/imageEdit">修改头像</a></li>
                                <li><a class="J_menuItem" href="${ctx}/sys/user/info">个人资料</a></li>
                                <li class="divider"></li>
                                <li><a href="${ctx}/logout">安全退出</a></li>
                            </ul>
                        </div>
                        <div class="logo-element">【·】
                        </div>
                    </li>
                    <c:forEach items="${fns:getMenuList()}" var="bean">
                        <c:if test="${bean.parentId eq '1'}">
                            <li>
                                <a href="#">
                                    <i class="fa ${bean.icon}"></i>
                                    <span class="nav-label"> ${bean.name} </span>
                                    <span class="fa arrow"></span>
                                </a>
                                <ul class="nav nav-second-level">
                                    <c:forEach items="${fns:getMenuList()}" var="bean1">
                                        <c:if test="${bean1.parentId == bean.id}">
                                            <li>
                                                <a class="J_menuItem" href="${ctx}${bean1.href}">
                                                    <i class="fa "></i>
                                                    <span class="nav-label">${bean1.name}</span>
                                                </a>
                                            </li>
                                        </c:if>
                                    </c:forEach>
                                </ul>
                            </li>
                        </c:if>
                    </c:forEach>
                </ul>
            </div>
        </nav>
        <!--左侧导航结束-->
        <!--右侧部分开始-->
        <div id="page-wrapper" class="gray-bg dashbard-1">
            <%--<div class="row border-bottom">
                <nav class="navbar navbar-static-top" role="navigation" style="margin-bottom: 0">
                    <div class="navbar-header">
                        <a class="navbar-minimalize minimalize-styl-2 btn btn-primary " href="#"><i class="fa fa-bars"></i> </a>
                        <form role="search" class="navbar-form-custom" method="post" action="search_results.html">
                            <div class="form-group">
                                <input type="text" placeholder="请输入您需要查找的内容 …" class="form-control" name="top-search" id="top-search">
                            </div>
                        </form>
                    </div>
                    <ul class="nav navbar-top-links navbar-right">
                        &lt;%&ndash;<li>
                            <iframe width="280" scrolling="no" height="25" frameborder="0"
                                    allowtransparency="true" src="http://i.tianqi.com/index.php?c=code&id=34&icon=1&num=3"></iframe>
                        </li>&ndash;%&gt;
                    </ul>
                </nav>
            </div>--%>
            <div class="row content-tabs">
                <button class="roll-nav roll-left J_tabLeft"><i class="fa fa-backward"></i></button>
                <nav class="page-tabs J_menuTabs">
                    <div class="page-tabs-content">
                        <a href="javascript:;" class="active J_menuTab" data-id="${ctx}/home">首页</a>
                    </div>
                </nav>
                <button class="roll-nav roll-right J_tabRight"><i class="fa fa-forward"></i></button>
                <div class="btn-group roll-nav roll-right">
                    <button class="dropdown J_tabClose" data-toggle="dropdown">关闭操作<span class="caret"></span></button>
                    <ul role="menu" class="dropdown-menu dropdown-menu-right">
                        <li class="J_tabShowActive"><a>定位当前选项卡</a></li>
                        <li class="divider"></li>
                        <li class="J_tabCloseAll"><a>关闭全部选项卡</a></li>
                        <li class="J_tabCloseOther"><a>关闭其他选项卡</a></li>
                    </ul>
                </div>
                <a href="${ctx}/logout" class="roll-nav roll-right J_tabExit"><i class="fa fa fa-sign-out"></i> 退出</a>
                <span class="roll-nav roll-right">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>
            </div>
            <div class="row J_mainContent" id="content-main">
                <iframe class="J_iframe" name="iframe0" width="100%" height="98%" src="${ctx}/home" frameborder="0" data-id="${ctx}/home" seamless></iframe>
            </div>
            <div class="footer">
                <div class="pull-left"><a href="http://www.zlgx.vip">http://www.lufengc.com</a> &copy; 2016-2026</div>
            </div>
        </div>
        <!--右侧部分结束-->
    </div>
</body>

</html>