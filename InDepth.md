# Innehållsförteckning #


# Referenser #
  * [API BankID](http://www.bankid.com/Global/wwwbankidcom/Calle/BISP%20-%20beskrivning%20till%20f%C3%B6rlitande%20part%202.0.pdf)
  * [API NetId](http://avtal.skane.se/HolgerDokument/09/02273/Bilaga20NetiDDeveloper'sGuidev2.01.pdf)
  * [Testcertifikat BankID](http://www.bankid.com/Global/wwwbankidcom/RP/Test%20BankIDn.zip)
  * [Testcertifikat Telia](http://eid.trust.telia.com/Testcertifikat/Teliaelegmjukt/tabid/59/Default.aspx)
  * 

# Bakgrund och ändamål #
En elektronisk signatur ska säkerställa att elektroniskt överförd information inte har ändrats
och för att identifiera informationens avsändare. Genom kryptering skyddas uppgifter i ett
dokument mot obehörig åtkomst.

## Vad är en elektronisk signatur? ##
En elektroniskt underskriven handling består av två delar. Dels _texten_ som skall signeras och dels själva signaturen. För att säkerställa att en _text_ är identisk vid två olika tillfällen beräknas en kontrollsumma av _texten_. Denna kontrollsumma är _alltid_ densamma så länge _texten_ inte har förändrats och därmed kan man garantera att _texten_ inte har förändrats över tid. Detta är dock inte tillräckligt utan man behöver även kunna säkerställa _vem_ som har utfärdat _texten_. Detta görs genom att kontrollsumman krypteras med undertecknarens privat nyckel. Dekryptering kan sedan _endast_ göras med hjälp av undertecknarens publika nyckel. Nu kan man med säkerhet knyta en viss _text_ till en bestämd utställare.

I den digitala signatur som genereras finns tillräckligt med information för att kontrollera äktheten hos en elektroniskt underskriven handling. Formatet på dessa signaturer kommer i två varianter, [PKCS#7](http://tools.ietf.org/html/rfc5652) eller [XMLDSig](http://www.w3.org/TR/xmldsig-core/) beroende på vilken typ av e-legitimation som används vid signeringstillfället.

## Hur signerar jag utan signeringtjänsten? ##
Första steget för en applikation är att erbjuda signering för en användare. När användaren har bett om signering måste applikationen visa en signeringsklient för användaren som skall utföra själva signeringen. Här måste applikationen ta hänsyn till vad för typ av certifikat som användaren vill signera med samt vilken browser som används. I nästa steg måste applikationen ta emot signaturen och genomföra en kontroll av den för att verifiera att certifikatet som användes vid signeringen är giltligt. Det som verifieras är att certifikatet är utfärdat av en giltig CA-Root, att det inte är revokerat samt att det inte har gått ut. I sista steget måste applikationen spara signaturen för framtida behov. Nedan visas en schematisk bild över hur flödet ser ut, de heldragna pilarna är de interaktioner som måste hanteras/implementeras av applikationen.
![http://oppna-program-signing-service.googlecode.com/svn/wiki/images/Signature-process.png](http://oppna-program-signing-service.googlecode.com/svn/wiki/images/Signature-process.png)

# Funktionsbeskrivning #
## Hur signerar jag med signeringtjänsten? ##
Första steget för en applikation är att erbjuda signering för en användare. Efter detta tar signeringstjänsten över och hanterar presentationen av signeringsklineten (t.ex. NetID) med allt vad det innebär, signeringstjänsten tar även hand om certifikatskontrollen som bla. verifierar att certifikatet som används i signaturen inte är spärrat. Det den nyttjande applikationen behöver ta hänsyn till är lagringen av signaturen. Nedan visas en schematisk bild över hur flödet ser ut, de heldragna pilarna är de interaktioner som måste hanteras/implementeras av applikationen.
![http://oppna-program-signing-service.googlecode.com/svn/wiki/images/Signatureservice-process.png](http://oppna-program-signing-service.googlecode.com/svn/wiki/images/Signatureservice-process.png)

## Vad gör signeringstjänsten för mig? ##
Signeringstjänsten hjälper till med följande:
  * Möjlighet att visa ett gränssnitt för val av signerings-certifikat
  * Presenatation av olika signeringsklienter beroende på certifikat
  * Hantering av olika webbläsare
  * Verifiering av utfärdad signatur

# Översiktlig systembeskrivning #
<img src='http://oppna-program-signing-service.googlecode.com/svn/wiki/images/solution.png' width='800' />

  1. Klienten begär att få något signerat(TBS – To Be Signed) av SS. Rekommenderat är att skicka en kontrollsumma av TBS. Kontrollsumman är förslagsvis beräknad utifrån [SHA-2](http://en.wikipedia.org/wiki/SHA-2)(t.ex. SHA-256) för att få en ökad säkerhet. SS behöver även ha en submitUri dit signaturen skall skickas när den är klar. Valbart(optional) är att skicka in en clientType.
  1. Har ingen clientType skickats med visas ett val av tillgängliga clientTypes för användaren. SS tar fram ett block av html/xhtml([PKI](http://en.wikipedia.org/wiki/Public_key_infrastructure) Client Code) baserat på clientType och TBS som den visar för klienten.
  1. [PKI](http://en.wikipedia.org/wiki/Public_key_infrastructure)-klienten startar upp för signering hos användaren.
  1. Användaren matar in lösenord för sin privata nyckel och TBS signeras. [PKI](http://en.wikipedia.org/wiki/Public_key_infrastructure)-klienten postar signaturen [PKSC#7](http://en.wikipedia.org/wiki/Cryptographic_Message_Syntax) eller [XML-Signature](http://www.w3.org/TR/xmldsig-core/)) till SS. Vilket format som skickas till SS beror på clientType.
  1. Signaturen verifieras via en [OSIF](http://sveid.episerverhotell.net/SVEIDtemplates/SVEIDpage.aspx?id=124) tjänst.
  1. Status för hur valideringen gick skickas tillbaka till SS.
  1. SS skickar signaturen till submitUri för lagring, detta sker över ftps eller https. Om https väljs finns även en möjlighet för applikationen att själv presentera status för signeringen (se steg 7). I annat fall kommer SS att hantera presentationen av status.
  1. Om X själv kan och vill presentera status för signeringen gör den en [302 Moved Temporary](http://en.wikipedia.org/wiki/HTTP_302) till en url som hanterar presentationen. Om X vill överlåta detta till SS räcker det med att svara med en vanlig [200 OK](http://en.wikipedia.org/wiki/List_of_HTTP_status_codes) response.
  1. Om SS får en [302 Moved Temporary](http://en.wikipedia.org/wiki/HTTP_302) från X skickar även SS en [302 redirect](http://en.wikipedia.org/wiki/HTTP_302) tillbaka till klienten med samma location som X satte i sin redirect. Om SS får en [200 OK](http://en.wikipedia.org/wiki/List_of_HTTP_status_codes) visar den en status sida för klienten.
  1. [302 Moved Temporary](http://en.wikipedia.org/wiki/HTTP_302) till location satt av X.
  1. X visar ett svar för klienten.
**Kommentarer**
> &#42;Steg 10 och 11 kommer endast att inträffa om X gör en [302 Moved Temporary](http://en.wikipedia.org/wiki/HTTP_302) i steg 8.

## Säkerhet ##
För att garantera säkerheten bör all kommunikation ske över en krypterad anslutning – https. Det finns dock fortfarande en risk att SS blir utsatt för en attack och [ersätts av en ”elak”](http://en.wikipedia.org/wiki/Man-in-the-middle_attack) SS som byter ut signatur och certifikat. För att förhindra detta är det starkt rekommenderat att X sätter upp [TLS](http://en.wikipedia.org/wiki/Transport_Layer_Security) mot SS. [TLS](http://en.wikipedia.org/wiki/Transport_Layer_Security) syftar till att genom utbyte av certifikat mellan SS och X kan X grantera att identiteten på SS. Alltså, med tillgång till SS publika nyckel kan X verifiera att det är SS som skickar signaturen till submitUri.

## Ordlista ##
| **Begrepp** | **Förklaring** |
|:------------|:----------------|
| TBS         | To Be Signed. Data som skall signeras. |
| submitUri   | Uri dit signaturen skall skickas efter signering. |
| clientType  | Typ av signering, i dagsläget har Signeringstjänsten stöd för BankId, Nordea, Posten och SITHS. |

# Sekvensdiagram #
![http://oppna-program-signing-service.googlecode.com/svn/wiki/sequencediagrams/system-level.png](http://oppna-program-signing-service.googlecode.com/svn/wiki/sequencediagrams/system-level.png)

# Projektstruktur #
Signeringstjänsten består egentligen av två projekt:
  * **signer-service** som i sin tur består av två moduler:
    * core-bc - detta är själva signeringstjänsten
    * reference-application - en konsument  som fungerar som ett exempel på hur man kan utnyttja signeringstjänsten
  * **signer-service-schemas** - innehåller schemat för det xml-meddelande, innehållandes signaturen, som skickas från signeringstjänsten till konsumenten.

# Installation #
Signeringstjänsten behöver konfigureras en hel  innan den kan startas. Detta beror på att den använder sig av [TLS](http://en.wikipedia.org/wiki/Transport_Layer_Security) för att datan som skickas mellan den och konsumenterna. För att i möjligaste mån underlätta så mycket som möjligt för utvecklarna av tjänsten har en viss default-konfiguration paketerats med i projektet. För en produktionsinstans måste dock denna konfiguration anpassas. Hur man gör detta beskrivs i avsnittet [konfiguration](InDepth#Konfiguration.md) längre ner i dokumentationen.

Även konsumenterna av signeringstjänsten kräver en del TLS-konfiguration vilket finns beskrivet i avsnittet [Anslut till signeringstjänsten](http://code.google.com/p/oppna-program-signing-service/wiki/InDepth#Anslut_till_signeringstjänsten).

## Sätta upp en instans av signeringstjänsten ##
Att installera och konfigurera signeringstjänsten i en produktionsmiljö ganska trixit. Installationen består av två steg: först skapar man en katalog med namnet `.ss` i hemkatalogen (här kommer konfigurationen att hamna) för den användare som kör webbservern (t.ex. Tomcat) och sen installerar man war-filen. Mellan dessa steg måste dock en hel del konfiguration göras, se avsnittet om [konfiguration](InDepth#Konfiguration.md).

# Konfiguration #
För att få en bakgrund till hur man skall konfigurera signeringstjänsten är det lämpligt att läsa  [denna blog-post](http://blog.callistaenterprise.se/2011/11/17/configure-your-spring-web-application/) som på ett bra sätt beskriver hur applikationen läser in sin konfiguration.

Med bakgrund till ovan nämnda blogg kommer här en förklaring till inställningar som kan göras för signeringstjänsten. Till att börja med måste en fil med namnet `config.properties` skapas och läggas in under `~/.ss`. Om man vill ha en mall att utgå ifrån så kan man använda sig av default-konfigurationen som ligger i projektets web-modul (`$PROJECT_HOME/core-bc/modules/web/src/main/resources/default.properties`). I denna fil kan följande parametrar sättas:

| **Parameter** | **Beskrivning** |
|:--------------|:----------------|
| connectionTimeout  | Vid överföring av signaturen till konsumenten används en http-klient. Detta värde anger hur länge applikationen skall vänta på en connection till servern. |
| soTimeout     | Vid överföring av signaturen till konsumenten används en http-klient. Detta värde anger hur länge en socket skall vänta på svar när den läser och skriver paket. Detta ligger på en lägre nivå i nätverksstacken än connectionTimeout. |
| maxTotalConnections  | Signeringstjänsten använder sig av en connection-pool för de http-connections den använder. Max antal connections som denna pool hanterar styrs av denna parameter. |
| keystore.type | Överföring av signaturen till konsumenten rekommenderas att ske över TLS med sk. [Mutual Authentication](http://en.wikipedia.org/wiki/Mutual_authentication) används. Detta aktiveras av konsumentens mottagande server (Finns beskrivet i  [anslut till signeringstjänsten](http://code.google.com/p/oppna-program-signing-service/wiki/InDepth#Anslut_till_signeringstjänsten)). För att Mutual Authentication skall fungera måste klienten (i det här fallet signeringstjänsten, se steg 7 i bilden under [översiktlig systembeskrivning](http://code.google.com/p/oppna-program-signing-service/wiki/InDepth#Översiktlig_systembeskrivning)) presentera sig med ett SITHS-certifikat innehållande en privat nyckel. Detta certifikat lagras i en viss typ av  i något som kallas [keystore](http://en.wikipedia.org/wiki/Keystore). Det är denna typ som anges i detta fält. Exempel på olika typer är `JKS, P12 och PFX`. |
| keystore.location | Anger sökvägen på ovan nämnda keystore |
| keystore.password | Anger lösenordet på ovan nämnda keystore |
| truststore.type | Överföring av signaturen till konsumenten rekommenderas att ske över [TLS](http://en.wikipedia.org/wiki/Transport_Layer_Security). Detta innebär att den http-klient som signeringstjänsten använder sig av för detta förfarande måste lita på mottagaren av signaturen. Signeringstjänsten måste således ta del av mottagarens (konsumentens) publika certifikat och lägga in det i sitt truststore. På samma sätt som keystore finns även truststore i olika format och det är detta format som skall anges i det här fältet. Exempel på olika typer är `JKS, P12 och PFX`. |
| truststore.location | Anger sökvägen på ovan nämnda keystore |
| truststore.password | Anger lösenordet på ovan nämnda keystore |
| eid.endpoint  | För att verifiera en signatur använder sig signeringstjänsten av en annan tjänst. Denna tjänst kallas [OSIF](http://sveid.episerverhotell.net/SVEIDtemplates/SVEIDpage.aspx?id=124) och hostas idag av Logica. I detta fält anges servicens endpoint adress. |
| eid.serviceid | För att få nyttja [OSIF](http://sveid.episerverhotell.net/SVEIDtemplates/SVEIDpage.aspx?id=124)-tjänsten krävs att man skickar med ett seriviceId. Detta seriviceId anges här. |

Utöver denna konfiguration har man även möjlighet att sätta en System Properties vid uppstart av applikationen. Denna sätts mha -D-flaggan när jvm:en startas.

| System Property | Värden | Beskrivning |
|:------------------|:--------|:-------------|
| `ssl.hostname.verification` | `strict` eller `lenient` | Normalt när ett certifikat verifieras vid en TLS-konversation krävs att hostnamnet överensstämmer med certifikatnamnet. Genom att sätta denna property till `lenient` så kan man komma runt det kravet. Detta är bra framför allt under utveckling och test. I en produktionsmiljö är det rekommenderat att använda `strict` validering av hostnamnet. Utelämnas denna konfiguration så används `strict` som default.|

Slutligen, SSL/TLS-konfigurationen som exponeras mot webb-läsarna görs på den server som signerinstjänstens installerats. På en tomcat görs denna konfiguation i $TOMCAT\_HOME/conf/server.xml. Där skall en ny https-connector skapas med inställningar för vilken bla. port och keystore som skall användas. Ett exempel på hur detta kan se ut:
```
<Connector port="443" protocol="HTTP/1.1" SSLEnabled="true"
  maxThreads="150" scheme="https" secure="true"
  clientAuth="false" sslProtocol="TLS" keystoreFile="${user.home}/.ss/keystore.jks"
  keystorePass="s3cr3t-passw0rd" keystoreType="JKS"/>
```

# Felhantering #
# Loggning #

# Lokal Utvecklingsmiljö #
## Signeringstjänsten ##
Att sätta upp en utvecklingsmiljö för signeringstjänsten är relativt enkelt:
  1. Ladda ner källkoden från https://oppna-program-signing-service.googlecode.com/svn/signer-service/trunk/
  1. Kopiera hela katalogen core-bc/modules/web/doc/.ss till din hemkatalog (ex. `/Users/<name>/.ss`). Mer om vad den innehåller finns beskrivet i avsnittet [konfiguration](InDepth#Konfiguration.md)
  1. Navigera dig fram till core-bc/modules/web i ett terminal-/kommandofönster
  1. Kör maven-kommandot `mvn install jetty:run`

Vill man istället använda tomcat från sin IDE bör man utöver [normal serverkonfiguration](http://code.google.com/p/oppna-program/wiki/Anvisningar_Utvecklingsmiljo#Uppsättning_av_Tomcat_(vid_utveckling_av_web_applikationer)) lägga till en System Property via D-flaggan när tomcat startar. Detta görs enklast genom att:
  1. Dubbelklicka på din server i eclipse server-vy
  1. Klicka på länken _Open launch configuration_
  1. Välj fliken _Arguments_
  1. I fältet _VM arguments_ lägg in `-Dssl.hostname.verification=lenient`
  1. För att slå på SSL/TLS debug lägg även in `-Djavax.net.debug=ssl`

Alla dessa inställningar som görs för tomcat är redan förkonfigurerade i web-modulens pom.xml om man väljer att använda jetty.

Sätter man upp allt själv så kan man för certifikatkonfigurationen utgå från [produktionsmiljöns](InDepth#Certfikathantering.md) konfiguration.

## Referensapplikation ##

## Testcertifkat ##
För att kunna testa är det bra om man använder sig av testcertifikat. Dessa hittas under följande länkar:
  * [BankID](http://www.bankid.com/Global/wwwbankidcom/RP/Test%20BankIDn.zip)
  * [Telia](http://eid.trust.telia.com/Testcertifikat/Teliaelegmjukt/tabid/59/Default.aspx)

# Säkerhet #
## Certifikathantering ##
Signeringstjänsten använder en hel del certifikat, i följande avsnitt beskrivs vilka certifikat som används och vart i applikationen de hör hemma. Även några mindre exempel på hur nya certifikat kan genereras och importeras kommer att visas.

Signeringstjänsten agerar både som tjänstekonsument samt tjänsteproducent och i samtliga fall är det krav eller åtminstone rekommenderat att kommunikationen sker insynsskyddat. I samtliga fall är detta implementerat med hjälp av [TLS](http://en.wikipedia.org/wiki/Transport_Layer_Security). Det är dessutom krav eller rekommenderat att även kunna autentisera klienten, detta görs antingen på protokollnivå med sk. [Mutual Authentication](http://en.wikipedia.org/wiki/Mutual_authentication) eller på applikationsnivå med en biljett(ticket) som måste bifogas för att få utnyttja tjänsten.

Nedan visas en bild över vilka certifikat som används samt vilken metod som används för att autentisera klienten:
![http://oppna-program-signing-service.googlecode.com/svn/wiki/images/Certifikathantering.png](http://oppna-program-signing-service.googlecode.com/svn/wiki/images/Certifikathantering.png)

**Förklaring till bilden**:
  * Vid inkommande kommunikation bör signeringstjänsten exponera ett certifikat som är betrott av de vanliga webbläsarna. Klienten behöver skicka med en biljett vid varje anrop för att signeringsstjänsten skall kunna bekräfta behörigheten.
  * För utgående kommunikation med Logicas eID-tjänst krävs att signeringstjänsten betror det certifikat som eID-tjänsten exponerar. Detta certifikat läggs in i signeringstjänstens Truststore. eID-tjänsten kan även kräva att någon form av biljett skickas med vid anrop.
  * För utgående kommunikation med AppX är det rekommenderat att AppX använder sig av en TLS-anslutning med [Mutual Authentication](http://en.wikipedia.org/wiki/Mutual_authentication). Det krävs då att signeringsstjänsten har ett eget certifikat i sin keystore samt AppX publika nyckel i sin truststore. Samma sak gäller hos AppX, dvs. sitt eget certifikat i keystore samt signeringstjänstens certifikat i truststore.

För att konfigurera certifikaten krävs att man använder sig av ett verktyg där man kan skapa keystores och truststores samt importera certifikat. Ett exempel på sådant verktyg är keytool som följer med Javas JDK. Keytool är ett kommandotolkverktyg vilket innebär att man behöver en kommandotolk till hands för att utnyttja det. Nedan följer några exempel på användbara kommandon:

**Lista innehållet i en keystore/truststore**
```
keytool -v -list -keystore keystore.jks
```
**Skapa en keystore/truststore**
```
keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass password -validity 360 -keysize 2048
```
**Exportera den publika nyckeln från ett keystore**
```
keytool -export -alias mydomain -file mydomain.crt -keystore keystore.jks
```
**Importera en publik nyckel i en befintlig truststore**
```
keytool -import -trustcacerts -alias mydomain -file mydomain.crt -keystore keystore.jks
```

Mer information om keytool och olika kommandon kan hittas här:
  * http://www.sslshopper.com/article-most-common-java-keytool-keystore-commands.html
  * http://docs.oracle.com/javase/6/docs/technotes/tools/windows/keytool.html (Windows)
  * http://docs.oracle.com/javase/6/docs/technotes/tools/solaris/keytool.html (Linux/Mac)
  * http://www.devdaily.com/java/java-keytool-keystore-certificate-tutorials

  1. Skapa ett keystore med ett "CA-trustat" certifikat. Detta keystore pekas sedan ut i tomcat:ens server.xml. Se sist i avsnittet [konfiguration](InDepth#Konfiguration.md). I VGR:s fall används deras certifikat `*`.vgregion.se som är i formatet [PKCS #12](http://en.wikipedia.org/wiki/PKCS12).
  1. Skapa ett till keystore med ett SHITS-certifikat. Detta certifkat används för [Mutual Authentication](http://en.wikipedia.org/wiki/Mutual_authentication) gentemot AppX. Detta keystore pekas ut i signeringstjänstens config.properties under variablerna keystore.`*`.
  1. Exportera den publika delen av SITHS-certifikatet och dela ut till alla konsumerande applikationer. De behöver ha detta certifikat i deras truststore.
  1. Skapa ett truststore där AppX och Logicas eID-tjänsts publika nyckel importeras. Truststoret pekas ut ut i signeringstjänstens config.properties under variablerna truststore.`*`. AppX måste skicka över sin publika nyckel till signeringstjänsten.

# Kvarstående utvecklingspunkter #
  * Som konsument skall man kunna begränsa valmöjligheten av vilka certifikat som man får signera med. T.ex. i fallet där AppX är behörighetsskyddad med eID, då skall AppX kunna välja att endast den inloggades certifikat skall få användas för signering. Detta görs genom att skicka in ett id för certifikatet till signeringstjänsten vilken sen endast exponerar signeringsrutan om certifikatet finns hos användaren. Detta kan göras genom att sätt fältet subject eller issuers i api:t för pki-klienterna.

# Anslut till signeringstjänsten #
In nedanstående guide kommer vi att gå igenom steg för steg hur man ansluter till signeringstjänsten. För att underlätta processen så kommer vi att göra det iterativt, första steget är att få det fungera över vanlig http, nästa steg blir att aktivera SSL/TLS och i sista steget lägger vi på Mutual Authentication.

  1. Det första vi behöver sätta upp är en tjänst dit signeringstjänsten kan leverera signaturen. Detta kan vara antingen i form av en http- eller ftp-server, här kommer vi att beskriva fallet http-server. Tjänsten måste acceptera POST med följande parametrar i dess body:
    * `envelope - XML-envelope innehållande  signaturen`
  1. Nästa steg blir att erbjuda signering till användarna i form av en html-sida. Från denna html sida görs en Http POST till https://service-host.com/sign/prepare med följande parametrar i dess body:
    * `tbs - Data som skall signeras`
    * `submitUri - Uri till den tjänst som vi satte upp i steg 1`
    * `clientType - Anger vilken typ av nyckel som skall användas vid signeringen. Välj mellan: BankId, Telia, Nordea eller SITHS. Utelämnas detta kommer signeringstjänsten att presentera sida där användaren får välja.`
  1. Nu är det dags för ett första försök, går allt bra så kan du fortsätta till nästa steg.
  1. Funkar allt nu så är det dags att säkra upp överföringen av signaturen. Detta görs i två steg
    1. Aktivera https på den tjänst som aktiverades i steg 1. Hur man gör detta beror på vilken webserver man använder. Vid anslutning mot utbildnings-miljön kan man använda ett egenutfärdat certifikat.
    1. Den publika delen av certifikatet måste skickas till en administratör av signeringstjänten för import i dess truststore.
  1. Slutligen så skall vi aktivera Mutual Authentication, detta för att den anslutande applikationen skall veta att det är signeringstjänsten som skickar signaturen och inte någon annan som utger sig för att vara det. Detta t.ex. kan ske i en [man-in-the-middle-attack](http://en.wikipedia.org/wiki/Man-in-the-middle_attack). Vi gör även detta i två steg:
    1. Aktivera Mutual Authentication på webbservern. Detta är levereantörsspecifikt, på tomcat t.ex. sätts attributet clientAuth på connector-taggen i server.xml till true.
    1. Ladda ner och lägg in signeringstjänstens [klient-certifikat] i ditt truststore.