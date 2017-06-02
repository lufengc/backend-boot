<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<html>
<head>
    <title>lock</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <meta name="author" content="http://www.lufengc.com/"/>

    <link rel="shortcut icon" href="${ctxStatic}/static/app/image/favicon.ico">
    <link rel="stylesheet" href="${ctxStatic}/static/plugins/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctxStatic}/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="${ctxStatic}/static/plugins/animate.min.css">
    <link rel='stylesheet' href='${ctxStatic}/static/plugins/magnific-popup.css'/>
    <link rel='stylesheet' href='${ctxStatic}/static/front/css/index.css'/>

    <script src="${ctxStatic}/static/plugins/jquery/jquery-2.1.4.min.js"></script>
    <script src="${ctxStatic}/static/plugins/jquery/jquery-migrate-1.1.1.min.js"></script>
    <script src="${ctxStatic}/static/plugins/bootstrap/js/bootstrap.min.js"></script>
    <script src='${ctxStatic}/static/plugins/jquery.lazyload.js'></script>
    <script src='${ctxStatic}/static/plugins/jquery.magnific-popup.js'></script>
    <!--[if lt IE 9]>
    <script src="${ctxStatic}/static/plugins/html5shiv.js"></script>
    <script src="${ctxStatic}/static/plugins/respond.min.js"></script>
    <![endif]-->

    <script type="text/javascript">var ctx = '${ctx}', ctxStatic = '${ctxStatic}';</script>
</head>

<body>
<div style="background-image: url('/static/app/image/light-login.jpg')" id="background_pattern"></div>
<div class="lock-word animated fadeInDown">
</div>
<div class="middle-box text-center lockscreen animated fadeInDown">
    <div>
        <div class="m-b-md">
            <img alt="image" class="img-circle circle-border" src="${ctxStatic}/static/front/image/2.png">
        </div>
        <h3>grz</h3>
        <p>请输入密码</p>
        <form class="m-t" role="form" action="${ctx}/grz">
            <div class="form-group">
                <input type="password" class="form-control" placeholder="******" required="">
            </div>
            <button type="submit" class="btn btn-danger block full-width">解锁</button>
        </form>
    </div>
</div>
</body>
</html>
