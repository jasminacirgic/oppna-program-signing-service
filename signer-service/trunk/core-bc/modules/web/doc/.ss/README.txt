Information about the files in this directory

appx.crt - This is the certificate file which is exported from appx's keystore and imported into
signer-service-truststore.jks. It is used for the HTTPS transport when the Signing Service issues a postback to
Appx.

config.properties - Properties for the application. The properties in this file override the properties in
connection.properties.

service-ids.properties - Here the service-ids are stored which are authorized to request a ticket for the signing.

signer-service.jks - This is the keystore with the private key entry. It is used for encryption for the HTTPS
transport with client browsers as well as authenticate in the mutual authentication which takes place when the
Signing Service makes a postback to Appx.