<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="X-UA-Compatible" content="requiresActiveX=true"/>
  <title>Signering - Telia/SITHS</title>
  <script type="text/javascript" src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.6.1.min.js"></script>
  <script type="text/javascript" src="resources/scripts/netId-plugin.js"></script>
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
    <object name="iid" width=0 height=0 classid="CLSID:5BF56AD2-E297-416E-BC49-00B327C4426E">
      <param name="DataToBeSigned" value="${signData.encodedTbs}" />
      <param name="DirectActivation" value="Sign" />
      <param name="IncludeCaCert" value="true" />
      <param name="IncludeRootCaCert" value="true" />
      <param name='PostURL' value='${postbackUrl}/verify?submitUri=${signData.submitUri}&clientType=${signData.clientType.id}' />
      <param name="Base64" value="true" />
      <param name="SignReturnName" value="signature">
      <param name="DataReturnName" value="encodedTbs">
    </object>
    <object name="iid" width=0 height=0 type="application/x-iid">
      <param name="DataToBeSigned" value="${signData.encodedTbs}" />
      <param name="DirectActivation" value="Sign" />
      <param name="IncludeCaCert" value="true" />
      <param name="IncludeRootCaCert" value="true" />
      <param name='PostURL'
        value='${postbackUrl}/verify?submitUri=${signData.submitUri}&clientType=${signData.clientType.id}' />
      <param name="Base64" value="true" />
      <param name="SignReturnName" value="signature">
      <param name="DataReturnName" value="encodedTbs">
    </object>
  </noscript>
</body>

</html>