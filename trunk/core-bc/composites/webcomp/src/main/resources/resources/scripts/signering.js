function initPlugin(typePlug){
  var objHldr = document.createElement("object");
  if( typePlug == "Plugin" ) {
    objHldr.setAttribute("type", "application/x-personal-signer2");
  } else {
    objHldr.setAttribute("classid", "CLSID:FB25B6FD-2119-4CEF-A915-A056184C565E");
  }
  objHldr.setAttribute("id", "pluginId");
  objHldr.setAttribute("name", "signer2");
  objHldr.setAttribute("width", "0");
  objHldr.setAttribute("height", "0");
  document.body.appendChild(objHldr);
}
// Kontrollerar om BISP är installerad genom att försöka så undviker man
// beroende till webbläsarnamn o.s.v.
function isInstalled (){
  try {
    var myObj = new
    ActiveXObject("Nexus.SignerV2Ctl");
    if (myObj) {
      return "ActiveX";
    }
  } catch(e) {
    if (navigator.plugins['Nexus Personal']) {
      return "Plugin";
    } else {
// BankID säkerhetsprogram är inte installerat
      return false;
    }
    return false;
  }
}

function Checkerror(paramName, errorMsg) {
  if (errorMsg) {
    if ( errorMsg == 8002 ) { //Användaren avbryter
      return false;
    }
    else if (errorMsg == 8015) { // Sidan måste köras över SSL
      alert ("Signeringen kräver att sidan den laddas ifrån är skyddad med SSL, dvs att sidans URL börjar med https:// .");
    }
    else {
// Lägg din kod för felhantering här. Se avsnitt 3.5 i dokumentet "BISP -
// Beskrivning till förlitande part"
      alert("Tekniskt fel: " + errorMsg + " vid " + paramName);
      return false;
    }
  }
  else {
    return true;
  }
}

function startSign(tbs, hiddentbs, nonce) {
  var typePlugin = isInstalled();
  if (typePlugin != false) {
// pluginerna bör laddas enl. funktionen initPlugin() p.g.a. kompabilitet med
// t.ex. Chrome. Det bör också ske efter att hela DOM-trädet är laddat, därav
// anropet via body onload.
    initPlugin(typePlugin);
    var retVal;
    var Plugin = document.getElementById('pluginId');
    var signature = "Not Present";
    
// Sätt parametrarna till klienten och kolla om det gick bra.

        // Todo Byt detta värde!!
        Plugin.SetParam('Nonce', nonce);
        Checkerror("Nonce", Plugin.GetLastError());
        
        // Texten som visas för användaren. "Vänligen överför 1000 kronor till konto 123456-7"
        Plugin.SetParam('TextToBeSigned', tbs);
        Checkerror("TBS", Plugin.GetLastError());

        // Text som signeras men inte visas för användaren t.ex. XML-data.
        Plugin.SetParam('NonVisibleData',hiddentbs);
        Checkerror("NVD", Plugin.GetLastError());

        // Starta signeringen
        retVal = Plugin.PerformAction('Sign');
        if (!retVal) {
          signature = Plugin.GetParam('Signature');
          if (Checkerror("hämtning av signatur", Plugin.GetLastError())) {
            // Lägg in din kod för att skicka transaktionen till din BICS här.
            document.signerData.SignedData.value = signature;
            document.signerData.submit();
            return true;
          }
        }
        else {
          Checkerror("performAction", Plugin.GetLastError());
        }
  }
  else {
    alert("BankID säkerhetsprogram är inte installerat. Gå till https:// install.bankid.com")
  }
}