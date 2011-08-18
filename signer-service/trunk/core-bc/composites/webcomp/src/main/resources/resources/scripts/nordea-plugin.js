$(document).ready(function() {
  var $tbs = $('input[name="DataToBeSigned"]')
  var $postUrl = $('input[name="PostURL"]')
  var typeOfPlugin = isSignIntalled()

  if (typeOfPlugin != false) {
    // Ladda in plugins
    var signer = initSignController(typeOfPlugin, $tbs.val(), $postUrl.val())

    // Starta signeringen
    retVal = signer.Sign();
    if(retVal !== 0) { // User Abort
      var errCode = -1
      if(signer.GetErrorString() === "USER_ABORT") {
        errCode = 1
      }
      $('#errorCode').attr('value', errCode)
      $("#cancel-form").submit() 
    }
  } else {
    $('#errorCode').attr('value', 2)
    $("#cancel-form").submit() 
  }
});

//Kontrollerar om NetId är installerad genom att försöka så undviker man
//beroende till webbläsarnamn o.s.v.
var isSignIntalled = function() {
  try {
    var myObj = new ActiveXObject("Nexus.SignerCtl");
    if (myObj) {
      return "ActiveX";
    }
  } catch (e) {
    if(navigator.plugins) {
      if (navigator.plugins.length > 0) {
        if (navigator.mimeTypes && navigator.mimeTypes["application/x-personal-signer"]) {
          if (navigator.mimeTypes["application/x-personal-signer"].enabledPlugin) {
            return "Plugin";
          }
        }
      }
    }
    return false;
  }
}

var initSignController = function(typePlugin, tbs, postUrl) {
  if( typePlugin == "Plugin" ) {
    $('body').append("<object type='application/x-personal-signer' id='signer-id' width=0 height=0></object>")
  } else {
    $('body').append("<object classid='CLSID:6969E7D5-223A-4982-9B79-CC4FAC2D5E5E' id='signer-id' width=0 height=0></object>");
  }
  
  var signer = document.getElementById('signer-id');
  signer.SetDataToBeSigned(tbs);
  signer.SetSignReturnName('signature');
  signer.SetIncludeCaCert('true');
  signer.SetIncludeRootCaCert('true');
  signer.SetPostURL(postUrl);
  signer.SetBase64('true');
  signer.SetCharacterEncoding('UTF-8');
  signer.SetDataReturnName('encodedTbs');

  return signer;

}