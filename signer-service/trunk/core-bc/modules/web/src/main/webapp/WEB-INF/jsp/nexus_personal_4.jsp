<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="X-UA-Compatible" content="requiresActiveX=true"/>
  <title>Signering - Nordea</title>
  <script type="text/javascript" src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.6.1.min.js"></script>
  <script type="text/javascript" src="resources/scripts/nordea-plugin.js"></script>
</head>

<body>
  <input type="hidden" name="DataToBeSigned" value="${signData.encodedTbs}" />
  <input type="hidden" name="PostURL" value="${postbackUrl}/verify?submitUri=${signData.submitUri}&clientType=${signData.clientType.id}" />
  
  <form id="cancel-form" method="post" action="cancel">
    <input type="hidden" name="submitUri" value="${signData.submitUri}" />
    <input type="hidden" name="clientType" value="${signData.clientType.id}" />
    <input type="hidden" id="errorCode" name="errorCode" />
  </form>
  
  <noscript>
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
  </noscript>
</body>

</html>