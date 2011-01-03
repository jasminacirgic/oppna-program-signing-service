<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Signering - BankId</title>
  <script type="text/javascript" src="resources/scripts/signering.js"></script>
</head>

<body onload='startSign("${signData.encodedTbs}", "${signData.encodedNonce}")'>
  <form:form commandName="signData" method="post" name="signData" action="verify">
    <form:hidden path="signature" value="" />
    <form:hidden path="tbs" value="${encodedTbs}" />
    <form:hidden path="nonce" value="${encodedNonce}" />
    <form:hidden path="submitUri" value="${submitUri}" />
    <input type="hidden" id="clientType" name="clientType" value="${signData.clientType.id}" />
  </form:form>
</body>
<jsp:include page="jsp/supportedBrowsers.jsp" />
</html>