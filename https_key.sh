sudo keytool -genkey -keyalg RSA -alias trackapi_cert -keystore trackapi_cert.jks -storepass trackapi123 -validity 365 -keysize 4096 -storetype pkcs12
sudo keytool -export -alias trackapi_cert -storepass trackapi123 -file trackapi.cer -keystore trackapi_cert.jks
