$(document).ready(function() {
  var $tbs = $('input[name="DataToBeSigned"]')
  var $nonce = $('input[name="Nonce"]')
  var $verifyForm = $('#signData')

  startSign($tbs.val(), $nonce.val())
})
var startSign = function(tbs, nonce) {
    var isBrowserMetroMode = isBrowserMetroMode();
    if (isBrowserMetroMode) {
      alert("Användning av BankID kräver en webbläsare startad i skrivbordsläge.");
  } else {
      var typeOfPlugin = isSignIntalled();
      if (typeOfPlugin != false) {
          // Ladda in plugins
          var plugin = initPlugin(typeOfPlugin, tbs, nonce);

          // Starta signeringen
          var retCode = plugin.PerformAction('Sign')
          if (!retCode) {
              var signature = plugin.GetParam('Signature');
              if(!handleError(plugin.GetLastError())) {
                  $('#signature').attr('value', signature)
                  $('#validate-form').submit()
              }
          } else {
              handleError(plugin.GetLastError());
          }
      }
      else {
          alert("BankID säkerhetsprogram är inte installerat. Gå till https://install.bankid.com")
      }
  }
}

var initPlugin = function(typePlugin, tbs, nonce) {
  if( typePlugin == "Plugin" ) {
    $('body').append("<object type='application/x-personal-signer2' id='signer-id' width=0 height=0></object>");
  } else {
    $('body').append("<object classid='CLSID:FB25B6FD-2119-4CEF-A915-A056184C565E' id='signer-id' width=0 height=0></object>");
  }
  var plugin = $('#signer-id')[0];
  plugin.SetParam('Nonce', nonce);
  plugin.SetParam('TextToBeSigned', tbs);
  return plugin;
}

//Kontrollerar om BISP är installerad genom att försöka så undviker man
//beroende till webbläsarnamn o.s.v.
var isSignIntalled = function() {
  try {
    var myObj = new ActiveXObject("Nexus.SignerV2Ctl");
    if (myObj) {
      return "ActiveX"
    }
  } catch (e) {
    if(navigator.plugins) {
      if (navigator.plugins.length > 0) {
        if (navigator.mimeTypes && navigator.mimeTypes["application/x-personal-signer2"]) {
          if (navigator.mimeTypes["application/x-personal-signer2"].enabledPlugin) {
            return "Plugin";
          }
        }
      }
    }
    return false;
  }
}

var handleError = function(errorMsg)  {
  if (errorMsg) {
    if ( errorMsg === 8002 ) { // Användaren avbryter
      $('#errorCode').attr('value', 1)
    } else {
      $('#errorCode').attr('value', -1)
    }
    $('#cancel-form').submit()
    return true
  } else {
    return false
  }
}
