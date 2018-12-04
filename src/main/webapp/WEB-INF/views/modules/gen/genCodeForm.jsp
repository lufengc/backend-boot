<%@ page language="java" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>

<html>
<head>
    <title>生成代码管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        var validateForm;
        function doSubmit() {//回调函数，在编辑和保存动作时，供openDialog调用提交表单。
            if (validateForm.form()) {
                $("#inputForm").submit();
                return true;
            }
            return false;
        }

        $(document).ready(function () {
            //设置了远程验证，在初始化时必须预先调用一次。
            validateForm = $("#inputForm").validate({
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

        });
    </script>
</head>
<body>
    <form:form id="inputForm" modelAttribute="genScheme" action="${ctx}/gen/genTable/genCode" method="post" class="form-horizontal">
        <form:hidden path="id"/>
        <sys:message content="${message}"/>
        <fieldset>
            <div class="control-group">
                <label class="control-label">业务表名:</label>
                <div class="controls">
                    <form:input path="name" htmlEscape="false" maxlength="500" class="form-control required" readonly="true"/>
                    <span class="help-inline">要生成基础代码的数据表</span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">模版分类:</label>
                <div class="controls">
                    <form:select path="category" class="form-control required">
                        <form:options items="${config.categoryList}" itemLabel="label" itemValue="value" htmlEscape="false"/>
                    </form:select>
                    <span class="help-inline">
                        生成结构：{包名}/{模块名}/{分层(dao,entity,service,web)}/{子模块名}/{java类}
                    </span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">生成包路径:</label>
                <div class="controls">
                    <input type="text" name="packageName" class="form-control required"
                           value="com.cheng.backend.modules">
                    <span class="help-inline">建议模块包：com.platform.modules</span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">生成模块名:</label>
                <div class="controls">
                    <input type="text" name="moduleName" class="form-control required" value="test">
                    <span class="help-inline">可理解为子系统名，例如 sys</span>
                </div>
            </div>
            <div class="control-group hide">
                <label class="control-label">生成子模块名:</label>
                <div class="controls">
                    <input type="text" name="subModuleName" class="form-control required" value="">
                    <span class="help-inline">可选，分层下的文件夹，例如 </span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">生成功能描述:</label>
                <div class="controls">
                    <input type="text" name="functionName" class="form-control required" value="测试描述">
                    <span class="help-inline">将设置到类描述</span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">生成功能名:</label>
                <div class="controls">
                    <input type="text" name="functionNameSimple" class="form-control required" value="测试功能名">
                    <span class="help-inline">用作功能提示，如：保存“某某”成功</span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">生成功能作者:</label>
                <div class="controls">
                    <input type="text" name="functionAuthor" class="form-control required" value="fengcheng">
                    <span class="help-inline">功能开发者</span>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label">生成选项:</label>
                <div class="controls m-t-xs">
                    <form:checkbox path="replaceFile" label="是否替换现有文件" class="i-checks"/>
                </div>
            </div>
        </fieldset>
    </form:form>
</body>
</html>