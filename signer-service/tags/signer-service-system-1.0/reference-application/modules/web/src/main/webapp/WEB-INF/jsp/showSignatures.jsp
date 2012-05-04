<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
</head>
<body>
<form action="clean" method="post">
    <a href="./">Tillbaka</a>&nbsp;
    <button type="submit">Rensa Signaturer</button>
</form>
    <c:forEach items="${signatures}" var="signature">
        <fieldset>
            <form action="verifySignature" method="post">
                    Format: ${signature.format}
                    <p>
                    <pre><c:out value="${signature.decoded}" escapeXml="true"/></pre>
                    </p>
                <br/>
                <input type="hidden" name="signature" value="${signature.encoded}" />
                <input type="hidden" name="signatureFormat" value="${signature.format}"/>
                <input type="submit" value="Verifiera signatur"/>
            </form>
        </fieldset>
    </c:forEach>
</body>
</html>