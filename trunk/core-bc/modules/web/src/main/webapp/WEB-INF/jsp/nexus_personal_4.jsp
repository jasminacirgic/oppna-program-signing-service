<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Signering - Nordea</title>
</head>

<body>
  <object name="signer" width=0 height=0 classid="CLSID:6969E7D5-223A-4982-9B79-CC4FAC2D5E5E">
    <param name="DataToBeSigned" value="${signData.encodedTbs}" />
    <param name="CharacterEncoding" value="UTF-8" />
    <param name="IncludeCaCert" value="true" />
    <param name="IncludeRootCaCert" value="true" />
    <param name="PostURL" value="${postbackUrl}?submitUri=${signData.submitUri}&clientType=${signData.clientType.id}" />
    <param name="Base64" value="true" />
    <param name="SignReturnName" value="signature" />
    <param name="DataReturnName" value="encodedTbs">
  </object>
  <object name="signer" width=0 height=0 type="application/x-personal-signer">
    <param name="DataToBeSigned" value="${signData.encodedTbs}" />
    <param name="SignReturnName" value="signature" />
    <param name="IncludeCaCert" value="true" />
    <param name="IncludeRootCaCert" value="true" />
    <param name="PostURL" value="${postbackUrl}?submitUri=${signData.submitUri}&clientType=${signData.clientType.id}" />
    <param name="Base64" value="true" />
    <param name="CharacterEncoding" value="UTF-8" />
    <param name="DataReturnName" value="encodedTbs">
  </object>
  <jsp:include page="jsp/supportedBrowsers.jsp" />
</body>

</html>