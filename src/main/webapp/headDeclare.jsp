<%@page language="java" contentType="text/html; charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<title>文件资源管理</title>
<%
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + request.getContextPath() + "/";
    pageContext.setAttribute("basePath", basePath);
%>
<base href="<%=basePath%>"/>
<base target="_self"></base>
<meta name="description" content="ssm+bjui"/>
<meta name="author" content="dean"/>
<meta name="keyword" content="ssm,bjui"/>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<script src="${basePath}js/jquery-1.7.2.min.js"></script>
<script src="${basePath}js/jQuery.md5.js"></script>
<script src="${basePath}js/jquery.cookie.js"></script>


