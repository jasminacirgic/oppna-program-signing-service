<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Signering - BankId</title>
  <script type="text/javascript" src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.6.1.min.js"></script>
<script type="text/javascript" src="resources/scripts/bankid-plugin.js"></script>
</head>

<body>
  <input type="hidden" name="DataToBeSigned" value="${signData.encodedTbs}" />
  <input type="hidden" name="Nonce" value="${signData.encodedNonce}" />

  <form:form id='cancel-form' commandName="signData" method="post" action="cancel">
    <form:hidden path="errorCode" />
    <form:hidden path="submitUri" value="${submitUri}" />
    <input type="hidden" id="clientType" name="clientType" value="${signData.clientType.id}" />
  </form:form>
  
  <form:form id='validate-form' commandName="signData" method="post" action="verify">
    <form:hidden path="signature" />
    <form:hidden path="encodedTbs" value="${encodedTbs}" />
    <form:hidden path="nonce" value="${encodedNonce}" />
    <form:hidden path="submitUri" value="${submitUri}" />
    <input type="hidden" id="clientType" name="clientType" value="${signData.clientType.id}" />
  </form:form>
</body>

</html>