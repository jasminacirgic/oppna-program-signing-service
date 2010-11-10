<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><title>Sign Post</title></head>
<body>
    <form action="http://localhost:8080/signer-service-core-bc-module-web/prepareSign" method="post">
        <label for="signedData">Signed Data:</label>
        <input type="text" id="tbs" name="tbs" value="QW5kZXJz">
        <input type="text" id="postUrl" name="postUrl" value="http://localhost:8080/signer-service-core-bc-module-test-web-client/saveSignature" />
        <input type="submit" value="Sign">
    </form>
    </body>
</html>