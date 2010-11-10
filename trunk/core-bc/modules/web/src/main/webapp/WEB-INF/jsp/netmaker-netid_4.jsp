<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sign Sevice</title>
</head>
<body>
<!--<object NAME='iid' WIDTH=0 HEIGHT=0 CLASSID='CLSID:5BF56AD2-E297-416E-BC49-00B327C4426E'>-->
<!--  <param NAME='DataToBeSigned' VALUE='${signData.tbs}' />-->
<!--  <param NAME='DirectActivation' VALUE='Sign' />-->
<!--  <param NAME='IncludeCaCert' VALUE='true' />-->
<!--  <param NAME='IncludeRootCaCert' VALUE='true' />-->
<!--  <param NAME='PostURL' VALUE='${signData.pkiPostBackUrl}' />-->
<!--  <param NAME='Base64' VALUE='true' />-->
<!--    <object NAME='iid' WIDTH=0 HEIGHT=0 TYPE='application/x-iid'>-->
<!--      <param NAME='DataToBeSigned' VALUE='${signData.tbs}' />-->
<!--      <param NAME='IncludeCaCert' VALUE='true' />-->
<!--      <param NAME='IncludeRootCaCert' VALUE='true' />-->
<!--      <param NAME='DirectActivation' VALUE='Sign' />-->
<!--      <param NAME='PostURL' VALUE='${signData.pkiPostBackUrl}' />-->
<!--      <param NAME='Base64' VALUE='true' />-->
<!--    </object>-->
<!--  </object>-->
<form action="${signData.pkiPostBackUrl}" method="post">
    <input type="text" id="signedData" name="signedData" value="${signData.tbs}">
    <input type="text" id="pkiPostBackUrl" name="postUrl" value="${signData.pkiPostBackUrl}">
    <input type="submit" value = "Verify Sign" />
</form>
</body>
</html>