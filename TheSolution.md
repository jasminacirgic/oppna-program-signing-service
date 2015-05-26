# Lösningsbeskrivning #
<img src='http://oppna-program-signing-service.googlecode.com/svn/wiki/images/solution.png' width='800' />

  1. Klienten begär att få något signerat(TBS – To Be Signed) av SS. Rekommenderat är att skicka en kontrollsumma av TBS. Kontrollsumman är förslagsvis beräknad utifrån [SHA-2](http://en.wikipedia.org/wiki/SHA-2)(t.ex. SHA-256) för att få en ökad säkerhet. SS behöver även ha en submitUri dit signaturen skall skickas när den är klar. Valbart(optional) är att skicka in en clientType.
  1. Har ingen clientType skickats med visas ett val av tillgängliga clientTypes för användaren. SS tar fram ett block av html/xhtml([PKI](http://en.wikipedia.org/wiki/Public_key_infrastructure) Client Code) baserat på clientType och TBS som den visar för klienten.
  1. [PKI](http://en.wikipedia.org/wiki/Public_key_infrastructure)-klienten startar upp för signering hos användaren.
  1. Användaren matar in lösenord för sin privata nyckel och TBS signeras. [PKI](http://en.wikipedia.org/wiki/Public_key_infrastructure)-klienten postar signaturen [PKSC#7](http://en.wikipedia.org/wiki/Cryptographic_Message_Syntax) eller [XML-Signature](http://www.w3.org/TR/xmldsig-core/)) till SS. Vilket format som skickas till SS beror på clientType.
  1. Signaturen verifieras via en [OSIF](http://sveid.episerverhotell.net/SVEIDtemplates/SVEIDpage.aspx?id=124) tjänst.
  1. Status för hur valideringen gick skickas tillbaka till SS.
  1. SS skickar signaturen till submitUri för lagring, detta sker över ftps eller https. Om https väljs finns även en möjlighet för applikationen att själv presentera status för signeringen (se steg 7). I annat fall kommer SS att hantera presentationen av status.
  1. Om X själv kan och vill presentera status för signeringen gör den en [302 redirect](http://en.wikipedia.org/wiki/HTTP_302) till en url som hanterar presentationen. Om X vill överlåta detta till SS räcker det med att svara med en vanlig [200 OK](http://en.wikipedia.org/wiki/List_of_HTTP_status_codes) response.
  1. Om SS får en [302 redirect](http://en.wikipedia.org/wiki/HTTP_302) från X skickar även SS en [302 redirect](http://en.wikipedia.org/wiki/HTTP_302) tillbaka till klienten med samma location som X satte i sin redirect. Om SS får en [200 OK](http://en.wikipedia.org/wiki/List_of_HTTP_status_codes) visar den en status sida för klienten.
  1. [302 redirect](http://en.wikipedia.org/wiki/HTTP_302) till location satt av X.
  1. X visar ett svar för klienten.
**Kommentarer**
  * Steg 10 och 11 kommer endast att inträffa om X gör en [302 redirect](http://en.wikipedia.org/wiki/HTTP_302) i steg 8.

# Säkerhet #
För att garantera säkerheten bör all kommunikation ske över en krypterad anslutning – https. Det finns dock fortfarande en risk att SS blir utsatt för en attack och [ersätts av en ”elak”](http://en.wikipedia.org/wiki/Man-in-the-middle_attack) SS som byter ut signatur och certifikat. För att förhindra detta är det starkt rekommenderat att X sätter upp [TLS](http://en.wikipedia.org/wiki/Transport_Layer_Security) mot SS. [TLS](http://en.wikipedia.org/wiki/Transport_Layer_Security) syftar till att genom utbyte av certifikat mellan SS och X kan X grantera att identiteten på SS. Alltså, med tillgång till SS publika nyckel kan X verifiera att det är SS som skickar signaturen till submitUri.

# Ordlista #
| **Begrepp** | **Förklaring** |
|:------------|:----------------|
| TBS         | To Be Signed. Data som skall signeras. |
| submitUri   | Uri dit signaturen skall skickas efter signering. |
| clientType  | Typ av signering, i dagsläget har Signeringstjänsten stöd för BankId, Nordea, Posten och SITHS. |