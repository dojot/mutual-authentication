#!/bin/sh

cd ..
java -jar -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4001,suspend=n -Dhazelcast.logging.type=log4j -Dlog4j.configurationFile=config/log4j2.xml CryptoIntegrationHazelcastServer.jar
