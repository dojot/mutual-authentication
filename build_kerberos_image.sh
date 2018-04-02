#!/bin/bash
set -ex

execstack --version || apt-get install -y execstack

madir=$(pwd)

# Build Jedis
cd ${madir}/jedis
mvn clean install -DskipTests -U
# Build communication
cd ${madir}/ma-communication
mvn clean install -U
# Build registry
cd ${madir}/ma-registryclient
mvn clean install -U

# Build loggingapi
cd ${madir}/ma-loggingapi/LoggingAPI
mvn clean package -U

# Build libjcrypto
cd ${madir}/ma-libjcrypto
mvn clean package install -U
# Build libaes-c
cd ${madir}/ma-libaes-c/src
make

# Build kerberos
cd ${madir}/ma-kerberos/Java/KerberosIntegration/
mvn clean package -U
# Build crypto
cd ${madir}/ma-crypto/Java/CryptoIntegration/
mvn clean package -U
# Build serviceregistry
cd ${madir}/ma-serviceregistry/Java/ServiceRegistry/
mvn clean package -U

# Copy targets
mkdir -p ${madir}/application

cp ${madir}/ma-kerberos/Java/KerberosIntegration/KerberosIntegration-ear/target/kerberosintegration.ear ${madir}/application
cp ${madir}/ma-crypto/Java/CryptoIntegration/CryptoIntegration-ear/target/cryptointegration.ear ${madir}/application
cp ${madir}/ma-serviceregistry/Java/ServiceRegistry/ServiceRegistry-ear/target/serviceregistry.ear ${madir}/application
cp ${madir}/ma-libjcrypto/target/libjcrypto-2.0.0-SNAPSHOT.jar ${madir}/application
cp ${madir}/ma-libjcrypto/module.xml ${madir}/application
cp ${madir}/ma-libaes-c/src/libjcrypto.so ${madir}/application
cp ${madir}/ma-loggingapi/LoggingAPI/LoggingAPI-ear/target/loggingapi.ear ${madir}/application
