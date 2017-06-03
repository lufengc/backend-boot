<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<!DOCTYPE html>
<html>
<head>
	<title><sitemesh:title default="Hi"/></title>
	<%@include file="/WEB-INF/views/modules/cms/front/include/head.jsp" %>
	<sitemesh:head/>

    <script>
        $(function () {
            var url = window.location.href;
            $(".navbar-nav > li").each(function(){
                $(this).removeClass("active");
                if(url.indexOf($(this).attr("data")) > 0){
                    $(this).addClass("active");
                }
            });
            if(url.indexOf("article") > 0){

            }
            $("a").tooltip();
            if (jQuery(window).width() > 768) {
                dropDown();
            }
            //导航二级菜单
            function dropDown() {
                var dropDownLi = jQuery('li.dropdown');
                dropDownLi.mouseover(function () {
                    jQuery(this).addClass('open');
                }).mouseout(function () {
                    jQuery(this).removeClass('open');
                });
            }

            $("#top").click(function () {
                $('body,html').animate({
                            scrollTop: 0
                        },
                        1000);
                return false;
            });
            $('.magnific').magnificPopup({
                type: 'image',
                gallery: {
                    enabled: true
                }
            });

            //警告框链接加样式
            $(".alert").children("p").children("a").addClass("alert-link");
            $(".alert").children("a").addClass("alert-link");

        });
        //返回顶部
        $(window).scroll(function () {
            if ($(window).scrollTop() > 100) {
                $('#top').show();
            } else {
                $('#top').hide();
            }
        });

        if (jQuery(window).width() > 768) {
            // Shrink menu on scroll
            var didScroll = false;
            jQuery(window).scroll(function () {
                didScroll = true;
            });
            var y;
            setInterval(function () {
                if (didScroll) {
                    didScroll = false;
                    y = jQuery(window).scrollTop();
                    if (y > 75) {
                        jQuery('#nav').addClass('fixed');
                    } else {
                        jQuery('#nav').removeClass('fixed');
                    }
                }
            }, 50);
        }

    </script>
</head>
<body>
<div style="background-image: url('/static/app/image/light-login.jpg')" id="background_pattern"></div>
<header>
    <div id="masthead" role="banner" class="hidden-xs">
        <div class="top-banner">
            <div class="container">
                <a class="brand brand-text" href="${ctx}" title="lufengc's blog" rel="home">
                    <h1>lufengc's blog<small>淡定思蛋痛</small></h1>
                </a>
                <div class="top-social pull-right hidden-xs">
                    <a id="s_sina_weibo" title="新浪微博" target="_blank" href="" data-toggle="tooltip" data-placement="bottom">
                        <i class="fa fa-weibo"></i>
                    </a>
                    <a id="s_email" title="EMAIL" target="_blank" href="mailto:798288668@qq.com" data-toggle="tooltip" data-placement="bottom">
                        <i class="fa fa-envelope-o"></i>
                    </a>
                    <a id="s_github" title="GITHUB" target="_blank" href="#" data-toggle="tooltip" data-placement="bottom">
                        <i class="fa fa-github"></i>
                    </a>
                    <a id="s_rss" title="RSS" target="_blank" href="#" data-toggle="tooltip" data-placement="bottom">
                        <i class="fa fa-rss-square"></i>
                    </a>
                </div>
            </div>
        </div>
    </div>
    <nav id="nav" class="navbar navbar-default container-fluid" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#navbar-collapse">
                    <span class="fa fa-bars"></span>
                </button>
                <a class="navbar-brand visible-xs" href="${ctx}">lufengc's blog</a>
            </div>
            <div class="collapse navbar-collapse" id="navbar-collapse">
                <ul class="nav navbar-nav">
                    <li class="menu-item" tag="/">
                        <a href="${ctx}">首页</a>
                    </li>
                    <li class="menu-item" data="article">
                        <a href="${ctx}/article">文章归档</a>
                    </li>
                    <li class="menu-item" data="about">
                        <a href="${ctx}/about">关于我</a></li>
                    <li class="menu-item" data="guestbook">
                        <a href="${ctx}/guestbook">给我留言</a>
                    </li>
                    <li class="menu-item" data="data">
                        <a href="${ctx}/data">无聊</a>
                    </li>
                </ul>
                <form action="#" method="get" id="searchform" class="navbar-form navbar-right visible-lg" role="search">
                    <div class="form-group">
                        <input type="text" name='s' id='s' class="form-control" placeholder="这里有你想要的">
                        <button class="btn btn-danger" type=""><i class="fa fa-search"></i></button>
                    </div>
                </form>
            </div>
        </div>
    </nav>
