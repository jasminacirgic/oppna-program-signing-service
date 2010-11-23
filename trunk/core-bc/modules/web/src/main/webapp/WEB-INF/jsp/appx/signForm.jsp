<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
  <form method="post" action="/sign/prepare">
    <fieldset>   
      <ul>
        <li>
          <label for="tbs">Data att signera:</label>
          <input type="text" id="tbs" name="tbs" value="Hej">
        </li>
        <li>
          <input type="radio" name="submitUri" id="submitUri_http" value="http://140.166.209.181:8080/appx/saveSignature" checked="checked" class="radio"/>
          <label for="submitUri_http">http</label>
          <input type="radio" name="submitUri" id="submitUri_ftp" value="ftp://Anders:w,5(Xm2s)E3F@140.166.209.181:21/tmp" class="radio"/>
          <label for="submitUri_ftp">ftp</label>
        </li>
      </ul>
    </fieldset>

    <fieldset class="submit">   
      <input type="submit" value="Signera" />
    </fieldset>
  </form>
</body>
</html>
