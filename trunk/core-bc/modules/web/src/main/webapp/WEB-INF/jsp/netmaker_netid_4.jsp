<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Signering - Telia/SITHS</title>
</head>

<body>
  <object name='iid' width=0 height=0 classid='CLSID:5BF56AD2-E297-416E-BC49-00B327C4426E'>
    <param name='DataToBeSigned' value='${signData.tbs}' />
    <param name='DirectActivation' value='Sign' />
    <param name='IncludeCaCert' value='true' />
    <param name='IncludeRootCaCert' value='true' />
    <param name='PostURL' value='${signData.postbackUrl}' />
    <param name='Base64' value='true' />
    <param name='SignReturnName' value='signature'>
  </object>
  <object name='iid' width=0 height=0 type='application/x-iid'>
    <param name='DataToBeSigned' value='${signData.tbs}' />
    <param name='DirectActivation' value='Sign' />
    <param name='IncludeCaCert' value='true' />
    <param name='IncludeRootCaCert' value='true' />
    <param name='PostURL' value='${signData.postbackUrl}' />
    <param name='Base64' value='true' />
    <param name='SignReturnName' value='signature'>
  </object>
  <jsp:include page="jsp/supportedBrowsers.jsp" />
</body>

</html>