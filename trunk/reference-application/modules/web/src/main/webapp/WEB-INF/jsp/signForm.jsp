<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Signera</title>

<style type="text/css">
  @import 'resources/styles/form.css';
</style>
</head>

<body>
  <h2>Demo signering</h2>
  <form method="post" action="https://localhost:9443/ss/sign/prepare">
    <fieldset>   
      <ul>
        <li>
          <label for="tbs">Data att signera:</label>
          <input type="text" id="tbs" name="tbs" value="Hej">
        </li>
        <li>
          <input type="radio" name="submitUri" id="submitUri_http" value="http://localhost:7080/appx/saveSignature" class="radio"/>
          <label for="submitUri_http">http</label>
          <input type="radio" name="submitUri" id="submitUri_https" value="https://localhost:7443/appx/saveSignature" checked="checked" class="radio"/>
          <label for="submitUri_http">https</label>
        </li>
      </ul>
    </fieldset>

    <fieldset class="submit">   
      <input type="submit" value="Signera" />
    </fieldset>
  </form>
</body>
</html>
