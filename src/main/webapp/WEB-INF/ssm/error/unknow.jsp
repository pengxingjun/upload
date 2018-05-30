<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	<title>错误</title>
  </head>
  
  <body>
  	<p>不好意思，服务器发生未知异常。
  	请等待我们的技术人员进行抢修。。。</p>
  	<p>异常信息如下：
    ${ex.message}</p>
    <p>${ex.cause}</p>
    <p>${ex.stackTrace}</p>
    <p>${ex.suppressed}</p>
  </body>
</html>