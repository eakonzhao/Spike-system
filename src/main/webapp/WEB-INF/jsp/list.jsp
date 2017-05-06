<%--
  Created by IntelliJ IDEA.
  User: Eakon
  Date: 2017/5/6
  Time: 13:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/tablib.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀列表页</title>
    <%@include file="common/head.jsp"%>
</head>
<body>
    <!--页面显示部分-->
    <div class="container">
        <div class="panel panel=default">
            <div class="panel-heading text-center">
                <h2>秒杀列表</h2>
            </div>
            <div class="panel-body">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>名称</th>
                            <th>库存</th>
                            <th>开始时间</th>
                            <th>结束时间</th>
                            <th>创建时间</th>
                            <th>详情页</th>
                        </tr>
                    </thead>
                    <tbody>
                            <c:forEach var="sk" items="${seckillInventoryList}">
                                <tr>
                                <td>${sk.name}</td>
                                <td>${sk.number}</td>
                                <td>
                                    <fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd hh:mm:ss"/>
                                </td>
                                <td>
                                    <fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd hh:mm:ss"/>
                                </td>
                                <td>
                                    <fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd hh:mm:ss"/>
                                </td>
                                <td>
                                    <a class="btn btn-info" href="/seckill/${sk.seckillId}/detail">查看</a>
                                </td>
                                </tr>
                            </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <%@include file="common/bottom.jsp"%>
</body>
</html>
