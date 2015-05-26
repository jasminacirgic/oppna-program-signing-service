## Bakgrund och ändamål ##

En elektronisk signatur ska säkerställa att elektroniskt överförd information inte har ändrats
och för att identifiera informationens avsändare. Genom kryptering skyddas uppgifter i ett
dokument mot obehörig åtkomst.

### Vad är en elektronisk signatur ###
En elektroniskt underskriven handling består av två delar. Dels _texten_ som skall signeras och dels själva signaturen. För att säkerställa att en _text_ är identisk vid två olika tillfällen beräknas en kontrollsumma av _texten_. Denna kontrollsumma är _alltid_ densamma så länge _texten_ inte har förändrats och därmed kan man garantera att _texten_ inte har förändrats över tid. Detta är dock inte tillräckligt utan man behöver även kunna säkerställa _vem_ som har utfärdat _texten_. Detta görs genom att kontrollsumman krypteras med undertecknarens privat nyckel. Dekryptering kan sedan _endast_ göras med hjälp av undertecknarens publika nyckel. Nu kan man med säkerhet knyta en viss _text_ till en bestämd utställare.

## Hur använder jag signeringstjänsten? ##
Första steget för en applikation är att erbjuda signering för en användare. Efter detta tar signeringstjänsten över och hanterar presentationen av signeringsklineten (t.ex. NetID) med allt vad det innebär, signeringstjänsten tar även hand om certifikatskontrollen som bla. verifierar att certifikatet som används i signaturen inte är spärrat. Det den nyttjande applikationen behöver ta hänsyn till är lagringen av signaturen. Nedan visas en schematisk bild över hur flödet ser ut, de heldragna pilarna är de interaktioner som måste hanteras/implementeras av applikationen.
![http://oppna-program-signing-service.googlecode.com/svn/wiki/images/Signatureservice-process.png](http://oppna-program-signing-service.googlecode.com/svn/wiki/images/Signatureservice-process.png)


[Läs mer](http://code.google.com/p/oppna-program-signing-service/wiki/InDepth)