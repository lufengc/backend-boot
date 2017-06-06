<%@ page language="java" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>文章管理</title>
    <meta name="decorator" content="default"/>
    <%@include file="/WEB-INF/views/include/datetime.jsp" %>
    <%@include file="/WEB-INF/views/include/editor.jsp" %>
    <%@include file="/WEB-INF/views/include/imageupload.jsp" %>
    <script type="text/javascript">
        $(document).ready(function () {
            if ($("#link").val()) {
                $('#linkBody').show();
                $('#url').attr("checked", true);
            }
            $("#title").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });

            $("#weightDate").datetime();
            $("#createDate").datetime({format: 'yyyy-mm-dd HH:mm:ss'});

            $("#content").wangEditor();

        });

    </script>
</head>
<body>
<form:form id="inputForm" modelAttribute="article" action="${ctx}/cms/article/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">归属栏目:</label>
        <div class="controls">
            <div class="pull-left m-r-sm">
                <sys:treeselect id="categoryId" name="categoryId" value="${article.categoryId}"
                                labelName="categoryName"
                                labelValue="${article.categoryName}" title="栏目" url="/cms/category/treeData"
                                module="article" selectScopeModule="true" notAllowSelectRoot="false"
                                notAllowSelectParent="true" cssClass="form-control input-xxlarge required"/>&nbsp;
            </div>
            <div class="m-xs">
                <input id="url" type="checkbox"
                       onclick="if(this.checked){$('#linkBody').show()}else{$('#linkBody').hide()}$('#link').val()">
                <label for="url">外部链接</label>
            </div>
        </div>
    </div>
    <div id="linkBody" class="control-group" style="display:none">
        <label class="control-label">外部链接:</label>
        <div class="controls">
            <form:input path="link" htmlEscape="false" maxlength="200" cssClass="form-control input-xxlarge"/>
            <span class="help-inline">绝对或相对地址。</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">标题:</label>
        <div class="controls">
            <form:input path="title" htmlEscape="false" maxlength="200"
                        cssClass="form-control measure-input input-xxlarge required"/>
            <label>颜色:</label>
            <form:select path="color" cssClass="form-control input-mini">
                <form:option value="" label="默认"/>
                <form:options items="${fns:getDictList('color')}" itemLabel="label" itemValue="value"
                              htmlEscape="false"/>
            </form:select>
            <label id="title-error" class="error" for="title" style="display:none"></label>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">关键字:</label>
        <div class="controls">
            <form:input path="keywords" htmlEscape="false" maxlength="200" cssClass="form-control input-xlarge"/>
            <span class="help-inline">多个关键字，用空格分隔。</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">权重:</label>
        <div class="controls">
            <form:input path="weight" htmlEscape="false" maxlength="200"
                        cssClass="form-control input-mini required digits"/>&nbsp;
				<span>
					<input id="weightTop" type="checkbox" onclick="$('#weight').val(this.checked?'999':'0')"><label
                        for="weightTop">置顶</label>
				</span>
            &nbsp;过期时间：
            <input id="weightDate" name="weightDate" type="text" readonly="readonly" maxlength="20"
                   class="form-control input-medium"
                   value="<fmt:formatDate value="${article.weightDate}"/>"/>
            <span class="help-inline">数值越大排序越靠前，过期时间可为空，过期后取消置顶。</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">缩略图:</label>
        <div class="controls">
            <input type="hidden" id="image" name="image" value="${article.image}"/>
            <div>
                <img src="<c:if test="${empty article.image}">${ctxStatic}/static/app/image/default.jpg</c:if>
                    <c:if test="${not empty article.image}">${fns:getFileAccessPath()}/${article.image}</c:if>"
                     id="thumbImg" style="max-width: 100px; max-height: 100px; line-height: 20px;"/>
            </div>
            <input type="file" class="default" name="upload" onchange="imgUpload(this.value);" id="fileId"
                   style="border: none; padding: 0;"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">来源:</label>
        <div class="controls">
            <form:input path="articleData.copyfrom" htmlEscape="false" maxlength="200"
                        cssClass="form-control input-xxlarge"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">相关文章:</label>
        <div class="controls">
            <form:hidden id="articleDataRelation" path="articleData.relation" htmlEscape="false" maxlength="200"
                         cssClass="form-control input-xlarge"/>
            <ol id="articleSelectList" style="margin-left: -12px;"></ol>
            <a id="relationButton" href="javascript:" class="btn">添加相关</a>
            <script type="text/javascript">
                var articleSelect = [];
                function articleSelectAddOrDel(id, title) {
                    var isExtents = false, index = 0;
                    for (var i = 0; i < articleSelect.length; i++) {
                        if (articleSelect[i][0] == id) {
                            isExtents = true;
                            index = i;
                        }
                    }
                    if (isExtents) {
                        articleSelect.splice(index, 1);
                    } else {
                        articleSelect.push([id, title]);
                    }
                    articleSelectRefresh();
                }
                function articleSelectRefresh() {
                    $("#articleDataRelation").val("");
                    $("#articleSelectList").children().remove();
                    for (var i = 0; i < articleSelect.length; i++) {
                        $("#articleSelectList").append("<li>" + articleSelect[i][1] +
                                "&nbsp;&nbsp;<a href=\"javascript:\" onclick=\"articleSelectAddOrDel('"
                                + articleSelect[i][0] + "','" + articleSelect[i][1] + "');\">×</a></li>");
                        $("#articleDataRelation").val($("#articleDataRelation").val() + articleSelect[i][0] + ",");
                    }
                }
                function getData() {
                    $.getJSON("${ctx}/cms/article/findByIds", {ids: $("#articleDataRelation").val()}, function (data) {
                        for (var i = 0; i < data.length; i++) {
                            articleSelect.push([data[i][1], data[i][2]]);
                        }
                        articleSelectRefresh();
                    });
                }

                $("#relationButton").click(function () {
                    top.layer.open({
                        type: 2,
                        area: ['800px', '680px'],
                        title: "添加相关",
                        maxmin: true, //开启最大化最小化按钮
                        content: "${ctx}/cms/article/selectList?pageSize=8",
                        btn: ['确定', '关闭'],
                        yes: function (index, layero) {
                            var ids = layero.find("iframe")[0].contentWindow.getIds();
                            if (ids == "") {
                                top.$.jBox.tip("未添加相关文章！", 'info');
                                top.layer.close(index);
                            } else {
                                $('#articleDataRelation').val(ids);
                                getData();
                                top.layer.close(index);
                            }
                        },
                        cancel: function (index) {
                        }
                    });
                });
            </script>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">是否允许评论:</label>
        <div class="controls">
            <form:radiobuttons path="articleData.allowComment" items="${fns:getDictList('yes_no')}"
                               itemLabel="label" itemValue="value" htmlEscape="false"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">推荐位:</label>
        <div class="controls">
            <form:checkboxes path="posidList" items="${fns:getDictList('cms_posid')}" itemLabel="label"
                             itemValue="value" htmlEscape="false"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">发布时间:</label>
        <div class="controls">
            <input id="createDate" name="createDate" type="text" readonly="readonly" maxlength="20"
                   class="form-control input-medium"
                   value="<fmt:formatDate value="${article.createDate}"/>"/>
        </div>
    </div>
    <shiro:hasPermission name="cms:article:edit">
        <div class="control-group">
            <label class="control-label">发布状态:</label>
            <div class="controls">
                <form:radiobuttons path="delFlag" items="${fns:getDictList('cms_del_flag')}" itemLabel="label"
                                   itemValue="value" htmlEscape="false" cssClass="required"/>
                <span class="help-inline"></span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">自定义内容视图:</label>
            <div class="controls">
                <form:select path="customContentView" cssClass="form-control input-medium">
                    <form:option value="" label="默认视图"/>
                    <form:options items="${contentViewList}" htmlEscape="false"/>
                </form:select>
                <span class="help-inline">自定义内容视图名称必须以"${article_DEFAULT_TEMPLATE}"开始</span>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label">自定义视图参数:</label>
            <div class="controls">
                <form:input path="viewConfig" htmlEscape="true" cssClass="form-control input-xlarge"/>
                <span class="help-inline">视图参数例如: {count:2, title_show:"yes"}</span>
            </div>
        </div>
    </shiro:hasPermission>
    <div class="control-group">
        <label class="control-label">摘要:</label>
        <div class="controls">
            <form:textarea path="description" htmlEscape="false" rows="4" maxlength="200" cssClass="form-control"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">正文:</label>
        <div class="controls">
            <form:textarea id="content" htmlEscape="true" path="articleData.content" rows="16" maxlength="200"
                           cssClass="form-control"/>
        </div>
    </div>
    <c:if test="${not empty article.id}">
        <div class="control-group hidden">
            <label class="control-label">查看评论:</label>
            <div class="controls">
                <input id="btnComment" class="btn" type="button" value="查看评论"
                       onclick="viewComment('${ctx}/cms/comment/?module=article&contentId=${article.id}&status=0')"/>
                <script type="text/javascript">
                    function viewComment(href) {
                        top.$.jBox.open('iframe:' + href, '查看评论', $(top.document).width() - 220, $(top.document).height() - 180, {
                            buttons: {"关闭": true},
                            loaded: function (h) {
                                $(".jbox-content", top.document).css("overflow-y", "hidden");
                                $(".nav,.form-actions,[class=btn]", h.find("iframe").contents()).hide();
                                $("body", h.find("iframe").contents()).css("margin", "10px");
                            }
                        });
                        return false;
                    }
                </script>
            </div>
        </div>
    </c:if>
    <div class="form-actions">
        <shiro:hasPermission name="cms:article:edit"><input id="btnSubmit" class="btn btn-primary" type="submit"
                                                            value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>