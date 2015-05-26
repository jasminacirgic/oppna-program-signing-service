# Introduktion #

Signeringstjänsten är framtagen för att andra system skall få hjälp med att signera viktig information och dokument. Detta görs i dagsläget genom att posta in signeringsdata till tjänsten. Nedan följer en kortfattad tutorial över hur man som konsument använder sig av signeringstjänsten.

# Krav #

För att kunna följa denna tutorial så krävs följande
  1. En fungerande instans av signeringstjänsten.
  1. [BISP](https://install.bankid.com) är installerat på klienten.
  1. Tillgång till antingen ett hårt eller mjukt BankId-certifikat.

# Klara, Färdiga, Gå! #
  * Anta att vi vill signera följande text: _Sätt in 1000 kronor på konto 123456-7_.
  * Anta även att vi vill göra det med ett BankId-certifikat.
  * Dessutom antar vi att signeringstjänsten finns på följande adress: https://signer-service.com.

Skapa då en html-sida med följande formulär:
```
<form method="post" action="https://signer-service.com/sign/prepare">
  <input name="tbs"  value="Sätt in 1000 kronor på konto 123456-7" type="text" />
  <input name="submitUri"  value="http://myapplication.com/saveSignature" type="text" />
  <input name="clientType" value="BankId" type="text" />
  <input type="submit" value="Signera" />
</form>
```

| **Parameter** | **Förklaring** |
|:--------------|:----------------|
|tbs            |Innehåller den data som man vill signera. I ett mer realistiskt exempel så bör ett kondensat(hash) räknas fram för signering istället.|
|submitUri      |Uri dit man vill skicka signaturen. Kan vara antingen en servlet eller en ftp-server. I praktiken bör denna överföring göras över ssl.|
|clientType     |Det certifikat som man vill signera med (BankId, Telia, Nordea eller SITHS)|


När formuläret postats in kommer signeringstjänsten att visa följande fönster för användaren där lösenordet för BankId-certifikatet matas in:

<img src='http://oppna-program-signing-service.googlecode.com/svn/wiki/images/BankId-Sign.png' width='400' />

Signaturen skickas till den angivna adressen i _submitUri_. Mottagaren av signaturen är antingen en servlet eller en ftp-server. I fallet där man har en servlet som mottagare så visas här ett exempel på hur en sådan skulle kunna implementeras i springs mvc-ramverk:

```
@RequestMapping(value = "/saveSignature", method = RequestMethod.POST)
  public void postback(@RequestBody byte[] signature) {
  signatures.store(signature);
}
```

# Klart! #
För att få en mer djupgående genomgång kika på InDepth.