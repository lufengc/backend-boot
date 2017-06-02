<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>lufengc's blog</title>
    <meta name="description" content="lufengc's blog">
    <meta name="keywords" content="lufengc's blog">
    <meta name="decorator" content="cms_default_basic"/>
    <script>
        function page(n, s) {//翻页
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            //$("span.page-size").text(s);
            return false;
        }
    </script>
</head>
<body>
<c:forEach items="${page.list}" var="bean">
    <c:if test="${bean.delFlag == 1}">
        <article class="well clearfix">
            <header class="entry-header">
                <h1 class="entry-title">
                    <a href="${ctx}/article/${bean.id}">${bean.title}
                    </a>
                </h1>
                <div class="clearfix entry-meta">
                        <span class="pull-left">
                            <span>${fn:substring(bean.createDate, 0, 10)}</span><span class="dot">•</span>
                            <span>${bean.categoryId}</span><span class="dot">•</span>
                            <span>${bean.author}</span>
                            <%--<span><a href="#">${bean.weight} 评论数</a></span><span class="dot">•</span>--%>
                            <%--<span>${bean.hits} 浏览数</span>--%>
                        </span>
                </div>
            </header>
            <div class="entry-summary">
                    ${bean.description}
            </div>
            <footer class="entry-footer clearfix visible-lg visible-md visible-sm">
                <div class="pull-left footer-tag">
                    <a href="#" rel="tag">${bean.categoryId}</a>
                    <a href="#" rel="tag">${bean.keywords}</a>
                </div>
                <a class="pull-right more-link" href="${ctx}/article/${bean.id}">阅读全文&raquo;</a>
            </footer>
        </article>
    </c:if>
</c:forEach>
<table:page page="${page}"/>
</body>
</html>
