<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="requiresActiveX=true"/>
    <title>Signering - Mobilt BankID</title>
    <script type="text/javascript" src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.6.1.min.js"></script>
    <script type="text/javascript" src="resources/scripts/main.js" charset="UTF-8"></script>
    <style type="text/css">
        @import 'resources/styles/form.css';
    </style>
</head>

<body>

<div id="mobileSignWrapper">
    <fieldset class="mobileSignWrapper">
        <legend>Signering Mobilt BankID</legend>

        <form:form id='validate-form' commandName="signData" method="post" acceptCharset="ISO-8859-1" action="signMobileBankId" onsubmit="return validatePersonalNumber(this)">
            <form:hidden path="encodedTbs" value="${encodedTbs}"/>
            <form:hidden path="submitUri" value="${submitUri}"/>
            <form:hidden path="ticket" value="${ticket}"/>
            <form:hidden path="clientType" value="${signData.clientType.id}"/>
            <span>Personnummer (12 siffror, t.ex. 191712319999)</span><form:input path="personalNumber"/>
            <input type="submit" value="OK"/>
        </form:form>
    </fieldset>
</div>
</body>


</html>