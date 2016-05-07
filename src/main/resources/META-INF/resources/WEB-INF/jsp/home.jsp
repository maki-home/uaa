<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="utf-8">
    <title>Home</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div id="wrapper">
    <h2>UAA</h2>

    <table>
        <tr>
            <th>Name</th>
            <th>Email</th>
        </tr>
        <tr>
            <td><c:out value="${member.familyName}"/> <c:out value="${member.givenName}"/></td>
            <td><c:out value="${member.email}" /></td>
        </tr>
    </table>

    <p>
        <form:form action="${pageContext.request.contextPath}/logout">
            <input type="submit" value="Logout"/>
        </form:form>
    </p>
</div>
</body>
</html>
</html>