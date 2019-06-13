# Generate the key pair and place it in a keystore
keytool -genkeypair -alias broker -keyalg RSA -keysize 512 -validity 365 -storetype JCEKS -keystore ../../../../../../resources/brokerKey.jck -storepass brokerStorePass -dname "cn=broker, ou=cp130, o=UW, l=Seattle, st=Washington, c=US" -keypass brokerPrivKeyPass

# Extract the certificate from the keystore
keytool -export -alias broker -storetype JCEKS -keystore ../../../../../../resources/brokerKey.jck -storepass brokerStorePass -file brokerCert.crt

# Place the certificate in a trust store for the client
keytool -importcert -noprompt -alias broker -file brokerCert.crt -storetype JCEKS -keystore ../../../../../../resources/clientTrust.jck -storepass clientTrustPass


# Generate the key pair and place it in a keystore
keytool -genkeypair -alias client -keyalg RSA -keysize 512 -validity 365 -storetype JCEKS -keystore ../../../../../../resources/clientKey.jck -storepass clientStorePass -dname "cn=client, ou=cp130, o=UW, l=Seattle, st=Washington, c=US" -keypass clientPrivKeyPass

# Extract the certificate from the keystore
keytool -export -alias client -storetype JCEKS -keystore ../../../../../../resources/clientKey.jck -storepass clientStorePass -file clientCert.crt

# Place the certificate in a trust store for the client
keytool -importcert -noprompt -alias client -file clientCert.crt -storetype JCEKS -keystore ../../../../../../resources/brokerTrust.jck -storepass brokerTrustPass

# List out the Broker Keystore file
keytool -v -list -storetype jceks -keystore ../../../../../../resources/brokerKey.jck -storepass brokerStorePass

# List out the Client Keystore file
keytool -v -list -storetype jceks -keystore ../../../../../../resources/clientKey.jck -storepass clientStorePass

# List out the Broker Trust file
keytool -v -list -storetype jceks -keystore ../../../../../../resources/brokerTrust.jck -storepass brokerTrustPass

# List out the CLient Trust file
keytool -v -list -storetype jceks -keystore ../../../../../../resources/clientTrust.jck -storepass clientTrustPass
