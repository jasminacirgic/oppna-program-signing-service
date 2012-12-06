Information about the files in this directory

appx.crt - The certificate which is exported from keystore.jks and is imported into the Signing Service's
truststore. The mentioned files are used when the Signing Service makes a postback to Appx for the HTTPS transport.

config.properties - Properties for Appx. Modify this e.g. to change between local, test, qa and prod mode.

GlobalSignDomainValidationCA.crt - This file is imported into truststore.jks in order to trust the *.vgregion.se
certificate since the *.vgregion.se certificate is signed by GlobalSignDomainValidationCA. This entry is used for
the test environment when Appx requests a ticket from the Signing Service.

keystore.jks - This keystore is used when the Signing Service issues a postback to Appx to deliver the signature.

signera-local.crt - This file is exported from the Signing Service's keystore and imported into truststore.jks and
truststore7443.jks. The mentioned files are used both when Appx issues a ticket request to the Signing Service as
well as when the Signing Service authenticates as part of the mutual authentication which occurs when the Signing
Service makes the postback to Appx with the signature. This file is only used when running the Signing Service
locally.

signera-test.crt - This certificate is exported from the Signing Service's keystore from the test environment. It
is actually not imported into the truststore.jks since it is enough that the issuer is imported, namely the
GlobalSignDomainValidationCA.crt certificate.

SITHS_CA_v3.crt - This certificate is imported into truststore7443.jks and is fetched from the keystore of any of
the environments except local. It is used for incoming requests when the Signing Service makes a postback to Appx
which requires mutual authentication.

truststore.jks - Used for outgoing traffic to the Signing Service. There should be one "trustedCertEntry" for each
environment your Appx should be able to communicate with.

truststore7443.jks - Used for incoming traffic from the Signing Service. "7443" is for the port number of the
connector this truststore is configured for. Having a truststore for incoming requests is for mutual
authentication.