</header>
<div class="container">
    <section class="row">
        <!--[if lt IE 8]>
        <div id="ie-warning" class="alert alert-danger fade in visible-lg">
            <button type="button" class="close" data-dismiss="alert">&times;</button>
            <i class="fa fa-warning"></i> 请注意，本网站不支持低于IE8的浏览器，为了获得最佳效果，请下载最新的浏览器，推荐下载
            <a href="http://www.google.cn/intl/zh-CN/chrome/" target="_blank"><i class="fa fa-compass"></i> Chrome</a>
        </div>
        <![endif]-->
        <section class='col-md-8'>
            <sitemesh:body/>
        </section>
        <aside id="side-bar" class="col-md-4">
            <aside class="widget widget_views panel panel-specs visible-lg visible-md">
                <div class="panel-heading"><h2>回味依旧</h2></div>
                <ul>
                    <li>
                        <EMBED pluginspage=http://www.macromedia.com/go/getflashplayer src="${ctxStatic}/static/front/file/time.swf"
                               width=270 height=70 type=application/x-shockwave-flash wmode="transparent" quality="high">
                    </li>
                    <li>
                        闲坐小窗读周易,不知春去已多时
                    </li>
                </ul>
            </aside>
            <aside class="widget widget_views panel panel-specs visible-lg visible-md">
                <div class="panel-heading"><h2>BGM</h2></div>
                <ul>
                    <li>
                        <%--<embed src="${ctxStatic}/static/front/file/player.swf?url=${ctxStatic}/static/front/file/dahua.mp3&amp;autoplay=1"
                               type="application/x-shockwave-flash" wmode="transparent" allowscriptaccess="always" width="260" height="26">--%>
                        <iframe frameborder="no" marginwidth="0" marginheight="0" width=320 height=100
                                src="http://music.163.com/outchain/player?type=2&id=2919622&auto=1"></iframe>
                    </li>
                </ul>

            </aside>
            <aside class="widget widget_views panel panel-specs visible-lg visible-md">
                <div class="panel-heading"><h2>分类</h2></div>
                <ul>
                    <li>
                        <script type="text/javascript" src="${ctxStatic}/static/front/tagcloud/swfobject.js"></script>
                        <embed src="${ctxStatic}/static/front/tagcloud/tagcloud.swf" width="310" height="250" tplayername="SWF" splayername="SWF"
                               type="application/x-shockwave-flash" mediawrapchecked="true"
                               pluginspage="http://www.macromedia.com/go/getflashplayer" id="tagcloudflash" name="tagcloudflash"
                               quality="high" wmode="transparent" allowscriptaccess="always"
                               flashvars="tcolor=0x333333&amp;tcolor2=0x666666&amp;hicolor=0xf5f5f5&amp;tspeed=100&amp;distr=true"></embed>
                    </li>
                </ul>
            </aside>
            <aside id="views-2" class="widget widget_views panel panel-specs visible-lg visible-md">
                <div class="panel-heading"><h2>热门文章</h2></div>
                <ul>
                    <li><a href="#" title="利用amh反代谷歌">利用amh反代谷歌</a> - 273,742 views</li>
                    <li><a href="#" title="12分钟看完科幻神作《三体》">12分钟看完《三体》</a> - 17,167 views</li>
                </ul>
            </aside>
            <aside id="categories-7" class="widget widget_categories panel panel-specs visible-lg visible-md">
                <div class="panel-heading"><h2>分类目录</h2></div>
                <ul>
                    <li class="cat-item cat-item-3"><a href="#">心情随笔</a>
                    </li>
                    <li class="cat-item cat-item-6"><a href="#">技术杂谈</a>
                    </li>
                    <li class="cat-item cat-item-4"><a href="#">情感天地</a>
                    </li>
                </ul>
            </aside>
            <aside id="text-12" class="widget widget_text panel panel-specs visible-lg visible-md">
                <div class="panel-heading"><h2>联系我</h2></div>
                <div class="textwidget">
                    联系我：798288668<br>
                    Email：<a href="mailto:lufengc@163.com">lufengc@163.com</a>
                </div>
            </aside>
            <aside id="text-18" class="widget widget_text panel panel-specs visible-lg visible-md">
                <div class="panel-heading"><h2>链接</h2></div>
                <div class="textwidget">
                    <a href="https://shop145397319.taobao.com/" target="_blank">淘宝旗舰店 </a>
                </div>
            </aside>
        </aside>
    </section>
</div>
<footer id="body-footer">
    <div class="container clearfix">
        Copyright © 2016 <a href="#">博客首页</a> |
        <a href="#">百度地图</a>
        -<a href="${ctx}" target="_blank">登录</a>-
        Theme By <a href="#" target="_blank">Specs</a><br>
        <div>${fns:getDate('yyyy年MM月dd日 E')}</div>
    </div>
    <ul id="jump" class="visible-lg">
        <li><a id="share" title="意见反馈" href="#" target="_blank"><i class="fa fa-comments-o"></i></a></li>
        <li><a id="top" href="#top" title="返回顶部" style="display:none;"><i class="fa fa-arrow-circle-up"></i></a></li>
    </ul>
</footer>
</body>
</html>