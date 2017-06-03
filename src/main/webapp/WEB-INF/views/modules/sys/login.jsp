<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>登录</title>
    <meta name="keywords" content="登录">
    <meta name="description" content="登录">
    <link rel="shortcut icon" href="${ctxStatic}/static/app/image/favicon.ico">
    <link rel="stylesheet" href="${ctxStatic}/static/plugins/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctxStatic}/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="${ctxStatic}/static/plugins/animate.min.css">
    <link rel="stylesheet" href="${ctxStatic}/static/plugins/icheck/custom.css">
    <link rel="stylesheet" href="${ctxStatic}/static/app/css/login.css">

    <script src="${ctxStatic}/static/plugins/jquery/jquery-2.1.4.min.js"></script>
    <script src="${ctxStatic}/static/plugins/jquery/jquery-migrate-1.2.1.min.js"></script>
    <script src="${ctxStatic}/static/plugins/jquery-validation/1.14.0/jquery.validate.min.js"></script>
    <script src="${ctxStatic}/static/plugins/jquery-validation/1.14.0/localization/messages_zh.min.js"></script>
    <script src="${ctxStatic}/static/plugins/bootstrap/js/bootstrap.min.js"></script>
    <script src="${ctxStatic}/static/plugins/icheck/icheck.min.js"></script>
    <script>
        /*if (window.top !== window.self) {
            window.top.location = window.location;
        }*/
        // 如果在框架或在对话框中，则弹出提示并跳转到首页
        if(self.frameElement && self.frameElement.tagName == "IFRAME" || $('#left').length > 0 || $('.jbox').length > 0){
            alert('未登录或登录超时。请重新登录，谢谢！');
            top.location = "${ctx}";
        }
        $(document).ready(function() {
            $("#loginForm").validate({
                rules: {
                    validateCode: {remote: "${pageContext.request.contextPath}/servlet/validateCodeServlet"}
                },
                messages: {
                    username: {required: "请填写用户名."},
                    password: {required: "请填写密码."},
                    validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
                },
                errorLabelContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    error.appendTo($("#loginError").parent());
                }
            });
        });

    </script>
</head>

<body class="light-login">
    <div class="row">
        <div class="middle-box text-center animated zoomIn">
            <h3 class="login-title">用户登录</h3>
            <form id="loginForm" class="form-group" method="post" action="${ctx}/login">
                <div class="form-group">
                    <input type="text" name="username" class="form-control required" placeholder="请输入用户名" value="${username}">
                </div>
                <div class="form-group">
                    <input type="password" name="password" class="form-control required" placeholder="请输入密码">
                </div>
                <c:if test="${isValidateCodeLogin}">
                    <div class="form-group">
                        <input type="text" name="validateCode" placeholder="验证码" class="col-sm-2 form-control required" style="width: 100px;"/>
                        <img id="validateCode" src="${pageContext.request.contextPath}/servlet/validateCodeServlet" onclick="$('#validateCodeRefresh').click();" style="margin-left: -50px;"/>
                        <a href="javascript:" id="validateCodeRefresh" onclick="$('#validateCode').attr('src','${pageContext.request.contextPath}/servlet/validateCodeServlet?'+new Date().getTime());">看不清</a>
                    </div>
                </c:if>
                <c:if test="${!empty message}">
                    <div class="alert alert-danger">
                        <button class="close" data-dismiss="alert">×</button>
                        <span>${message }</span>
                    </div>
                </c:if>
                <div class="form-group">
                    <label for="rememberMe" title="下次不需要再登录">
                        <input class="i-checks" type="checkbox" id="rememberMe" name="rememberMe" ${rememberMe ? 'checked' : ''}/>
                        <label for="rememberMe" style="cursor: pointer;">记住我（公共场所慎用）</label>
                    </label>
                </div>
                <button type="submit" class="btn btn-primary btn-group-justified">登录</button>
            </form>
        </div>
    </div>

</body>
</html>