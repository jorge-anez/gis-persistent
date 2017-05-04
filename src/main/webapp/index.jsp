<%@ page import="org.springframework.util.StringUtils" %><%--
  Created by IntelliJ IDEA.
  User: user
  Date: 1/4/2017
  Time: 6:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%
    String sessionVar = request.getParameter("projectId");
    if(!StringUtils.isEmpty(sessionVar)) {
        session.setAttribute("projectId", Long.parseLong(sessionVar));
        response.sendRedirect("http://localhost/maps/app/app.html");
    }else {
        out.println("Error!");
    }
%>
</body>
</html>
