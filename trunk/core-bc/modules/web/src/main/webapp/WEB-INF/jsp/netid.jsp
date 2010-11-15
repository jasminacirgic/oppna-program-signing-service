<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
  <c:when test="${browserType == 'FIREFOX'}">
    <OBJECT NAME='iid' WIDTH=0 HEIGHT=0 TYPE='application/x-iid'>
  </c:when>
  <c:when test="${browserType == 'CHROME'}">
    <h1>NOT SUPPORTED</h1>
    <object>
  </c:when>
  <c:when test="${browserType == 'MSIE'}">
    <object NAME='iid' WIDTH=0 HEIGHT=0 CLASSID='CLSID:5BF56AD2-E297-416E-BC49-00B327C4426E'>
  </c:when>
  <c:otherwise>
    <h1>NOT SUPPORTED</h1>
    <object>
  </c:otherwise>
</c:choose>

  <param name='DataToBeSigned' value='${signData.tbs}' />
  <param name='DirectActivation' value='Sign' />
  <param name='IncludeCaCert' value='true' />
  <param name='IncludeRootCaCert' value='true' />
  <param name='PostURL' value='${signData.pkiPostBackUrl}' />
  <param name='Base64' value='true' /> 
<!--  <param name='password' value='433876' />-->
</object>
