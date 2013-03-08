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
    <script type="text/javascript" src="resources/scripts/main.js"></script>
    <style type="text/css">
        @import 'resources/styles/form.css';
    </style>
</head>

<body>

<div id="mobileSignWrapper">
    <fieldset class="mobileSignWrapper">
        <legend>Signering Mobilt BankID</legend>
        <div>Starta din Mobilt BankID säkerhetsapp.</div>

        <img id="spinner" src="resources/images/spinner.gif" height="20px" width="20px"/>

        <div>När du har angett din pin-kod i Mobilt BankID säkerhetsapp kommer du skickas vidare automatiskt.</div>

        <c:if test="${isMobileDevice}">
            <p>Du verkar surfa med en mobil enhet. Klicka <a href="bankid://redirect=null">här</a> om du vill öppna
                din
                BankID-app direkt i din enhet.
            </p>
        </c:if>

        <h4 id="responseText"></h4>
    </fieldset>
</div>

</body>

<script type="text/javascript">
    var orderRef = '${orderRef}';
    var data = '${data}';

    $(document).ready(function () {
        setTimeout(pollForCompletion(orderRef, data, 0), 10000);
    });
</script>

</html>