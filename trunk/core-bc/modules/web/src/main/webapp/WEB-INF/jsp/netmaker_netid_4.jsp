<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<OBJECT NAME='iid' WIDTH=0 HEIGHT=0 TYPE='application/x-iid'>
  <param name='DataToBeSigned' value='${signData.tbs}' />
  <param name='DirectActivation' value='Sign' />
  <param name='IncludeCaCert' value='true' />
  <param name='IncludeRootCaCert' value='true' />
  <param name='PostURL' value='${signData.submitUri}' />
  <param name='Base64' value='true' />
  <param name='Detached' value='true' />
</object>

<object NAME='iid' WIDTH=0 HEIGHT=0 CLASSID='CLSID:5BF56AD2-E297-416E-BC49-00B327C4426E'>
  <param name='DataToBeSigned' value='${signData.tbs}' />
  <param name='DirectActivation' value='Sign' />
  <param name='IncludeCaCert' value='true' />
  <param name='IncludeRootCaCert' value='true' />
  <param name='PostURL' value='${signData.submitUri}' />
  <param name='Base64' value='true' />
  <param name='Detached' value='true' />
</object>

<jsp:include page="jsp/supportedBrowsers.jsp" />
