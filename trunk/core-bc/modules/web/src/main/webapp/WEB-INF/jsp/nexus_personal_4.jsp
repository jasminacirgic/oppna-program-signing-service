<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sign Sevice</title>
</head>
<body>
<object name='signer' classid='CLSID:6969E7D5-223A-4982-9B79-CC4FAC2D5E5E'>
  <param name='DataToBeSigned' value='${signData.tbs}' />
  <param name='CharacterEncoding' value='UTF-8' />
  <param name='SignReturnName' value='SignedData' />
  <param name='IncludeCaCert' value='true' />
  <param name='IncludeRootCaCert' value='true' />
  <param name='PostURL' value='${signData.submitUri}' />
  <param name='Base64' value='true' />
</object>
<object name='signer' type='application/x-personal-signer'>
  <param name='DataToBeSigned' value='${signData.tbs}' />
  <param name='CharacterEncoding' value='UTF-8' />
  <param name='SignReturnName' value='SignedData' />
  <param name='IncludeCaCert' value='true' />
  <param name='IncludeRootCaCert' value='true' />
  <param name='PostURL' value='${signData.submitUri}' />
  <param name='Base64' value='true' />
</object>

<jsp:include page="jsp/supportedBrowsers.jsp" />

</body>
</html>