<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login Page</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<div id="wrapper">
    <h2>Login with Username and Password</h2>
    <c:if test="${param.error != null}">
        <p style="color: indianred;">
            Login fails ...
        </p>
    </c:if>
    <c:if test="${param.logout != null}">
        <p style="color: mediumseagreen;">
            Logout succeeded!
        </p>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/login">
        <table>
            <tr>
                <td><label for="username">User:</label></td>
                <td><input type="text" id="username"
                           name="username" value='maki@example.com'>
                </td>
            </tr>
            <tr>
                <td><label for="password">Password:</label></td>
                <td><input type="password" id="password"
                           name="password" value="demo"/>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td><input name="submit" type="submit" value="Login"/></td>
            </tr>
        </table>
    </form:form>
</div>
</body>
</html>